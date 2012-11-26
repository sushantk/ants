package ants;

import ants.annotation.ConfigurableMethod;
import ants.core.Configurable;
import ants.core.Const;
import ants.core.ContextModule;
import ants.core.Data;
import ants.core.IString;
import ants.core.Task;
import ants.exception.EvaluateException;
import ants.exception.ExecuteException;
import ants.ext.Module;
import ants.ext.TaskCompleted;
import ants.ext.TaskFailed;

public class ModuleEcho extends Module {

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
