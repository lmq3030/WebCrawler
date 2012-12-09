import scala.actors.Actor
import scala.collection.mutable.Map

class LecCollector extends Actor {
  var lecLinks: Map[String, (String, String)] = Map()

  def processLecLink(link: String): (String, (String, String)) = {
    val linkSeg = link.split("/")
    val fileName = linkSeg.last
    val pureFileName = if (fileName.charAt(0).isDigit) {
      fileName.split("-").last
    } else {
      fileName
    }
    val yearPattern = """-([0-9]+)/""".r

    val year = yearPattern.findAllIn(link).matchData.map(_.group(1)).toList.head
    (pureFileName, (year, link))

  }
  
  def addLecLinks(lecSets:Set[(String, (String, String))]){
    lecSets.foreach(lecture => {
      val (name, (year, link)) = lecture
      (lecLinks.get(name)) match {
        case None => lecLinks(name) = (year, link)
        case Some((oldYear, _)) => if(oldYear < year) lecLinks(name) = (year, link)
      }
    })
  }

  def act {
    loop {
      react {
        case lecs: Set[String] =>addLecLinks(lecs.map(processLecLink))
        case ("DONE", daemon:Actor) => {
          //println(lecLinks.mkString("\n"))
          println(lecLinks.size)
          daemon ! (2,lecLinks.toList.sorted.map(c => c._1 +" -> "+ c._2._2).mkString("The Lecture Slides:\n", "\n", "\n\n\n\n\n\n"))
          exit
        }
      }
    }
  }
}