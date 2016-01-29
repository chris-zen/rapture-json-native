import com.typesafe.sbt.pgp.PgpKeys.publishSigned
import ReleaseTransformations._

//enablePlugins(GitBranchPrompt)

lazy val buildSettings = Seq(
  organization := "org.zalando",
  scalaVersion := "2.11.7",
  crossScalaVersions := Seq("2.11.7", "2.10.5")
)

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Xlint",
    "-Ywarn-dead-code"
    /*   "-language:existentials",
       "-language:higherKinds",
       "-language:implicitConversions",
       "-language:experimental.macros",
       "-Xfatal-warnings",
       "-Yinline-warnings",
       "-Yno-adapted-args",
       "-Ywarn-numeric-widen",
       "-Ywarn-value-discard",
       "-Xfuture" */
  ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 11)) => Seq("-Ywarn-unused-import")
    case _             => Seq.empty
  }),
  scalacOptions in (Compile, console) ~= (_ filterNot (_ == "-Ywarn-unused-import")),
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
  scalaJSStage in Test := FastOptStage,
  concurrentRestrictions in Global ++= Seq(Tags.limitSum(2, Tags.CPU, Tags.Untagged), Tags.limit(Tags.Test, 1)),
  scmInfo := Some(ScmInfo(url("https://github.com/chris-zen/rapture-json-native"),
    "scm:git:git@github.com:chris-zen/rapture-json-native.git"))
) //++ scalaMacroDependencies

lazy val raptureSettings = buildSettings ++ commonSettings ++ publishSettings

// rapture-json-native
lazy val `json-native` = crossProject
  .settings(moduleName := "rapture-json-native")
  .settings(raptureSettings:_*)
  .settings(libraryDependencies ++= Seq(
    "com.propensive"             %% "rapture-json"      % "2.0.0-M3",
    "com.fasterxml.jackson.core"  % "jackson-databind"  % "2.6.3"))

lazy val jsonNativeJVM = `json-native`.jvm
lazy val jsonNativeJS = `json-native`.js

// rapture-json-test
lazy val `json-test` = crossProject.dependsOn(`json-native`)
  .settings(moduleName := "rapture-json-test")
  .settings(raptureSettings:_*)

lazy val jsonTestJVM = `json-test`.jvm
lazy val jsonTestJS = `json-test`.js

lazy val publishSettings = Seq(
  homepage := Some(url("http://github.com/chris-zen/rapture-json-native/")),
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  autoAPIMappings := true,
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := (
    <developers>
      <developer>
        <id>chris-zen</id>
        <name>Christian Perez-Llamas</name>
        <url>http://github.com/chris-zen/rapture-json-native</url>
      </developer>
    </developers>
    ),
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
    pushChanges
  ),
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

lazy val noSourceSettings = Seq(
  sources in Compile := Seq(),
  sources in Test := Seq()
)


import java.io.File

def crossVersionSharedSources()  = Seq(
  (unmanagedSourceDirectories in Compile) ++= { (unmanagedSourceDirectories in Compile ).value.map {
    dir:File => new File(dir.getPath + "_" + scalaBinaryVersion.value)}}
)

addCommandAlias("gitSnapshots", ";set version in ThisBuild := git.gitDescribedVersion.value.get + \"-SNAPSHOT\"")

// For Travis CI - see http://www.cakesolutions.net/teamblogs/publishing-artefacts-to-oss-sonatype-nexus-using-sbt-and-travis-ci
credentials ++= (for {
  username <- Option(System.getenv().get("SONATYPE_USERNAME"))
  password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
} yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq
