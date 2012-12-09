import scala.actors.Actor._
import swing.FileChooser
import java.io._

object Manager {
  def main(args: Array[String]) {
    
    val chooser = new FileChooser(new File("."))
    chooser.title = "select directory to save result"
    chooser.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly
    val theFileResult = chooser.showSaveDialog(null)
    if (theFileResult != FileChooser.Result.Approve) 
      exit
        
    val daemon = new Daemon(chooser.selectedFile.toString()+"\\result.txt").start
    print(chooser.selectedFile.toString()+"\\result.txt")
    val imgCollector  = new ImgCollector().start
    val lecCollector = new LecCollector().start
    val pageLinksCollector = new PageLinksCollector(imgCollector, lecCollector, daemon).start
    pageLinksCollector ! Set("http://www.cis.upenn.edu/~matuszek")
    
  }


}
