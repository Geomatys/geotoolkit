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

import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.util.logging.Logging;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public abstract class ActionController {
    
    protected static final Logger LOGGER = Logging.getLogger(ActionController.class);
    protected final A3DCanvas canvas;

    public ActionController(final A3DCanvas canvas) {
        this.canvas = canvas;
    }
    
    /**
     * Position of the camera in 3d space.
     */
    public Vector3 getCamera3DSpacePosition() throws 
            FactoryException, MismatchedDimensionException, TransformException{
        final MathTransform objTo3DSpace = canvas.getObjectiveTo3DSpace();
        final DirectPosition posObjective = getCameraObjectivePosition();
        final DirectPosition pos3dSpace = objTo3DSpace.transform(posObjective, null);
        
        return new Vector3(
                pos3dSpace.getOrdinate(0), 
                pos3dSpace.getOrdinate(1), 
                pos3dSpace.getOrdinate(2));
    }
    
    /**
     * Pointing direction of the camera in 3d space.
     */
    public Vector3 getCamera3DSpaceDirection() throws 
            FactoryException, MismatchedDimensionException, TransformException{
        final MathTransform objTo3DSpace = canvas.getObjectiveTo3DSpace();
        final DirectPosition posObjective = getCameraObjectiveDirection();
        final DirectPosition pos3dSpace = objTo3DSpace.transform(posObjective, null);
        
        return new Vector3(
                pos3dSpace.getOrdinate(0), 
                pos3dSpace.getOrdinate(1), 
                pos3dSpace.getOrdinate(2));
    }
    
    /**
     * Vector toward the up direction.
     */
    public Vector3 getCamera3DSpaceUpAxis() throws 
            FactoryException, MismatchedDimensionException, TransformException{
        if(canvas.isPlanView()){
            //up axis is third one
            return new Vector3(0, 0, 1);
        }else{
            //we are on a globe, up axis depend on current position
            final MathTransform objTo3DSpace = canvas.getObjectiveTo3DSpace();
            final DirectPosition posObjective = getCameraObjectivePosition();
            final DirectPosition pos3dSpace = objTo3DSpace.transform(posObjective, null);
            final DirectPosition upObjective = getCameraObjectiveUpAxis();
            final DirectPosition up3dSpace = objTo3DSpace.transform(upObjective, null);
            
            final Vector3 origine = new Vector3(
                pos3dSpace.getOrdinate(0), 
                pos3dSpace.getOrdinate(1), 
                pos3dSpace.getOrdinate(2));
            final Vector3 up = new Vector3(
                up3dSpace.getOrdinate(0), 
                up3dSpace.getOrdinate(1), 
                up3dSpace.getOrdinate(2));
            
            up.subtractLocal(origine);
            up.normalizeLocal();
            return up;            
        }
    }
        
    /**
     * Get a position on the upward axis relative to the camera current position.
     */
    public DirectPosition getCameraObjectiveUpAxis(){
        final DirectPosition posObjective = getCameraObjectivePosition();
        final DirectPosition upObjective = new GeneralDirectPosition(posObjective);
        upObjective.setOrdinate(2, upObjective.getOrdinate(2)+1);
        return upObjective;
    }
    
    /**
     * Get camera position in canvas Objective CRS.
     */
    public abstract DirectPosition getCameraObjectivePosition();
    
    /**
     * Get camera pointing direction in canvas Objective CRS.
     */
    public abstract DirectPosition getCameraObjectiveDirection();
    
    public abstract void install(final LogicalLayer logicalLayer);
    
    public abstract void uninstall(final LogicalLayer logicalLayer);
    
    
}
