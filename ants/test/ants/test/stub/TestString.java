package ants.test.stub;

import ants.api.Configurable;
import ants.api.ExecuteContext;
import ants.api.IString;
import ants.exception.EvaluateException;

public class TestString extends Configurable
                           implements IString {

    private String value;
    private boolean returnSuccess;

    public TestString(String value, boolean returnSuccess) {
        super("string", "");
        
        this.value = value;
        this.returnSuccess = returnSuccess;
    }
    
    @Override
    public String getValue(ExecuteContext context) throws EvaluateException {
        if(this.returnSuccess) {
            return this.value;
        }
        
        throw new EvaluateException(this.value, context, this);
    }
    
    public String toString() {
        return this.value;
    }
}
