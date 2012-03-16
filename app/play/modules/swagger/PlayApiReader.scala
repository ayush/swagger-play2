package play.modules.swagger

import java.lang.reflect.Method
import com.wordnik.swagger.core._
import collection.JavaConversions._
import play.api.Play.current
import play.api.Logger
import play.core.Router.RoutesCompiler.Route
import play.core.Router.DynamicPart
import play.core.Router.StaticPart

/**
  * Caches and retrieves API information for a given Swagger compatible class
  *
  * @author ayush
  * @since 10/9/11 7:13 PM
  *
  */
object PlayApiReader {
  import scalax.file.Path
  import java.io.File
  import play.core.Router.RoutesCompiler.RouteFileParser
  private val endpointsCache = scala.collection.mutable.Map.empty[Class[_], Documentation]
  lazy val routesCache : Map[String,Route] = populateRoutesCache

  def read(hostClass: Class[_], apiVersion: String, swaggerVersion: String, basePath: String, apiPath: String): Documentation = {
    endpointsCache.get(hostClass) match {
      case None => val doc = new PlayApiSpecParser(hostClass, apiVersion, swaggerVersion, basePath, apiPath).parse; endpointsCache += hostClass -> doc.clone.asInstanceOf[Documentation]; doc
      case Some(doc) => doc.clone.asInstanceOf[Documentation]
      case _ => null
    }
  }

  private def populateRoutesCache : Map[String,Route] = {
    val routesFile = Path(new File("conf/routes"))
    val routesString = routesFile.slurpString
    val parser = new RouteFileParser
    val parsedRoutes = parser.parse(routesString)
    parsedRoutes match {
        case parser.Success(routes,_) => {
            routes map {
                route => {
                    val routeName = route.call.packageName + "." + route.call.controller + "$." + route.call.method
                    Logger info "route:" + routeName
                    (routeName, route)
                }
            } toMap
        }
        case _ => Map[String,Route]()
    }
  }
}

/**
  * Reads swaggers annotations, play route information and uses reflection to build API information on a given class
  */
private class PlayApiSpecParser(hostClass: Class[_], apiVersion: String, swaggerVersion: String, basePath: String, resourcePath: String)
  extends ApiSpecParser(hostClass, apiVersion, swaggerVersion, basePath, resourcePath) {
  override def getPath(method: Method) = {
    val fullMethodName = method.getDeclaringClass.getName + "." + method.getName
    val lookup = PlayApiReader.routesCache.get(fullMethodName)
    lookup match {
      case Some(route) => route.path.parts map { 
        part => {
          part match {
            case DynamicPart(name,_) => "{" + name + "}"
            case StaticPart(name) => name
          }
        }
      } mkString
      case None => Logger info "Cannot determine Path. Nothing defined in play routes file for api method " + method.toString; this.resourcePath
    }
  }
  override protected def processOperation(method: Method, o: DocumentationOperation) = {
    val fullMethodName = method.getDeclaringClass.getCanonicalName + "." + method.getName
    Logger info fullMethodName
    val lookup = PlayApiReader.routesCache.get(fullMethodName)
    lookup match {
      case Some(route) => o.httpMethod = route.verb.value
      case None => Logger info "Could not find route " + fullMethodName
    }
    o
  }
}
