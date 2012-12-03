import scala.actors.Actor._
import java.io.BufferedReader
import java.io.InputStreamReader

object Manager {
  def main(args: Array[String]) {
    val webpage = io.Source.fromURL("http://www.cis.upenn.edu/~matuszek/cis554-2012/index.html", "ISO-8859-1").mkString
    parsePage("", webpage)
  }

  def parsePage(url: String, page: String): (Set[String], Set[String], Set[String]) = {
    val HtmlPattern = """(?i).*<\s*(a)\s+.*href\s*=\s*['"]?([^'" ]+\.html?)['" >].*""".r
    val ImgPattern = """(?i).*<\s*(img)\s+.*src\s*=\s*['"]?([^'" ]+\.(gif|jpg|png))['" >].*""".r
    val PPTPattern = """(?i).*<\s*(a)\s+.*href\s*=\s*['"]?([^'" ]+\.ppt?)['" >].*""".r
    val links = HtmlPattern.findAllIn(page).matchData.map(_.group(2)).toSet
    val imgs = ImgPattern.findAllIn(page).matchData.map(_.group(2)).toSet
    val ppts = PPTPattern.findAllIn(page).matchData.map(_.group(2)).toSet
    //print(imgs)

    def filterLink(link: String): Boolean =
      (link.startsWith("http") && !link.startsWith("http://www.cis.upenn.edu/~matuszek/"))|| 
      (link.startsWith("www") && !link.startsWith("www.cis.upenn.edu/~matuszek/")) || 
      link.startsWith("..") || link.startsWith("/") || link.contains("#") || links.contains("?")
      
    (links.filter(filterLink), imgs, ppts)
  }

}