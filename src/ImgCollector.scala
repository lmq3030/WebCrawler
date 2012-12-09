import scala.actors.Actor

class ImgCollector extends Actor {
  var imgLinks: Map[String,String] = Map()
  
  def act {
    loop {
      react {
        case imgs: Set[String] => imgLinks = imgLinks ++ imgs.map(x=> (x.split("/").last, x)).toMap;
        case ("DONE", daemon:Actor) => {
          //println(imgLinks.mkString("Image Links: "," | ", ""))
          //imgLinks.toList.sorted.foreach(println)
          println("image size: "+imgLinks.size)
          daemon ! (3,imgLinks.toList.sorted.map(c => c._1 +" -> "+ c._2).mkString("The Images:\n","\n", "\n\n\n"))
          exit
        }
      }
    }
  }
}