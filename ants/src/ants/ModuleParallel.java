package ants;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import ants.annotation.ConfigurableMethod;
import ants.core.Configurable;
import ants.core.Const;
import ants.core.ContextModule;
import ants.core.Data;
import ants.core.IList;
import ants.core.Task;
import ants.core.Util;
import ants.exception.EvaluateException;
import ants.exception.ExecuteException;
import ants.ext.Module;
import ants.ext.ModuleInstanceSpec;
import ants.ext.TaskFailed;
import ants.ext.TaskFork;
import ants.ext.TaskModule;

public class ModuleParallel extends Module implements TaskFork.ICallback {

    private IList members;
    private ContextModule runContext;

    public ModuleParallel(String tagName, String id) {
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
            TreeMap<String, Task> tasks = new TreeMap<>();
            
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
            
            Task fork = new TaskFork(this.toContextString(context), tasks, this);
            return fork;
        } catch (EvaluateException e) {
            return new TaskFailed(new ExecuteException(this.toContextString(context),
                    "Failed to evaluate members's list", e));
        } catch(ExecuteException e) {
            return new TaskFailed(new ExecuteException(this.toContextString(context),
                    "Failed to create a member's context", e));            
        }
    }

    @Override
    public Data join(Map<String, Task> tasks) throws ExecuteException {
        LinkedList<String> failedModuleList = new LinkedList<>();
        TreeMap<String, Data> dataMap = new TreeMap<>();
        Iterator<Entry<String, Task>> iter = tasks.entrySet()
                .iterator();
        while(iter.hasNext()) {
            Entry<String, Task> entry = iter.next();
            TaskModule moduleTask = (TaskModule)entry.getValue();
            if(Task.Result.COMPLETED != moduleTask.getResult()) {
                failedModuleList.add(entry.getKey());
            } else {
                dataMap.put(entry.getKey(), moduleTask.getData());
            }
        }
        
        // TODO: option to complete on error
        if(failedModuleList.size() > 0) {
            throw new ExecuteException(this.runContext.toString(),
                    "Failed to execute: " + Util.implode(failedModuleList, Const.comma));
        }

        Data moduleData = new Data(dataMap, "object/" + dataMap.getClass().getName());
        return moduleData;
    }

}
