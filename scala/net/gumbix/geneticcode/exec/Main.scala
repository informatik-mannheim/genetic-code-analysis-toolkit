package net.gumbix.geneticcode.exec

import net.gumbix.geneticcode.dich.scan.{ConcClassPower2Scan, ScanNumberOfClassesScan, ClassPower2Scan, ErrorScan}
import net.gumbix.util.{Version, ArgsParser}
import java.util.Properties
import net.gumbix.geneticcode.dich.BinaryAlgorithm

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
object Main extends ArgsParser {

  def main(args: Array[String]) {

    def printHelp {
      println(
        """
          |The following commands <cmd> are possible:
          |<cmd> ::== ErrorScan <BDAList> <E_d> <k_d> <c_d> <Table> <Prop> |
          |           Power2Scan <BDAList> <k_d> |
          |           ConcPower2Scan <BDAList> <k_d> <actors> |
          |           NumberOfClassesScan <BDAList> <k_d>.
          |
          |<BDAList> ::= R|A|P|().  // ():empty, R:Rumer etc.
          |<k_d> ::= <Int>.         // max. BDA set size |D|
          |<c_d> ::= <Int>.         // Number of classes  |M|
          |<Table> ::= <Int>.       // IUPAC genetic code table.
          |                         // 1:Standard, 2: VERTEBRATE_MITOCHONDRIAL
          |                         // 10:EUPLOTID_NUCLEAR
          |<E_d> ::= <Double>.      // Max. compatibility error
          |<Prop> ::= 64|AST|AVM.   // 64:64 Codons, AST=amino acid std. genetic code
          |                         // AVM: amino acid vertebrate mitoch. genetic code
          |<actor> ::= <Int>        // Number of actors (threads)
        """.stripMargin)
    }

    println("Welcome to " + Version + ".")
    if (args.size == 0) printHelp
    else args(0) match {
      case "-help" => printHelp
      case "ErrorScan" =>
        new ErrorScan(
          toBDAList(args, 1),
          toDouble(args, 2), toInt(args, 3), toInt(args, 4), // E, k_d, |M|
          toInt(args, 5), toProp(args, 6) // code table, aaProps
        ).run()
      case "BAErrorScan" =>
        new ErrorScan(
          toBDAList(args, 1),
          toDouble(args, 2), toInt(args, 3), toInt(args, 4), // E, k_d, |M|
          toInt(args, 5), toProp(args, 6), // code table, aaProps
          BinaryAlgorithm.allBAs
        ).run()
      case "Power2Scan" =>
        new ClassPower2Scan(toBDAList(args, 1), toInt(args, 2)).run()
      case "ConcPower2Scan" =>
        new ConcClassPower2Scan(toBDAList(args, 1), toInt(args, 2), toInt(args, 3)).run()
      case "BAPower2Scan" =>
        new ClassPower2Scan(toBDAList(args, 1), toInt(args, 2),
          BinaryAlgorithm.allBAs).run()
      case "NumberOfClassesScan" =>
        new ScanNumberOfClassesScan(toBDAList(args, 1), toInt(args, 2)).run()
      case _ => {
        "Unknown command. Use -help."
        printHelp
      }
    }
  }
}
