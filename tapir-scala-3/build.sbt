// *****************************************************************************
// Projects
// *****************************************************************************

lazy val tapir =
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
        library.catsCore,
        library.circeCore,
        library.circeGeneric,
        library.circeRefined,
        library.circeParser,
        library.doobieCore,
        library.doobieHikari,
        library.doobiePostgres,
        library.doobieRefined,
        library.flywayCore,
        library.http4sCirce,
        library.http4sDsl,
        library.http4sEmberClient,
        library.http4sEmberServer,
        library.kittens,
        library.logback,
        library.monocleCore,
        library.monocleMacro,
        library.postgresql,
        library.pureConfig,
        library.refinedCats,
        library.refinedCore,
        //library.refinedPureConfig.cross(CrossVersion.for3Use2_13),
        library.tapirCirce,
        library.tapirCore,
        library.tapirHttp4s,
        library.tapirOpenApiDocs,
        library.tapirOpenApiYaml,
        library.tapirSwaggerUi,
        library.disciplineScalaT  % IntegrationTest,
        library.doobieScalaTest   % IntegrationTest,
        library.refinedScalaCheck % IntegrationTest,
        library.scalaCheck        % IntegrationTest,
        library.scalaTest         % IntegrationTest,
        library.disciplineScalaT  % Test,
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
      val cats          = "2.7.0"
      val circe         = "0.14.1"
      val disciplineST  = "2.1.5"
      val doobie        = "1.0.0-RC2"
      val flyway        = "8.4.4"
      val http4s        = "0.23.10"
      val kittens       = "3.0.0-M3"
      val logback       = "1.2.10"
      val monocle       = "3.1.0"
      val postgresql    = "42.3.2"
      val pureConfig    = "0.17.1"
      val refined       = "0.9.28"
      val scalaCheck    = "1.15.4"
      val scalaTest     = "3.2.11"
      val scalaTestPlus = "3.2.11.0"
      val tapir         = "0.19.4"
    }
    val catsCore          = "org.typelevel"               %% "cats-core"                % Version.cats
    val circeCore         = "io.circe"                    %% "circe-core"               % Version.circe
    val circeGeneric      = "io.circe"                    %% "circe-generic"            % Version.circe
    val circeRefined      = "io.circe"                    %% "circe-refined"            % Version.circe
    val circeParser       = "io.circe"                    %% "circe-parser"             % Version.circe
    val disciplineScalaT  = "org.typelevel"               %% "discipline-scalatest"     % Version.disciplineST
    val doobieCore        = "org.tpolecat"                %% "doobie-core"              % Version.doobie
    val doobieHikari      = "org.tpolecat"                %% "doobie-hikari"            % Version.doobie
    val doobiePostgres    = "org.tpolecat"                %% "doobie-postgres"          % Version.doobie
    val doobieRefined     = "org.tpolecat"                %% "doobie-refined"           % Version.doobie
    val doobieScalaTest   = "org.tpolecat"                %% "doobie-scalatest"         % Version.doobie
    val flywayCore        = "org.flywaydb"                %  "flyway-core"              % Version.flyway
    val http4sCirce       = "org.http4s"                  %% "http4s-circe"             % Version.http4s
    val http4sDsl         = "org.http4s"                  %% "http4s-dsl"               % Version.http4s
    val http4sEmberServer = "org.http4s"                  %% "http4s-ember-server"      % Version.http4s
    val http4sEmberClient = "org.http4s"                  %% "http4s-ember-client"      % Version.http4s
    val kittens           = "org.typelevel"               %% "kittens"                  % Version.kittens
    val logback           = "ch.qos.logback"              %  "logback-classic"          % Version.logback
    val monocleCore       = "dev.optics"                  %% "monocle-core"             % Version.monocle
    val monocleMacro      = "dev.optics"                  %% "monocle-macro"            % Version.monocle
    val postgresql        = "org.postgresql"              %  "postgresql"               % Version.postgresql
    val pureConfig        = "com.github.pureconfig"       %% "pureconfig-core"          % Version.pureConfig
    val refinedCore       = "eu.timepit"                  %% "refined"                  % Version.refined
    val refinedCats       = "eu.timepit"                  %% "refined-cats"             % Version.refined
    val refinedPureConfig = "eu.timepit"                  %% "refined-pureconfig"       % Version.refined
    val refinedScalaCheck = "eu.timepit"                  %% "refined-scalacheck"       % Version.refined
    val scalaCheck        = "org.scalacheck"              %% "scalacheck"               % Version.scalaCheck
    val scalaTest         = "org.scalatest"               %% "scalatest"                % Version.scalaTest
    val scalaTestPlus     = "org.scalatestplus"           %% "scalacheck-1-15"          % Version.scalaTestPlus
    val tapirCirce        = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % Version.tapir
    val tapirCore         = "com.softwaremill.sttp.tapir" %% "tapir-core"               % Version.tapir
    val tapirHttp4s       = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % Version.tapir
    val tapirOpenApiDocs  = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % Version.tapir
    val tapirOpenApiYaml  = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Version.tapir
    val tapirSwaggerUi    = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui"         % Version.tapir
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

