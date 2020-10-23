public class Rabbit
{
    private String message;

    public Rabbit()
    {}

    public Rabbit(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Rabbit{" +
                "message='" + message + '\'' +
                '}';
    }
}
