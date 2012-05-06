/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
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

import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.CanvasRenderer;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.jogl.JoglAwtCanvas;
import com.ardor3d.framework.jogl.JoglCanvasRenderer;
import com.ardor3d.input.Key;
import com.ardor3d.input.MouseCursor;
import com.ardor3d.input.MouseManager;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.awt.AwtFocusWrapper;
import com.ardor3d.input.awt.AwtKeyboardWrapper;
import com.ardor3d.input.awt.AwtMouseManager;
import com.ardor3d.input.awt.AwtMouseWrapper;
import com.ardor3d.input.logical.*;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.logging.Logger;
import javax.media.opengl.GLCanvas;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import org.geotoolkit.display.canvas.AbstractCanvas;
import org.geotoolkit.display.container.AbstractContainer;
import org.geotoolkit.display3d.container.A3DContainer;
import org.geotoolkit.display3d.controller.A3DController;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.logging.Logging;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class A3DCanvas extends AbstractCanvas{

    public static final String GEOTK_MANAGER = "geotk";
    
    private static final Logger LOGGER = Logging.getLogger(A3DCanvas.class);

    public static final String CAMERA_POSITION = "camera_position";

    private final LogicalLayer logicalLayer = new LogicalLayer();
    private final A3DContainer container = new A3DContainer(this);
    private final A3DController controller;
    private final JScrollPane swingPane;
    private final JoglAwtCanvas canvas;
    private CoordinateReferenceSystem objectiveCRS;

    public A3DCanvas(final CoordinateReferenceSystem objectiveCRS, final Hints hints) {
        super(hints);
        this.objectiveCRS = objectiveCRS;
        this.canvas = (JoglAwtCanvas) initContext();
        this.controller = new A3DController(this, logicalLayer);
        this.controller.init();

        this.swingPane = new JScrollPane(canvas);
        this.swingPane.setBorder(null);
        this.swingPane.setWheelScrollingEnabled(false);
        this.swingPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        this.swingPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.swingPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                CanvasRenderer canvasRenderer = canvas.getCanvasRenderer();

                if (canvasRenderer.getCamera() != null) {
                    System.out.println("resized");
                    // tell our camera the correct new size
                    canvasRenderer.getCamera().resize(canvas.getWidth(), canvas.getHeight());

                    // keep our aspect ratio the same.
                    canvasRenderer.getCamera().setFrustumPerspective(45.0,
                            canvas.getWidth() / (float) canvas.getHeight(), 1, 5000);
                }
            }
        });

        Thread updater = new A3DPaintingUpdater(canvas, controller);
        updater.setPriority(Thread.MAX_PRIORITY);
        updater.start();
    }

    public synchronized void setObjectiveCRS(final CoordinateReferenceSystem crs) throws TransformException {
        throw new TransformException("You are not allowed to change CRS after creation on 3D canvas");
    }

    @Override
    public A3DController getController() {
        return controller;
    }

    public A3DContainer getContainer2() {
        return container;
    }

    @Override
    public AbstractContainer getContainer() {
        return null;
    }

    public JComponent getComponent(){
        return swingPane;
    }

    public JoglAwtCanvas getNativeCanvas(){
        return canvas;
    }

    private GLCanvas initContext() {
//        refresher.addUpdater(controller);

        JoglCanvasRenderer renderer = new JoglCanvasRenderer(container);
        final DisplaySettings settings = new DisplaySettings(1, 1, 32, 0, 0, 32, 0, 4, false, false);
        final JoglAwtCanvas canvas = new JoglAwtCanvas(settings,renderer);
        canvas.setSize(new Dimension(100, 100));
        canvas.setPreferredSize(new Dimension(1,1));
        canvas.setVisible(true);

        final MouseManager manager = new AwtMouseManager(canvas);
        final AwtMouseWrapper    mouseWrapper    = new AwtMouseWrapper(canvas,manager);
        final AwtKeyboardWrapper keyboardWrapper = new AwtKeyboardWrapper(canvas);
        final AwtFocusWrapper    focusWrapper    = new AwtFocusWrapper(canvas);
        final AwtMouseManager    mouseManager    = new AwtMouseManager(canvas);

        final PhysicalLayer pl = new PhysicalLayer(keyboardWrapper, mouseWrapper, focusWrapper);

        logicalLayer.registerInput(canvas, pl);

        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.H),
            new TriggerAction() {
            @Override
            public void perform(Canvas source, TwoInputStates arg1, double arg2) {
                if (source != canvas) {
                        return;
                    }
            }
            }));
        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.J),
            new TriggerAction() {

            @Override
            public void perform(Canvas source, TwoInputStates arg1, double arg2) {
                if (source != canvas) {
                        return;
                    }
                    mouseManager.setCursor(MouseCursor.SYSTEM_DEFAULT);
            }
            }));

//        refresher.addCanvas(canvas);

        return canvas;
    }

    public CoordinateReferenceSystem getObjectiveCRS() {
        return objectiveCRS;
    }

}
