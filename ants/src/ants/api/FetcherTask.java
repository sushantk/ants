package ants.api;

import java.util.Collection;
import java.util.Collections;

import ants.Const;

public class FetcherTask extends Task 
                         implements Task.ICallback {

    private ExecuteContext context;

    public FetcherTask(ExecuteContext context) {
        super(Task.Type.SYNC);

        this.context = context;
    }

    @Override
    protected Collection<Task> runImpl() {
        IObjectFactory factory = this.context.getRequestContext().getFactory();
        IFetcher fetcher = IFetcher.class.cast(factory.create(context.getId(), Const.thefault.FetcherClass, Const.tag.fetcher));
        Task task = fetcher.fetch(this.context);
        task.addCallback(this);
        return Collections.singletonList(task);
    }

    @Override
    public Collection<Task> onDone(final Task task) {
        this.setData(task.getData(), task.getResult());
        return Collections.emptyList();
    }

}
