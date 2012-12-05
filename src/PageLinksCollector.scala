import scala.actors.Actor

class PageLinksCollector(val imgCollector: Actor, val lecCollector: Actor) extends Actor {
	var pageLinks: Set[String] = Set()
	var crawlerCounter = 0;
	
	def endCrawler {
	  imgCollector ! "DONE"
	  lecCollector ! "DONE"
	}
	
	
	def act {
	  loop {
	    react{
	      case links:Set[String] => {
	        //println(links.mkString(""," | ", ""))
	        val newLinks = links.map(_.toLowerCase).diff(pageLinks)
	        pageLinks = pageLinks ++ newLinks
	        newLinks.foreach(link => {
	          crawlerCounter+=1
	          val processor = new PageProcessor(this, imgCollector, lecCollector) 
	          processor.start 
	          processor ! link
	        })
	      }
	      
	      case "DONE" =>  {
	        crawlerCounter-=1
	        if(crawlerCounter == 0){
	          endCrawler
	          println(pageLinks.size)
	          exit
	        }
	      }
	    }
	  }
	}
  
}