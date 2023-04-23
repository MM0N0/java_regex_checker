package app.commands;

import app.ServiceManager;
import app.service.Service;
import app.service.YamlService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.util.List;


@CommandLine.Command(name = "sub", synopsisSubcommandLabel = "COMMAND")
public class SubCommand implements Runnable{

    static Logger logger = LogManager.getLogger(SubCommand.class);

    @CommandLine.Option(names = {"-y", "--yaml"}, required = false, description = "Yaml DataModel.")
    private List<String> yaml;

    @CommandLine.Option(names = {"-p", "--properties"}, required = false, description = "prop1:{}|[]|v1;prop2:val2")
    private String properties;

    @CommandLine.Parameters(paramLabel = "ftl")
    private String ftl;

    private final YamlService yamlService;

    public SubCommand() {
        yamlService = ServiceManager.getService(YamlService.class);
    }

    @Override
    public void run() {
        String stuff = yamlService.getStuff();
        System.out.println("done stuff: " + stuff);

        System.out.println("Subcommand!");
    }
}
