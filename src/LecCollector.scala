import scala.actors.Actor

class LecCollector extends Actor {
  var lecLinks: Set[String] = Set()

  def processLecLink(link: String):String = {
    val linkSeg = link.split("/")
    val base = linkSeg.init
    val fileName = linkSeg.last
    val newFileName = if(fileName.startsWith("0")|| fileName.startsWith("1")) {
      fileName.split("-").last
    }else {
      fileName
    }
    base.mkString("","/","/")+newFileName
  }
  
  def act {
    loop {
      react {
        case lecs: Set[String] => lecLinks = lecLinks ++ lecs.map(_.toLowerCase).map(processLecLink)
        case "DONE" => {
          //println(lecLinks.mkString("Lecture Links: ", " | ", ""))
          println(lecLinks.size)
          exit
        }
      }
    }
  }
}