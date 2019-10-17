package cn.entertech.realtimedatasdk.report.file;

/**
 * Created by EnterTech on 2017/3/29.
 */

public class StringUtil {

    public static String substring(String string, int start, int end) {
        return substring(string, start, end, "0");
    }

    public static String substring(String string, int start, int end, String def) {
        if (null == string || string.equals("")) {
            return def;
        }
        if (start < 0) {
            return def;
        }
        final int length = string.length();
        if (length <= start) {
            return def;
        } else if (length < end) {
            return string.substring(start);
        } else {
            return string.substring(start, end);
        }
    }

}
