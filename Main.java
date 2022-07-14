package battleship;

import java.util.*;

public class Main {
    private static final int FIELD_SIZE = 10;
    private static final int CODE_OF_A = 65;

    private static final List<Ship> ships = new ArrayList<>();

    public static void main(String[] args) {

        Map<Integer, List<String>> shipsLengthToNames =  new LinkedHashMap<>();
        shipsLengthToNames.put(5, List.of("Aircraft Carrier (5 cells)"));
        shipsLengthToNames.put(4, List.of("Battleship (4 cells)"));
        shipsLengthToNames.put(3, List.of("Submarine (3 cells)", "Cruiser (3 cells)"));
        shipsLengthToNames.put(2, List.of("Destroyer (2 cells)"));

        String[][] actualField = initField();
        String[][] foggedField = initField();

        printField(actualField);

        Scanner scanner = new Scanner(System.in);

        tryPlaceShips(scanner, actualField, shipsLengthToNames);

        System.out.println("The game starts!");

        tryShootShip(scanner, actualField, foggedField);
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
                Ship ship = getUserShipCoordinates(field, name, scanner, length);
                ships.add(ship);
                boolean isVertical = ship.getFirstCoordCol() == ship.getSecondCoordCol();

                if (isVertical) {
                    for (int i = ship.getFirstCoordRow(); i <= ship.getSecondCoordRow(); i++) {
                        field[i][ship.getFirstCoordCol()] = "O";
                    }
                } else {
                    for (int j = ship.getFirstCoordCol(); j <= ship.getSecondCoordCol(); j++) {
                        field[ship.getFirstCoordRow()][j] = "O";
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

    /**
     * This method reads the shot coordinates, checks if they are inside the field bounds, and if they are -
     * tells if the shot was a hit or a miss.
     */
    private static void tryShootShip(Scanner scanner, String[][] actualField, String[][] foggedField) {
        printField(foggedField);
        System.out.println("Take a shot!");

        while (true) {
            System.out.println();

            String shotStr = scanner.nextLine();
            int shotRow = extractRow(shotStr);
            int shotCol = extractCol(shotStr);
            String shotCoord;

            try {
                shotCoord = actualField[shotRow][shotCol];
            } catch (ArrayIndexOutOfBoundsException exception) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
                continue;
            }

            if (shotCoord.equals("~")) {
                foggedField[shotRow][shotCol] = "M";
                printField(foggedField);
                System.out.println("You missed. Try again:");
            } else if (shotCoord.equals("O") || shotCoord.equals("X")) {
                actualField[shotRow][shotCol] = "X";
                foggedField[shotRow][shotCol] = "X";
                printField(foggedField);
                if (isShipSunk(actualField, shotRow, shotCol)) {
                    if (areAllShipsSunk()) {
                        System.out.println("You sank the last ship. You won. Congratulations!");
                        return;
                    } else {
                        System.out.println("You sank a ship! Specify a new target:");
                    }
                } else {
                    System.out.println("You hit a ship! Try again:");
                }
            }
        }
    }

    private static boolean areAllShipsSunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }

    private static boolean isShipSunk(String[][] actualField, int shotRow, int shotCol) {
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
}