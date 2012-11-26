package ants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ants.annotation.ConfigurableClass;
import ants.annotation.ConfigurableMethod;
import ants.core.Configurable;
import ants.core.ContextModule;
import ants.core.IString;

@ConfigurableClass(expectsValue=true)
public class StringDefault extends Configurable
                           implements IString {
    private static final Logger logger = LoggerFactory.getLogger(StringDefault.class);

    private String value;

    public StringDefault(String tagName, String id) {
        super(tagName, id);
    }

    @ConfigurableMethod(required=true)
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getValue(ContextModule context) {
        String evaledText = this.value;
        if(context.isLogging()) logger.debug("{} - Evaluated {}=>{}", new Object[]{context, this.value, evaledText});
        return evaledText;
    }

    public String toString() {
        return this.value;
    }
}
