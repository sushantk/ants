package ants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ants.annotation.ConfigurableClass;
import ants.annotation.ConfigurableMethod;
import ants.api.Configurable;
import ants.api.Context;
import ants.api.IString;

@ConfigurableClass(expectsValue=true)
public class StringDefault extends Configurable
                           implements IString {
    static final Logger logger = LoggerFactory.getLogger(StringDefault.class);

    public StringDefault(String tagName, String id) {
        super(tagName, id);
    }
    
    private String value;

    @ConfigurableMethod(required=true)
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getValue(Context context) {
        return StringDefault.evaluate(context, this.value);
    }
    
    public static String evaluate(Context context, String text) {
        String evaledText = text;
        if(context.isLogging()) logger.debug("{} - Evaluated {}=>{}", new Object[]{context, text, evaledText});
        return evaledText;
    }
    
    public String toString() {
        return this.value;
    }
}
