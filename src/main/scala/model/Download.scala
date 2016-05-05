package model

import com.ning.http.client.{ListenableFuture, Response}

/**
  * Created by admin on 5/5/2016.
  */
class Download (
  val response:ListenableFuture[Response],
  val progressReporter: TransferListenerProgressReporter
)
