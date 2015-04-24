package net.gumbix.geneticcode.dich.db

import net.gumbix.geneticcode.dich.Scan
import java.io._

/**
 * Serialize an object graph to and from a file.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
trait DB[A] {

  /**
   * Saves an object graph to a *.ser file.
   * @param filename Filename without suffix.
   */
  def store(filename: String) {
    val file = filename + ".ser"
    new File(file).delete()
    // Save the object to file
    val out = new ObjectOutputStream(new FileOutputStream(file))
    out.writeObject(this)
    out.close()
  }

  /**
   * Load an object graph from a *.ser file.
   * @param filename Filename without suffix.
   */
  def load(filename: String): A = {
    val file = filename + ".ser"
    val in = new ObjectInputStream(new FileInputStream(file))
    val p = in.readObject().asInstanceOf[A]
    in.close()
    p
  }
}