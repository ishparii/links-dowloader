package model

import com.ning.http.client.{ListenableFuture, Response}

import scala.collection.mutable.ListBuffer

/**
  * Created by Chingari on 5/5/2016.
  */
class Downloader() {

  private val downloads = ListBuffer[ListenableFuture[Response]]()
  private val asyncDownloader= new AsyncDownloader

  def start (url:String, file: String): Unit =
  downloads += asyncDownloader.download(url, file)

 // def cancel(index:Int): Boolean

  def getDownloads: List[(Int,Int, Int)] ={
    var index=0
    downloads.map{
      index +=1;
      download => (index, download.get.getStatusCode, download.get.getStatusCode )}.toList

  }

  def getDownload(index:Int):ListenableFuture[Response] = downloads(index)

}
