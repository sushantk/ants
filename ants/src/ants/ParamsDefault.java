package ants;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ants.annotation.ConfigurableClass;
import ants.annotation.ConfigurableMethod;
import ants.api.Configurable;
import ants.api.ExecuteContext;
import ants.api.IParams;
import ants.api.IString;
import ants.exception.ObjectEvaluateException;

/**
 * 
 */
@ConfigurableClass(expectsList=true)
public class ParamsDefault extends Configurable
                           implements IParams {
    static final Logger logger = LoggerFactory.getLogger(ParamsDefault.class);
    
    private LinkedHashMap<String, Configurable> params;

    public ParamsDefault(String tagName, String id) {
        super(tagName, id);
    }

    @ConfigurableMethod(required=true, defaultListItemClass="ants.StringDefault", listItemTag="param")
    public void setList(LinkedHashMap<String, Configurable> params) {
        this.params = params;
    }

    @Override
    public LinkedHashMap<String, Type> getPairs(ExecuteContext context,
            boolean simple) throws ObjectEvaluateException {
        LinkedHashMap<String, Type> result = new  LinkedHashMap<String, Type>();
        
        Iterator<Entry<String, Configurable>> iter = this.params.entrySet().iterator();
        while(iter.hasNext()) {
            Entry<String, Configurable> entry = iter.next();
            Configurable configurable = entry.getValue();
            
            String name =  configurable.getAttribute(Const.attribute.name);
            if(null == name) {
                name = entry.getKey();
            }

            if(configurable instanceof IString) {
                IString istring = (IString)configurable;
                String string = istring.getValue(context);
                if(simple) {
                    result.put(name, new Simple(string));
                } else {
                    Type value = result.get(name);
                    if(null == value) {
                        value = new Complex(new LinkedHashMap<String, Type>());
                        result.put(name, value);
                    }
                    value.getComplex().put(configurable.getId(), new Simple(string));
                }
                
            } else if(!simple && (configurable instanceof IParams)) {
                IParams iparams = (IParams)configurable;
                result.put(name, new Complex(iparams.getPairs(context, false)));
            }
            else {
                throw new ObjectEvaluateException("Invalid parameter type, string, list or map expected: " + name, 
                        context, this);
            }
        }

        return result;
    }    

    public String toString() {
        return this.params.toString();
    }
}
