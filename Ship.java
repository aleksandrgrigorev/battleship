package battleship;

public class Ship {
    private final int firstCoordRow;
    private final int firstCoordCol;
    private final int secondCoordRow;
    private final int secondCoordCol;

    private boolean isSunk = false;

    public Ship(int firstCoordRow, int firstCoordCol, int secondCoordRow, int secondCoordCol) {
        this.firstCoordRow = firstCoordRow;
        this.firstCoordCol = firstCoordCol;
        this.secondCoordRow = secondCoordRow;
        this.secondCoordCol = secondCoordCol;
    }

    /**
     * This method checks if input coordinates belong to the ship.
     */
    public boolean contains(int row, int col) {
        return firstCoordRow <= row && secondCoordRow >= row
                && firstCoordCol <= col && secondCoordCol >= col;
    }

    public void sink() {
        isSunk = true;
    }

    public int getFirstCoordRow() {
        return firstCoordRow;
    }

    public int getFirstCoordCol() {
        return firstCoordCol;
    }

    public int getSecondCoordRow() {
        return secondCoordRow;
    }

    public int getSecondCoordCol() {
        return secondCoordCol;
    }

    public boolean isSunk() {
        return isSunk;
    }
}
