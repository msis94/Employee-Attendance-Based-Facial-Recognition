package sample;

public class Employee
{
    private String emp_name;
    private String emp_status;
    private int emp_num;

    public Employee()
    {}

    public Employee(String emp_name, String emp_status, int emp_num) {
        this.emp_name = emp_name;
        this.emp_status = emp_status;
        this.emp_num = emp_num;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getEmp_status() {
        return emp_status;
    }

    public void setEmp_status(String emp_status) {
        this.emp_status = emp_status;
    }

    public int getEmp_num() {
        return emp_num;
    }

    public void setEmp_num(int emp_num) {
        this.emp_num = emp_num;
    }

}
