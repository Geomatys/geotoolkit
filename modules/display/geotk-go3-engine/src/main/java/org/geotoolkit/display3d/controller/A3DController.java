/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.display3d.controller;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.awt.AwtCanvas;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.util.Constants;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.ReadOnlyTimer;

import com.ardor3d.util.stat.StatCollector;
import org.geotoolkit.display3d.canvas.A3DCanvas;

import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class A3DController implements Updater,CanvasController3D {

    private final A3DCanvas canvas;
    private final LogicalLayer logicalLayer;
    //variable to update camera on next canvas update
    private final Vector3 updateLocation = new Vector3(0, 0, 0);
    private final LocationSensitiveUpdater locationUpdater;
    private final CameraControl controller;


    public A3DController(final A3DCanvas canvas, final LogicalLayer logicalLayer) {
        this.canvas = canvas;
        this.logicalLayer = logicalLayer;
        controller = CameraControl.setupTriggers(logicalLayer, new Vector3(0, 1, 0), true);

        locationUpdater = new  LocationSensitiveUpdater(canvas.getContainer2());
        locationUpdater.setPriority(Thread.MIN_PRIORITY);
        locationUpdater.start();
    }

    @Override
    public double[] getCameraPosition() {
        Camera cam = getCamera();

        if(cam != null){
            double[] coords = new double[3];
            coords[0] = cam.getLocation().getX();
            coords[1] = cam.getLocation().getY();
            coords[2] = cam.getLocation().getZ();
            return coords;
        }

        return null;
    }

    @Override
    public void setCameraPosition(double x, double y, double z) {
        synchronized(updateLocation){
            updateLocation.setX(x);
            updateLocation.setY(y);
            updateLocation.setZ(z);
        }
    }

    @Override
    public void setCameraSpeed(double speed) {
        controller.setMoveSpeed(speed);
    }

    private Camera getCamera(){
        AwtCanvas nativ = canvas.getNativeCanvas();
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
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.UPDATE).execute();

        Camera camera = getCamera();
        synchronized (updateLocation) {

            if(camera != null && camera != lastCamera){
                lastCamera = camera;
                camera.setFrustumPerspective(
                    45.0,
                    (float) camera.getWidth()/ (float) camera.getHeight(),
                    1,
                    100000);
                camera.lookAt(new Vector3(0, 0, 0), Vector3.UNIT_Y);
            }


            if ((updateLocation.getX() != 0 ||
                    updateLocation.getY() != 0 ||
                    updateLocation.getZ() != 0) &&
                    camera != null) {
                camera.setLocation(updateLocation);
                updateLocation.setX(0);
                updateLocation.setY(0);
                updateLocation.setZ(0);
            }

        }


        if(camera != null){
            locationUpdater.updateCameraLocation(camera.getLocation());
            canvas.getContainer2().update(camera,tpf,true);
        }


        /** Update controllers/render states/transforms/bounds for rootNode. */
        canvas.getContainer2().getRoot().updateGeometricState(timer.getTimePerFrame(), true);
    }

    @Override
    public void setTitle(InternationalString arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCenter(DirectPosition arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setObjectiveCRS(CoordinateReferenceSystem arg0) throws TransformException {
        canvas.setObjectiveCRS(arg0);
    }

    @Override
    public CoordinateReferenceSystem getObjectiveCRS() {
        return canvas.getObjectiveCRS();
    }

    @Override
    public void addLocationSensitiveGraphic(LocationSensitiveGraphic graphic, double distance) {
        locationUpdater.put(graphic, distance);
    }

}
