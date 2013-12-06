

package org.geotoolkit.display2d.container.statefull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.geotoolkit.display.canvas.Canvas;
import org.geotoolkit.display.primitive.SceneNode;

/**
 * Root scene node for statefull context.
 * Contains an executor pool for parallal data loading.
 *
 * @author Johann Sorel (Geomatys)
 */
public class RootSceneNode extends SceneNode{


    /** Executor used to update graphics */
    private static final RejectedExecutionHandler LOCAL_REJECT_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy(){

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            System.out.println("> Rejected update");
            super.rejectedExecution(r, e);
        }

    };
    private final BlockingQueue queue;
    private final ThreadPoolExecutor exec;

    public RootSceneNode(Canvas canvas) {
        super(canvas,true);

        queue = new ArrayBlockingQueue(100);
            exec = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
                    1, TimeUnit.MINUTES, queue,
                    LOCAL_REJECT_EXECUTION_HANDLER);

    }

    protected ThreadPoolExecutor getExecutor(){
        return exec;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        queue.clear();
        exec.shutdown();
    }

}
