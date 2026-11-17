package edu.utsa.cs3443.hotbirds.model;

public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;

    public User() {
        firstName = "";
        lastName = "";
        username = "";
        password = "";
        email = "";
    }

    public User(String firstName, String lastName, String username, String password, String email) {
        this.firstName = normalize(firstName);
        this.lastName = normalize(lastName);
        this.username = normalize(username);
        this.password = normalize(password);
        this.email = normalize(email);
    }

    public User(String username, String email, String password) {
        this("", "", username, password, email);
    }

    public String getName() {
        return (firstName + " " + lastName).trim();
    }

    public void setName(String name) {
        String normalizedName = normalize(name);
        int separator = normalizedName.indexOf(' ');
        if (separator < 0) {
            firstName = normalizedName;
            lastName = "";
        } else {
            firstName = normalizedName.substring(0, separator);
            lastName = normalizedName.substring(separator + 1).trim();
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = normalize(firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = normalize(lastName);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = normalize(username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = normalize(password);
    }

    // An account isn't usable without a password, so createUser checks this
    boolean hasPassword() {
        return !password.isBlank();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = normalize(email);
    }

    // Passwords are stored as plain text, so signing in is a straight comparison
    boolean passwordMatches(String candidate) {
        return candidate != null && password.equals(candidate);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
