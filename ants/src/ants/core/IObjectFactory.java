package ants.core;

import ants.exception.ObjectConfigureException;
import ants.exception.ParseException;

public interface IObjectFactory {
    
    Configurable create(String id, String defaultClass, String tagName)
            throws ObjectConfigureException, ParseException;

}
