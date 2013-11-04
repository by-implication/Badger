// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.1.0")

// sasseh
addSbtPlugin("net.litola" % "play-sass" % "0.1.3" from "https://raw.github.com/tthraine/play-sass/master/play-sass-0.1.3.jar")
