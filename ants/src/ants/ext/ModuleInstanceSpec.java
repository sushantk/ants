package ants.ext;

import java.util.LinkedHashMap;

import ants.annotation.ConfigurableMethod;
import ants.core.Configurable;
import ants.core.ContextModule;
import ants.core.IParams;
import ants.core.IString;
import ants.core.IParams.Type;
import ants.exception.EvaluateException;
import ants.exception.ExecuteException;

public class ModuleInstanceSpec extends Configurable {

    private IString moduleId;
    private IParams params;

    public ModuleInstanceSpec(String tagName, String id) {
        super(tagName, id);
    }
    
    @ConfigurableMethod(required=true, defaultClass = "ants.StringLiteral")
    public void setModuleId(Configurable value) {
        this.moduleId = IString.class.cast(value);
    }

    public void setParams(Configurable params) {
        this.params = IParams.class.cast(params);
    }
    
    public ContextModule createContext(ContextModule context) throws ExecuteException {
        try {
            String moduleId = this.moduleId.getValue(context);
            
            LinkedHashMap<String, Type> params;
            if(null != this.params) {
                params = this.params.getPairs(context, false);
            } else {
                params = new LinkedHashMap<>();
            }
            
            return new ContextModule(moduleId, params, context);
        } catch (EvaluateException e) {
            throw new ExecuteException(this.toContextString(context),
                    "Failed to evaluate configured parameter", e);
        }
    }

}
