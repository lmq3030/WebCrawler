import scala.actors.Actor

class PageLinksCollector(val imgCollector: Actor, val lecCollector: Actor, val daemon: Actor) extends Actor {
  var pageLinks: Set[String] = Set()
  var crawlerCounter = 0;

  def endCrawler {
    lecCollector ! ("DONE", daemon)
    imgCollector ! ("DONE", daemon)
  }

  def act {
    loop {
      react {
        case links: Set[String] => {
          //println(links.mkString(""," | ", ""))
          val newLinks = links.diff(pageLinks)
          pageLinks = pageLinks ++ newLinks
          newLinks.foreach(link => {
            crawlerCounter += 1
            val processor = new PageProcessor(this, imgCollector, lecCollector)
            processor.start
            processor ! link
          })
        }

        case "DONE" => {
          crawlerCounter -= 1
          if (crawlerCounter == 0) {
            daemon ! (1, pageLinks.map(x => x.split("/").last + " -> " + x).toList.sorted.mkString("The HTML Pages:\n ", "\n", "\n\n\n\n\n\n"))
            endCrawler
            println("page size: " + pageLinks.size)
            exit
          }
        }
      }
    }
  }

}