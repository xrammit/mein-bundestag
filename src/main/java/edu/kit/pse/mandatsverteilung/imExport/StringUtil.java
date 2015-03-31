package edu.kit.pse.mandatsverteilung.imExport;

public class StringUtil {

    public static String deleteHyphen(String string) {
        if(string == null) {
            return null;
        }
        int help = -1;
        int afterHyphen = 0;
        String returnString = "";
        help = string.indexOf("-");
        while (help != -1) {
            if (string.charAt(help + 1) == '\n') {
                returnString = returnString.concat(string.substring(afterHyphen, help));
                afterHyphen = help + 2;
            }
            help = string.indexOf("-", help +1);
        }
        return returnString.concat(string.substring(afterHyphen));
    }

}
