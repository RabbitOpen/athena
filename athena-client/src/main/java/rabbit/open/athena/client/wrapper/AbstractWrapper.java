package rabbit.open.athena.client.wrapper;

public abstract class AbstractWrapper {

    protected Runnable before = () -> {};
    protected Runnable after = () -> {};
    protected ErrorHandler error = e -> {};

    protected Object realObject;

    public void setBefore(Runnable before) {
        this.before = before;
    }

    public void setAfter(Runnable after) {
        this.after = after;
    }

    public void setError(ErrorHandler error) {
        this.error = error;
    }

    public void setRealObject(Object realObject) {
        this.realObject = realObject;
    }

}
