import java.sql.Date;

class Attendance
{
    private int emp_num;
    private String date;
    private String time_in;
    private String time_out;

    public Attendance(){}

    public Attendance(int emp_num, String date, String time_in, String time_out) {
        this.emp_num = emp_num;
        this.date = date;
        this.time_in = time_in;
        this.time_out = time_out;
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

    @Override
    public String toString() {
        return "Attendance{" +
                "emp_num=" + emp_num +
                ", date='" + date + '\'' +
                ", time_in='" + time_in + '\'' +
                ", time_out='" + time_out + '\'' +
                '}';
    }
}
