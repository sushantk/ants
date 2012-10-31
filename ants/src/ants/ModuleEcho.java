package ants;

import ants.annotation.ConfigurableMethod;
import ants.api.Configurable;
import ants.api.Data;
import ants.api.TaskCompleted;
import ants.api.IModule;
import ants.api.IString;
import ants.api.ContextModule;
import ants.api.Task;
import ants.api.TaskFailed;
import ants.exception.EvaluateException;
import ants.exception.ExecuteException;

public class ModuleEcho extends Configurable implements IModule {

    private IString value;

    public ModuleEcho(String tagName, String id) {
        super(tagName, id);
    }

    @ConfigurableMethod(required = true)
    public void setString(Configurable value) {
        this.value = IString.class.cast(value);
    }

    @Override
    public Task execute(ContextModule context, Data data) {

        try {
            String str = this.value.getValue(context);
            return new TaskCompleted(new Data(str, Const.mime.plain,
                    Const.charSet.utf8));
        } catch (EvaluateException e) {
            return new TaskFailed(new ExecuteException(this.toContextString(context),
                    "Failed to evaluate echo string", e));
        }
    }

}
