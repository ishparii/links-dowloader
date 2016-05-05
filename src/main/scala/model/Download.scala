package model
import com.ning.http.client.{ListenableFuture, Response}
import com.ning.http.client.listener._

class Download (
  val response:ListenableFuture[Response], val progress:TransferListener

               )
