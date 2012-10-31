package ants.api;

import java.util.Map;
import java.util.TreeMap;

public abstract class Configurable {

    private String tagName;
    private String id;
    private Map<String, String> attrs;

    public Configurable(String tagName, String id) {
        this.tagName = tagName;
        this.id = id;
        
        this.attrs = new TreeMap<String, String>();
    }

    public String getAttribute(String name) {
        return this.attrs.get(name);
    }

    public String getTag() {
        return this.tagName;
    }

    public String getId() {
        return this.id;
    }
    
    public String setAttribute(String name, String value) {
        return this.attrs.put(name, value);
    }
    
    public String toTagString() {
        return this.getTag() + "<" + this.getClass().getName() + ", "
                + this.getId() + ">";
    }
    
    public String toContextString(Context context) {
        return context + "/" + this.toTagString();
    }

    public String toContextString(Context context, String s) {
        return context + "/" + this.toTagString() + "/" + s;
    }

}
