import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "swagger-play2"
    val appVersion      = "1.0"

	Credentials.add("wordnik-remote-repos", "ci.wordnik.com", "mavenuser", "n3wm4v3np455")

    val appDependencies = Seq(
	  "org.codehaus.jackson" % "jackson-jaxrs" % "1.7.1",
	  "org.codehaus.jackson" % "jackson-xc" % "1.7.1",
	  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.7.1",
	  "org.codehaus.jackson" % "jackson-core-asl" % "1.7.1",
      "org.slf4j" % "slf4j-api" % "1.6.4",
      "com.wordnik" % "swagger-core" % "1.1-SNAPSHOT.121132",
      "javax.ws.rs" % "jsr311-api" % "1.1.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    // val main = PlayProject(appName, appVersion, appDependencies).settings(
	 	resolvers += "local-maven-repo" at "file://" + Path.userHome.absolutePath + "/.m2/repository/",
	 	resolvers += Resolver.url("local-ivy", new URL( "file://" + Path.userHome.absolutePath + "/.ivy2/local"))(Resolver.ivyStylePatterns),
	 	resolvers += Resolver.url("local-ivy-cache", new URL( "file://" + Path.userHome.absolutePath + "/.ivy2/cache"))(Resolver.ivyStylePatterns),
	 	resolvers += Resolver.url("wordnik-remote-repos", new URL( "https://ci.wordnik.com/artifactory/remote-repos"))(Resolver.ivyStylePatterns),
	 	resolvers += "java-net" at "http://download.java.net/maven/2"
	    )

}
