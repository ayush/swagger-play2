import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "swagger-play2"
    val appVersion      = "1.0.2-rc"

    val appDependencies = Seq(
	  "org.codehaus.jackson" % "jackson-jaxrs" % "1.7.1",
	  "org.codehaus.jackson" % "jackson-xc" % "1.7.1",
	  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.7.1",
	  "org.codehaus.jackson" % "jackson-core-asl" % "1.7.1",
      "org.slf4j" % "slf4j-api" % "1.6.4",
      "com.wordnik" % "swagger-core" % "1.1-SNAPSHOT.121132",
      "javax.ws.rs" % "jsr311-api" % "1.1.1"
    )


	val wnRepo = Some(Resolver.url("wordnik-remote-repos", new URL( "https://ci.aws.wordnik.com/artifactory/libs-snapshots"))(Resolver.ivyStylePatterns))

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
        // Never publish routes or application.conf
        mappings in (Compile,packageBin) ~= { (ms: Seq[(File, String)]) =>
            val routesFilter = """Routes\$\$anonfun\$routes\$[0-9]+\$\$anonfun\$apply\$([\w\$]+).class""".r
            val mainRoutesFilter = """Routes\$\$anonfun\$routes\$([0-9]+).class""".r
            val routesStraightFilter = """(Routes\$.class)""".r
            val controllerRoutesFilter = """([\w\$/]*)controllers/routes([\w\$]*).class""".r
            ms filter {                
                case (file, toPath) => {
                    println(file.toString)
                    println(toPath)
                    val routesMatch = toPath match {
                        case routesFilter(_) => true
                        case mainRoutesFilter(_) => true
                        case routesStraightFilter(_) => true
                        case controllerRoutesFilter(_,_) => true
                        case _ => false }
                    !routesMatch && toPath != "routes" && toPath != "application.conf"
                }
            }
        },
        
        credentials := Credentials(Path.userHome / ".ivy2" / ".credentials") :: Nil,
        publishTo := Some("kloutPluginReleases" at "http://maven-repo:8081/artifactory/ext-release-local"),

	 	resolvers += "local-maven-repo" at "file://" + Path.userHome.absolutePath + "/.m2/repository/",
	 	resolvers += Resolver.url("local-ivy", new URL( "file://" + Path.userHome.absolutePath + "/.ivy2/local"))(Resolver.ivyStylePatterns),
	 	resolvers += Resolver.url("local-ivy-cache", new URL( "file://" + Path.userHome.absolutePath + "/.ivy2/cache"))(Resolver.ivyStylePatterns),
	 	resolvers += Resolver.url("wordnik-remote-repos", new URL( "https://ci.aws.wordnik.com/artifactory/libs-snapshots"))(Resolver.ivyStylePatterns),
	 	resolvers += "java-net" at "http://download.java.net/maven/2"
        )
}
