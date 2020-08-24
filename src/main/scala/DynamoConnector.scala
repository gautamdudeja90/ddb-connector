package com.spokeo.spark_emr_ddb.writetoddb

import org.apache.hadoop.dynamodb.DynamoDBItemWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapred.JobConf
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession


/** EMR-DynamoLoader
 *
 *  It reads the json file with each line as an independent json onbject
 *  created an RDD, converts into dynamo AttributeValue objects(serialized)
 *  inserts data into dynamoDb Table
 */
object DynamoConnector{

  private val logger = Logger.getLogger(this.getClass)

  def main(args: Array[String]) {

    if (args.length != 2)
      throw new IllegalArgumentException(
        "Parameters : "+
          "path: location where json files are stored "+
          "dynamoTable : dynamo table name"
      )

    val path = args(0)
    val output_table = args(1)

    logger.setLevel(Level.INFO)
    lazy val spark = SparkSession
      .builder
      .appName("Dynamo-Connector")
      .master("yarn-client")
      .enableHiveSupport()
      .getOrCreate()

    try {
      runJob(spark = spark,
        path,
        output_table
      )
      spark.stop()
    } catch {
      case ex: Exception =>
        logger.error(ex.getMessage)
        logger.error(ex.getStackTrace.toString)
    }

  }

  def runJob(spark :SparkSession,path :String, output_table :String) = {

    logger.info("Execution started")
    val ddbConf = new JobConf(spark.sparkContext.hadoopConfiguration)
    ddbConf.set("dynamodb.output.tableName", output_table)
    ddbConf.set("dynamodb.throughput.write.percent", "1.0")
    ddbConf.set("mapred.input.format.class", "org.apache.hadoop.dynamodb.read.DynamoDBInputFormat")
    ddbConf.set("mapred.output.format.class", "org.apache.hadoop.dynamodb.write.DynamoDBOutputFormat")

    //spark context
    val sc = spark.sparkContext

    //read json file as text file, expects every line to be a json object
    val initial_rdd =sc.textFile(path).map(a=>a.toString()).distinct

    val rdd = initial_rdd.map(a=>{
      //Serialization of imports for rdd
      import com.amazonaws.services.dynamodbv2.document.{Item, ItemUtils}

      val item = new Item()
      item.withJSON("document",a.toString())
      val ddb_map = ItemUtils.fromSimpleMap(item.asMap()).get("document").getM
      var ddb_item = new DynamoDBItemWritable()
      ddb_item.setItem(ddb_map)
      (new Text(""), ddb_item)
    })
    rdd.saveAsHadoopDataset(ddbConf)
  }
}
