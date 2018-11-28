package goose_game
import scala.util.{Either, Try, Success, Failure, Left, Right}
import cats._
import cats.instances.list._
import cats.data._
import cats.implicits._
import cats.syntax.writer._
import cats.syntax.applicative._

case class Player(val name: String, position: Position, lastRoll: (Dice, Dice), last: Position = Position.start)

object Player{
    def atStart(name: String) = new Player(name, Position.start, (Dice(0), Dice(0)))
}

case class Dice(value: Int)
object Dice{
    def roll = new Dice(1+ (Math.random * 1000 % 6).toInt)
}

case class Position(offset: Int){
    def moveBy(n: Int) = Position.moveBy(this)(n)
    def moveTo(n: Int) = Position.moveTo(this)(n)
}

object Position{
    def start = new Position(0)
    def moveBy(p: Position)(n: Int) = Position(p.offset + n)
    def moveTo(p: Position)(n: Int) = Position(n)
}

case class Board(players: Map[String, Player], bridges: List[Int], geese: List[Int], winPos: Position)(tell: String => Unit){
    
    def addPlayer(p: Player): Board = this.players.get(p.name) match{
        case None => {
            val res = this.withUpdatedPlayer(p)
            tell(s"""Players: ${res.players.keys.mkString(", ")}""")
            res
        }
        case Some(pl) => {
            tell(s"${p.name}: Already existing player") 
            this
        }
    }

    def lookupPlayer(name: String): Option[Player] = this.players.get(name)

    def withUpdatedPlayer(p: Player): Board = {
        val newb = this.copy(players ++ Map(p.name -> p), bridges, geese, winPos)(tell)
        newb.hasWon match{
            case Some(pl) => tell(s"${pl.name} wins!!")
            case None => ()
        }
        newb
    }
        
    def hasWon: Option[Player] = players.values.collectFirst{ case pl if pl.position.offset == winPos.offset => pl}

    def movePlayer(p: Player)(steps: Int): Board = {
        tell(s"${p.name} rolls ${p.lastRoll._1.value},${p.lastRoll._2.value} ")
        val newpos = Position(p.position.offset+steps)
        val from = if(p.position.offset == 0) s" Start" else  s"${p.position.offset}"
        val newpl = p.copy(position = newpos)
        
        val board = (
            if(newpos.offset > winPos.offset){
                tell(s"${p.name} moves from ${p.position.offset} to ${winPos.offset}")
                movePlayerBouncingBack(p)(steps)
            }    
            else if(steps == 0 && !bridges.contains(newpos.offset) && !geese.contains(newpos.offset))
                this    
            else{
                tell(s"${p.name} moves from ${from} to ${newpl.position.offset}")
                withUpdatedPlayer(newpl)
        })
        
        if(bridges.contains(newpos.offset))
                board.bridgeJump(newpl)(steps) 
        else if(geese.contains(newpos.offset)){
                board.gooseJump(newpl)(steps) 
        }  else
            board
    }


    def movePlayerBouncingBack(p: Player)(steps: Int): Board = { 
        val newpos = Position.moveTo(p.position)(winPos.offset - (p.position.offset + steps)%(winPos.offset))
        val newpl = p.copy(position = newpos)
        tell(s" ${p.name} bounces! ${p.name} returns to ${newpl.position.offset}")
        withUpdatedPlayer(newpl)
    } 

    def gooseJump(p: Player)(steps: Int): Board = geese.collectFirst{case gp if(gp == p.position.offset) => gp} match{
        case Some(goosepos) => {
            val newpl = p.copy(position = Position(goosepos))
            tell(s"The Goose. ${p.name} moves again and goes to ${newpl.position.offset}")
            val checkForward = Position(goosepos + steps)
            if(geese.contains(checkForward.offset)){
                gooseJump(newpl.copy(position = checkForward))(steps)
            }
            else
                withUpdatedPlayer(newpl)
        }
        case None => this
    }

    def bridgeJump(p: Player)(steps: Int): Board = bridges.collectFirst{
            case bridge if bridge == p.position.offset => bridge
        } match{
            case Some(bridge) => {
                tell(s" ${p.name} moves from ${p.position.offset} to The Bridge")
                val newpl = p.copy(position = Position(bridge))
                withUpdatedPlayer(newpl)
            }
            case None => this
    }

    def run(command: Command): Board = hasWon match{
        case Some(player) => {
            tell(s"Game over ${player.name} has won")
            sys.exit(0)
            this
        }
        case None => command match{
            case AddPlayer(name) => addPlayer(Player.atStart(name))
            case MoveSpecific(name, dice) => lookupPlayer(name) match{
                case Some(pl) => movePlayer(pl.copy(lastRoll = dice))(dice._1.value + dice._2.value)
                case None => {
                    tell(s"Player ${name} does not exist")
                    this
                }
            }
            case MoveRandom(name)  => if(lookupPlayer(name).isDefined){
                val die = (Dice.roll, Dice.roll)
                val action = MoveSpecific(name, die)
                run(action)
            } else run(InvalidCommand)
            case InvalidCommand  => tell(s"Invalid command entered"); this
            case _ => this
        }
    } 

}
