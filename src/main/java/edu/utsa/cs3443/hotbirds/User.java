package edu.utsa.cs3443.espi_test_project;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

public class User {
    private static final String PASSWORD_PREFIX = "$pbkdf2-sha256$";
    private static final int PASSWORD_ITERATIONS = 210_000;
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 256;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private boolean passwordSet;

    public User() {
        firstName = "";
        lastName = "";
        username = "";
        password = "";
        email = "";
    }

    public User(String firstName, String lastName, String username, String password, String email) {
        this(firstName, lastName, username, password, email, false);
    }

    private User(String firstName, String lastName, String username,
                 String password, String email, boolean storedPassword) {
        this.firstName = normalize(firstName);
        this.lastName = normalize(lastName);
        this.username = normalize(username);
        this.email = normalize(email);
        if (storedPassword) {
            this.password = password;
            this.passwordSet = password != null && !password.isBlank();
        } else {
            setPassword(password);
        }
    }

    public User(String username, String email, String password) {
        this("", "", username, password, email);
    }

    static User fromStoredPassword(String firstName, String lastName, String username,
                                   String passwordHash, String email) {
        if (!isValidPasswordHash(passwordHash)) {
            throw new IllegalArgumentException("Invalid stored password hash");
        }
        return new User(firstName, lastName, username, passwordHash, email, true);
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
        String value = Objects.requireNonNull(password, "password");
        this.password = hashPassword(value);
        this.passwordSet = !value.isBlank();
    }

    boolean hasPassword() {
        return passwordSet;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = normalize(email);
    }

    boolean passwordMatches(String candidate) {
        if (candidate == null || password == null || !password.startsWith(PASSWORD_PREFIX)) {
            return false;
        }

        String[] parts = password.substring(PASSWORD_PREFIX.length()).split("\\$", -1);
        if (parts.length != 3) {
            return false;
        }

        try {
            int iterations = Integer.parseInt(parts[0]);
            if (iterations != PASSWORD_ITERATIONS) {
                return false;
            }
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[2]);
            if (salt.length != SALT_LENGTH || expectedHash.length != HASH_LENGTH / 8) {
                return false;
            }
            byte[] actualHash = derivePassword(candidate.toCharArray(), salt, iterations);
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static String hashPassword(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        byte[] hash = derivePassword(password.toCharArray(), salt, PASSWORD_ITERATIONS);
        return PASSWORD_PREFIX + PASSWORD_ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    private static boolean isValidPasswordHash(String passwordHash) {
        if (passwordHash == null || !passwordHash.startsWith(PASSWORD_PREFIX)) {
            return false;
        }
        String[] parts = passwordHash.substring(PASSWORD_PREFIX.length()).split("\\$", -1);
        if (parts.length != 3) {
            return false;
        }

        try {
            return Integer.parseInt(parts[0]) == PASSWORD_ITERATIONS
                    && Base64.getDecoder().decode(parts[1]).length == SALT_LENGTH
                    && Base64.getDecoder().decode(parts[2]).length == HASH_LENGTH / 8;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static byte[] derivePassword(char[] password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, HASH_LENGTH);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to securely process the password", exception);
        } finally {
            spec.clearPassword();
            Arrays.fill(password, '\0');
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
