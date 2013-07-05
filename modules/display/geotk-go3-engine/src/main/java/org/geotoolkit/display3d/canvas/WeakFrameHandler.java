/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.display3d.canvas;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.jogl.JoglAwtCanvas;
import com.ardor3d.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.collection.WeakHashSet;
import org.apache.sis.util.logging.Logging;

/**
 * Derivate from Ardor3D FrameHandler.
 * - Avoids painting canvas which are not showing.
 * - Store a single thread for update operations
 * - Use weak references toward canvas and updater avoiding possible memory leaks.
 */
public final class WeakFrameHandler {

    private static final Logger LOGGER = Logging.getLogger(WeakFrameHandler.class);
    private static WeakFrameHandler INSTANCE = null;

    private final WeakHashSet<Updater> _updaters = new WeakHashSet<Updater>(Updater.class);
    private final WeakHashSet<Canvas> _canvases = new WeakHashSet<Canvas>(Canvas.class);
    private final Timer _timer;

    /**
     * Number of seconds we'll wait for a latch to count down to 0. Default is 5.
     */
    private long _timeoutSeconds = 5;

    private WeakFrameHandler(final Timer timer) {
        _timer = timer;
    }

    @MainThread
    public void updateFrame() {
        // calculate tpf
        // update updaters
        // draw canvases

        _timer.update();


        //defensive copy, avoids concurrent modification
        final Updater[] updaters;
        synchronized(_updaters){
            updaters = _updaters.toArray(new Updater[0]);
        }

        for (final Updater updater : updaters) {
            updater.update(_timer);
        }


        //defensive copy, avoids concurrent modification
        final Canvas[] canvas;
        synchronized(_canvases){
            canvas = _canvases.toArray(new Canvas[0]);
        }

        final CountDownLatch latch = new CountDownLatch(canvas.length);
        for(final Canvas canva : canvas){
            if(canva instanceof JoglAwtCanvas){
                if(((JoglAwtCanvas)canva).isShowing()){
                    canva.draw(latch);
                }else{
                    //canva not visible, drawing cause a jvm crash
                    latch.countDown();
                }
            }else{
                canva.draw(latch);
            }
        }

        try {
            // wait for all canvases to be drawn - the reason for using the latch is that
            // in some cases (AWT, for instance), the thread that calls canvas.draw() is not the
            // one that holds the OpenGL context, which means that drawing is simply queued.
            // When the actual OpenGL rendering has been done, the OpenGL thread will countdown
            // on the latch, and once all the canvases have finished rendering, this method
            // will return.
            final boolean success = latch.await(_timeoutSeconds, TimeUnit.SECONDS);

            if (!success) {
                LOGGER.logp(Level.SEVERE, com.ardor3d.framework.FrameHandler.class.toString(), "updateFrame",
                        "Timeout while waiting for renderers");
                // FIXME: should probably reset update flag in canvases?
            }
        } catch (final InterruptedException e) {
            // restore updated status
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Add an updater to the frame handler.
     * <p>
     * The frame handler calls the {@link Updater#update(com.ardor3d.util.ReadOnlyTimer) update} method of each updater
     * that has been added to it once per frame, before rendering begins.
     * <p>
     * <strong>Note:</strong> that is the frame handler has already been initialized then the updater will <em>not</em>
     * have it's {@code init} method called automatically, it is up to the client code to perform any initialization
     * explicitly under this scenario.
     *
     * @param updater
     *            the updater to add.
     */
    public void addUpdater(final Updater updater) {
        synchronized(_updaters){
            _updaters.add(updater);
        }
    }

    /**
     * Remove an updater from the frame handler.
     *
     * @param updater
     *            the updater to remove.
     * @return {@code true} if the updater was removed, {@code false} otherwise (which will happen if, for example, the
     *         updater had not previously been added to the frame handler).
     */
    public boolean removeUpdater(final Updater updater) {
        final boolean removed;
        synchronized(_updaters){
            removed = _updaters.remove(updater);
        }
        return removed;
    }

    /**
     * Add a canvas to the frame handler.
     * <p>
     * The frame handler calls the {@link Canvas#draw(java.util.concurrent.CountDownLatch)} draw} method of each canvas
     * that has been added to it once per frame, after updating is complete.
     * <p>
     * <strong>Note:</strong> that is the frame handler has already been initialized then the canvas will <em>not</em>
     * have it's {@code init} method called automatically, it is up to the client code to perform any initialization
     * explicitly under this scenario.
     *
     * @param canvas
     *            the canvas to add.
     */
    public void addCanvas(final Canvas canvas) {
        synchronized(_canvases){
            _canvases.add(canvas);
        }
    }

    /**
     * Remove a canvas from the frame handler.
     *
     * @param canvas
     *            the canvas to remove.
     * @return {@code true} if the canvas was removed, {@code false} otherwise (which will happen if, for example, the
     *         canvas had not previously been added to the frame handler).
     */
    public boolean removeCanvas(final Canvas canvas) {
        final boolean removed;
        synchronized(_canvases){
            removed = _canvases.remove(canvas);
        }
        return removed;
    }

    public void init() {
        // TODO: this can lead to problems with canvases and updaters added after init() has been called once...
        for (final Canvas canvas : _canvases) {
            canvas.init();
        }

        for (final Updater updater : _updaters) {
            updater.init();
        }
    }

    public long getTimeoutSeconds() {
        return _timeoutSeconds;
    }

    public void setTimeoutSeconds(final long timeoutSeconds) {
        _timeoutSeconds = timeoutSeconds;
    }

    public Timer getTimer() {
        return _timer;
    }

    public static synchronized WeakFrameHandler getInstance(){
        if(INSTANCE == null){
            INSTANCE = new WeakFrameHandler(new Timer());

            final Thread t = new Thread(){
                @Override
                public void run() {
                    while (true) {
                        INSTANCE.updateFrame();
                        Thread.yield();
                    }
                }
            };
            t.setName("Geotk-Ardor3D: updater thread");
            t.start();
        }

        return INSTANCE;
    }

}
