package model

import com.ning.http.client.{ListenableFuture, Response}


class Download (
  val response:ListenableFuture[Response],
  val progressReporter: TransferListenerProgressReporter
)
