package bulletinBoardService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.swing.SwingUtilities;

public class EDTInvocationHandler implements InvocationHandler {
    private Object invocationResult = null;
    private UITasks ui;

    public EDTInvocationHandler(UITasks ui) {
        this.ui = ui;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        if (SwingUtilities.isEventDispatchThread()) {
            invocationResult = method.invoke(ui, args);
        } else {
            Runnable shell = new Runnable() {
                @Override
                public void run() {
                    try {
                        invocationResult = method.invoke(ui, args);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            };
            SwingUtilities.invokeAndWait(shell);
        }
        return invocationResult;
    }
}