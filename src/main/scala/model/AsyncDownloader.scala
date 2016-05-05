package model

import com.ning.http.client._
import com.ning.http.client.listener._
import java.io.{ File, FileOutputStream }

trait TransferListenerProgressReporter extends TransferListener {
  def getBytesDownloaded():Int
  def getBytesTotal():Int
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



      def onRequestHeadersSent(headers: FluentCaseInsensitiveStringsMap): Unit = ()

      def onResponseHeadersReceived(headers: FluentCaseInsensitiveStringsMap): Unit = print("+")

      def onBytesReceived(buffer: Array[Byte]): Unit = print("*")

      def onBytesSent(amount: Long, current: Long, total: Long): Unit = ()

      def onRequestResponseCompleted(): Unit = println("!")

      def onThrowable(t: Throwable): Unit = {}

      override def getBytesDownloaded(): Int = ???

      override def getBytesTotal(): Int = ???
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