def compilerSettings(sv: String) =
  CrossVersion.partialVersion(sv) match {
    case Some((2, 13)) =>
      Seq(
        "-deprecation",
        "-explaintypes",
        "-feature",
        "-language:higherKinds",
        "-unchecked",
        "-Xcheckinit",
        //"-Xfatal-warnings", // Disable for migration
        "-Xlint:adapted-args",
        "-Xlint:constant",
        "-Xlint:delayedinit-select",
        "-Xlint:doc-detached",
        "-Xlint:inaccessible",
        "-Xlint:infer-any",
        "-Xlint:missing-interpolator",
        //"-Xlint:nullary-override", // Flag was removed
        "-Xlint:nullary-unit",
        "-Xlint:option-implicit",
        "-Xlint:package-object-classes",
        "-Xlint:poly-implicit-overload",
        "-Xlint:private-shadow",
        "-Xlint:stars-align",
        "-Xlint:type-parameter-shadow",
        "-Ymacro-annotations",
        "-Ywarn-dead-code",
        "-Ywarn-extra-implicit",
        "-Ywarn-numeric-widen",
        //"-Ywarn-unused:implicits",
        "-Ywarn-unused:imports",
        //"-Ywarn-unused:locals",
        //"-Ywarn-unused:params",
        "-Ywarn-unused:patvars",
        "-Ywarn-unused:privates",
        "-Ywarn-value-discard",
        "-Ycache-plugin-class-loader:last-modified",
        "-Ycache-macro-class-loader:last-modified",
      )
    case Some((3, _)) =>
      Seq(
        "-deprecation",
        "-explain-types",
        "-feature",
        "-language:higherKinds",
        "-unchecked",
        //"-Xfatal-warnings", // Disable for migration
        "-Ycheck-init",
        "-Ykind-projector",
        "-source:3.0-migration", // Gives warnings instead of errors on most syntax changes.
        "-rewrite",              // Resolve warnings via the compiler of possible.
      )
    case _ => Seq()
  }

lazy val commonSettings =
  Seq(
    scalaVersion := "2.13.8",
    crossScalaVersions := Seq(scalaVersion.value, "3.1.1"),
    organization := "com.wegtam",
    organizationName := "Jens Grassel",
    startYear := Some(2019),
    headerLicense := Some(HeaderLicense.Custom(licenseText)),
    libraryDependencies ++= (
      if (scalaVersion.value.startsWith("2")) {
        Seq(
          compilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
          compilerPlugin("org.typelevel" % "kind-projector"      % "0.13.2" cross CrossVersion.full)
        )
      } else {
        Seq()
      }
    ),
    scalacOptions ++= compilerSettings(scalaVersion.value),
    Compile / console / scalacOptions --= Seq("-Xfatal-warnings", "-Ywarn-unused-import"),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Test / console / scalacOptions --= Seq("-Xfatal-warnings", "-Ywarn-unused-import"),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value),
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := false,
  )
