package night.app.services;

public class DateTimeFormatter {
    private static String fillZero(int integer) {
        return integer < 10 ? "0" + integer : String.valueOf(integer);
    }

    public static String convertIntTime(int time) {
        int hours = Math.round(time / 60);
        int minutes = time - hours * 60;

        return fillZero(hours) + ':' + fillZero(minutes);
    }

    public static int getTimeDifference(int prevTime, int laterTime) {
        if (prevTime > laterTime) {
            return 24*60 + laterTime - prevTime;
        }
        return laterTime - prevTime;
    }
}
