import scala.actors.Actor
import java.io.FileNotFoundException

class PageProcessor(val pageLinksCollector: Actor, val imgCollector: Actor, val lecCollector: Actor) extends Actor {
  def processPage(url: String): Option[(Set[String], Set[String], Set[String])] = {

    def parsePage(page: String): (Set[String], Set[String], Set[String]) = {
      def filterLink(link: String): Boolean =
        !((link.startsWith("http") && !link.startsWith("http://www.cis.upenn.edu/~matuszek/")) ||
          (link.startsWith("www") && !link.startsWith("www.cis.upenn.edu/~matuszek/")) ||
          link.startsWith("..") || link.startsWith("/") || link.contains("#") || link.contains("?"))

      val HtmlPattern = """(?i).*<\s*(a)\s+.*href\s*=\s*['"]?([^'" ]+\.html?)['" >].*""".r
      val ImgPattern = """(?i).*<\s*(img)\s+.*src\s*=\s*['"]?([^'" ]+\.(gif|jpg|png))['" >].*""".r
      val PPTPattern = """(?i).*<\s*(a)\s+.*href\s*=\s*['"]?([^'" ]+\.ppt?)['" >].*""".r
      val links = HtmlPattern.findAllIn(page).matchData.map(_.group(2)).toSet
      val imgs = ImgPattern.findAllIn(page).matchData.map(_.group(2)).toSet
      val ppts = PPTPattern.findAllIn(page).matchData.map(_.group(2)).toSet

      (links.filter(filterLink), imgs.filter(filterLink), ppts.filter(filterLink))

    }

    def fetchPage(url: String) = io.Source.fromURL(url, "ISO-8859-1").mkString

    try {
      Some(parsePage(fetchPage(url)))
    } catch {
      case ex: FileNotFoundException => None
    }
  }

  def reconstructLinks(pageLink: String, links: Set[String]): Set[String] = {
    val baseLink = pageLink.split('/').init.mkString("", "/", "")
    
    def reconstructSingleLink(link: String): String = {
      if (link.startsWith("http")) link
      else if (link.startsWith("www")) link
      else {
        val pureLink = if (link.startsWith("./")) link.drop(2) else link
        if (pageLink.contains(".htm")) baseLink + "/" + pureLink
        else pageLink.split('/').mkString("", "/", "") + "/" + pureLink
      }
    }

    links.map(reconstructSingleLink)

  }

  def act = {
    react {
      case link: String => {
        processPage(link) match {
          case Some((links, imgs, lecs)) => {
            pageLinksCollector ! reconstructLinks(link, links)
            pageLinksCollector ! "DONE"
            imgCollector ! reconstructLinks(link, imgs)
            lecCollector ! reconstructLinks(link, lecs)
          }
          case None => pageLinksCollector ! "DONE"
        }
      }
    }
  }
}
