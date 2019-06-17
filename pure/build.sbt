// *****************************************************************************
// Projects
// *****************************************************************************

lazy val pure =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.catsCore,
        library.circeCore,
        library.circeGeneric,
        library.circeRefined,
        library.circeParser,
	library.doobieCore,
	library.doobieHikari,
	library.doobiePostgres,
	library.flywayCore,
	library.http4sBlazeClient,
	library.http4sBlazeServer,
	library.http4sCirce,
	library.http4sDsl,
        library.logback,
	library.postgresql,
        library.refinedCats,
        library.refinedCore,
	library.doobieScalaTest   % Test,
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
      val cats         = "1.6.1"
      val circe        = "0.11.1"
      val doobie       = "0.7.0"
      val flyway       = "5.2.4"
      val http4s       = "0.20.3"
      val logback      = "1.2.3"
      val postgresql   = "42.2.5"
      val refined      = "0.9.8"
      val scalaCheck   = "1.14.0"
      val scalaTest    = "3.0.7"
    }
    val catsCore          = "org.typelevel"          %% "cats-core"            % Version.cats
    val circeCore         = "io.circe"               %% "circe-core"           % Version.circe
    val circeGeneric      = "io.circe"               %% "circe-generic"        % Version.circe
    val circeRefined      = "io.circe"               %% "circe-refined"        % Version.circe
    val circeParser       = "io.circe"               %% "circe-parser"         % Version.circe
    val doobieCore        = "org.tpolecat"           %% "doobie-core"          % Version.doobie
    val doobieHikari      = "org.tpolecat"           %% "doobie-hikari"        % Version.doobie
    val doobiePostgres    = "org.tpolecat"           %% "doobie-postgres"      % Version.doobie
    val doobieScalaTest   = "org.tpolecat"           %% "doobie-scalatest"     % Version.doobie
    val flywayCore        = "org.flywaydb"           %  "flyway-core"          % Version.flyway
    val http4sBlazeServer = "org.http4s"             %% "http4s-blaze-server"  % Version.http4s
    val http4sBlazeClient = "org.http4s"             %% "http4s-blaze-client"  % Version.http4s
    val http4sCirce       = "org.http4s"             %% "http4s-circe"         % Version.http4s
    val http4sDsl         = "org.http4s"             %% "http4s-dsl"           % Version.http4s
    val logback           = "ch.qos.logback"         %  "logback-classic"      % Version.logback
    val postgresql        = "org.postgresql"         %  "postgresql"           % Version.postgresql
    val refinedCore       = "eu.timepit"             %% "refined"              % Version.refined
    val refinedCats       = "eu.timepit"             %% "refined-cats"         % Version.refined
    val refinedScalaCheck = "eu.timepit"             %% "refined-scalacheck"   % Version.refined
    val scalaCheck        = "org.scalacheck"         %% "scalacheck"           % Version.scalaCheck
    val scalaTest         = "org.scalatest"          %% "scalatest"            % Version.scalaTest
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
    scalaVersion := "2.12.8",
    organization := "com.wegtam",
    organizationName := "Jens Grassel",
    startYear := Some(2019),
    headerLicense := Some(HeaderLicense.Custom(licenseText)),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0"),
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