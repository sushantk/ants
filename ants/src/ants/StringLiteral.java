package ants;

import ants.annotation.ConfigurableClass;
import ants.annotation.ConfigurableMethod;
import ants.core.Configurable;
import ants.core.ContextModule;
import ants.core.IString;

@ConfigurableClass(expectsValue=true)
public class StringLiteral extends Configurable
                           implements IString {

    public StringLiteral(String tagName, String id) {
        super(tagName, id);
    }
    
    private String value;

    @ConfigurableMethod(required=true)
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getValue(ContextModule context) {
        return this.value;
    }
    
    public String toString() {
        return this.value;
    }
}
