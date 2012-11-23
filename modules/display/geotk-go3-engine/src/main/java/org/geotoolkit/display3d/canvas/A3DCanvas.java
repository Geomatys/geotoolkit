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

import com.ardor3d.bounding.BoundingBox;
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
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.geom.BufferUtils;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.nio.FloatBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import javax.measure.unit.NonSI;
import javax.media.opengl.awt.GLCanvas;
import org.geotoolkit.display.canvas.AbstractCanvas;
import org.geotoolkit.display.container.AbstractContainer;
import org.geotoolkit.display3d.container.A3DContainer;
import org.geotoolkit.display3d.controller.A3DController;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.*;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.display.canvas.CanvasController;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class A3DCanvas extends AbstractCanvas{

    //generate a single queue manager id for each instance of canvas.
    private static final AtomicInteger ID_INC = new AtomicInteger();
    private final String queueManagerID = "geotk-ardor3d-"+ID_INC.incrementAndGet();

    private final LogicalLayer logicalLayer = new LogicalLayer();
    private final A3DContainer container = new A3DContainer(this);
    private final A3DController controller;
    private final JoglAwtCanvas canvas;
    
    //objective coordinate reference system
    //all datas are transformed toward this crs
    private CoordinateReferenceSystem objectiveCRS;
    private MathTransform objToDisp;
    private MathTransform dispToObj;
    
    //display a globe or a plan view
    private boolean planView = true;
    
    //a grid plan over the world
    private Node basePlan = null;

    public A3DCanvas(final Hints hints) {
        super(hints);
        this.objectiveCRS = DefaultGeographicCRS.WGS84_3D;
        this.canvas = (JoglAwtCanvas) initContext();
        this.controller = new A3DController(this, logicalLayer);
        this.controller.init();

        canvas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                final CanvasRenderer canvasRenderer = canvas.getCanvasRenderer();

                if (canvasRenderer.getCamera() != null) {
                    // tell our camera the correct new size
                    canvasRenderer.getCamera().resize(canvas.getWidth(), canvas.getHeight());
                    // keep our aspect ratio the same.
                    canvasRenderer.getCamera().setFrustumPerspective(45.0,
                            canvas.getWidth() / (float) canvas.getHeight(), 
                            A3DController.CAMERA_NEAR_PLAN, 
                            A3DController.CAMERA_FAR_PLAN);
                }
            }
        });

        final WeakFrameHandler handler = WeakFrameHandler.getInstance();
        handler.addUpdater(controller);
        handler.addCanvas(canvas);
        
        setPlanView(false);
    }

    /**
     * 
     * @return CoordinateReferenceSystem. always 3 dimensional crs.
     */
    public synchronized CoordinateReferenceSystem getObjectiveCRS() {
        return objectiveCRS;
    }
    
    /**
     * Set the canvas Objective CRS, if no vertical crs is defined then an
     * Elipsoidal axis is automaticly added.
     * 
     * @param crs
     * @throws TransformException 
     */
    public synchronized void setObjectiveCRS(CoordinateReferenceSystem crs) throws TransformException {
        ArgumentChecks.ensureNonNull("crs", crs);
        
        VerticalCRS vertical = CRS.getVerticalCRS(crs);
        if(vertical == null){
            crs = new DefaultCompoundCRS("compound+vertical", crs, DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT);
        }
        
        
        this.objectiveCRS = crs;
        //clear cache
        objToDisp = null; 
        dispToObj = null;
        
        getTaskQueueManager().getQueue(GameTaskQueue.UPDATE).enqueue(new PlanUpdateTask());
    }

    public synchronized void setPlanView(boolean planView) {
        this.planView = planView;
        //clear cache
        objToDisp = null; 
        dispToObj = null;
        getTaskQueueManager().getQueue(GameTaskQueue.UPDATE).enqueue(new PlanUpdateTask());
    }

    public synchronized boolean isPlanView() {
        return planView;
    }

    /**
     * Return the current mathtransform from objectiveCRS to displayCRS
     * if the canvas is in planar view then the transform is identify otherwise
     * a transformation to geocentric is applied.
     */
    public MathTransform getObjectiveTo3DSpace() throws FactoryException{
        if(objToDisp != null){
            return objToDisp;
        }
        
        CoordinateReferenceSystem targetCRS;
        if(planView){
            targetCRS = objectiveCRS;
            
            if(!targetCRS.getCoordinateSystem().getAxis(0).getUnit().equals(NonSI.DEGREE_ANGLE)){
                //scale the projection
                final MathTransform scaletrs = MathTransforms.linear(3, 1d/1000000d, 0);
                targetCRS = new DefaultDerivedCRS("rescaled-projected", targetCRS, scaletrs, targetCRS.getCoordinateSystem());
            }
            
        }else{
            targetCRS = DefaultGeocentricCRS.CARTESIAN;
            //rescale metric projection
            final MathTransform scaletrs = MathTransforms.linear(3, 1d/1000000d, 0);
            targetCRS = new DefaultDerivedCRS("rescaled-geocentric", targetCRS, scaletrs, targetCRS.getCoordinateSystem());
        }
        
        objToDisp = CRS.findMathTransform(objectiveCRS, targetCRS);
        return objToDisp;
    }
    
    public MathTransform get3DSpaceToObjective() throws FactoryException, NoninvertibleTransformException{
        if(dispToObj == null){
            dispToObj = getObjectiveTo3DSpace().inverse();
        }
        
        return dispToObj;
    }
    
    public A3DController get3DController() {
        return controller;
    }

    @Override
    public CanvasController getController() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Container holds all nodes displayed in the canvas.
     * Use the TaskQueur
     * @return A3DContainer
     */
    public A3DContainer getA3DContainer() {
        return container;
    }

    @Override
    public AbstractContainer getContainer() {
        return null;
    }

    /**
     * @return AWT component to add in your frame.
     */
    public JoglAwtCanvas getComponent(){
        return canvas;
    }

    /**
     * Default task queue manager for this canvas.
     * Whenever there is a need to modify the scene ALWAYS do it in
     * this queue manager, the scene modification while occure at the appropriate
     * time in the rendering life cycle.
     * @return GameTaskQueueManager
     */
    public GameTaskQueueManager getTaskQueueManager(){
        return GameTaskQueueManager.getManager(queueManagerID);
    }
    
    private GLCanvas initContext() {

        final JoglCanvasRenderer renderer = new JoglCanvasRenderer(container);
        final DisplaySettings settings = new DisplaySettings(100, 100, 32, 0, 0, 32, 0, 4, false, false);
        final JoglAwtCanvas canvas = new JoglAwtCanvas(settings,renderer);
        canvas.setSize(new Dimension(100, 100));
        canvas.setPreferredSize(new Dimension(100,100));
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

        return canvas;
    }
    
    private class PlanUpdateTask implements Callable{

        @Override
        public Object call() throws Exception {
            if(basePlan != null){
                basePlan.removeFromParent();
            }

            basePlan = new Node("plan");
            //basePlan.getSceneHints().setLightCombineMode(LightCombineMode.Off);

            try{
                final MathTransform wgsToObj = CRS.findMathTransform(DefaultGeographicCRS.WGS84_3D, getObjectiveCRS());
                final MathTransform objTo3D = getObjectiveTo3DSpace();
                final MathTransform wgsTo3D = MathTransforms.concatenate(wgsToObj, objTo3D);

                int step = 10;
                final float width = 1f;

                //create a world grid
                final FloatBuffer verts = BufferUtils.createVector3Buffer( 
                        ((360/step)+1) * (180/step) * 6 //meridian lines
                        +
                        ((360/step)+1) * (180/step) * 6 //parallale lines
                        );
                final float[] buffer = new float[6];        
                for(int lon=-180;lon<=180;lon+=step){
                    for(int lat=-90;lat<90;lat+=step){
                        buffer[0] = lon;
                        buffer[1] = lat;
                        buffer[2] = 0;
                        buffer[3] = lon;
                        buffer[4] = lat+step;
                        buffer[5] = 0;
                        wgsTo3D.transform(buffer, 0, buffer, 0, 2);
                        verts.put(buffer);
                    }
                }
                for(int lat=-90;lat<=90;lat+=step){
                    for(int lon=-180;lon<180;lon+=step){
                        buffer[0] = lon;
                        buffer[1] = lat;
                        buffer[2] = 0;
                        buffer[3] = lon+step;
                        buffer[4] = lat;
                        buffer[5] = 0;
                        wgsTo3D.transform(buffer, 0, buffer, 0, 2);
                        verts.put(buffer);
                    }
                }


                final Line line = new Line("Lines", verts, null, null, null);
                line.getMeshData().setIndexMode(IndexMode.Lines);
                line.setLineWidth(width);
                line.setDefaultColor(ColorRGBA.GRAY);
                line.setModelBound(new BoundingBox());
                line.updateModelBound();
                final CullState cullFrontFace = new CullState();
                cullFrontFace.setEnabled(true);
                cullFrontFace.setCullFace(CullState.Face.Back);
                line.setRenderState(cullFrontFace);

                basePlan.attachChild(line);
                getA3DContainer().getScene().attachChild(basePlan);
            }catch(Exception ex){
                ex.printStackTrace();
            }
            
//            /** Set up a basic, default light. */
//            PointLight light = new PointLight();
//            light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
//            light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
//            light.setLocation(new Vector3(100, 100, 100));
//            light.setEnabled(true);
//
//            /** Attach the light to a lightState and the lightState to rootNode. */
//            LightState _lightState = new LightState();
//            _lightState.setEnabled(true);
//            _lightState.attach(light);
//            getA3DContainer().getRoot().setRenderState(_lightState);
            
            
            
            return null;
        }
        
    }
        
}
