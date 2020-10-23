//package sample;
//
//import com.mongodb.DB;
//import com.mongodb.MongoClient;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import org.jongo.Jongo;
//import org.jongo.MongoCollection;
//import org.jongo.MongoCursor;
//
//public class Controller
//{
//    @FXML
//    TextField tf_name;
//
//    @FXML
//    Button btn_search;
//
//    @FXML
//    Label lbl_info;
//
//    @FXML
//    TableView<Attendance> tbl_attendance;
//
//    @FXML
//    TableColumn<Attendance, String> col_date;
//
//    @FXML
//    TableColumn<Attendance, String> col_time_in;
//
//    @FXML
//    TableColumn<Attendance, String> col_time_out;
//
////-------------------------------------------------------------------
//
//    DB db = new MongoClient().getDB("skymind");
//    Jongo jongo = new Jongo(db);
//    MongoCollection empColl = jongo.getCollection("employee");
//    MongoCollection attColl = jongo.getCollection("attendance");
//
//    ObservableList<Attendance> attendances = FXCollections.observableArrayList();
//
//    MongoCursor<Attendance> search;
//
//    public void initialize()
//    {
//        allDataBtn();
//        System.out.println("Init");
//        col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
//        col_time_in.setCellValueFactory(new PropertyValueFactory<>("time_in"));
//        col_time_out.setCellValueFactory(new PropertyValueFactory<>("time_out"));
//    }
//
////    public ObservableList<Attendance> getAttendance()
////    {
////        col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
////        col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
////        col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
////
////        ObservableList<Product> products = FXCollections.observableArrayList();
////        for (int i = 0; i < 3; i++)
////        {
////            products.add(new Product("Laptop", 890.0, 20));
////        }
////
////        return products;
////    }
//
//    public void searchButton(ActionEvent actionEvent)
//    {
//        tbl_attendance.getItems().clear();
//        String query = "{emp_num:"+Integer.parseInt(tf_name.getText())+"}";
//        search = attColl.find(query).as(Attendance.class);
//
//
//        while(search.hasNext())
//        {
//            Attendance detail = search.next();
//            attendances.add(new Attendance(detail.getEmp_num(), detail.getDate(), detail.getTime_in(), detail.getTime_out()));
//        }
//        tbl_attendance.setItems(attendances);
//
////        Find name of that employee number
//
//        Employee e = empColl.findOne(query).as(Employee.class);
//
//        if(e==null)
//        {
//            lbl_info.setText("Employee Number does not exist");
//
//        }
//        else
//        {
//            lbl_info.setText("Employee Number : "+tf_name.getText()+" -------- Employee Name : "+e.getEmp_name());
//        }
//
//    }
//
//    public void allDataBtn()
//    {
//        tbl_attendance.getItems().clear();
//        search = attColl.find().as(Attendance.class);
//
//        while(search.hasNext())
//        {
//            Attendance detail = search.next();
//            attendances.add(new Attendance(detail.getEmp_num(), detail.getDate(), detail.getTime_in(), detail.getTime_out()));
//        }
//        tbl_attendance.setItems(attendances);
//
//    }
//}
