package battleship;

import java.util.*;

import static battleship.CoordinatesUtils.*;
import static battleship.TurnResult.*;

public class GameBoard {
    private static final int FIELD_SIZE = 10;
    private static final Map<Integer, List<String>> shipsLengthToNames = initShipsMap();

    private final List<Ship> ships = new ArrayList<>();
    private final String[][] actualField;
    private final String[][] foggedField;

    public GameBoard() {
        actualField = initField();
        foggedField = initField();
    }

    public void tryPlaceShips(Scanner scanner) {

        printBoardField(actualField);

        for (int length : shipsLengthToNames.keySet()) {
            for (String name : shipsLengthToNames.get(length)) {
                Ship ship = getUserShipCoordinates(actualField, name, scanner, length);
                ships.add(ship);
                boolean isVertical = ship.getFirstCoordCol() == ship.getSecondCoordCol();

                if (isVertical) {
                    for (int i = ship.getFirstCoordRow(); i <= ship.getSecondCoordRow(); i++) {
                        actualField[i][ship.getFirstCoordCol()] = "O";
                    }
                } else {
                    for (int j = ship.getFirstCoordCol(); j <= ship.getSecondCoordCol(); j++) {
                        actualField[ship.getFirstCoordRow()][j] = "O";
                    }
                }
                printBoardField(actualField);
            }
        }
    }

    public void printNewTurnStart() {
        System.out.println();
        printField(foggedField);
        System.out.println("---------------------");
        printField(actualField);
        System.out.println();
    }

    /**
     * This method reads the shot coordinates, checks if they are inside the field bounds, and if they are -
     * tells if the shot was a hit or a miss.
     */
    public TurnResult doTurn(int shotRow, int shotCol) {
        if (!isCoordsCorrect(shotRow, shotCol)) {
            return WRONG_COORDS;
        }

        while (true) {
            String shotCoord = actualField[shotRow][shotCol];

            if (shotCoord.equals("~")) {
                actualField[shotRow][shotCol] = "M";
                return MISSED;
            } else if (shotCoord.equals("O") || shotCoord.equals("X")) {
                actualField[shotRow][shotCol] = "X";
                if (isShipSunk(actualField, shotRow, shotCol)) {
                    if (areAllShipsSunk()) {
                        return WON;
                    } else {
                        return SANK_SHIP;
                    }
                } else {
                    return HIT_SHIP;
                }
            }
        }
    }

    public void markFoggedField(TurnResult turnResult, int shotRow, int shotCol) {
        if (turnResult == MISSED) {
            foggedField[shotRow][shotCol] = "M";
        } else if (turnResult == HIT_SHIP || turnResult == SANK_SHIP || turnResult == WON) {
            foggedField[shotRow][shotCol] = "X";
        }
    }

    public boolean isCoordsCorrect(int shotRow, int shotCol) {
        return 1 <= shotRow && shotRow <= FIELD_SIZE
                && 1 <= shotCol && shotCol <= FIELD_SIZE;
    }

    private String[][] initField() {
       String[][] field = new String[FIELD_SIZE + 1][FIELD_SIZE + 1];
        for (int i = 0; i <= FIELD_SIZE; i++) {
            for (int j = 0; j <= FIELD_SIZE; j++) {
                if (i == 0 && j == 0) {
                    field[i][j] = " ";
                    continue;
                }
                if (i == 0) {
                    field[i][j] = Integer.toString(j);
                    continue;
                }
                if (j == 0) {
                    field[i][j] = Character.toString((char) (CODE_OF_A + i - 1));
                    continue;
                }
                field[i][j] = "~";
            }
        }
        return field;
    }



