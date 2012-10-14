package ants.api;


public class SourceTask extends Task {

    private Context context;

    public SourceTask(ModuleContext context) {
        super(Task.Type.CPU);
        
        this.context = context;
    }

    @Override
    public Iterable<Task> run() {
        return null;
    }

}
