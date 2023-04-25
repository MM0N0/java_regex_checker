package app;

import app.service.ServiceManager;
import app.service.DummyService;
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
                    DummyService.class
            ));
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException
                 | IllegalAccessException e) {
            logger.error("initServices failed", e);
            System.exit(1);
        }

        try {
            new CommandLine(new RegexCheckerCommand()).execute(args);
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
