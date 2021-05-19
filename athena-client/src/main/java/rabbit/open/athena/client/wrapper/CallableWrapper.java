package rabbit.open.athena.client.wrapper;

import java.util.concurrent.Callable;

public class CallableWrapper<V> extends AbstractWrapper implements Callable<V> {

    public CallableWrapper(Callable<V> realObject) {
        this.realObject = realObject;
    }

    @Override
    public V call() throws Exception {
        try {
            before.run();
            V result = ((Callable<V>)realObject).call();
            after.run();
            return result;
        } catch (Exception e) {
            error.runWith(e);
            throw e;
        }
    }

    public static <D> CallableWrapper<D> of(Callable<D> task) {
        return new CallableWrapper<>(task);
    }

}
