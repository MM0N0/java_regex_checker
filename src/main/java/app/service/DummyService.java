package app.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DummyService extends Service{

    static Logger logger = LogManager.getLogger(DummyService.class);

    public DummyService() {

    }


    public String getStuff() {
        logger.debug("running getStuff");
        return "service Stuff";
    }
}
