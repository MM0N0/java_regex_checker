package app;

import app.commands.GoodByeCommand;
import app.commands.HelloCommand;
import app.commands.SubCommand;
import app.service.YamlService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import picocli.CommandLine;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class App {
    static Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        setVerboseModeIfInArgs(args);

        try {
            ServiceManager.initServices(List.of(
                    YamlService.class
            ));
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException
                 | IllegalAccessException e) {
            logger.error("initServices failed", e);
            System.exit(1);
        }

        try {
            new CommandLine(new RootCommand()).execute(args);
        } catch (Exception e) {
            logger.error("Command failed", e);
            System.exit(1);
        } finally {
            ServiceManager.deconstructServices();
        }

    }

    private static void setVerboseModeIfInArgs(String[] args) {
        List<String> argsList = Arrays.asList(args);
        boolean verbose = argsList.contains("-v") || argsList.contains("--verbose");
        if (verbose) {
            // set root Logger to DEBUG
            Configurator.setRootLevel(Level.DEBUG);
            logger.debug("verbose mode on");
        }
    }
}
@CommandLine.Command(name = "rootCMD", description = "test",
        subcommands = {HelloCommand.class, GoodByeCommand.class, SubCommand.class}
        , version = "test 0.1", mixinStandardHelpOptions = true)
class RootCommand implements Runnable {

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
}
