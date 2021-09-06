import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.drivers.inmemory.InMemoryStore;
import persistence.models.InMemoryRepositoryFactory;
import services.FakeLoggerService;
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
            new InMemoryRepositoryFactory(),
            new FakeLoggerService()
        );
        app.run();
        return appOutput.dump();
    }

    @BeforeEach
    void setupTest() {
        InMemoryStore.destroy();
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
            "/books/add test-book 9784607084182 13-May-1990 test-author default-shelve 100",
            "/books/search test-book"
        });

        assertTrue(output.contains("Found the following books:\nBook(name=test-book"));
    }

    @Test
    void shouldListUsers() {
        var output = runApp(new String[]{
            "/login admin admin",
            "/admin/users/list"
        });

        assertTrue(output.contains("User: admin Email: admin@email.com Rights: MANAGE_BOOKS MANAGE_USERS"));
    }

    @Test
    void shouldAddNewRight() {
        var output = runApp(new String[]{
           "/register test test test@test.com",
           "/login admin admin",
            "/admin/users/add_right not_existent MANAGE_USERS",
            "/admin/users/add_right test NOT_EXISTENT",
            "/admin/users/add_right test MANAGE_USERS",
            "/admin/users/add_right test MANAGE_USERS",
            "/admin/users/list"
        });

        assertTrue(output.contains("User not found."));
        assertTrue(output.contains("Unknown right type."));
        assertTrue(output.contains("User already has this right."));
        assertTrue(output.contains("User: test Email: test@test.com Rights: BORROW_BOOKS MANAGE_USERS"));
    }

    @Test
    void shouldBorrowAndReturnBook() {
        var output = runApp(new String[]{
            "/login admin admin",
            "/books/borrow default-book",
            "/books/return default-book",
            "/books/borrow default-book"
        });

        assertTrue(output.contains("You borrowed default-book"));
        assertTrue(output.contains("You returned default-book"));
        assertFalse(output.contains("The book is not available for borrowing"));
    }

    @Test
    void shouldCreateShelve() {
        var output = runApp(new String[] {
           "/login admin admin",
            "/books/shelve/add romance",
            "/books/shelve/list_books romance",
        });

        assertTrue(output.contains("Created new shelve romance"));
        assertTrue(output.contains("The shelve is empty"));
    }

    @Test
    void shouldMoveBookToShelve() {
        var output = runApp(new String[] {
            "/login admin admin",
            "/books/shelve/add romance",
            "/books/shelve/add sf",
            "/authors/add test-author Romania",
            "/books/add test-book 9784607084182 13-May-1990 test-author romance 100",
            "/books/shelve/move_book sf test-book",
            "/books/shelve/list_books sf"
        });

        assertTrue(output.contains("Books in sf:\nBook(name=test-book"));
    }

    @Test
    void shouldAddBookToToRead() {
        var output = runApp(new String[] {
            "/login admin admin",
            "/books/to_read/add default-book",
            "/books/to_read/list",
        });

        assertTrue(output.contains("Books in to-read list:\nBook(name=default-book"));
    }

    @Test
    void shouldReadPages() {
        var output = runApp(new String[] {
            "/login admin admin",
            "/books/to_read/add default-book",
            "/books/read_pages default-book 50",
            "/books/reading/list"
        });

        assertTrue(output.contains("Books in reading list:\nBook(name=default-book"));
        assertTrue(output.contains("50/100"));
    }

    @Test
    void shouldFinishBook() {
        var output = runApp(new String[] {
            "/login admin admin",
            "/books/to_read/add default-book",
            "/books/read_pages default-book 999",
            "/books/read/list"
        });

        assertTrue(output.contains("Books in read list:\nBook(name=default-book"));
    }

    @Test
    void shouldAddReviewToBook() {
        var output = runApp(new String[] {
            "/login admin admin",
            "/books/to_read/add default-book",
            "/books/read_pages default-book 200",
            "/books/reviews/add default-book 3 This-is-a-review-text",
            "/books/reviews/list default-book"
        });

        assertTrue(output.contains("Reviews for default-book:\n- #1 admin 3/5 This-is-a-review-text"));
    }

    @Test
    void shouldAddCommentToReview() {
        var output = runApp(new String[] {
            "/login admin admin",
            "/books/to_read/add default-book",
            "/books/read_pages default-book 200",
            "/books/reviews/add default-book 3 This-is-a-review-text",
            "/books/reviews/comments/add 1 This-is-a-comment",
            "/books/reviews/show 1",
        });

        assertTrue(output.contains("admin: This-is-a-comment"));
    }
}
