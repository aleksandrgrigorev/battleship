package battleship;

public class CoordinatesUtils {
    public static final int CODE_OF_A = 65;

    public static int extractRow(String coord) {
        return coord.toCharArray()[0] - CODE_OF_A + 1;
    }

    public static int extractCol(String coord) {
        String coordSubstr = coord.substring(1);
        return Integer.parseInt(coordSubstr);
    }
}
