package ants.core;

import java.io.InputStream;

import org.codehaus.jackson.JsonNode;

import ants.exception.ObjectConfigureException;
import ants.exception.ParseException;

public class ObjectStore {
    
    // TODO: cache object instance
    public static Configurable getInstance(String id, String defaultClass, String tagName) throws ObjectConfigureException, ParseException {
        int idIndex = id.indexOf('.');
        if(-1 == idIndex) {
            throw new ParseException("Failed to extract object id " + " from " + id);            
        }

        String jsonFile = id.substring(0, idIndex) + ".json";
        String oid = id.substring(idIndex + 1);

        InputStream is = ObjectStore.class.getResourceAsStream(jsonFile);
        JsonNode tree = ObjectFactory.parse(jsonFile, is);
        JsonNode node = tree.get(oid);
        if(null == node) {
            throw new ParseException("Failed to locate " + oid + " in " + jsonFile);
        }
        
        return ObjectFactory.configure(node, defaultClass, tagName, oid, "", "");
    }

}
