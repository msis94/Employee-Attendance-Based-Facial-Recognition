import java.util.Date;

public class MongoAttendance
{
    private int emp_num;
    private Date date;
    private Date time_in;
    private Date time_out;
    private String remark;

    public MongoAttendance() {}

    public MongoAttendance(int emp_num, Date date, Date time_in, Date time_out, String remark) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTime_in() {
        return time_in;
    }

    public void setTime_in(Date time_in) {
        this.time_in = time_in;
    }

    public Date getTime_out() {
        return time_out;
    }

    public void setTime_out(Date time_out) {
        this.time_out = time_out;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "MongoAttendance{" +
                "emp_num=" + emp_num +
                ", date=" + date +
                ", time_in=" + time_in +
                ", time_out=" + time_out +
                ", remark='" + remark + '\'' +
                '}';
    }
}
