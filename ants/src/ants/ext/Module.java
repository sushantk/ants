package ants.ext;

import ants.core.Configurable;
import ants.core.Context;
import ants.core.IModule;

public abstract class Module extends Configurable implements IModule {

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
