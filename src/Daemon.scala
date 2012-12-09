import scala.actors.Actor
import java.io._
import scala.collection.mutable.ListBuffer

class Daemon(val filePath: String) extends Actor {
  var dataList: ListBuffer[(Int, String)] = ListBuffer()

  def act {
    loop {
      react {
        case (i: Int, data: String) => {
          dataList += ((i, data))
          if (dataList.size == 3) {
            val fw = new FileWriter(filePath, false)
            val data = dataList.sortWith(_._1 < _._1)
            try {
            	data.foreach( d =>fw.write(d._2))
            } finally {
              fw.close()
              exit
            }
          }
        }
      }
    }
  }
}