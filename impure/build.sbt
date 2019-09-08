// *****************************************************************************
// Projects
// *****************************************************************************

lazy val impure =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .configs(IntegrationTest)
    .settings(settings)
    .settings(
      Defaults.itSettings,
      headerSettings(IntegrationTest),
      inConfig(IntegrationTest)(scalafmtSettings),
      IntegrationTest / console / scalacOptions --= Seq("-Xfatal-warnings", "-Ywarn-unused-import"),
      IntegrationTest / parallelExecution := false,
      IntegrationTest / unmanagedSourceDirectories := Seq((IntegrationTest / scalaSource).value)
    )
    .settings(
      libraryDependencies ++= Seq(
	library.akkaActor,
        library.akkaHttp,
        library.akkaHttpJson,
        library.akkaSlf4j,
        library.akkaStream,
        library.catsCore,
        library.circeCore,
        library.circeGeneric,
        library.circeRefined,
        library.circeParser,
	library.flywayCore,
        library.logback,
	library.postgresql,
        library.refinedCats,
        library.refinedCore,
        library.slick,
        library.slickHikariCP,
        library.akkaHttpTestkit   % IntegrationTest,
        library.akkaStreamTestkit % IntegrationTest,
        library.akkaTestkit       % IntegrationTest,
        library.refinedScalaCheck % IntegrationTest,
        library.scalaCheck        % IntegrationTest,
        library.scalaTest         % IntegrationTest,
        library.akkaHttpTestkit   % Test,
        library.akkaStreamTestkit % Test,
        library.akkaTestkit       % Test,
        library.refinedScalaCheck % Test,
        library.scalaCheck        % Test,
        library.scalaTest         % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val akka         = "2.5.24"
      val akkaHttp     = "10.1.9"
      val akkaHttpJson = "1.27.0"
      val cats         = "1.6.1"
      val circe        = "0.11.1"
      val flyway       = "6.0.1"
      val logback      = "1.2.3"
      val postgresql   = "42.2.6"
      val refined      = "0.9.9"
      val scalaCheck   = "1.14.0"
      val scalaTest    = "3.0.8"
      val slick        = "3.3.2"
    }
    val akkaActor         = "com.typesafe.akka"      %% "akka-actor"           % Version.akka
    val akkaTestkit       = "com.typesafe.akka"      %% "akka-testkit"         % Version.akka
    val akkaHttp          = "com.typesafe.akka"      %% "akka-http"            % Version.akkaHttp
    val akkaHttpJson      = "de.heikoseeberger"      %% "akka-http-circe"      % Version.akkaHttpJson
    val akkaHttpTestkit   = "com.typesafe.akka"      %% "akka-http-testkit"    % Version.akkaHttp
    val akkaSlf4j         = "com.typesafe.akka"      %% "akka-slf4j"           % Version.akka
    val akkaStream        = "com.typesafe.akka"      %% "akka-stream"          % Version.akka
    val akkaStreamTestkit = "com.typesafe.akka"      %% "akka-stream-testkit"  % Version.akka
    val catsCore          = "org.typelevel"          %% "cats-core"            % Version.cats
    val circeCore         = "io.circe"               %% "circe-core"           % Version.circe
    val circeGeneric      = "io.circe"               %% "circe-generic"        % Version.circe
    val circeRefined      = "io.circe"               %% "circe-refined"        % Version.circe
    val circeParser       = "io.circe"               %% "circe-parser"         % Version.circe
    val flywayCore        = "org.flywaydb"           %  "flyway-core"          % Version.flyway
    val logback           = "ch.qos.logback"         %  "logback-classic"      % Version.logback
    val postgresql        = "org.postgresql"         %  "postgresql"           % Version.postgresql
    val refinedCore       = "eu.timepit"             %% "refined"              % Version.refined
    val refinedCats       = "eu.timepit"             %% "refined-cats"         % Version.refined
    val refinedScalaCheck = "eu.timepit"             %% "refined-scalacheck"   % Version.refined
    val scalaCheck        = "org.scalacheck"         %% "scalacheck"           % Version.scalaCheck
    val scalaTest         = "org.scalatest"          %% "scalatest"            % Version.scalaTest
    val slick             = "com.typesafe.slick"     %% "slick"                % Version.slick
    val slickHikariCP     = "com.typesafe.slick"     %% "slick-hikaricp"       % Version.slick
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  scalafmtSettings

val licenseText = s"""CC0 1.0 Universal (CC0 1.0) - Public Domain Dedication
                   |
                   |                               No Copyright
                   |
                   |The person who associated a work with this deed has dedicated the work to 
                   |the public domain by waiving all of his or her rights to the work worldwide
                   |under copyright law, including all related and neighboring rights, to the 
                   |extent allowed by law.""".stripMargin

lazy val commonSettings =
  Seq(
    scalaVersion := "2.12.9",
    organization := "com.wegtam",
    organizationName := "Jens Grassel",
    startYear := Some(2019),
    headerLicense := Some(HeaderLicense.Custom(licenseText)),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-explaintypes",
      "-feature",
      "-language:higherKinds",
      "-target:jvm-1.8",
      "-unchecked",
      "-Xcheckinit",
      "-Xfatal-warnings",
      "-Xfuture",
      "-Xlint:adapted-args",
      "-Xlint:by-name-right-associative",
      "-Xlint:constant",
      "-Xlint:delayedinit-select",
      "-Xlint:doc-detached",
      "-Xlint:inaccessible",
      "-Xlint:infer-any",
      "-Xlint:missing-interpolator",
      "-Xlint:nullary-override",
      "-Xlint:nullary-unit",
      "-Xlint:option-implicit",
      "-Xlint:package-object-classes",
      "-Xlint:poly-implicit-overload",
      "-Xlint:private-shadow",
      "-Xlint:stars-align",
      "-Xlint:type-parameter-shadow",
      "-Xlint:unsound-match",
      "-Ydelambdafy:method",
      "-Yno-adapted-args",
      "-Ypartial-unification",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused-import",
      "-Ywarn-value-discard"
    ),
    Compile / console / scalacOptions --= Seq("-Xfatal-warnings", "-Ywarn-unused-import"),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Compile / compile / wartremoverWarnings ++= Warts.unsafe,
    Test / console / scalacOptions --= Seq("-Xfatal-warnings", "-Ywarn-unused-import"),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value),
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
  )
