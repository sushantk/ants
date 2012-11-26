package ants.core;

public class Util {

    public static String implode(Iterable<String> list, String seperator) {
        StringBuffer value = new StringBuffer();

        int i = 0;
        for (String string : list) {
            if (i > 0) {
                value.append(seperator);
            }
            value.append(string);
            i++;
        }

        return value.toString();
    }

}
