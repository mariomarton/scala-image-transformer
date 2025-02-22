name := "ASCIIArt"

version := "1.0.0"

scalaVersion := "3.3.1" //originally 3.4.2

Compile / mainClass := Some("view.AsciiArtUI")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19"
libraryDependencies += "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0"
