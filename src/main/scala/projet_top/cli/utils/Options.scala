package projet_top.cli.utils

object Options extends Enumeration {
  type Option = Int

  // basic answers
  val Ok      = "o"
  val No      = "N"

  // basic operations
  val Quit    = 0
  val Help    = 8

  // subject-specific questions
  val Quest1  = 1
  val Quest2  = 2
  val Quest3  = 3
  val Quest4  = 4
  val Quest5  = 5
  val Quest6  = 6
}
