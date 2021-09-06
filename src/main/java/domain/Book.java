package domain;

import domain.exceptions.BookNotAvailable;
import domain.exceptions.BookNotBorrowed;
import domain.exceptions.InvalidISBN;
import domain.exceptions.NotEnoughRights;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class Book {
    @NonNull
    private String name;
    @NonNull
    private String ISBN;
    @NonNull
    private String publishedAt;
    @NonNull
    private Boolean available;
    @NonNull
    private String borrowerName;

    @NonNull
    private String authorName;
    private Author author;

    @NonNull
    private String shelveName;
    private Shelve shelve;

    @NonNull
    private Integer pages;

    private Collection<Review> reviews;

    public Book(@NonNull String name, @NonNull String ISBN, @NonNull String publishedAt,
                @NonNull Boolean available, @NonNull String borrowerName, @NonNull String authorName,
                @NonNull String shelveName, @NonNull Integer pages) {
        this.name = name;
        this.ISBN = ISBN;
        this.publishedAt = publishedAt;
        this.available = available;
        this.borrowerName = borrowerName;
        this.authorName = authorName;
        this.shelveName = shelveName;
        this.pages = pages;
        this.reviews = new ArrayList<>();
    }

    public Book(@NonNull String name, @NonNull String ISBN,
                @NonNull String publishedAt, @NonNull String authorName,
                @NonNull String shelveName, @NonNull Integer pages) throws InvalidISBN {
        if (!isISBN13(ISBN)) {
            throw new InvalidISBN(ISBN);
        }

        this.name = name;
        this.ISBN = ISBN;
        this.publishedAt = publishedAt;
        this.authorName = authorName;
        this.available = true;
        this.borrowerName = "";
        this.shelveName = shelveName;
        this.pages = pages;
        this.reviews = new ArrayList<>();
    }

    public void borrowTo(User user) throws NotEnoughRights, BookNotAvailable {
        if (!user.hasRight(RightType.BORROW_BOOKS)) {
            throw new NotEnoughRights(RightType.BORROW_BOOKS);
        }

        if (!this.available) {
            throw new BookNotAvailable(this.name);
        }

        this.available = false;
        this.borrowerName = user.getName();
    }

    public void returnIt() throws BookNotBorrowed {
        if (this.available) {
            throw new BookNotBorrowed(this.name);
        }

        this.available = true;
        this.borrowerName = "";
    }

    private boolean isISBN13(String number) {
        int sum = 0;
        int multiple = 0;
        char ch = '\0';
        int digit = 0;

        for (int i = 1; i <= 13; i++) {

            if (i % 2 == 0)
                multiple = 3;
            else multiple = 1;

            ch = number.charAt(i - 1);
            digit = Character.getNumericValue(ch);
            sum += (multiple * digit);
        }

        return sum % 10 == 0;
    }
}