    /**
     * This method gets ship coordinates from user and checks if they are correct.
     */
    private static Ship getUserShipCoordinates(String[][] field, String name, Scanner scanner, int length) {
        int firstCoordRow;
        int firstCoordCol;
        int secondCoordRow;
        int secondCoordCol;
        System.out.println("Enter the coordinates of the " + name + ":");
        while (true) {
            System.out.println();
            String shipCoordinates = scanner.nextLine();
            String[] shipCoordinatesArray = shipCoordinates.split(" ");
            String firstCoord = shipCoordinatesArray[0];
            firstCoordRow = extractRow(firstCoord);
            firstCoordCol = extractCol(firstCoord);
            String secondCoord = shipCoordinatesArray[1];
            secondCoordRow = extractRow(secondCoord);
            secondCoordCol = extractCol(secondCoord);

            if (firstCoordRow > secondCoordRow) {
                int temp = firstCoordRow;
                firstCoordRow = secondCoordRow;
                secondCoordRow = temp;
            }

            if (firstCoordCol > secondCoordCol) {
                int temp = firstCoordCol;
                firstCoordCol = secondCoordCol;
                secondCoordCol = temp;
            }

            if (!validateCoords(field, name, length, firstCoordRow, firstCoordCol, secondCoordRow, secondCoordCol)) {
                continue;
            }

            break;
        }
        return new Ship(firstCoordRow, firstCoordCol, secondCoordRow, secondCoordCol);
    }

    private static boolean validateCoords(String[][] field, String name, int length, int firstCoordRow,
                                          int firstCoordCol, int secondCoordRow, int secondCoordCol) {
        int length1 = secondCoordRow - firstCoordRow + 1;
        int length2 = secondCoordCol - firstCoordCol + 1;

        boolean isVertical = firstCoordCol == secondCoordCol;

        if (isVertical && length1 != length || !isVertical && length2 != length) {
            System.out.println();
            System.out.println("Error! Wrong length of the " + name + "! Try again:");
            return false;
        }

        if (firstCoordRow > secondCoordRow || firstCoordCol > secondCoordCol) {
            System.out.println();
            System.out.println("Error! Wrong ship location! Try again:");
            return false;
        }

        return !isShipTooClose(field, firstCoordRow, firstCoordCol, secondCoordRow, secondCoordCol);
    }

    /**
     * This method checks if the ship is too close to already placed ships.
     */
    private static boolean isShipTooClose(String[][] actualField, int firstCoordRow, int firstCoordCol,
                                          int secondCoordRow, int secondCoordCol) {
        int upperRow = firstCoordRow - 1;
        int lowerRow = secondCoordRow + 1;
        int leftCol = firstCoordCol - 1;
        int rightCol = secondCoordCol + 1;
        boolean result = false;

        for (int i = upperRow; i <= lowerRow; i++) {
            for (int j = leftCol; j <= rightCol; j++) {
                if (i <= 0 || j <= 0 || i > FIELD_SIZE || j > FIELD_SIZE) {
                    continue;
                }
                if (actualField[i][j].equals("O")) {
                    result = true;
                    break;
                }
            }
        }

        if (result) {
            System.out.println();
            System.out.println("Error! You placed it too close to another one. Try again:");
        }
        return result;
    }

    private void printBoardField(String[][] field) {
        System.out.println();
        printField(field);
        System.out.println();
    }

    private void printField(String[][] field) {
        for (String[] fieldStr : field) {
            for (String place : fieldStr) {
                System.out.print(place + " ");
            }
            System.out.println();
        }
    }

    private boolean areAllShipsSunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }

    private boolean isShipSunk(String[][] actualField, int shotRow, int shotCol) {
        for (Ship ship : ships) {
            if (ship.contains(shotRow, shotCol)) {
                for (int i = ship.getFirstCoordRow(); i <= ship.getSecondCoordRow(); i++) {
                    for (int j = ship.getFirstCoordCol(); j <= ship.getSecondCoordCol(); j++) {
                        if (actualField[i][j].equals("O")) {
                            return false;
                        }
                    }
                }
                ship.sink();
                return true;
            }
        }
        return false;
    }

    private static Map<Integer, List<String>> initShipsMap() {
        Map<Integer, List<String>> shipsLengthToNames =  new LinkedHashMap<>();
        shipsLengthToNames.put(5, List.of("Aircraft Carrier (5 cells)"));
        shipsLengthToNames.put(4, List.of("Battleship (4 cells)"));
        shipsLengthToNames.put(3, List.of("Submarine (3 cells)", "Cruiser (3 cells)"));
        shipsLengthToNames.put(2, List.of("Destroyer (2 cells)"));

        return shipsLengthToNames;
    }
}