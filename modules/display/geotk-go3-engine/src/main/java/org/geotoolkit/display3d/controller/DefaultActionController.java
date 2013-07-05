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

import com.ardor3d.framework.Canvas;
import com.ardor3d.input.Key;
import com.ardor3d.input.KeyboardState;
import com.ardor3d.input.MouseState;
import com.ardor3d.input.logical.*;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.logging.Level;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class DefaultActionController extends ActionController{

    private double rotateSpeed = 0.005;
    private double moveSpeed = 1;
    private CoordinateReferenceSystem oldCRS = null;
    private MathTransform oldTRS = null;
    private GeneralDirectPosition position = null;
    private GeneralDirectPosition direction = null;
    
    private final InputTrigger mouseDragTrigger;
    private final InputTrigger keyMoveTrigger;
    
    
    public DefaultActionController(final A3DCanvas canvas) {
        super(canvas);
        
        //create the different input triggers
        final Predicate<TwoInputStates> someMouseDown = Predicates.or(
                TriggerConditions.leftButtonDown(),
                TriggerConditions.rightButtonDown(), 
                TriggerConditions.middleButtonDown());
        
        final Predicate<TwoInputStates> dragged = Predicates.and(TriggerConditions.mouseMoved(), someMouseDown);
        
        final TriggerAction dragAction = new TriggerAction() {
            
            @Override
            public void perform(Canvas source, TwoInputStates inputState, double arg2) {
                checkCRS();
                try{
                    final MouseState mouse = inputState.getCurrent().getMouseState();
                    final int dx = -mouse.getDx();
                    final int dy = -mouse.getDy();
                    
                    if (dx != 0 || dy != 0) {
                        final Camera camera = canvas.get3DController().getCamera();
                        final Matrix3 _workerMatrix = new Matrix3();
                        final Vector3 _workerStoreA = new Vector3();
                        final Vector3 up = getCamera3DSpaceUpAxis();
                        
                        if(dx != 0){
                            _workerMatrix.fromAngleNormalAxis(rotateSpeed * dx, up);
                            _workerMatrix.applyPost(camera.getLeft(), _workerStoreA);
                            camera.setLeft(_workerStoreA);
                            _workerMatrix.applyPost(camera.getDirection(), _workerStoreA);
                            camera.setDirection(_workerStoreA);
                            _workerMatrix.applyPost(camera.getUp(), _workerStoreA);
                            camera.setUp(_workerStoreA);
                        }
                        
                        if(dy != 0 ){
                            // apply dy angle change to direction vector
                            _workerMatrix.fromAngleNormalAxis(rotateSpeed * dy, camera.getLeft());
                            _workerMatrix.applyPost(camera.getDirection(), _workerStoreA);
                            camera.setDirection(_workerStoreA);
                        }
                        
                        camera.normalize();
                        updateObjective();                        
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        };
        
        mouseDragTrigger = new InputTrigger(dragged , dragAction);
        
        
        final Predicate<TwoInputStates> keysHeld = new Predicate<TwoInputStates>() {
            Key[] keys = new Key[] { Key.PAGEDOWN_NEXT, Key.PAGEUP_PRIOR, Key.LEFT, Key.RIGHT, Key.UP, Key.DOWN };

            @Override
            public boolean apply(final TwoInputStates states) {
                for (final Key k : keys) {
                    if (states.getCurrent() != null && states.getCurrent().getKeyboardState().isDown(k)) {
                        return true;
                    }
                }
                return false;
            }
        };
        final TriggerAction moveAction = new TriggerAction() {
            @Override
            public void perform(Canvas source, TwoInputStates inputState, double tpf) {
                
                final KeyboardState kb = inputState.getCurrent().getKeyboardState();
                final Camera camera = canvas.get3DController().getCamera();
                final Vector3 _workerStoreA = new Vector3();
                final Vector3 _workerStoreB = new Vector3();
                
                // MOVEMENT
                int moveFB = 0, strafeLR = 0;
                if (kb.isDown(Key.UP)) {
                    moveFB += 1;
                }
                if (kb.isDown(Key.DOWN)) {
                    moveFB -= 1;
                }
                if (kb.isDown(Key.LEFT)) {
                    strafeLR += 1;
                }
                if (kb.isDown(Key.RIGHT)) {
                    strafeLR -= 1;
                }

                final Vector3 loc = _workerStoreA.set(camera.getLocation());
                if (moveFB != 0) {
                    loc.addLocal(_workerStoreB.set(camera.getDirection()).multiplyLocal(moveFB * moveSpeed * tpf));
                }
                if (strafeLR != 0) {
                    loc.addLocal(_workerStoreB.set(camera.getLeft()).multiplyLocal(strafeLR * moveSpeed * tpf));
                }
                camera.setLocation(loc);
                updateObjective();       
            }
        };
        
        keyMoveTrigger = new InputTrigger(keysHeld, moveAction);        
        
    }

    @Override
    public GeneralDirectPosition getCameraObjectivePosition() {
        checkCRS();
        return position;
    }

    @Override
    public GeneralDirectPosition getCameraObjectiveDirection() {
        checkCRS();
        return direction;
    }

    @Override
    public void install(final LogicalLayer logicalLayer) {        
        logicalLayer.registerTrigger(mouseDragTrigger);
        logicalLayer.registerTrigger(keyMoveTrigger);
    }

    @Override
    public void uninstall(final LogicalLayer logicalLayer) {
        logicalLayer.deregisterTrigger(mouseDragTrigger);
        logicalLayer.deregisterTrigger(keyMoveTrigger);
    }
    
    private void checkCRS(){
        
        try{
            final CoordinateReferenceSystem newCRS = canvas.getObjectiveCRS();
            final MathTransform newTRS = canvas.getObjectiveTo3DSpace();

            if(oldTRS == null){
                oldTRS = newTRS;
                oldCRS = newCRS;
                position = new GeneralDirectPosition(newCRS);
                direction = new GeneralDirectPosition(newCRS);
                direction.setOrdinate(0, 1);
            }else{
                //check if the crs changed
                if(oldTRS != newTRS){
                    try {
                        //reproject points
                        final MathTransform trs = CRS.findMathTransform(oldCRS, newCRS);
                        trs.transform(position, position);
                        trs.transform(direction, direction);

                    } catch (FactoryException ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    } catch (TransformException ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    }
                    oldCRS = newCRS;
                    oldTRS = newTRS;
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
    
    /**
     * Update the objective camera information from the current camera configuration
     */
    public void updateObjective(){
        final Camera camera = canvas.get3DController().getCamera();
        if(camera == null){
            return;
        }
        try {
            final MathTransform dispToObj = canvas.get3DSpaceToObjective();
            
            final ReadOnlyVector3 pos = camera.getLocation();
            final Vector3 dir = new Vector3(camera.getDirection());
            dir.addLocal(pos);
            
            final GeneralDirectPosition tmp = new GeneralDirectPosition(getCameraObjectivePosition());
            tmp.setOrdinate(0, pos.getX());
            tmp.setOrdinate(1, pos.getY());
            tmp.setOrdinate(2, pos.getZ());
            dispToObj.transform(tmp, tmp);
            final GeneralDirectPosition objpos = getCameraObjectivePosition();
            objpos.setLocation(tmp);
            
            tmp.setOrdinate(0, dir.getX());
            tmp.setOrdinate(1, dir.getY());
            tmp.setOrdinate(2, dir.getZ());
            dispToObj.transform(tmp, tmp);
            final GeneralDirectPosition objdir = getCameraObjectiveDirection();
            objdir.setLocation(tmp);
                        
            
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
        
    }
    
}
