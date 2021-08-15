import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import persistence.models.InMemoryRepositoryFactory;
import ui.*;

public class ApplicationTest {
    String runApp(String[] commands) {
        var appInput = new ApplicationStringInput();
        for (var command: commands) {
            appInput.feed(command);
        }
        appInput.feed("exit");
        var appOutput = new ApplicationStringOutput();
        var app = new Application(
            appInput,
            appOutput,
            new InMemoryRepositoryFactory()
        );
        app.run();
        return appOutput.dump();
    }

    @Test
    void shouldLoginAsAdmin() {
        var output = runApp(new String[]{
            "/login admin admin"
        });

        assertTrue(output.contains("Logged in as admin"));
    }

    @Test
    void shouldRegisterANewUser() {
        var output = runApp(new String[]{
            "/register test test test@test.com",
            "/login test test"
        });

        assertTrue(output.contains("Logged in as test"));
    }

    @Test
    void shouldAddAndFindNewBook() {
        var output = runApp(new String[]{
            "/login admin admin",
            "/authors/add test-author Romania",
            "/books/add test-book 9784607084182 13-May-1990 test-author",
            "/books/search test-book"
        });

        assertTrue(output.contains("Found the following books:\nBook(name=test-book"));
    }
}
