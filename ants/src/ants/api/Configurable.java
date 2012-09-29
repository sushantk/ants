package ants.api;


public abstract class Configurable implements IConfigurable {

    private String tagName;
    private String id;

    public Configurable(String tagName, String id) {
        this.tagName = tagName;
        this.id = id;
    }

    public String getTag() {
        return this.tagName;
    }

    public String getId() {
        return this.id;
    }
}
