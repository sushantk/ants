package ants.api;

import java.util.Collection;
import java.util.Collections;

import ants.Const;

public class TaskModule extends Task {
    
    private ContextModule context;
    private Data input;
    
    public TaskModule(ContextModule context, Data input) {
        super(Task.Type.SYNC, "");

        this.context = context;
        this.input = input;
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
