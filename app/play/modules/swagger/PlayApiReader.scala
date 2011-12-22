package play.modules.swagger

import java.lang.reflect.Method
import com.wordnik.swagger.core._
import collection.JavaConversions._
import play.api.Play.current
import play.api.Logger

/**
  * Caches and retrieves API information for a given Swagger compatible class
  *
  * @author ayush
  * @since 10/9/11 7:13 PM
  *
  */
object PlayApiReader {
  private val endpointsCache = scala.collection.mutable.Map.empty[Class[_], Documentation]

  def read(hostClass: Class[_], apiVersion: String, swaggerVersion: String, basePath: String, apiPath: String): Documentation = {
    endpointsCache.get(hostClass) match {
      case None => val doc = new PlayApiSpecParser(hostClass, apiVersion, swaggerVersion, basePath, apiPath).parse; endpointsCache += hostClass -> doc.clone.asInstanceOf[Documentation]; doc
      case Some(doc) => doc.clone.asInstanceOf[Documentation]
      case _ => null
    }
  }
}

/**
  * Reads swaggers annotations, play route information and uses reflection to build API information on a given class
  */
private class PlayApiSpecParser(hostClass: Class[_], apiVersion: String, swaggerVersion: String, basePath: String, resourcePath: String)
  extends ApiSpecParser(hostClass, apiVersion, swaggerVersion, basePath, resourcePath) {

  override protected def processOperation(method: Method, o: DocumentationOperation) = o
}
