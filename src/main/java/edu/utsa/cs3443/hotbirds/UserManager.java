package edu.utsa.cs3443.espi_test_project;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

public class UserManager {
    private static final class SharedInstance {
        private static final UserManager INSTANCE = createDefaultManager();
    }

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

        Path temporaryFile = Files.createTempFile(parent, "users-", ".tmp");
        try {
            List<String> rows = users.stream().map(UserManager::serialize).toList();
            Files.write(temporaryFile, rows, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
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
        List<User> loadedUsers = new ArrayList<>();
        if (Files.exists(path)) {
            int lineNumber = 0;
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }

                String[] fields = line.split("\\t", -1);
                if (fields.length != 5) {
                    throw new IOException("Invalid user record at line " + lineNumber);
                }
                try {
                    loadedUsers.add(User.fromStoredPassword(
                            decode(fields[1]), decode(fields[2]), decode(fields[0]),
                            decode(fields[4]), decode(fields[3])));
                } catch (IllegalArgumentException exception) {
                    throw new IOException("Invalid user record at line " + lineNumber, exception);
                }
            }
        }

        users.clear();
        users.addAll(loadedUsers);
        activeUser = null;
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
        return encode(user.getUsername()) + "\t"
                + encode(user.getFirstName()) + "\t"
                + encode(user.getLastName()) + "\t"
                + encode(user.getEmail()) + "\t"
                + encode(user.getPassword());
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private static File defaultUsersFile() {
        return Path.of(System.getProperty("user.home"), ".espi-hot-birds", "users.tsv").toFile();
    }

    private static UserManager createDefaultManager() {
        return new UserManager();
    }
}
