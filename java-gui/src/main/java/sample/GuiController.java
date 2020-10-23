package sample;

import com.fasterxml.jackson.databind.ser.impl.FailingSerializer;
import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Window;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.spec.ECField;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;

public class GuiController
{

    @FXML
    private Label lbl_info;

    @FXML
    private TextField tf_emp_name_register;

    @FXML
    private TextField tf_emp_num_register;

    @FXML
    private TextField tf_emp_num_search;

    @FXML
    private Button btn_register_registration;

    @FXML
    private Button btn_all_data;

    @FXML
    private TableView<Employee> tbl_registration;

    @FXML
    private TableColumn<Employee, String> col_emp_name;

    @FXML
    private TableColumn<Employee, String> col_emp_num;

    @FXML
    private TableColumn<Employee, String> col_emp_status;

    @FXML
    private TableView<Attendance> tbl_attendance;

    @FXML
    private TableColumn<Attendance, String> col_date;

    @FXML
    private TableColumn<Attendance, String> col_time_in;

    @FXML
    private TableColumn<Attendance, String> col_time_out;

    @FXML
    private TableColumn<Attendance, String> col_remark;

    @FXML
    private Button btn_search;

    @FXML
    private TextField tf_day_startdate;

    @FXML
    private TextField tf_month_startdate;

    @FXML
    private TextField tf_year_startdate;

    @FXML
    private TextField tf_day_enddate;

    @FXML
    private TextField tf_month_enddate;

    @FXML
    private TextField tf_year_enddate;

    @FXML
    private TextField tf_hour_starttime;

    @FXML
    private TextField tf_minute_starttime;

    @FXML
    private TextField tf_second_starttime;

    @FXML
    private TextField tf_hour_endtime;

    @FXML
    private TextField tf_minute_endtime;

    @FXML
    private TextField tf_second_endtime;

    @FXML
    private ChoiceBox<String> cb_status;

    //-------------------------------------------------------------------

    DB db = new MongoClient().getDB("skymind");
    Jongo jongo = new Jongo(db);
    MongoCollection empColl = jongo.getCollection("employee");
    MongoCollection attColl = jongo.getCollection("attendance");

    ObservableList<Attendance> attendances = FXCollections.observableArrayList();
    ObservableList<Employee> employeeList = FXCollections.observableArrayList();


    MongoCursor<Attendance> search;
    MongoCursor<Employee> findEmployee;

    //-------------------------------------------------------------------

    public void initialize() throws ParseException
    {
        showEmployeeDetail();
        showAttendanceDetail();
        System.out.println("Init");

//      Manage configuration for choicebox
        cb_status.getItems().add("active");
        cb_status.getItems().add("not active");

//      Manage configuration for Attendance column and table
        col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        col_time_in.setCellValueFactory(new PropertyValueFactory<>("time_in"));
        col_time_out.setCellValueFactory(new PropertyValueFactory<>("time_out"));
        col_remark.setCellValueFactory(new PropertyValueFactory<>("remark"));

//      Manage configuration for Employee column and table
        col_emp_name.setCellValueFactory(new PropertyValueFactory<>("emp_name"));
        col_emp_num.setCellValueFactory(new PropertyValueFactory<>("emp_num"));
        col_emp_status.setCellValueFactory(new PropertyValueFactory<>("emp_status"));
    }

//  Registration Tab
    public void register(ActionEvent actionEvent)
    {
        try
        {

//      0. Check if all the data has been field
            if(tf_emp_name_register.getText().isEmpty() ||
                    tf_emp_num_register.getText().isEmpty() ||
                    cb_status.getSelectionModel().isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning");
                // Header Text: null
                alert.setHeaderText(null);
                alert.setContentText("Make sure All Data are Filled");
                alert.showAndWait();
            }
            else
            {

//          1. Save data to mongo
                Employee message = saveEmployeeDetail();

//          2. Show it in the table
                showEmployeeDetail();

//          3. Send it to the rabbitmq
                Gson gson = new Gson();
                String json = gson.toJson(message);

                sendEmployeeDetail(json);
            }

        }
        catch (Exception e)
        {
            System.out.println(e);
        }

    }

