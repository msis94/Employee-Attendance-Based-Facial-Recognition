package sample;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Attendance
{
    private int emp_num;
    private String date;
    private String time_in;
    private String time_out;
    private String remark;

//    public Attendance()
//    {
//        this.emp_num = 0;
//        this.date = "";
//        this.time_in = "";
//        this.time_out = "";
//    }

    public Attendance() {}

    public Attendance(int emp_num, String date, String time_in, String time_out, String remark) {
        this.emp_num = emp_num;
        this.date = date;
        this.time_in = time_in;
        this.time_out = time_out;
        this.remark = remark;
    }

    public int getEmp_num() {
        return emp_num;
    }

    public void setEmp_num(int emp_num) {
        this.emp_num = emp_num;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime_in() {
        return time_in;
    }

    public void setTime_in(String time_in) {
        this.time_in = time_in;
    }

    public String getTime_out() {
        return time_out;
    }

    public void setTime_out(String time_out) {
        this.time_out = time_out;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
