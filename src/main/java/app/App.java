package app;

import app.service.YamlService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import picocli.CommandLine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

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
@CommandLine.Command(name = "java-regex-checker", description = "TODO",
        subcommands = {}
        , version = "dev 0.1", mixinStandardHelpOptions = true)
class RootCommand implements Runnable {
    static Logger logger = LogManager.getLogger(RootCommand.class);

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "TODO")
    boolean verbose;

    @CommandLine.Parameters(description = "TODO")
    Path path;

    @Override
    public void run() {
        // read file or die trying
        List<String> contentLines;
        try {
            contentLines = Files.readAllLines(path);
        } catch (IOException e) {
            logger.error("Error while reading File", e);
            return;
        }

        // remove empty lines
        contentLines = contentLines.stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // find separator
        Pattern separatorPattern = Pattern.compile("^//$");
        int separatorIndex = -1;
        for (int i = 0; i < contentLines.size(); i++) {
            String line = contentLines.get(i);
            if (separatorPattern.matcher(line).find()) {
                separatorIndex = i;
                break;
            }
        }
        if (separatorIndex == -1) {
            logger.error("no separator found");
            return;
        }
        if (separatorIndex+1 == contentLines.size()) {
            logger.error("separator at the end of the file");
            return;
        }

        // split patternLines from example
        List<String> patternLines = contentLines.subList(0, separatorIndex);
        List<String> exampleLines = contentLines.subList(separatorIndex+1, contentLines.size());
        logger.debug("splitting done:");
        logger.debug("patternLines:");
        patternLines.forEach(logger::debug);
        logger.debug("exampleLines:");
        exampleLines.forEach(logger::debug);

        // filter patternLines and get patternString
        Pattern commentedPattern = Pattern.compile("^//.+$");
        patternLines = patternLines.stream()
                .filter(line -> !commentedPattern.matcher(line).find())
                .collect(Collectors.toList());
        if(patternLines.size()==0){
            logger.error("found no patterns");
            return;
        } else if (patternLines.size()>1) {
            logger.error("found more than one pattern:");
            patternLines.forEach(logger::info);
            return;
        }

        // sanitise patternString
        String patternString = patternLines.get(0);
        Pattern pattern;
        try {
            pattern = Pattern.compile(patternString);
        } catch (PatternSyntaxException patternSyntaxException) {
            logger.error("Error building Pattern.", patternSyntaxException);
            logger.info(String.format("pattern: %s", patternString));
            return;
        }

        // output pattern
        System.out.println(String.format("pattern: %s", patternString));
        System.out.println();

        // check pattern on examples
        List<String> matchedExampleStrings = exampleLines.stream()
                .filter(example -> pattern.matcher(example).find())
                .collect(Collectors.toList());
        List<String> notMatchedExamples = exampleLines.stream()
                .filter(example -> !pattern.matcher(example).find())
                .collect(Collectors.toList());

        // analyse results
        List<HashMap<String, Object>> matchedExample =
                matchedExampleStrings.stream().map(matchedLine -> {
                    HashMap<String, Object> attributes = new HashMap<>();
                    attributes.put("matchedLine", matchedLine);

                    List<String> groups = new ArrayList<>();
                    Matcher matcher = pattern.matcher(matchedLine);
                    matcher.find();

                    for (int i = 0; i < matcher.groupCount(); i++) {
                        String group = matcher.group(i+1);
                        groups.add(group);
                    }
                    attributes.put("groups", groups);

                    return attributes;
                }).collect(Collectors.toList());


        // output results
        System.out.println("matchedExamples:");
        matchedExample.forEach(attributes -> {
            String matchedLine = (String) attributes.get("matchedLine");
            List<String> groups = (List<String>) attributes.get("groups");

            String groupsString = String.join(", ", groups);

            System.out.println(String.format("%s --> groups: %s", matchedLine, groupsString));
        });
        System.out.println();
        System.out.println("notMatchedExamples:");
        notMatchedExamples.forEach(System.out::println);
        System.out.println();


        // Documentation: https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
    }
}
