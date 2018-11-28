package goose_game

trait Command{
    val pattern: scala.util.matching.Regex
}

case class MoveSpecific(name: String, dice: (Dice, Dice)) extends Command{
    val pattern = """^move\s*([\w\d]+)\s*([1-6])\s*,\s*([1-6])$""".r
}

case class MoveRandom(name: String) extends Command{
    val pattern = """^move\s*([\w\d]+)$""".r
}

case class AddPlayer(name: String) extends Command{
    val pattern = """^add\s*player\s*([\w\d]+)""".r
}

case object InvalidCommand extends Command{
    val pattern = """^(.*)$""".r
}

object Parser{
    val addP = AddPlayer("")
    val moveRand = MoveRandom("")
    val moveSpecf = MoveSpecific("", (Dice.roll,Dice.roll))

    def parse(input: String) = input match{
        case addP.pattern(name) => AddPlayer(name)
        case moveSpecf.pattern(name, die1, die2) => MoveSpecific(name, (Dice(die1.toInt), Dice(die2.toInt)))
        case moveRand.pattern(name) => MoveRandom(name)
        case _ => InvalidCommand 
    }
}
