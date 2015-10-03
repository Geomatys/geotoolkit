/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.display2d;

import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.util.WeakPropertyChangeListener;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Synchronize map canvas.
 *
 * @author Johann Sorel (Geomatys)
 */
public class Canvas2DSynchronizer implements PropertyChangeListener{

    private static final class CanvasState{
        private final boolean isSource;
        private final boolean isTarget;
        private final J2DCanvas canvas;

        public CanvasState(J2DCanvas canvas, boolean isSource, boolean isTarget) {
            this.isSource = isSource;
            this.isTarget = isTarget;
            this.canvas = canvas;
        }

    }

    private volatile boolean updating = false;
    private final List<CanvasState> canvas = Collections.synchronizedList(new ArrayList());

    public void addCanvas(J2DCanvas canvas, boolean isSource, boolean isTarget){
        this.canvas.add(new CanvasState(canvas, isSource, isTarget));
        if(isSource){
            //attach sync listener to update the other maps when needed
            new WeakPropertyChangeListener(canvas, this);
        }
    }

    /**
     * Listen to map movements and propagate to other maps.
     *
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(!J2DCanvas.TRANSFORM_KEY.equals(evt.getPropertyName())){
            return;
        }

        if(updating) return;

        updating = true;
        final J2DCanvas baseCanvas = (J2DCanvas) evt.getSource();
        final CoordinateReferenceSystem crs = baseCanvas.getObjectiveCRS();
        final AffineTransform centerTransform = baseCanvas.getCenterTransform();

        for(CanvasState state : canvas){
            if(state.canvas == baseCanvas || !state.isTarget) continue;
            try {
                state.canvas.setObjectiveCRS(crs);
                state.canvas.setCenterTransform(centerTransform);
            } catch (TransformException ex) {
                Logging.getLogger("org.geotoolkit.display2d").log(Level.INFO, ex.getMessage(),ex);
            }
        }
        updating = false;
    }

};
