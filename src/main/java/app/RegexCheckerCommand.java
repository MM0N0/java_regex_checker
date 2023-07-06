package app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@CommandLine.Command(name = "java-regex-checker", description = "TODO",
        subcommands = {}
        , version = "dev 0.1", mixinStandardHelpOptions = true)
class RegexCheckerCommand implements Runnable {
    static Logger logger = LogManager.getLogger(RegexCheckerCommand.class);

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "verbosity")
    boolean verbose;

    @CommandLine.Option(names = {"-l", "--loop"})
    boolean loop;

    @CommandLine.Option(names = {"-m", "--millis"}, defaultValue = "500")
    String millis;

    @CommandLine.Parameters(description = "path to the file to process")
    Path path;

    @Override
    public void run() {
        if(loop) {
            int waitTime;
            try {
                waitTime = Integer.parseInt(millis);
            } catch (NumberFormatException e) {
                logger.error("Error while parsing --millis", e);
                return;
            }
            while(true) {
                check_regex();
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    logger.error("Error while waiting", e);
                    return;
                }
            }
        } else {
            check_regex();
        }
    }

    private void check_regex() {
        // read file or die trying
        List<String> contentLines;
        //
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
        int separatorIndex = -1;
        //
        Pattern separatorPattern = Pattern.compile("^//$");
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
        //
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
        //
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
        //
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
    }
}
