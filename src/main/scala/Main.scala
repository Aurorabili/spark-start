import org.apache.spark.sql.{SparkSession, Row}
import org.apache.spark.sql.functions._
import org.apache.spark.rdd.RDD._

object EmailFrequencyStatistics {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
      .appName("Email and Date Statistics")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._
    val sc = spark.sparkContext

    val rdd = sc.textFile("data/email_log_with_date.txt")

    val emailCounts = rdd.map(line => {
      val fields = line.split("\\|")
      val email = fields(0)
      (email, 1)
    })
    val emailFrequency = emailCounts.reduceByKey(_ + _)

    // 对map进行排序
    val sortedEmailFrequency = emailFrequency.sortBy(_._2, false)

    sortedEmailFrequency.take(10).foreach(println)

    spark.stop()
  }
}

object DateFrequencyStatistics {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
      .appName("Email and Date Statistics")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._
    val sc = spark.sparkContext

    val rdd = sc.textFile("data/email_log_with_date.txt")

    val dateCounts = rdd.map(line => {
      val fields = line.split("\\|")
      val date = fields(1)
      (date, 1)
    })
    val dateFrequency = dateCounts.reduceByKey(_ + _)

    // 对map进行排序
    val sortedDateFrequency = dateFrequency.sortBy(_._2, false)

    sortedDateFrequency.take(10).foreach(println)

    spark.stop()
  }
}

object EmailDateCounterStatistics {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Email and Date Statistics")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._
    val sc = spark.sparkContext

    val rdd = sc.textFile("data/email_log_with_date.txt")

    val emailDateCounts = rdd.map(line => {
      val fields = line.split("\\|")
      val email = fields(0)
      val date = fields(1)
      ((date, email), 1)
    })

    val emailDateFrequency = emailDateCounts.reduceByKey(_ + _)

    val dateEmailGrouped = emailDateFrequency
      .map { case ((date, email), count) =>
        (date, (email, count))
      }
      .groupByKey()

    val mostFrequentEmails = dateEmailGrouped.mapValues { emails =>
      emails.maxBy(_._2)
    }

    mostFrequentEmails.collect().foreach(println)
  }
}
