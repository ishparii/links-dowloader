package view

import java.net.{MalformedURLException, URL}

import _root_.jline.console.ConsoleReader

import scala.util.matching.Regex
import model.Downloader

import scala.util.Try

object Console {
  val console = new ConsoleReader()
  val downloader = new Downloader
  var httpUrl= new Regex("/^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$/")
  var sampleLink= "http://download.redis.io/releases/redis-3.0.7.tar.gz"

  def main(args: Array[String])= {
    console.setPrompt("\nDownload Link> ")
    println("Press l for list of completed downloads; c <Number> to cancel the download; q to quit the application")

    Iterator continually {
      console.readLine()
    } takeWhile {
       isValid(_: String)
     } foreach{
      (s:String) => s match {
        case "c" => println("C")
        case "l" =>{
          downloader.getDownloads.foreach{
            tuple => getProgress(tuple._2, tuple._3)
          }

        }
        case "d" => downloader.start(sampleLink, "redis-3.0.7.tar.gz")
        case _ => {
          //print("input: " + s)
          val parsedUrl = Try(new URL(s))
          if (parsedUrl.isSuccess) {
            val file = s.substring(s.lastIndexOf('/'), s.length)
            
            try {
              downloader.start(s, file)
            } catch {
              case e: MalformedURLException => println("Invalid URL!")
            }
          } else {
            println("The command is not recognized or provided URL is invalid!")
          }
        }
      }
     }
    println()
  }

  def isValid(s:String):Boolean = !(s.equals("q"))

  def getProgress(downloadCompleted: Int, totalDownloads:Int):Unit ={
    val progress = setProgress(downloadCompleted, totalDownloads)
    console.getCursorBuffer().clear()
    console.getCursorBuffer().write( "\nStatus of file: \n")
    console.getCursorBuffer().write(progress)
    console.setCursorPosition(console.getTerminal.getWidth)
    console.redrawLine()
  }

  def setProgress(downloadCompleted:Int, totalDownloads:Int):String ={
    val progress = (downloadCompleted * 10)/totalDownloads
    val buffer = new StringBuffer()
    val completeness = (downloadCompleted.toFloat/totalDownloads) * 100
    buffer.append(completeness.toInt.toString).append("%")
    buffer.append(" " + totalDownloads).append(EOL)
    buffer.toString()
  }

  val EOL = scala.util.Properties.lineSeparator

}
