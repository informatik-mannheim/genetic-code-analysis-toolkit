package net.gumbix.util

import java.util.Properties

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
object Version {

  def label = "Mannheim Genetic Code (" + version + ")"

  def version = {
    val path = "/dev.properties"
    val stream = getClass().getResourceAsStream(path)
    if (stream == null) "UNKNOWN"
    val props = new Properties()
    try {
      props.load(stream)
      stream.close()
      props.get("project.version")
    } catch {
      case _ : Throwable => "UNKNOWN"
    }
  }

  override def toString = label
}
