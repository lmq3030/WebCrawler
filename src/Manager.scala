import scala.actors.Actor._
import java.io.BufferedReader
import java.io.InputStreamReader

object Manager {
  def main(args: Array[String]) {
    
    
    val imgCollector  = new ImgCollector
    val lecCollector = new LecCollector
    val pageLinksCollector = new PageLinksCollector(imgCollector, lecCollector)
    pageLinksCollector.start
    imgCollector.start
    lecCollector.start
    pageLinksCollector ! Set("http://www.cis.upenn.edu/~matuszek")
    
  }


}