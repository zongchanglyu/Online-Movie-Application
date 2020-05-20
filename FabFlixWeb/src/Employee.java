/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class Employee {

    private String email;
    private String password;
    private String fullname;

    public Employee(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }
}
