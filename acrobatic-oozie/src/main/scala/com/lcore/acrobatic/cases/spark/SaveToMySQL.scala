package com.lcore.acrobatic.cases.spark

import java.util.Properties

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Random


object SaveToMySQL {
  val AGES = Array(21, 22, 23, 24, 25, 26, 27, 28, 29, 30)
  val JOBS = Array("C++Pro", "JavaPro", "BigDataPro", "ScalaPro", "PythonPro")
  val SALARIES = Array(15000.00, 17800.00, 8000.00, 7500.00, 30000.00, 45000.00, 20000.00)

  def main(args: Array[String]): Unit = {
    import scala.collection.mutable.ListBuffer
    val workers = new ListBuffer[Worker]()
    for (i <- 1 to 1000) {
      val worker = Worker("w000" + i, "wName" + i, getAge(), getJob(), getSalary())
      workers.append(worker)
    }


    val spark = SparkSession.builder()
      .appName("SaveToMySQL")
      //.master("local[*]")
      .getOrCreate()
    val wRDD: RDD[Worker] = spark.sparkContext.parallelize(workers)
    val properties = new Properties()
    properties.put("user", "root")
    properties.put("password", "root")
    properties.setProperty("driver", "com.mysql.jdbc.Driver")

    import spark.implicits._
    wRDD.toDF().write.mode(SaveMode.Overwrite).jdbc("jdbc:mysql://192.168.21.110:3306/yss", "workers", properties)

    spark.stop()
  }


  private def getJob() = {
    val random = Random
    JOBS(random.nextInt(JOBS.length))
  }

  private def getSalary() = {
    val random = Random
    SALARIES(random.nextInt(SALARIES.length))
  }

  private def getAge() = {
    val random = Random
    AGES(random.nextInt(AGES.length))
  }
}

case class Worker(workerId: String, workerName: String, workerAge: Int, workerJob: String, workerSalary: Double)
