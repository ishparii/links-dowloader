import java.net.MalformedURLException
import model.Downloader
import org.scalatest.FunSuite


class TestDownload extends FunSuite{

  def testfixtureURL(): String = {
    "http://download.redis.io/releases/redis-3.0.7.tar.gz"
  }

  def testfixturePath(): String ={
    scala.util.Properties.userDir + "file.gz"
     }

  def testfixture(): Downloader={
    new Downloader
  }

  test("Invalid URL is rejected"){
    val dm= testfixture()
    intercept[MalformedURLException]{
      dm.start(" ", testfixturePath())
    }
  }

  test("URL with empty path don't download"){
    val dm = testfixture()
    intercept[MalformedURLException]{
      dm.start("http://", testfixturePath())
    }
  }





}
