package common;

public class User {
    private final String username;
    private final String password;
    private final String fullName;
    private final int customerNo;

    public User(String username, String password, String fullName, int customerNo) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.customerNo = customerNo;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public int getCustomerNo() {
        return customerNo;
    }
}

