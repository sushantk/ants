package ants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ants.annotation.ConfigurableClass;
import ants.annotation.ConfigurableMethod;
import ants.core.Configurable;
import ants.core.ContextModule;
import ants.core.IString;
import ants.exception.EvaluateException;

@ConfigurableClass(expectsValue=true)
public class StringUrl extends Configurable
                           implements IString {
    private static final Logger logger = LoggerFactory.getLogger(StringUrl.class);

    private IString base;

    public StringUrl(String tagName, String id) {
        super(tagName, id);
    }

    public void setValue(String value) {
        StringLiteral literal = new StringLiteral("base", "");
        literal.setValue(value);
        this.base = literal;
    }

    @ConfigurableMethod(required=true)
    public void setBase(Configurable value) {
        this.base = IString.class.cast(value);
    }

    @Override
    public String getValue(ContextModule context) throws EvaluateException {
        return this.base.getValue(context);
    }

}
