package edu.utsa.cs3443.hotbirds.model;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
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
    private static final String HASH_PREFIX = "$pbkdf2-sha256$";

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

    public synchronized void updateUserFile() throws IOException {
        Path destination = usersFile.toPath();
        Path parent = destination.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        // Write the complete account list first, then replace the destination to avoid partial files.
        Path temporaryFile = Files.createTempFile(parent, "users-", ".tmp");
        try {
            List<String> rows = new ArrayList<>();
            rows.add(HEADER);
            users.stream().map(UserManager::serialize).forEach(rows::add);
            Files.write(temporaryFile, rows, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                // Some filesystems cannot atomically move files; still replace the destination.
                Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
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

                List<String> fields = splitCsv(line);
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

     //Turns one CSV row into a User. A password already saved as a hash is kept as is;
     //one still written in plain text is hashed now and saved that way from the next write on.
    private static User readUser(List<String> fields) {
        String username = fields.get(0);
        String firstName = fields.get(1);
        String lastName = fields.get(2);
        String email = fields.get(3);
        String password = fields.get(4);

        if (password.startsWith(HASH_PREFIX)) {
            return User.fromStoredPassword(firstName, lastName, username, password, email);
        }
        return new User(firstName, lastName, username, password, email);
    }

    private User findByUsername(String username) {
        if (username == null) {
            return null;
        }
        return users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username.trim()))
                .findFirst()
                .orElse(null);
    }

    private User findByEmail(String email) {
        if (email == null) {
            return null;
        }
        return users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst()
                .orElse(null);
    }

    private static String serialize(User user) {
        return String.join(",",
                csv(user.getUsername()), csv(user.getFirstName()), csv(user.getLastName()),
                csv(user.getEmail()), csv(user.getPassword()));
    }

    // Wraps a value in quotes only if it contains a comma or a quote of its own
    private static String csv(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // Splits one CSV line, keeping commas that sit inside quotes
    private static List<String> splitCsv(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        out.add(sb.toString().trim());
        return out;
    }

    private static File defaultUsersFile() {
        // The project's own accounts file, so new sign-ups land where the rest of the data lives.
        return Path.of("data", "users.csv").toFile();
    }

    private static UserManager createDefaultManager() {
        return new UserManager();
    }
}
