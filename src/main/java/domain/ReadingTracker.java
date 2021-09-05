package domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class ReadingTracker {
    @NonNull
    private Boolean started;

    @NonNull
    private Boolean finished;

    @NonNull
    private Integer page;

    @NonNull
    private String userName;

    @NonNull
    private String bookName;
    private Book book;

    public ReadingTracker(@NonNull Boolean started, @NonNull Boolean finished, @NonNull Integer page, @NonNull String userName, @NonNull String bookName) {
        this.started = started;
        this.finished = finished;
        this.page = page;
        this.userName = userName;
        this.bookName = bookName;
    }

    public ReadingTracker(@NonNull String userName, @NonNull String bookName) {
        this.started = false;
        this.finished = false;
        this.page = 0;
        this.userName = userName;
        this.bookName = bookName;
    }

    public void readPages(Integer readPages) {
        this.page = Math.min(this.page + readPages, this.book.getPages());
        if (this.page > 0) {
            this.started = true;
        }
        if (this.page.equals(this.book.getPages())) {
            this.finished = true;
        }
    }

    public void setPage(Integer page) {
        if (page <= 0) {
            this.page = 0;
            this.started = false;
            this.finished = false;
        } else if (page >= this.book.getPages()) {
            this.page = this.book.getPages();
            this.started = true;
            this.finished = true;
        } else {
            this.page = page;
            this.started = true;
        }
    }
}