    public void searchRegistration()
    {
        try
        {
            //      0. Check if all the employee number data has been field
            if(tf_emp_num_register.getText().isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning");
                // Header Text: null
                alert.setHeaderText(null);
                alert.setContentText("Make sure Employee Number are Filled");
                alert.showAndWait();
            }
            else
            {
                tbl_registration.getItems().clear();
                Employee findOne = empColl.findOne("{emp_num: "+Integer.parseInt(tf_emp_num_register.getText())+"}").as(Employee.class);
                if(findOne != null)
                {
                    employeeList.add(new Employee(findOne.getEmp_name(), findOne.getEmp_status(), findOne.getEmp_num()));
                    tbl_registration.setItems(employeeList);
                }
                else
                {
                    System.out.println("data are not found");
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

    }

    public void allDataRegistration(ActionEvent actionEvent)
    {
        showEmployeeDetail();
    }

    public void searchButton(ActionEvent actionEvent) throws ParseException
    {
        try
        {
            StringBuilder querys = new StringBuilder("");
            String query = "";

            if(!tf_emp_num_search.getText().isEmpty())
            {
                querys.append(",emp_num: "+Integer.parseInt(tf_emp_num_search.getText())+"");
                //        Find name of that employee number

                Employee e = empColl.findOne("{emp_num: "+Integer.parseInt(tf_emp_num_search.getText())+"}").as(Employee.class);

                if(e==null)
                {
                    lbl_info.setText("Employee Number does not exist");

                }
                else
                {
//            lbl_info.setText("Employee Number : "+tf_emp_num_search.getText()+" -------- Employee Name : "+e.getEmp_name());
                    lbl_info.setText("Employee Name : "+e.getEmp_name());

                }
            }

            if(!tf_day_startdate.getText().isEmpty() &&
                    !tf_day_enddate.getText().isEmpty() &&
                    tf_hour_starttime.getText().isEmpty() &&
                    tf_hour_endtime.getText().isEmpty())
            {
                String start_date = changeToUtc(tf_day_startdate.getText(), "00:00:00");
                String end_date = changeToUtc(tf_day_enddate.getText(), "00:00:00");
                querys.append(",date: { $gte: { $date: '"+start_date+"' },$lte: { $date: '"+end_date+"' }}");
            }

            if(!tf_day_startdate.getText().isEmpty() &&
                    !tf_day_enddate.getText().isEmpty() &&
                    !tf_hour_starttime.getText().isEmpty() &&
                    !tf_hour_endtime.getText().isEmpty())
            {
                String start_date = changeToUtc(tf_day_startdate.getText(), tf_hour_starttime.getText());
                String end_date = changeToUtc(tf_day_enddate.getText(), tf_hour_endtime.getText());
                querys.append(",time_in: { $gte: { $date: '"+start_date+"' }}, time_out: { $lte: { $date: '"+end_date+"' }}");
            }

            if(tf_day_startdate.getText().isEmpty() &&
                    tf_day_enddate.getText().isEmpty() &&
                    tf_hour_starttime.getText().isEmpty() &&
                    tf_hour_endtime.getText().isEmpty() &&
                    tf_emp_num_search.getText().isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning");
                // Header Text: null
                alert.setHeaderText(null);
                alert.setContentText("No data inserted");
                alert.showAndWait();
            }



            String noise = querys.substring(0,1);

            System.out.println(noise);
            if(noise.equals(","))
            {
                System.out.println("sini");
//            System.out.println(sb1.toString());
                String strNew = querys.toString().replaceFirst(",", "");
                query = "{"+strNew+"}";
                System.out.println("{"+strNew+"}");
            }

            tbl_attendance.getItems().clear();

            search = attColl.find(query).as(Attendance.class);

            while(search.hasNext())
            {
                Attendance detail = search.next();
//            attendances.add(new Attendance(detail.getEmp_num(), detail.getDate(), detail.getTime_in(), detail.getTime_out(), detail.getRemark()));
                attendances.add(new Attendance(
                        detail.getEmp_num(),
                        changeDateTimeToDate(detail.getDate()),
                        changeDateTimeToTime(detail.getTime_in()),
                        changeDateTimeToTime(detail.getTime_out()),
                        detail.getRemark()));
            }
            tbl_attendance.setItems(attendances);

        }
        catch (Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Warning");
            // Header Text: null
            alert.setHeaderText(null);
            alert.setContentText("Check All Data That You Try To Insert");
            alert.showAndWait();
            System.out.println(e);
        }
    }

    public String changeDateTimeToDate(String mongodate) throws ParseException
    {
        final String OLD_FORMAT = "E MMM dd HH:mm:ss z yyyy";
        final String NEW_FORMAT = "dd-MM-yyyy";

        String oldDateString = mongodate;
        String newDateString;

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = sdf.parse(oldDateString);
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);
        System.out.println(newDateString);

        return newDateString;
    }

    public String changeDateTimeToTime(String mongotime) throws ParseException {
        final String OLD_FORMAT = "E MMM dd HH:mm:ss z yyyy";
        final String NEW_FORMAT = "HH:mm:ss";

        String oldDateString = mongotime;
        String newDateString;

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = sdf.parse(oldDateString);
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);
        System.out.println(newDateString);

        return newDateString;
    }

    public void showAttendanceDetail() throws ParseException
    {
        tbl_attendance.getItems().clear();
        search = attColl.find().as(Attendance.class);

        while(search.hasNext())
        {
            Attendance detail = search.next();
            attendances.add(new Attendance(
                    detail.getEmp_num(),
                    changeDateTimeToDate(detail.getDate()),
                    changeDateTimeToTime(detail.getTime_in()),
                    changeDateTimeToTime(detail.getTime_out()),
                    detail.getRemark()));
        }
        tbl_attendance.setItems(attendances);
    }

    public void sendEmployeeDetail(String messages)
    {
        String QUEUE_NAME = "employee";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel())
        {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = messages;
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
        catch (IOException | TimeoutException e)
        {
            e.printStackTrace();
        }
    }

    public Employee saveEmployeeDetail()
    {
        String emp_name = tf_emp_name_register.getText();
        String status = cb_status.getSelectionModel().getSelectedItem();
        int emp_num = Integer.parseInt(tf_emp_num_register.getText());

        Employee new_data = null;
//      1. Before save check first whether the emp number already there or not, if it is already
//         there than show warning
        if(empColl.findOne("{emp_num: "+Integer.parseInt(tf_emp_num_register.getText())+"}").as(Employee.class)!=null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Warning");
            // Header Text: null
            alert.setHeaderText(null);
            alert.setContentText("That employee number are already exist !!!");
            alert.showAndWait();


        }
//      2. If that emp number are not there, than save that data into mongo
        else
        {
            new_data = new Employee(emp_name, status, emp_num);
            empColl.save(new_data);

//          3. Change the data into json
//          4. Send it to the rabbitmq
        }
        return new_data;

    }

    public void showEmployeeDetail()
    {
        tbl_registration.getItems().clear();
        findEmployee = empColl.find().as(Employee.class);

        while(findEmployee.hasNext())
        {
            Employee detail = findEmployee.next();
            employeeList.add(new Employee(detail.getEmp_name(), detail.getEmp_status(), detail.getEmp_num()));
        }
        tbl_registration.setItems(employeeList);


    }

    public String changeToUtc(String dates, String times) throws ParseException
    {
        //        1. Change local date & time to milisecond
        String localdatetime = ""+dates+" "+times+"";
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = sdf1.parse(localdatetime);
        long millis = date.getTime();

//      2. Change local date & time (milisecond) to UTC Format
        String utcFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
        SimpleDateFormat sdf2 = new SimpleDateFormat(utcFormat);
        sdf2.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utcLocalDateTime = sdf2.format(new Date(millis));

        System.out.println(utcLocalDateTime);
        return utcLocalDateTime;
    }


}
