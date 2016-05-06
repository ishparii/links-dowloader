package model

import com.ning.http.client._
import com.ning.http.client.listener._
import java.io.{File, FileOutputStream}
import java.util.concurrent.atomic.AtomicInteger

trait TransferListenerProgressReporter extends TransferListener {
  def getBytesDownloaded():Int
  def getBytesTotal():Int
  def getStatus():String
  def setStatus(status:String):Unit
}

  class AsyncDownloader {

    def fileSaver(name: String) = new TransferListener {
      val file = new File(name)
      var stream: Option[FileOutputStream] = None

      def onRequestHeadersSent(headers: FluentCaseInsensitiveStringsMap): Unit = ()

      def onResponseHeadersReceived(headers: FluentCaseInsensitiveStringsMap): Unit =
        stream = Some(new FileOutputStream(file))

      def onBytesReceived(buffer: Array[Byte]): Unit =
        stream.get.write(buffer)

      def onBytesSent(amount: Long, current: Long, total: Long): Unit = ()

      def onRequestResponseCompleted(): Unit = stream.get.close()

      def onThrowable(t: Throwable): Unit = stream.get.close()
    }

    val progressReporter = new TransferListenerProgressReporter {

      var totalBytes = 0

      val downloadedBytes = new AtomicInteger(0)

      var status = "Not Started"

      def onRequestHeadersSent(headers: FluentCaseInsensitiveStringsMap): Unit = ()

      def onResponseHeadersReceived(headers: FluentCaseInsensitiveStringsMap): Unit = {
        val contentLength = Some(headers.getFirstValue("Content-Length"))
        if (contentLength.isDefined) {
          totalBytes = contentLength.get.toInt
          status = "In Progress"
        }
        //print("+")
      }

      def onBytesReceived(buffer: Array[Byte]): Unit = {
        downloadedBytes.addAndGet(buffer.length)
        //print("*")
      }

      def onBytesSent(amount: Long, current: Long, total: Long): Unit = ()

      def onRequestResponseCompleted(): Unit = {
        println("\nDownload is Completed!")
        status = "Completed"
        //println("!")
      }

      def onThrowable(t: Throwable): Unit = {
        println("Download Failed!")
      }

      override def getBytesDownloaded(): Int = downloadedBytes.get()

      override def getBytesTotal(): Int = totalBytes

      override def getStatus(): String = status

      override def setStatus(s:String): Unit = {
        status = s
      }
    }

    val client = new AsyncHttpClient

    def download(url: String, local: String): Download = {
      val t = new TransferCompletionHandler
      t.addTransferListener(fileSaver(local))
      t.addTransferListener(progressReporter)
      val response = client.prepareGet(url).execute(t)
      new Download(response, progressReporter)
    }

  }