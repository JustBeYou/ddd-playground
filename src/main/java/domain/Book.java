package domain;

import domain.exceptions.InvalidISBN;
import lombok.Data;
import lombok.NonNull;

@Data
public class Book {
    @NonNull
    private String name;
    @NonNull
    private String ISBN;
    @NonNull
    private String publishedAt;

    @NonNull
    private String authorName;
    private Author author;

    public Book(@NonNull String name, @NonNull String ISBN, @NonNull String publishedAt, @NonNull String authorName) throws InvalidISBN {
        if (!isISBN13(ISBN)) {
            throw new InvalidISBN(ISBN);
        }

        this.name = name;
        this.ISBN = ISBN;
        this.publishedAt = publishedAt;
        this.authorName = authorName;
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
