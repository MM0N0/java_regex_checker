package app.service;

import app.App;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YamlService extends Service{

    static Logger logger = LogManager.getLogger(YamlService.class);

    public YamlService() {

    }


    public String getStuff() {
        logger.debug("running getStuff");
        return "service Stuff";
    }
}
