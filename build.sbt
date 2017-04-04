name := "SeleniumAuto"

version := "1.0"

scalaVersion := "2.11.8"


libraryDependencies ++= Seq(
  "org.seleniumhq.selenium" % "selenium-java" % "3.2.0",
  "org.seleniumhq.selenium" % "selenium-chrome-driver" % "3.2.0",
  "org.seleniumhq.selenium" % "selenium-api" % "3.3.1",
  "org.seleniumhq.selenium" % "selenium-support" % "3.3.1"
)

mainClass in (Compile, run) := Some("Main")