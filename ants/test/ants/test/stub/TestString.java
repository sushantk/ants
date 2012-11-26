package ants.test.stub;

import ants.core.Configurable;
import ants.core.ContextModule;
import ants.core.IString;
import ants.exception.EvaluateException;

public class TestString extends Configurable
                        implements IString {

    private String value;
    private boolean returnSuccess;

    public TestString() {
        super("string", "");
        
        this.returnSuccess = false;
    }

    public TestString(String value) {
        super("string", "");
        
        this.value = value;
        this.returnSuccess = true;
    }

    @Override
    public String getValue(ContextModule context) throws EvaluateException {
        if(this.returnSuccess) {
            return this.value;
        }
        
        throw new EvaluateException(this.toContextString(context, this.value), "Returning failure");
    }
    
    public String toString() {
        return this.value;
    }
}
