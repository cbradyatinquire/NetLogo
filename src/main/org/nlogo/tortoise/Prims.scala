// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.tortoise

import org.nlogo.{ compile, nvm, prim },
  compile._

object Prims {

  object InfixReporter {
    def unapply(r: nvm.Reporter): Option[String] =
      PartialFunction.condOpt(r) {
        case _: prim.etc._plus           => "+"
        case _: prim._minus              => "-"
        case _: prim.etc._mult           => "*"
        case _: prim.etc._div            => "/"
        case _: prim._equal              => "==="
        case _: prim._notequal           => "!=="
        case _: prim._lessthan           => "<"
        case _: prim._greaterthan        => ">"
        case _: prim.etc._greaterorequal => ">="
        case _: prim.etc._lessorequal    => "<="
        case _: prim._and                => "&&"
        case _: prim._or                 => "||"
      }
  }

  object NormalReporter {
    def unapply(r: nvm.Reporter): Option[String] =
      PartialFunction.condOpt(r) {
        case _: prim.etc._self        => "AgentSet.self"
        case _: prim.etc._patch       => "world.getPatchAt"
        case _: prim.etc._turtles     => "world.turtles"
        case _: prim._patches         => "world.patches"
        case _: prim.etc._ticks       => "world.ticks"
        case _: prim._count           => "AgentSet.count"
        case _: prim._any             => "AgentSet.any"
        case _: prim._random          => "Random.nextLong"
        case _: prim._list            => "Prims.list"
        case _: prim.etc._sort        => "Prims.sort"
        case _: prim.etc._max         => "Prims.max"
        case _: prim.etc._min         => "Prims.min"
        case _: prim._sum             => "Prims.sum"
        case _: prim.etc._randomfloat => "Prims.randomfloat"
        case _: prim.etc._randomxcor  => "Prims.randomxcor"
        case _: prim.etc._randomycor  => "Prims.randomycor"
        case _: prim._oneof           => "AgentSet.oneOf"
        case _: prim.etc._removeduplicates => "Prims.removeDuplicates"
        case _: prim._not             => "!"
        case _: prim.etc._patchahead  => "AgentSet.self().patchAhead"
        case _: prim.etc._shadeof     => "Prims.shadeOf"
        case _: prim.etc._turtleshere => "AgentSet.self().turtlesHere"
        case _: prim.etc._sin         => "Trig.unsquashedSin"
        case _: prim.etc._cos         => "Trig.unsquashedCos"
        case _: prim.etc._round       => "StrictMath.round"
      }
  }

  object NormalCommand {
    def unapply(c: nvm.Command): Option[String] =
      PartialFunction.condOpt(c) {
        case _: prim.etc._outputprint      => "println"
        case _: prim.etc._clearall         => "world.clearall"
        case _: prim.etc._clearticks       => "world.clearTicks"
        case _: prim.etc._resetticks       => "world.resetTicks"
        case _: prim.etc._tick             => "world.tick"
        case _: prim.etc._tickadvance      => "world.advancetick"
        case _: prim._fd                   => "Prims.fd"
        case _: prim._bk                   => "Prims.bk"
        case _: prim.etc._left             => "Prims.left"
        case _: prim.etc._right            => "Prims.right"
        case _: prim.etc._setxy            => "Prims.setxy"
        case _: prim.etc._die              => "AgentSet.die"
        case _: prim.etc._randomseed       => "Random.setSeed"
      }
  }

  def generateWhile(w: Statement): String = {
    val pred = Compiler.genReporterBlock(w.args.head)
    val body = Compiler.genCommandBlock(w.args.tail.head)
    s"""while ($pred) {
      |$body
      |}""".stripMargin
  }

  def generateIf(s: Statement): String = {
    val pred = Compiler.genReporterApp(s.args.head)
    val body = Compiler.genCommandBlock(s.args.tail.head)
    s"""if ($pred) {
      |$body
      |}""".stripMargin
  }

  def generateIfElse(s: Statement): String = {
    val pred      = Compiler.genReporterApp(s.args.head)
    val thenBlock = Compiler.genCommandBlock(s.args.tail.head)
    val elseBlock = Compiler.genCommandBlock(s.args.tail.tail.head)
    s"""if ($pred) {
      |$thenBlock
      |} else {
      |$elseBlock
      |}""".stripMargin
  }

  def generateAsk(s: Statement, shuffle: Boolean): String = {
    val agents = Compiler.genReporterApp(s.args.head)
    val body   = fun(Compiler.genCommandBlock(s.args.tail.head))
    s"AgentSet.ask($agents, $shuffle, $body);"
  }

  def generateCreateTurtles(s: Statement, ordered: Boolean): String = {
    val n = Compiler.genReporterApp(s.args.head)
    val name = if (ordered) "createorderedturtles" else "createturtles"
    val body = fun(Compiler.genCommandBlock(s.args.tail.head))
    s"AgentSet.ask(world.$name($n), true, $body);"
  }

  def generateSprout(s: Statement): String = {
    val n = Compiler.genReporterApp(s.args.head)
    val body = fun(Compiler.genCommandBlock(s.args.tail.head))
    s"AgentSet.ask(Prims.sprout($n), true, $body);"
  }

  def fun(body: String) = s"function(){ $body }"

}