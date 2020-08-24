# DynamoDb-Connector

DynamoDb connector contains six spark job for ingesting json data into dynamodb.
Arguments Required :
  - json file location
  - dynamodb table name
## Compile and package
### Prerequisites
Please make sure you have the following software installed on your local machine:
* For Scala: Scala 2.11, SBT
### Git Clone
```
$ git clone https://github.com/Spokeo/dq_scripts.git
```
### DynamoConnector
```
$ cd ./dynamo-connector
$ sbt assembly
```
### Submit your fat jar to Spark
After running the command mentioned above, you are able to see a fat jar in `./target` folder. Please take it and use `./bin/spark-submit` to submit this jar.
To run the jar in this way, you need to:
* Either change Spark Master Address in DynamoConnector.scala. Currently, they are hard coded for EMR which means to run it locally `local[4]` should be set in main class.
* Change the dependency packaging scope of Apache Spark from "compile" to "provided". This is a common packaging strategy in Maven and SBT which means do not package Spark into your fat jar. Otherwise, this may lead to a huge jar and version conflicts!
* Make sure the dependency versions in build.sbt are consistent with your Spark version.
* To submit spark job on EMR, there is hard dependecy of emr-ddb-hadoop.jar available on EMR under its `/usr/share/aws/emr/ddb/lib` location and jar file created in target folder needs to be copied to EMR instance and run spark-submit. See example below.

```
nohup spark-submit --class "com.spokeo.spark_emr_ddb.writetoddb.DynamoConnector" --executor-memory 60g --executor-cores 32 --jars /usr/share/aws/emr/ddb/lib/emr-ddb-hadoop.jar dynamo-connector-assembly-0.1.jar s3://backend-platform/person_seo/ person_name_preview_dev &
```

## Run job locally
We highly suggest you use IDEs to run code on your local machine. For Scala, we recommend IntelliJ IDEA with Scala plug-in, **you don't have to prepare anything** (even don't need to download and set up Spark!). As long as you have Scala and Java, everything works properly!
### Scala
Import the Scala dynamo-connector project as SBT project. Then run the Main file in this project.
