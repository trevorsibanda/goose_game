package goose_game

import cats._
import scala.util.{Try, Success, Failure}
import scala.collection.mutable.ArrayBuffer

object Game extends App{
    val log: ArrayBuffer[String] = ArrayBuffer()
    val tell = (msg: String) => {
        log.append(msg)
    }
    val board: Board = new Board(Map(), List(6), List(5, 9, 14, 18, 23, 27), Position(63))(tell)
    Stream.from(1).foldLeft(board){(board, counter) => 
        val line  = scala.io.StdIn.readLine
        val command = Parser.parse(line)
        val newboard = board.run(command)
        println(log.mkString(", "))
        log.clear
        newboard
    }
}