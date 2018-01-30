package dk.webbies.tajscheck.tajstester;

public class Utils {
    public static String readableStackTrace(StackTraceElement[] ss) {
        StringBuilder sb = new StringBuilder();
        for(StackTraceElement e : ss) {
            sb.append(e);
            sb.append("\n");
        }
        return sb.toString();
    }
}
