import persistence.models.InMemoryRepositoryFactory;
import services.CsvLoggerService;
import ui.Application;
import ui.ApplicationConsoleOutput;
import ui.ApplicationKeyboardInput;

public class Books {
    public static void main(String[] args) {
        var app = new Application(
            new ApplicationKeyboardInput(),
            new ApplicationConsoleOutput(),
            new InMemoryRepositoryFactory(),
            new CsvLoggerService("log.csv")
        );
        app.run();
    }
}