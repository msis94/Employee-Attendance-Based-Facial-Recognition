import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

public class DATE
{
    public static void main(String[] args) throws ParseException {
        //        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        System.out.println(timestamp);

//        Date a = new Date(1601965785027L);
//        System.out.println(a);


//        String str = "Wed Oct 07 11:58:32 MYT 2020";
////        String str = "geekss@for@geekss";
//        String[] arrOfStr = str.split(" ");
//        System.out.println(arrOfStr[2]+" "+arrOfStr[1]+" "+arrOfStr[5]);
//        System.out.println("------------------------------");
//
//        for (String a : arrOfStr)
//            System.out.println(a);
//
//        Timestamp a = new Timestamp(1602128025869L);
//        System.out.println(a.toLocalDateTime()+"Z");
//
//        System.out.println(new Time(1602128025869L).getHours());

//        Timestamp b = new Timestamp(1602121110468L);
//        System.out.println(b.getDate());
//
//
//        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//        String string1 = "2001-07-04T12:08:56.235-0700";
//        Date result1 = df1.parse(string1);

//        String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
//        String DATEFORMAT = "YYYY-MM-dd"+"T"+"HH:mm:ss";
//        SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        String utcTime = sdf.format(new Date(1602173680237L));
//        System.out.println(utcTime);


////      1. Change local date & time to milisecond
//        String myDate = "2014-10-29 18:10:45";
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = sdf.parse(myDate);
//        long millis = date.getTime();
//
////      2. Change local date & time (milisecond) to UTC Format
//        String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
//        SimpleDateFormat sdfk = new SimpleDateFormat(DATEFORMAT);
//        sdfk.setTimeZone(TimeZone.getTimeZone("UTC"));
//        String utcTime = sdfk.format(new Date(millis));
//
//        System.out.println(utcTime);



//        for (String b : a)
//            System.out.println(b);
//
//        System.out.println(masuk);
//        System.out.println(masa_ditetapkan);
//
//        if(masuk > masa_ditetapkan)
//        {
//            System.out.println("late");
//        }
//        else
//        {
//            System.out.println("not late");
//        }

//        String[] a = new Timestamp(1602378000000L).toString().split(" ");
//        System.out.println(a[0]);
//
//        Date b = new Date(1602518400000L);
//        System.out.println(b.toInstant().toString());

//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
//        try {
//            Date date = formatter.parse("Sun Oct 11 23:54:45 MYT 2020");
//            System.out.println("Date is: "+date);
//        } catch (ParseException e) {e.printStackTrace();}
//
//        final String OLD_FORMAT = "dd/MM/yyyy";
//        final String OLD_FORMAT = "E MMM dd HH:mm:ss z yyyy";
//        final String NEW_FORMAT = "yyyy/MM/dd";
//
//// August 12, 2010
//        String oldDateString = "Sun Oct 11 23:54:45 MYT 2020";
//        String newDateString;
//
//        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
//        Date d = sdf.parse(oldDateString);
//        sdf.applyPattern(NEW_FORMAT);
//        newDateString = sdf.format(d);
//        System.out.println(newDateString);

        LocalDate date = LocalDate.of(2017, 1, 13);
        LocalDateTime datetime = date.atTime(1,50,9);
        System.out.println(datetime+"Z");

    }
}
