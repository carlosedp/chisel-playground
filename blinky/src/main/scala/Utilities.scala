package scalautils

import scala.collection.mutable.ListBuffer

object ParseArguments {

  /**
   * Parses command line arguments
   *
   * This function parses command line arguments from `args`, as
   * an `Array[String]`.
   * The parameters to be extracted can be prefixed with `-` as default or
   * the param prefix in `paramFormat` (for example `--`) and passed in
   * `parameters` as `List[String]`.
   *
   * The function returns a `Map[String, String]` with extracted parameters and a `List[String]` with remaining arguments that were not extracted. The parameter name is returned with it's leading `paramFormat` (`"-"` for example) removed.
   *
   * If a parameter does not contain a value and is followed with another parameter (with same prefix), it is ignored.
   *
   * Usage:
   *
   * {{{
   * // Received args
   * val args = Array( "-param1", "-wrong", "generated", "singleparam1", "singleparam2", "-anotherparam", "mydata", "-param2", "myboard" )
   * // Parsing parameters
   * val (params, remaininglargs) = ParseArguments(args, List("param1", "param2"))
   * }}}
   *
   * Results:
   * {{{
   * Map("param2" -> "myboard")
   * Array("-wrong", "generated", "singleparam1", "singleparam2", "-anotherparam", "mydata")
   * }}}
   *
   * @param args the input arguments
   * @param parameters the parameters to be extracted from input `args`.
   * @param paramFormat the param prefix to be extracted (Eg. `-` or `--`).
   * @return a `Map[String,String]` with extracted params and an `Array[String]` with remaining arguments.
   */
  def apply(
    args: Array[String],
    parameters: List[String],
    paramFormat: String = "-"
  ): (Map[String, String], Array[String]) = {
    var params: Map[String, String] = Map()
    val remainingArgs               = new ListBuffer[String]()
    var lastKey                     = ""
    for (key <- args.toList) {
      key match {
        // Below are the required module parameters
        case p if parameters.contains(p.drop(paramFormat.length)) => lastKey = p.drop(paramFormat.length)
        // Below are the extraction cases. No need to change.
        case param if (lastKey.nonEmpty && param.slice(0, paramFormat.length) != paramFormat) =>
          params += (lastKey -> param); lastKey = ""
        case param if (lastKey.nonEmpty && param.slice(0, paramFormat.length) == paramFormat) =>
          remainingArgs += param; lastKey = ""
        case param if lastKey.isEmpty =>
          remainingArgs += param; lastKey = ""
      }
    }
    (params, remainingArgs.toArray)
  }
}
