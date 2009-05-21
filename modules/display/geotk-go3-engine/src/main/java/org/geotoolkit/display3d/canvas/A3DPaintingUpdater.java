/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.display3d.canvas;

import com.ardor3d.framework.Updater;
import com.ardor3d.framework.awt.AwtCanvas;
import com.ardor3d.util.Timer;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.util.logging.Logging;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class A3DPaintingUpdater extends Thread {

    private static final Logger LOGGER = Logging.getLogger(A3DPaintingUpdater.class);

    private final Timer timer = new Timer();
    private final WeakReference<AwtCanvas> canvasRef;
    private final Updater updater;

    public A3DPaintingUpdater(AwtCanvas canvas, Updater updater) {
        canvasRef = new WeakReference<AwtCanvas>(canvas);
        this.updater = updater;
    }

    @Override
    public void run() {
        while (true) {
            final AwtCanvas canvas = canvasRef.get();
            if (canvas == null) {
                //canvas isn't referenced anywhere, we let this thread dye
                break;
            }

            if (canvas.isShowing()) {
                //no need to repaint when not visible
                timer.update();
                updater.update(timer);
                final CountDownLatch latch = new CountDownLatch(1);
                canvas.draw(latch);

                try {
                    final boolean success = latch.await(5, TimeUnit.SECONDS);
                    if (!success) {
                        LOGGER.log(Level.INFO, "Waiting failed");
                    }
                } catch (final InterruptedException e) {}
            }
        }
    }
}
