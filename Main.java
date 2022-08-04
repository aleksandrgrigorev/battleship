package battleship;

import java.util.*;

import static battleship.CoordinatesUtils.extractCol;
import static battleship.CoordinatesUtils.extractRow;
import static battleship.TurnResult.WRONG_COORDS;

public class Main {

    private static final int PLAYER_1 = 1;
    private static final int PLAYER_2 = 2;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        Map<Integer, GameBoard> playerToBoard = initGame();

        playerToBoard.forEach((playerId, board) -> {
            System.out.println("Player " + playerId + ", place your ships on the game field");
            board.tryPlaceShips(scanner);
            passTurn(scanner);
        });

        int currentPlayer = PLAYER_1;

        while (true) {

            int anotherPlayer = getAnotherPlayer(currentPlayer);
            GameBoard currentGameBoard = playerToBoard.get(currentPlayer);
            GameBoard anotherGameBoard = playerToBoard.get(anotherPlayer);
            currentGameBoard.printNewTurnStart();

            System.out.println("Player " + currentPlayer + ", it's your turn:");
            System.out.println();
            TurnResult turnResult;
            int shotRow;
            int shotCol;
            do {
                String shotStr = scanner.nextLine();
                shotRow = extractRow(shotStr);
                shotCol = extractCol(shotStr);
                turnResult = anotherGameBoard.doTurn(shotRow, shotCol);
            } while (turnResult == WRONG_COORDS);

            currentGameBoard.markFoggedField(turnResult, shotRow, shotCol);

            processResult(turnResult);
            if (turnResult == TurnResult.WON) {
                break;
            } else {
                passTurn(scanner);
                currentPlayer = anotherPlayer;
            }
        }
    }

    public static void processResult(TurnResult turnResult) {
        System.out.println();
        switch(turnResult) {
            case WON:
                System.out.println("You sank the last ship. You won. Congratulations!");
                break;
            case MISSED:
                System.out.println("You missed!");
                break;
            case HIT_SHIP:
                System.out.println("You hit a ship!");
                break;
            case SANK_SHIP:
                System.out.println("You sank a ship!");
                break;
            case WRONG_COORDS:
                System.out.println("Error! You entered the wrong coordinates! Try again:");
                break;
        }
    }

    private static int getAnotherPlayer(int currentPlayer) {
        if (currentPlayer == PLAYER_1) {
            return PLAYER_2;
        } else {
            return PLAYER_1;
        }
    }

    private static void passTurn(Scanner scanner) {
        System.out.print("Press Enter and pass the move to another player");
        scanner.nextLine();
        System.out.println("...");
    }

    private static Map<Integer, GameBoard> initGame() {
        GameBoard gameBoard1 = new GameBoard();
        GameBoard gameBoard2 = new GameBoard();

        Map<Integer, GameBoard> playerToBoard =  new LinkedHashMap<>();
        playerToBoard.put(PLAYER_1, gameBoard1);
        playerToBoard.put(PLAYER_2, gameBoard2);

        return playerToBoard;
    }
}