package rabbit.open.athena.client.wrapper;

public class RunnableWrapper extends AbstractWrapper implements Runnable {

    public RunnableWrapper(Runnable realObject) {
        setRealObject(realObject);
    }

    @Override
    public void run() {
        before.run();
        try {
            ((Runnable)realObject).run();
            after.run();
        } catch (Exception e) {
            error.runWith(e);
            throw e;
        }
    }

    public static Runnable of(Runnable task) {
        return new RunnableWrapper(task);
    }

}
