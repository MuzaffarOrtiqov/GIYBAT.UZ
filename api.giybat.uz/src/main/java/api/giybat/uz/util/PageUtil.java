package api.giybat.uz.util;

public class PageUtil {
    public static int giveProperPageNumbering(int value) {
        return value <= 0 ? 1 : value - 1;
    }
}
