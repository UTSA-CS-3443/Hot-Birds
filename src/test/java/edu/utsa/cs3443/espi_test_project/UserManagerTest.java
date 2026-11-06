package edu.utsa.cs3443.espi_test_project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserManagerTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void createsAndReloadsAccountAndAuthenticatesByUsernameOrEmail() throws IOException {
        Path usersFile = temporaryDirectory.resolve("accounts.tsv");
        UserManager manager = new UserManager(usersFile.toFile());
        User user = new User("Ada", "Lovelace", "ada", "analytical-engine", "ada@example.com");

        assertTrue(manager.createUser(user));
        assertNotEquals("analytical-engine", user.getPassword());
        assertFalse(Files.readString(usersFile).contains("analytical-engine"));

        UserManager reloadedManager = new UserManager(usersFile.toFile());
        assertTrue(reloadedManager.login("ADA", "analytical-engine"));
        assertEquals("ada", reloadedManager.getActiveUser());
        assertTrue(reloadedManager.login("ada@example.com", "analytical-engine"));
        assertFalse(reloadedManager.login("ada@example.com", "incorrect"));
        assertEquals("ada", reloadedManager.getActiveUser());
    }

    @Test
    void rejectsDuplicateAccountsAndSupportsLogoutAndDeletion() throws IOException {
        UserManager manager = new UserManager(temporaryDirectory.resolve("accounts.tsv").toFile());
        User original = new User("ada", "ada@example.com", "secret");
        User duplicate = new User("another", "ADA@example.com", "secret");

        assertTrue(manager.createUser(original));
        assertFalse(manager.createUser(duplicate));
        assertTrue(manager.login("ada", "secret"));
        manager.logout();
        assertNull(manager.getActiveUser());
        assertTrue(manager.login("ada", "secret"));
        assertTrue(manager.deleteUser(original));
        assertNull(manager.getActiveUser());
        assertNull(manager.findUser("ada"));
    }

    @Test
    void rejectsAccountsWithoutRequiredCredentials() throws IOException {
        UserManager manager = new UserManager(temporaryDirectory.resolve("accounts.tsv").toFile());

        assertFalse(manager.createUser(new User("", "ada@example.com", "")));
        assertFalse(manager.login("ada@example.com", ""));
        assertNull(manager.findUser("ada@example.com"));
    }

    @Test
    void invalidStoredRowsAreReported() throws IOException {
        Path usersFile = temporaryDirectory.resolve("accounts.tsv");
        Files.writeString(usersFile, "invalid\trow\n");

        IOException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IOException.class, () -> new UserManager(usersFile.toFile()));

        assertNotNull(exception.getMessage());
    }

    @Test
    void failedUserFileSwitchPreservesCurrentAccounts() throws IOException {
        Path validFile = temporaryDirectory.resolve("valid.tsv");
        UserManager manager = new UserManager(validFile.toFile());
        User user = new User("ada", "ada@example.com", "secret");
        assertTrue(manager.createUser(user));

        Path invalidFile = temporaryDirectory.resolve("invalid.tsv");
        Files.writeString(invalidFile, "invalid\trow\n");

        org.junit.jupiter.api.Assertions.assertThrows(
                IOException.class, () -> manager.setUserFile(invalidFile.toFile()));

        assertEquals(validFile.toFile().getAbsoluteFile(), manager.getUserFile());
        assertNotNull(manager.findUser("ada"));
    }
}
