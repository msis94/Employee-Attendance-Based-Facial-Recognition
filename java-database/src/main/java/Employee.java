public class Employee
{
    private int emp_num;
    private String emp_name;
    private String emp_status;

    public Employee() {}

    public Employee(int emp_num, String emp_name, String emp_status) {
        this.emp_num = emp_num;
        this.emp_name = emp_name;
        this.emp_status = emp_status;
    }

    public int getEmp_num() {
        return emp_num;
    }

    public void setEmp_num(int emp_num) {
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

    @Override
    public String toString() {
        return "Employee{" +
                "emp_num=" + emp_num +
                ", emp_name='" + emp_name + '\'' +
                ", emp_status='" + emp_status + '\'' +
                '}';
    }
}
