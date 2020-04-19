/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class Customer {

    private int id;
    private String firstName;
    private String lastName;
    private String ccId;
    private String address;
    private String email;
    private String password;
//    private final String username;

    public Customer(int id, String firstName, String lastName, String ccId,
                    String address, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ccId = ccId;
        this.address = address;
        this.email = email;
        this.password = password;
    }


}
