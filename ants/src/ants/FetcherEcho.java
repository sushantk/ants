package ants;

import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ants.annotation.ConfigurableMethod;
import ants.api.Configurable;
import ants.api.Data;
import ants.api.ExecuteContext;
import ants.api.IFetcher;
import ants.api.IString;
import ants.api.Task;
import ants.exception.EvaluateException;

public class FetcherEcho extends Configurable 
                         implements IFetcher {

    private static final Logger logger = LoggerFactory
            .getLogger(FetcherEcho.class);

    private IString value;

    public FetcherEcho(String tagName, String id) {
        super(tagName, id);
    }

    @ConfigurableMethod(required=true)
    public void setString(Configurable value) {
        this.value = IString.class.cast(value);
    }

    /**
     * In general fetchers will be sync_io or async_io. This is
     * shown as an example.
     */
    @Override
    public Task fetch(final ExecuteContext context) {
        return new Task(Task.Type.SYNC_IO) {
            protected Collection<Task> runImpl() {
                try {
                    String str = FetcherEcho.this.value.getValue(context);
                    this.setData(new Data(str, Const.mime.plain), Task.Result.SUCCEDED);
                } catch (EvaluateException e) {
                    logger.error(FetcherEcho.this.toContextString(context) + ": Failed to fetch data", e);
                }
                return Collections.emptyList();
            }
        };
    }    
}
