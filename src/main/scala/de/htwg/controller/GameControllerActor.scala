package de.htwg.controller

import akka.actor.Actor
import akka.actor.Props
import de.htwg.controller.GameControllerActor.GetGrid
import de.htwg.controller.GameControllerActor.GetGridAck
import de.htwg.model.SelectPosition
import de.htwg.model.Player
import de.htwg.model.GridPosition
import de.htwg.model.SelectPositionAck
import de.htwg.model.SelectPositionAck.SelectPositionReturnCode.NotThisPlayersTurn
import de.htwg.model.SelectPositionAck.SelectPositionReturnCode.PositionAlreadySelected
import de.htwg.model.SelectPositionAck.SelectPositionReturnCode.GameAlreadyFinished
import de.htwg.model.SelectPositionAck.SelectPositionReturnCode.GameWon
import de.htwg.model.SelectPositionAck.SelectPositionReturnCode.PositionSet

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

  case object GetGrid
  case class GetGridAck(map: Map[GridPosition, Player])

  def props(startingPlayer: Player) = Props(new GameControllerActor(startingPlayer))
}
