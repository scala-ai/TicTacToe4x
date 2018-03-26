package de.htwg

import akka.actor.Actor
import akka.actor.Props
import de.htwg.model.SelectPosition
import de.htwg.model.Player
import de.htwg.model.GridPosition
import de.htwg.GameControllerActor.GetGrid
import de.htwg.GameControllerActor.GetGridAck
import de.htwg.GameControllerActor.SelectPositionAck
import de.htwg.GameControllerActor.SelectPositionReturnCode.NotThisPlayersTurn
import de.htwg.GameControllerActor.SelectPositionReturnCode.PositionAlreadySelected
import de.htwg.GameControllerActor.SelectPositionReturnCode.PositionSet
import de.htwg.GameControllerActor.SelectPositionReturnCode.GameAlreadyFinished
import de.htwg.GameControllerActor.SelectPositionReturnCode.GameWon

class GameControllerActor private(startingPlayer: Player) extends Actor {

  var current: Player = startingPlayer
  var finished: Boolean = false
  var gameField: Map[GridPosition, Player] = Map.empty

  override def receive: Receive = {
    case GetGrid => sender ! GetGridAck(gameField)

    case SelectPosition(p, pos) if p != current => sender ! SelectPositionAck(p, pos, gameField, NotThisPlayersTurn)
    case SelectPosition(p, pos) if gameField.contains(pos) => sender ! SelectPositionAck(p, pos, gameField, PositionAlreadySelected)
    case SelectPosition(p, pos) if finished => sender ! SelectPositionAck(p, pos, gameField, GameAlreadyFinished)
    case SelectPosition(p, pos) =>
      gameField += (pos -> p)
      val ret = if (checkGameWon(pos, p)) {
        finished = true
        GameWon
      } else {
        current = Player.other(current)
        PositionSet
      }

      sender ! SelectPositionAck(p, pos, gameField, ret)
  }

  private def checkGameWon(last: GridPosition, p: Player): Boolean = {
    last.buildCombinationsOf4.exists(isWinCondition(p))
  }

  private def isWinCondition(current: Player)(list: List[GridPosition]): Boolean = {
    list.flatMap(gameField.get).count(player => player == current) == GameControllerActor.noConnectedFieldRequiredToWin
  }

}


object GameControllerActor {
  private[GameControllerActor] val noConnectedFieldRequiredToWin = 4

  case class SelectPositionAck(p: Player, pos: GridPosition, state: Map[GridPosition, Player], returnCode: SelectPositionReturnCode)

  sealed trait SelectPositionReturnCode
  object SelectPositionReturnCode {
    case object PositionSet extends SelectPositionReturnCode
    case object GameWon extends SelectPositionReturnCode
    case object GameAlreadyFinished extends SelectPositionReturnCode
    case object PositionAlreadySelected extends SelectPositionReturnCode
    case object NotThisPlayersTurn extends SelectPositionReturnCode
  }

  case object GetGrid
  case class GetGridAck(map: Map[GridPosition, Player])

  def props(startingPlayer: Player) = Props(new GameControllerActor(startingPlayer))
}
