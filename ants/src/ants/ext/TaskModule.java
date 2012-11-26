package ants.ext;

import java.util.Collection;
import java.util.Collections;

import ants.core.Const;
import ants.core.ContextModule;
import ants.core.Data;
import ants.core.IModule;
import ants.core.IObjectFactory;
import ants.core.Task;

public class TaskModule extends Task {
    
    private ContextModule context;
    private Data input;
    
    public TaskModule(ContextModule context, Data input) {
        super(Task.Type.SYNC, context.toString());

        this.context = context;
        this.input = input;
    }
    
    public ContextModule getContext() {
        return this.context;
    }

    @Override
    protected Collection<Task> runImpl() {
        IObjectFactory factory = context.getRequestContext().getFactory();
        IModule module = IModule.class.cast(factory.create(context.getModuleId(), Const.thefault.ModuleClass, Const.tag.module));
        Task task = module.execute(this.context, input);
        task.addCallback(new CallbackCopy(this));
        return Collections.singletonList(task);
    }

}
