package ui.modules;

import auth.InvalidLogin;
import domain.RightType;
import lombok.Getter;
import lombok.NonNull;
import persistence.base.exceptions.CreationException;
import services.AuthService;
import services.SessionStatefulService;
import ui.ApplicationOutput;
import ui.Command;
import ui.CommandStatus;
import ui.Module;

import java.util.ArrayList;

public class AuthModule implements Module {
    @NonNull @Getter
    private final ArrayList<Command> commands;

    public AuthModule(
        ApplicationOutput appOutput,
        SessionStatefulService sessionService,
        AuthService authService
    ) {
        commands = new ArrayList<>();

        commands.add(new Command(
            "/login", "<user> <password>",
            "Creates a new session.",
            (String[] args) -> {
                var user = args[0];
                var password = args[1];

                try {
                    sessionService.login(user, password);
                } catch (InvalidLogin ex) {
                    appOutput.writeLine("Invalid login: " + ex.getMessage());
                    return CommandStatus.FAIL;
                }
                return CommandStatus.SUCCESS;
            }, null));

        commands.add(new Command("/logout", "",
            "Deletes the current session.",
            (String[] args) -> {
                sessionService.logout();
                return CommandStatus.SUCCESS;
            }, new RightType[]{}));

        commands.add(new Command("/register", "<user> <password> <email>",
            "Registers a new reader user.",
            (String[] args) -> {
                var user = args[0];
                var password = args[1];
                var email = args[2];

                try {
                    authService.register(user, password, email);
                } catch (CreationException ex) {
                    appOutput.writeLine("Couldn't register user: " + ex.getMessage());
                    return CommandStatus.FAIL;
                }
                return CommandStatus.SUCCESS;
            }, null));
    }
}
