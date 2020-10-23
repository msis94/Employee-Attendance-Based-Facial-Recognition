import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;

public class receiver
{
    public static void main(String[] argv) throws Exception
    {
//      (RECEIVE DATA) AI APP --> RABBITMQ --> JAVA APP (SAVE OR UPDATE ATTENDANCE) -->CHANGE TO JSON --> MONGO
//      0. AI APP SEND MESSAGE TO RABBITMQ
//      1. JAVA APP RECEIVE THE MESSAGE FROM RABBITMQ
//      2. CHANGE TO JSON
//      3. SEND TO MONGO

        RabbitMQReceive();
//        String message  = RabbitMQReceiveMessage();

//      (SEND DATA) CHANGE TO GSON --> JAVA APP --> RABBITMQ --> AI APP
//      0. CHANGE TO JSON
//      1. JAVA APP SEND MESSAGE TO RABBITMQ WHICH IS EMPLOYEE NAME & PERMISSION
//      2. AI APP RECEIVE MESSAGE WHICH IS EMPLOYEE NAME & PERMISSION



    }

    static void RabbitMQReceive() throws IOException, TimeoutException
    {
        System.out.println("Function : RabbitMMQReceive");
//      CONNECTION CONFIGURATION FOR RABBITMQ

        String ATTENDANCE = "attendance";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel attendance = connection.createChannel();

        attendance.queueDeclare(ATTENDANCE, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) ->
        {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            try
            {
                SaveToMongo(JsonToJava(message));
                System.out.println("siniiiii"+message);

//                RabbitMQSend();
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        };
        attendance.basicConsume(ATTENDANCE, true, deliverCallback, consumerTag -> { });
    }

    static void RabbitMQSend(String message)
    {
        String QUEUE_NAME = "hello";
        //      CONNECTION CONFIGURATION FOR RABBITMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel())
        {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    static Attendance JsonToJava(String message)
    {
        System.out.println("JsonToJava");
        Gson gson = new Gson();
        Attendance attendance = null;
        Employee emp = new Employee();

//      Convert JSON File to Java Object
        attendance = gson.fromJson(message, Attendance.class);
        ReturnToPython(attendance.getEmp_num(), emp.getEmp_status());
        return attendance;
    }

    static void JavaToJson(String message)
    {
        Gson gson = new Gson();
        Rabbit rb = new Rabbit(message);

        // 2. Java object to JSON string
        String jsonInString = gson.toJson(rb);

        RabbitMQSend(jsonInString);
    }

    static void SaveToMongo(Attendance attendance) throws ParseException
    {
        MongoCollection attColl;

        //Initialize the connection to the database
        DB db = new MongoClient().getDB("skymind");
        Jongo jongo = new Jongo(db);
//      Establish the connection to the employee collection
        attColl = jongo.getCollection("attendance");

//      ---------------------------------------------------------------------------
        Date date = new Date(Long.parseLong(attendance.getDate()));
        Date time_in = new Date(Long.parseLong(attendance.getTime_in()));
        Date time_out = new Date(Long.parseLong(attendance.getTime_out()));

//        String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
//        SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        String utcTime = sdf.format(new Date(Long.parseLong(attendance.getTime_out())));
//        String query_date = sdf.format(new Date(Long.parseLong(attendance.getDate())));
//        System.out.println("Sini Time_in : "+query_date);

        String utcTime = new Date(Long.parseLong(attendance.getTime_out())).toInstant().toString();
        String query_date = new Date(Long.parseLong(attendance.getDate())).toInstant().toString();


        String query = "{emp_num: "+attendance.getEmp_num()+", date: {$date: '"+query_date+"'}}";
//        kalau x de sila simpan baru
        System.out.println("status");
        System.out.println(attColl.findOne(query).as(Attendance.class));

//        kalau x de sila save baru punya

        if(attColl.findOne(query).as(Attendance.class)==null)
        {
            String[] dates = new Timestamp(Long.parseLong(attendance.getDate())).toString().split(" ");
            String times = "09:00:00";
            String localdatetime = ""+dates[0]+" "+times+"";
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dated = sdf1.parse(localdatetime);
            long mandatory = dated.getTime();
            String remark = "";

            if(Long.parseLong(attendance.getTime_in())>mandatory)
            {
                remark = "late";
            }
            else
            {
                remark = "on time";
            }

//            String mandatory = 'currentdate + 09.00' //nanti kena tukar ke milisecond
//            kalau time in > 9.00 then remarks = late
//            if(time_in > )
//            remarks = not late

            MongoAttendance new_data = new MongoAttendance(attendance.getEmp_num(), date, time_in, time_out, remark);
            attColl.save(new_data);
            System.out.println("save attendance");
        }
//        kalau ada sila update
        else
        {
            attColl.update("{emp_num: "+attendance.getEmp_num()+", date: {$date: '"+query_date+"'}}").with("{$set:{time_out: {$date:'"+utcTime+"'}}}");
            System.out.println("update attendance");
        }
        MongoCursor<Attendance> all = attColl.find().as(Attendance.class);

        while(all.hasNext())
        {
            Attendance a = all.next();
            System.out.println(a.getEmp_num()+" "+a.getTime_in()+" "+a.getTime_out());
        }

    }

    static void ReturnToPython(int emp_num, String permission)
    {
        //Initialize the connection to the database
        DB db = new MongoClient().getDB("skymind");
        Jongo jongo = new Jongo(db);
//      Establish the connection to the employee collection
        MongoCollection empColl = jongo.getCollection("employee");

        Attendance att = new Attendance();
        System.out.println("Result : "+ att.getEmp_num());
        String query = "{emp_num: "+emp_num+"}";

        Employee emp = empColl.findOne(query).as(Employee.class);
//        String json = "{\"emp_name\": 9, \"status\": \"active\"}";
        String json = "{" +
                "\"emp_name\": \""+emp.getEmp_name()+"\", " +
                "\"emp_status\": \""+emp.getEmp_status()+"\"}";

        JavaToJson(json);
    }

}