package it.bigdataitaly.spark.twitter

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.StreamingContext._
import twitter4j.Status

object TweetSaver {
  
  def getTweet(tweet: Status) {
    Tweet(tweet.getText())
  }
  
  /** Our main function where the action happens */
  def main(args: Array[String]) {

    // Configure Twitter credentials using twitter.txt
    Utils.setupTwitter()
    
    val ssc = new StreamingContext("local[*]", "TweetSaver", Seconds(1))
    
    // Get rid of log spam (should be called after the context is set up)
    Utils.setupLogging()

    // Create a DStream from Twitter using our streaming context
    val tweets = TwitterUtils.createStream(ssc, None)
    
    // Now extract the text of each status update into DStreams using map()
    val normTweets = tweets.map(status => getTweet(status))
    
    normTweets.print
/*    // Blow out each word into a new DStream
    val tweetwords = normTweets.flatMap(tweetText => tweetText.split(" "))
    
    // Now eliminate anything that's not a hashtag
    val hashtags = tweetwords.filter(word => word.startsWith("#"))
    
    // Map each hashtag to a key/value pair of (hashtag, 1) so we can count them up by adding up the values
    val hashtagKeyValues = hashtags.map(hashtag => (hashtag, 1))
    
    // Now count them up over a 5 minute window sliding every one second
    val hashtagCounts = hashtagKeyValues.reduceByKeyAndWindow( (x,y) => x + y, (x,y) => x - y, Seconds(300), Seconds(1))
    //  You will often see this written in the following shorthand:
    //val hashtagCounts = hashtagKeyValues.reduceByKeyAndWindow( _ + _, _ -_, Seconds(300), Seconds(1))
    
    // Sort the results by the count values
    val sortedResults = hashtagCounts.transform(rdd => rdd.sortBy(x => x._2, false))
    
    // Print the top 10
    sortedResults.print
*/    
    // Set a checkpoint directory, and kick it all off
    // I could watch this all day!
    ssc.checkpoint("C:/checkpoint/")
    ssc.start()
    ssc.awaitTermination()
  }  
}
