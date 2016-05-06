package model

import java.net.{MalformedURLException, URL}

import com.ning.http.client.{ListenableFuture, Response}

import scala.collection.mutable.ListBuffer
import scala.util.Try


class Downloader() {

  private val downloads = ListBuffer[Download]()
  private val asyncDownloader= new AsyncDownloader

  def start (url:String, file: String): Unit = {
    if (Try(new URL(url)).isFailure || (new URL(url).getHost.isEmpty)) {
      throw new MalformedURLException
    }
    else {
      downloads += asyncDownloader.download(url, file)
    }
  }
  // index, status, bytes downloaded, total bytes
  def getDownloadsInfo: List[(Int,String, Int, Int)] ={
    var index=0
    downloads.map{
      download => {
        index +=1
        (index, download.progressReporter.getStatus(), download.progressReporter.getBytesDownloaded(), download.progressReporter.getBytesTotal() )
      }}.toList
  }

  def getDownload(index:Int):Download = downloads(index)
  // cancel the download
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
