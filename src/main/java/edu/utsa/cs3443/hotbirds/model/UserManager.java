package edu.utsa.cs3443.hotbirds.model;

import edu.utsa.cs3443.hotbirds.util.CsvUtil;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserManager {
    // Holder initialization provides a lazily created, thread-safe shared manager.
    private static final class SharedInstance {
        private static final UserManager INSTANCE = createDefaultManager();
    }

    // The CSV's first line; written back out every save so the file stays readable
    private static final String HEADER = "Username,First name,Last name,Email Address,Password";

    private File usersFile;
    private final List<User> users = new ArrayList<>();
    private String activeUser;

    public UserManager() {
        usersFile = defaultUsersFile().getAbsoluteFile();
        try {
            loadUsers();
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to load user accounts", exception);
        }
    }

    public UserManager(File usersFile) throws IOException {
        setUserFile(usersFile);
    }

    public static UserManager shared() {
        return SharedInstance.INSTANCE;
    }

    public synchronized boolean createUser(User user) throws IOException {
        Objects.requireNonNull(user, "user");
        if (user.getUsername().isBlank() || user.getEmail().isBlank()
                || !user.hasPassword() || findByUsername(user.getUsername()) != null
                || findByEmail(user.getEmail()) != null) {
            return false;
        }

        users.add(user);
        try {
            updateUserFile();
            return true;
        } catch (IOException exception) {
            // Keep memory consistent with disk if persistence fails.
            users.remove(user);
            throw exception;
        }
    }

    public synchronized boolean deleteUser(User user) throws IOException {
        int userIndex = users.indexOf(user);
        if (userIndex < 0) {
            return false;
        }

        users.remove(userIndex);
        String previousActiveUser = activeUser;
        if (user.getUsername().equalsIgnoreCase(activeUser)) {
            activeUser = null;
        }

        try {
            updateUserFile();
            return true;
        } catch (IOException exception) {
            // Restore both the account list and session if the file update fails.
            users.add(userIndex, user);
            activeUser = previousActiveUser;
            throw exception;
        }
    }

    public synchronized boolean login(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || password == null) {
            return false;
        }

        User user = findByUsername(usernameOrEmail);
        if (user == null) {
            user = findByEmail(usernameOrEmail);
        }
        if (user == null || !user.passwordMatches(password)) {
            return false;
        }

        activeUser = user.getUsername();
        return true;
    }

    public synchronized void logout() {
        activeUser = null;
    }

    public synchronized void setUserFile(File usersFile) throws IOException {
        File previousUsersFile = this.usersFile;
        this.usersFile = Objects.requireNonNull(usersFile, "usersFile")
                .getAbsoluteFile();
        try {
            loadUsers();
        } catch (IOException exception) {
            this.usersFile = previousUsersFile;
            throw exception;
        }
    }

    public synchronized File getUserFile() {
        return usersFile;
    }

    // Writes the header and every account back out to the CSV
    public synchronized void updateUserFile() throws IOException {
        Path destination = usersFile.toPath();
        Path parent = destination.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> rows = new ArrayList<>();
        rows.add(HEADER);
        for (User user : users) {
            rows.add(serialize(user));
        }
        Files.write(destination, rows, StandardCharsets.UTF_8);
    }

    public synchronized String getActiveUser() {
        return activeUser;
    }

    public synchronized User findUser(String usernameOrEmail) {
        User user = findByUsername(usernameOrEmail);
        return user == null ? findByEmail(usernameOrEmail) : user;
    }

    private void loadUsers() throws IOException {
        Path path = usersFile.toPath();
        // Build a complete replacement before changing the in-memory list, so malformed data is not partially loaded.
        List<User> loadedUsers = new ArrayList<>();
        if (Files.exists(path)) {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) { // line 0 is the header
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }

                List<String> fields = CsvUtil.split(line);
                if (fields.size() != 5) {
                    System.out.println("Skipping bad user row at line " + (i + 1));
                    continue;
                }
                try {
                    loadedUsers.add(readUser(fields));
                } catch (IllegalArgumentException exception) {
                    System.out.println("Skipping bad user row at line " + (i + 1));
                }
            }
        }

        users.clear();
        users.addAll(loadedUsers);
        activeUser = null;
    }

    // Turns one CSV row into a User. The columns are read in header order:
    // Username, First name, Last name, Email Address, Password.
    private static User readUser(List<String> fields) {
        String username = fields.get(0);
        String firstName = fields.get(1);
        String lastName = fields.get(2);
        String email = fields.get(3);
        String password = fields.get(4);

        return new User(firstName, lastName, username, password, email);
    }

    // The first account with this username, or null if nobody has it
    private User findByUsername(String username) {
        if (username == null) {
            return null;
        }
        String wanted = username.trim();
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(wanted)) {
                return user;
            }
        }
        return null;
    }

    // The first account with this email, or null if nobody has it
    private User findByEmail(String email) {
        if (email == null) {
            return null;
        }
        String wanted = email.trim();
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(wanted)) {
                return user;
            }
        }
        return null;
    }

    // One account as a CSV row, in the same column order as the header
    private static String serialize(User user) {
        return String.join(",",
                CsvUtil.quote(user.getUsername()), CsvUtil.quote(user.getFirstName()),
                CsvUtil.quote(user.getLastName()), CsvUtil.quote(user.getEmail()),
                CsvUtil.quote(user.getPassword()));
    }



    private static File defaultUsersFile() {
        // The project's own accounts file, so new sign-ups land where the rest of the data lives.
        return Path.of("data", "users.csv").toFile();
    }

    private static UserManager createDefaultManager() {
        return new UserManager();
    }
}
