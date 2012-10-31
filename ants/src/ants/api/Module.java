package ants.api;

public abstract class Module extends Configurable {

    public Module(String tagName, String id) {
        super(tagName, id);
    }
    
    public String toContextString(Context context) {
        return context + "/" + this.getClass().getName();
    }

    public String toContextString(Context context, String s) {
        return context + "/" + this.getClass().getName() + "<" + s + ">";
    }

}
