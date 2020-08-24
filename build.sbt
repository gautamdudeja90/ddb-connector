name := "dynamo-connector"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.4" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.4" % "provided"
libraryDependencies += "com.amazon.emr" % "emr-dynamodb-hadoop" % "4.2.0" % "provided"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.827"



