package ants;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import ants.annotation.ConfigurableMethod;
import ants.core.Configurable;
import ants.core.ContextModule;
import ants.core.Data;
import ants.core.IList;
import ants.core.Task;
import ants.exception.EvaluateException;
import ants.exception.ExecuteException;
import ants.ext.Module;
import ants.ext.ModuleInstanceSpec;
import ants.ext.TaskFailed;
import ants.ext.TaskRelay;
import ants.ext.TaskModule;

public class ModuleSerial extends Module implements TaskRelay.ICallback {

    private IList members;
    private ContextModule runContext;

    public ModuleSerial(String tagName, String id) {
        super(tagName, id);
    }

    @ConfigurableMethod(required = true, defaultClass="ants.ListDefault",
            defaultListItemClass="ants.ModuleInstance", listItemTag="member")
    public void setMembers(Configurable value) {
        this.members = IList.class.cast(value);
    }

    @Override
    public Task execute(ContextModule context, Data data) {
        this.runContext = context;
        
        try {
            LinkedHashMap<String, Task> tasks = new LinkedHashMap<>();

            LinkedHashMap<String, Configurable> members = this.members.getItems(context);
            Iterator<Entry<String, Configurable>> iter = members.entrySet()
                    .iterator();
            while (iter.hasNext()) {
                Entry<String, Configurable> entry = iter.next();
                Configurable configurable = entry.getValue();
                
                ModuleInstanceSpec spec = ModuleInstanceSpec.class.cast(configurable);
                ContextModule instanceContext = spec.createContext(context);
                TaskModule task = new TaskModule(instanceContext, data);
                tasks.put(entry.getKey(), task);
            }
            
            Task fork = new TaskRelay(this.toContextString(context), tasks, this);
            return fork;
        } catch (EvaluateException e) {
            return new TaskFailed(new ExecuteException(this.toContextString(context),
                    "Failed to evaluate members' list", e));
        } catch(ExecuteException e) {
            return new TaskFailed(new ExecuteException(this.toContextString(context),
                    "Failed to create member's context", e));            
        }
    }

    @Override
    public Data filter(String taskId, Task task) throws ExecuteException {
        if(Task.Result.COMPLETED == task.getResult()) {
            return task.getData();
        }
        
        throw new ExecuteException(this.toContextString(this.runContext), "Failed to execute module: " + taskId);
    }

}
