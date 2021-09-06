package ui.modules;

import domain.Author;
import domain.Country;
import domain.RightType;
import lombok.Getter;
import lombok.NonNull;
import persistence.base.Repository;
import ui.ApplicationOutput;
import ui.Command;
import ui.CommandStatus;
import ui.Module;

import java.util.ArrayList;

public class AuthorsModule implements Module {
    @NonNull
    @Getter
    private final ArrayList<Command> commands;

    public AuthorsModule(
        ApplicationOutput appOutput,
        Repository<Author> authorRepository
    ) {
        commands = new ArrayList<>();

        commands.add(new Command(
            "/authors/add", "<name> <country>",
            "Adds a new book author.",
            (String[] args) -> {
                var name = args[0];
                var country = args[1];

                try {
                    authorRepository.create(new Author(name, Country.valueOf(country)));
                } catch (Exception ex) {
                    appOutput.writeLine("Could not add new author: " + ex.getMessage());
                    return CommandStatus.FAIL;
                }

                return CommandStatus.SUCCESS;
            }, new RightType[]{RightType.MANAGE_BOOKS}
        ));
    }
}
