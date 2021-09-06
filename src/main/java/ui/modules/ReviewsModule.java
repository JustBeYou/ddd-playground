package ui.modules;

import domain.Book;
import domain.Comment;
import domain.Review;
import domain.RightType;
import domain.exceptions.InvalidRating;
import lombok.Getter;
import lombok.NonNull;
import persistence.base.Repository;
import persistence.base.exceptions.CreationException;
import persistence.base.exceptions.InvalidQueryOperation;
import persistence.base.queries.QueryFactory;
import persistence.base.queries.QueryOperation;
import services.SessionStatefulService;
import ui.ApplicationOutput;
import ui.Command;
import ui.CommandStatus;
import ui.Module;

import java.util.ArrayList;

public class ReviewsModule implements Module {
    @NonNull
    @Getter
    private final ArrayList<Command> commands;

    public ReviewsModule(
        ApplicationOutput appOutput,
        SessionStatefulService sessionStatefulService,
        Repository<Review> reviewRepository,
        Repository<Comment> commentRepository,
        Repository<Book> bookRepository
    ) {
        commands = new ArrayList<>();

        commands.add(new Command(
            "/books/reviews/add", "<book> <rating> <text>",
            "Add a review to a read book.",
            args -> {
                var bookName = args[0];
                var rating = Integer.valueOf(args[1]);
                var text = args[2];

                var foundBook = bookRepository.findOne(
                    new QueryFactory().buildSimpleQuery(
                        "name", bookName, QueryOperation.IsSame
                    )
                );
                if (foundBook.isEmpty()) {
                    appOutput.writeLine("Book not found.");
                    return CommandStatus.FAIL;
                }

                try {
                    reviewRepository.create(
                        new Review(rating, text, bookName, sessionStatefulService.getCurrentUser().getName())
                    );
                    appOutput.writeLine("Successfully added rating.");
                } catch (CreationException e) {
                    appOutput.writeLine("Couldn't add review: " + e.getMessage());
                    return CommandStatus.FAIL;
                } catch (InvalidRating invalidRating) {
                    appOutput.writeLine("Rating is invalid: " + invalidRating.getMessage());
                    return CommandStatus.FAIL;
                }

                return CommandStatus.SUCCESS;
            }, new RightType[]{}
        ));

        commands.add(new Command(
            "/books/reviews/list", "<book>",
            "List reviews for a book.",
            args -> {
                var bookName = args[0];

                var foundBook = bookRepository.findOne(
                    new QueryFactory().buildSimpleQuery(
                        "name", bookName, QueryOperation.IsSame
                    )
                );
                if (foundBook.isEmpty()) {
                    appOutput.writeLine("Book not found.");
                    return CommandStatus.FAIL;
                }

                try {
                    var reviews = reviewRepository.find(
                        new QueryFactory().buildSimpleQuery(
                            "bookName", bookName, QueryOperation.IsSame
                        )
                    );

                    appOutput.writeLine("Reviews for " + bookName + ":");
                    for (var review: reviews) {
                        appOutput.writeLine(
                            "- #" + review.getId() + " "
                                + review.getData().getUserName() + " " +
                                review.getData().getRating() + "/5 " +
                                review.getData().getText()
                        );
                    }
                } catch (Exception e) {
                    appOutput.writeLine("Couldn't search reviews: " + e.getMessage());
                    return CommandStatus.FAIL;
                }

                return CommandStatus.SUCCESS;
            }, new RightType[]{}
        ));

        commands.add(new Command(
            "/books/reviews/comments/add", "<id> <text>",
            "Add a comment to a review.",
            args -> {
                var id = Integer.valueOf(args[0]);
                var text = args[1];

                var foundReview = reviewRepository.findById(id);
                if (foundReview.isEmpty()) {
                    appOutput.writeLine("Could not find review.");
                    return CommandStatus.FAIL;
                }

                try {
                    commentRepository.create(
                        new Comment(text, id, sessionStatefulService.getCurrentUser().getName())
                    );
                    appOutput.writeLine("Successfully added comment.");
                } catch (CreationException e) {
                    appOutput.writeLine("Could not add comment: " + e.getMessage());
                    return CommandStatus.FAIL;
                }

                return CommandStatus.SUCCESS;
            }, new RightType[]{}
        ));

        commands.add(new Command(
            "/books/reviews/show", "<id>",
            "Display a review and its comments.",
            args -> {
                var id = Integer.valueOf(args[0]);

                var foundReview = reviewRepository.findById(id);
                if (foundReview.isEmpty()) {
                    appOutput.writeLine("Could not find review.");
                    return CommandStatus.FAIL;
                }

                var review = foundReview.get();
                appOutput.writeLine(
                    "*** #" + review.getId() + " "
                        + review.getData().getUserName() + " " +
                        review.getData().getRating() + "/5 " +
                        review.getData().getText()
                );

                try {
                    reviewRepository.loadRelations(review);
                    for (var comment: review.getData().getComments()) {
                        appOutput.writeLine(
                            comment.getCommenterName() + ": " +
                                comment.getText()
                        );
                    }
                } catch (Exception e) {
                    appOutput.writeLine("Couldn't load comments: " + e.getMessage());
                    return CommandStatus.FAIL;
                }

                return CommandStatus.SUCCESS;
            }, new RightType[]{}
        ));
    }
}
