package rabbit.open.athena.plugin.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeRunner {

    static Logger logger = LoggerFactory.getLogger(SafeRunner.class);

    private SafeRunner() {};

    public static void handle(Invoke task) {
        try {
            task.run();
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }

    public interface Invoke {
        void run() throws Exception;
    }
}
