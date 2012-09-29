package ants;

import ants.annotation.ConfigurableMethod;
import ants.api.Configurable;
import ants.api.Context;
import ants.api.IString;

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
    public String getValue(Context context) {
        return this.value;
    }    
}