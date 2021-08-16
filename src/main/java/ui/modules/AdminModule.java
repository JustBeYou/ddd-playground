package ui.modules;

import domain.Right;
import domain.RightType;
import domain.User;
import lombok.Getter;
import lombok.NonNull;
import persistence.base.Repository;
import persistence.base.exceptions.CreationException;
import persistence.base.queries.Query;
import persistence.base.queries.QueryFactory;
import persistence.base.queries.QueryOperation;
import services.SessionStatefulService;
import ui.ApplicationOutput;
import ui.Command;
import ui.CommandStatus;
import ui.Module;

import java.util.ArrayList;

public class AdminModule implements Module {
    @NonNull
    @Getter
    private final ArrayList<Command> commands;

    public AdminModule(
        ApplicationOutput appOutput,
        Repository<User> userRepository,
        Repository<Right> rightRepository
    ) {
        this.commands = new ArrayList<>();

        this.commands.add(new Command(
           "/admin/users/list", "",
           "Lists all users.",
           args -> {
               try {
                   var users = userRepository.find(new Query());
                   for (var user: users) {
                       userRepository.loadRelations(user);
                       appOutput.write("User: " + user.getData().getName() + " Email: " +
                           user.getData().getEmail() + " Rights: ");
                       for (var right: user.getData().getRights()) {
                           appOutput.write(right.getType().toString() + " ");
                       }
                       appOutput.writeLine("");
                   }
               } catch (Exception ex) {
                   appOutput.writeLine("Something went wrong: " + ex.toString());
                   return CommandStatus.FAIL;
               }

               return CommandStatus.SUCCESS;
           }, new RightType[]{RightType.MANAGE_USERS}
        ));

        this.commands.add(new Command(
           "/admin/users/add_right", "<user> <right>",
           "Adds a new right to a specified user.",
           args -> {
               var userName = args[0];
               var rightStr = args[1];

               RightType newRightType;
               try {
                   newRightType = RightType.valueOf(rightStr);
               } catch (Exception ignored) {
                   appOutput.writeLine("Unknown right type.");
                   return CommandStatus.FAIL;
               }

               var foundUser = userRepository.findOne(
                   new QueryFactory().buildSimpleQuery(
                       "name", userName, QueryOperation.IsSame
                   )
               );

               if (foundUser.isEmpty()) {
                   appOutput.writeLine("User not found.");
                   return CommandStatus.FAIL;
               }

               var user = foundUser.get();
               try {
                   userRepository.loadRelations(user);
               } catch (Exception ex) {
                   appOutput.writeLine("Failed loading rights: " + ex.getMessage());
               }
               if (user.getData().hasRight(newRightType)) {
                   appOutput.writeLine("User already has this right.");
                   return CommandStatus.FAIL;
               }

               try {
                   rightRepository.create(new Right(newRightType, user.getData().getName()));
               } catch (CreationException e) {
                   appOutput.writeLine("Couldn't create right: " + e.getMessage());
                   return CommandStatus.FAIL;
               }

               return CommandStatus.SUCCESS;
           }, new RightType[]{RightType.MANAGE_USERS}
        ));
    }
}
