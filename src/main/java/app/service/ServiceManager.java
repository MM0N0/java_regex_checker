package app.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

public class ServiceManager {
    static Logger logger = LogManager.getLogger(ServiceManager.class);
    private static HashMap<Class<? extends Service>, Object> services;

    @SuppressWarnings("unchecked")
    public static <T extends Service> T getService(Class<T> serviceClass) throws CanNotProvideServiceException {
        T requestedService = (T) services.get(serviceClass);
        if(requestedService==null) {
            throw new CanNotProvideServiceException();
        }
        return requestedService;
    }

    public static void initServices(List<Class<? extends Service>> servicesToInit) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        services = new HashMap<>();
        for (Class<? extends Service> serviceClass : servicesToInit) {
            addService(serviceClass);
        }
    }

    private static void addService(Class<? extends Service> serviceClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        logger.debug("create Service:"+serviceClass.getCanonicalName());
        Object p = serviceClass.getConstructor().newInstance();
        services.put(serviceClass, p);
    }

    public static void deconstructServices() {
        logger.debug("start deconstruction.");
        services.keySet().forEach(serviceClass ->
                ((Service) services.get(serviceClass)).deconstruct());
        logger.debug("finished deconstruction.");
    }

    public static class CanNotProvideServiceException extends RuntimeException {}
}
