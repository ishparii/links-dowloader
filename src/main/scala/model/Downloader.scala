package model

import com.ning.http.client.{ListenableFuture, Response}

import scala.collection.mutable.ListBuffer
import scala.util.Try

/**
  * Created by Chingari on 5/5/2016.
  */
class Downloader() {

  private val downloads = ListBuffer[Download]()
  private val asyncDownloader= new AsyncDownloader

  def start (url:String, file: String): Unit =
  downloads += asyncDownloader.download(url, file)
  
  def getDownloadsInfo: List[(Int,String, Int, Int)] ={
    var index=0
    downloads.map{
      index +=1;
      download => (index, download.progressReporter.getStatus(), download.progressReporter.getBytesDownloaded(), download.progressReporter.getBytesTotal() )}.toList

  }

  def getDownload(index:Int):Download = downloads(index)

  def cancel(index: Int): Boolean = {
    val size = downloads.size
    if (index>=0 && index < size) {
      val download = downloads(index)
      val canceled = download.response.cancel(true)
      if (canceled) download.progressReporter.setStatus("Canceled")
      canceled
    } else {
      false
    }
  }

}
