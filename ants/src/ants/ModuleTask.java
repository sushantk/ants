package ants;

import java.util.LinkedHashMap;

import ants.api.Context;
import ants.api.IModule;
import ants.api.IObjectFactory;
import ants.api.IParams;
import ants.api.ModuleContext;
import ants.api.RequestContext;
import ants.api.IParams.Type;
import ants.api.Task;
import ants.exception.ObjectConfigureException;
import ants.exception.ParseException;

public class ModuleTask extends Task {
    
    ModuleContext context;
    
    public ModuleTask(String iid, String moduleId, 
            LinkedHashMap<String, IParams.Type> params,
            RequestContext requestContext, Context parent) {
        super(Task.Type.CPU);

        this.context = new ModuleContext(iid, moduleId, params, requestContext, parent);
    }

    @Override
    public Iterable<Task> run() {
        IObjectFactory factory = this.requestContext.getFactory();
        
        IModule module = IModule.class.cast(factory.create(moduleId, Const.thefault.ModuleClass, Const.tag.module));
        return null;
    }

}
