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
package org.geotoolkit.display3d.controller;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.jogl.JoglAwtCanvas;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.util.Constants;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.stat.StatCollector;
import org.geotoolkit.display3d.canvas.A3DCanvas;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class A3DController implements Updater {

    public static final double CAMERA_NEAR_PLAN = Math.nextUp(0);
    public static final double CAMERA_FAR_PLAN = 10000;
    
    private final A3DCanvas canvas;
    private final LogicalLayer logicalLayer;
    private ActionController actionController;


    public A3DController(final A3DCanvas canvas, final LogicalLayer logicalLayer) {
        this.canvas = canvas;
        this.logicalLayer = logicalLayer;
        setController(new DefaultActionController(canvas));
    }

    public synchronized void setController(final ActionController controller) {
        if(actionController != null){
            //uninstall previous controller
            actionController.uninstall(logicalLayer);
        }
        
        actionController = controller;
        
        if(actionController != null){
            //install new one
            actionController.install(logicalLayer);
        }
        
    }

    public synchronized ActionController getActionController() {
        return actionController;
    }

    public Camera getCamera(){
        JoglAwtCanvas nativ = canvas.getComponent();
        if(nativ != null && nativ.getCanvasRenderer() != null){
            return nativ.getCanvasRenderer().getCamera();
        }
        return null;
    }

    @MainThread
    @Override
    public void init() {
    }

    Camera lastCamera = null;

    @MainThread
    @Override
    public void update(final ReadOnlyTimer timer) {
        double tpf = timer.getTimePerFrame();

        /** update stats, if enabled. */
        if (Constants.stats) {
            StatCollector.update();
        }

        //ensure we dont have big movements
        if(tpf > 0.5) tpf = 0.5d;

        // check and execute any input triggers, if we are concerned with input
        logicalLayer.checkTriggers(tpf);

        // Execute updateQueue item
        canvas.getTaskQueueManager().getQueue(GameTaskQueue.UPDATE).execute();

        final Camera camera = getCamera();
        
        final ActionController controller = getActionController();
        if(controller != null)
        try{
            final Vector3 cameraPosition = controller.getCamera3DSpacePosition();
            final Vector3 cameraDirection = controller.getCamera3DSpaceDirection();
            final Vector3 cameraUpAxis = controller.getCamera3DSpaceUpAxis();
                        
            if(camera != null){
                //set camera frustrum if camera changed
                if(camera != lastCamera){
                    lastCamera = camera;
                    camera.setFrustumPerspective(
                        45.0,
                        (float) camera.getWidth()/ (float) camera.getHeight(),
                        CAMERA_NEAR_PLAN,
                        CAMERA_FAR_PLAN);
                    camera.lookAt(new Vector3(0, 0, 0), Vector3.UNIT_Y);
                }
                
                //update camera properties
                camera.setLocation(cameraPosition);
                camera.lookAt(cameraDirection, cameraUpAxis);
                
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        

        if(camera != null){
            canvas.getA3DContainer().update(camera,tpf,true);
        }

        /** Update controllers/render states/transforms/bounds for rootNode. */
        canvas.getA3DContainer().getRoot().updateGeometricState(timer.getTimePerFrame(), true);
    }

}
