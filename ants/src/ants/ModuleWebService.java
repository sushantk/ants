package ants;

import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ning.http.client.AsyncCompletionHandlerBase;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.Request;
import com.ning.http.client.Response;

import ants.annotation.ConfigurableMethod;
import ants.core.Configurable;
import ants.core.ContextModule;
import ants.core.Data;
import ants.core.IString;
import ants.core.Task;
import ants.exception.EvaluateException;
import ants.exception.ExecuteException;
import ants.ext.Module;
import ants.ext.TaskFailed;

public class ModuleWebService extends Module {
    private static final Logger logger = LoggerFactory.getLogger(ModuleWebService.class);
    private static AsyncHttpClient httpClient = new AsyncHttpClient();

    private IString url;

    public ModuleWebService(String tagName, String id) {
        super(tagName, id);
    }

    @ConfigurableMethod(required=true)
    public void setUrl(Configurable value) {
        this.url = IString.class.cast(value);
    }

    @Override
    public Task execute(ContextModule context, Data input) {
        
        String url = null;
        try {
            url = this.url.getValue(context);

            BoundRequestBuilder requestBuilder = httpClient.prepareGet(url);
            Request request = requestBuilder.build();
            
            TaskNing task = new TaskNing(this.toContextString(context, url));
            httpClient.executeRequest(request, task.new NingHandler());
            return task;
        } catch (EvaluateException e) {
            return new TaskFailed(new ExecuteException(this.toContextString(context),
                    "Failed to evaluate configured parameter", e));
        } catch (Exception e) {
            return new TaskFailed(new ExecuteException(this.toContextString(context, url),
                    "Failed to initiate webservice fetch", e));
        }
    }
    
    public static class TaskNing extends Task {
        private Response response;
        private Throwable exception;
        
        public TaskNing(String key) {
            super(Task.Type.ASYNC, key);
        }

        @Override
        protected Collection<Task> runImpl() {
            if (null != this.exception) {
                super.failed(null, new ExecuteException(this.toString(), "Failed to fetch", this.exception));
            } else if(null == this.response) {
                super.failed(null, new ExecuteException(this.toString(), "Internal error, failed to get response"));
            }

            super.completed(new Data(this.response, "object/" + Response.class.getName()));
            return Collections.emptyList();
        }

        public class NingHandler extends AsyncCompletionHandlerBase {
            @Override
            public Response onCompleted(Response response) throws Exception {
                TaskNing.this.response = response;
                TaskNing.this.ready();
                return response;
            }

            @Override
            public void onThrowable(Throwable t) {
                TaskNing.this.exception = t;
                TaskNing.this.ready();
            }            
        }
    }

}
