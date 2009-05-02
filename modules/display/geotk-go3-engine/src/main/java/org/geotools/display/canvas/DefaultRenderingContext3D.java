/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotools.display.canvas;

import java.awt.geom.AffineTransform;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.opengis.display.canvas.Canvas;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author axel
 */
public class DefaultRenderingContext3D implements RenderingContext3D {

    @Override
    public Canvas getCanvas() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CoordinateReferenceSystem getObjectiveCRS() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CoordinateReferenceSystem getDisplayCRS() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setGraphicsCRS(CoordinateReferenceSystem crs) throws TransformException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AffineTransform getAffineTransform(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS) throws FactoryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MathTransform getMathTransform(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS) throws FactoryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CanvasMonitor getMonitor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
