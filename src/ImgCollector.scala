import scala.actors.Actor

class ImgCollector extends Actor {
  var imgLinks: Set[String] = Set()

  def act {
    loop {
      react {
        case imgs: Set[String] => imgLinks = imgLinks ++ imgs.map(_.toLowerCase).filter(!_.contains(".."));
        case "DONE" => {
          //println(imgLinks.mkString("Image Links: "," | ", ""))
          imgLinks.toList.sorted.foreach(println)
          //println(imgLinks.size)
          exit
        }
      }
    }
  }
}