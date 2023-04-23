package app;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexSnippet implements Runnable {
    @Override
    public void run(){

        String string = "</script>\n" +
                "<li id=\"showview_videos_media_721469\" class=\"hover-bubble group-item\">\n" +
                "  <div class=\"wrapper container-shadow hover-classes\" data-classes=\"container-shadow-dark\">\n" +
        "<a href=\"/de/cheating-craft/episode-4-attention-please-play-in-the-sky-with-ca-3-721469\" title=\"Cheating Craft Folge 4\"\n" +
                "       class=\"portrait-element block-link titlefix episode\">\n" +
                "              <img alt=\"Attention Please! Vergnügen mit CA in den Lüften &lt;3\" src=\"https://img1.ak.crunchyroll.com/i/spire4-tmb/be5a4012ea5236badfb89001d5e434561477423723_wide.jpg\" class=\"landscape\"/>\n" +
                "            <div class=\"episode-progress-bar\">\n" +
                "        <div class=\"episode-progress\" media_id=\"721469\"\n" +
                "             style=\"width: 0%;\"></div>\n" +
                "      </div>";

        Pattern r = Pattern.compile("(.*href=\")(.*)(\" title.*)");
        Arrays.stream(string.split("\n"))
                .filter(s->s.contains("episode-"))
                .filter(s->s.contains("href"))
                .map(s->{
                    Matcher m = r.matcher(s);
                    if (m.find()){
                        String urlFrag = m.group(2);
                        return "youtube-dl -v --embed-subs --write-sub --sub-lang deDE https://www.crunchyroll.com"+urlFrag+" -o "+urlFrag.substring(urlFrag.lastIndexOf("/")).replace("/","")+".mp4;";
                    }
                    return "";
                })
                .filter(s->!s.isEmpty())
                .collect(Collectors.toCollection(LinkedList::new))
                .descendingIterator()
                .forEachRemaining(System.out::println);

        Pattern pattern = Pattern.compile(".*title.*");
        Arrays.stream(string.split("\n"))
                .filter(s -> pattern.matcher(s).matches())
                .filter(s->s.matches(".*title.*"))
                .forEach(s -> System.out.println("!!! "+s+" !!!"));
    }
}
