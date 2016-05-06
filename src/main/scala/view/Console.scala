package view

import java.net.{MalformedURLException, URL}

import _root_.jline.console.ConsoleReader

import scala.util.matching.Regex
import model.Downloader

import scala.io.Source
import scala.util.Try

object Console {
  val console = new ConsoleReader()
  val downloader = new Downloader
  var httpUrl= new Regex("/^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$/")
  var sampleLink= "http://download.redis.io/releases/redis-3.0.7.tar.gz"
  val cancelRegEx = """^\s*([cC])\s+([0-9]+)\s*$""".r

  def main(args: Array[String])= {
    console.setPrompt("\nDownload Link> ")
    println("Press l for list of completed downloads; c <Number> to cancel the download; q to quit the application")

    Iterator continually {
      console.readLine()
    } takeWhile {
       isValid(_: String)
     } foreach{
      (s:String) => s match {
        case cancelRegEx(c, index) => {
          val canceled = downloader.cancel(index.toInt)
          if (canceled) {
            println("Download was Canceled!")
          } else {
            println("Cancel Failed!")
          }
          //println("C")
        }
        case "l" =>{
          downloader.getDownloadsInfo.foreach{
            tuple => showDownloadInfo(tuple._1, tuple._2, tuple._3, tuple._4)
          }

        }
        case _ => {
          val parsedUrl = Try(new URL(s))
          if (parsedUrl.isSuccess) {
            val file = s.substring(s.lastIndexOf('/')+1, s.length)
            println("File name: " + file)
            if (resourseExists(parsedUrl.get)) {
              try {
                downloader.start(s, file)
              } catch {
                case e: MalformedURLException => println("Invalid URL!")
              }
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

  def showDownloadInfo(index:Int, status:String, downloaded: Int, total:Int):Unit ={
    val progress = (downloaded)/total * 100
    console.getCursorBuffer().clear()
    console.getCursorBuffer().write(index + ". size: " + total + " Bytes --- " + status + " --- ")
    console.getCursorBuffer().write(downloaded + " Bytes downloaded ")
    console.getCursorBuffer().write("--- " + progress + "%\n")
    console.setCursorPosition(console.getTerminal.getWidth)
    console.redrawLine()
  }

  def resourseExists(url:URL):Boolean = {
    try {
      val text = Source.fromURL(url).getLines()
    } catch {
      case e: java.io.IOException => {
        println("No recourse at the given url")
        return false
      }
    }
    return true
  }

}
