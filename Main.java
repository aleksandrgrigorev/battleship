package battleship;

import java.util.*;

public class Main {
    private static final int FIELD_SIZE = 10;
    private static final int CODE_OF_A = 65;

    public static void main(String[] args) {

        Map<Integer, List<String>> shipsLengthToNames =  new LinkedHashMap<>();
        shipsLengthToNames.put(5, List.of("Aircraft Carrier (5 cells)"));
        shipsLengthToNames.put(4, List.of("Battleship (4 cells)"));
        shipsLengthToNames.put(3, List.of("Submarine (3 cells)", "Cruiser (3 cells)"));
        shipsLengthToNames.put(2, List.of("Destroyer (2 cells)"));

        String[][] field = initField();
        printField(field);

        Scanner scanner = new Scanner(System.in);

        tryPlaceShips(scanner, field, shipsLengthToNames);

        System.out.println("The game starts!");

        tryShootShip(scanner, field);
    }

    private static String[][] initField() {
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

    private static void printField(String[][] field) {
        System.out.println();
        for (String[] fieldStr : field) {
            for (String place : fieldStr) {
                System.out.print(place + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void tryPlaceShips(Scanner scanner, String[][] field, Map<Integer,
            List<String>> shipsLengthToNames) {
        for (int length : shipsLengthToNames.keySet()) {
            for (String name : shipsLengthToNames.get(length)) {
                ShipCoordinates shipCoordinates = getUserShipCoordinates(field, name, scanner, length);
                boolean isVertical = shipCoordinates.firstCoordCol == shipCoordinates.secondCoordCol;

                if (isVertical) {
                    for (int i = shipCoordinates.firstCoordRow; i <= shipCoordinates.secondCoordRow; i++) {
                        field[i][shipCoordinates.firstCoordCol] = "O";
                    }
                } else {
                    for (int j = shipCoordinates.firstCoordCol; j <= shipCoordinates.secondCoordCol; j++) {
                        field[shipCoordinates.firstCoordRow][j] = "O";
                    }
                }
                printField(field);
            }
        }
    }

    private static int extractRow(String coord) {
        return coord.toCharArray()[0] - CODE_OF_A + 1;
    }

    private static int extractCol(String coord) {
        String coordSubstr = coord.substring(1);
        return Integer.parseInt(coordSubstr);
    }

    /**
     * This method gets ship coordinates from user and checks if they are correct.
     */
    private static ShipCoordinates getUserShipCoordinates(String[][] field, String name, Scanner scanner, int length) {
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
        return new ShipCoordinates(firstCoordRow, firstCoordCol, secondCoordRow, secondCoordCol);
    }

    private static boolean validateCoords(String[][] field, String name, int length, int firstCoordRow,
                                          int firstCoordCol, int secondCoordRow, int secondCoordCol) {
        int length1 = secondCoordRow - firstCoordRow + 1;
        int length2 = secondCoordCol - firstCoordCol + 1;

        boolean isVertical = firstCoordCol == secondCoordCol;

        if (isVertical && length1 != length || !isVertical && length2 != length) {
            System.out.println();
            System.out.println("Error! Wrong length of the " + name + "! Try again:");
            System.out.println();
            return false;
        }

        if (firstCoordRow > secondCoordRow || firstCoordCol > secondCoordCol) {
            System.out.println();
            System.out.println("Error! Wrong ship location! Try again:");
            System.out.println();
            return false;
        }

        return !isShipTooClose(field, firstCoordRow, firstCoordCol, secondCoordRow, secondCoordCol);
    }

    /**
     * This method checks if the ship is too close to already placed ships.
     */
    private static boolean isShipTooClose(String[][] field, int firstCoordRow, int firstCoordCol, int secondCoordRow,
                                          int secondCoordCol) {
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
                if (field[i][j].equals("O")) {
                    result = true;
                    break;
                }
            }
        }

        if (result) {
            System.out.println();
            System.out.println("Error! You placed it too close to another one. Try again:");
            System.out.println();
        }
        return result;
    }

    /**
     * This method reads the shot coordinates, checks if they are inside the field bounds, and if they are -
     * tells if the shot was a hit or a miss.
     */
    private static void tryShootShip(Scanner scanner, String[][] field) {
        printField(field);
        System.out.println("Take a shot!");
        System.out.println();

        while (true) {
            String shotStr = scanner.nextLine();
            int shotRow = extractRow(shotStr);
            int shotCol = extractCol(shotStr);
            String shotCoord;

            try {
                shotCoord = field[shotRow][shotCol];
            } catch (ArrayIndexOutOfBoundsException exception) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
                continue;
            }

            if (shotCoord.equals("O")) {
                field[shotRow][shotCol] = "X";
                printField(field);
                System.out.println("You hit a ship!");
                return;

            } else if (shotCoord.equals("~")) {
                field[shotRow][shotCol] = "M";
                printField(field);
                System.out.println("You missed!");
                return;
            }
        }
    }

    private static class ShipCoordinates {
        private final int firstCoordRow;
        private final int firstCoordCol;
        private final int secondCoordRow;
        private final int secondCoordCol;

        public ShipCoordinates(int firstCoordRow, int firstCoordCol, int secondCoordRow, int secondCoordCol) {
            this.firstCoordRow = firstCoordRow;
            this.firstCoordCol = firstCoordCol;
            this.secondCoordRow = secondCoordRow;
            this.secondCoordCol = secondCoordCol;
        }
    }
}