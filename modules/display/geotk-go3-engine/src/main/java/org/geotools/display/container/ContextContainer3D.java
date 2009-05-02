/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotools.display.container;

import java.awt.geom.Rectangle2D;
import org.geotoolkit.display.canvas.ReferencedCanvas;
import org.geotoolkit.display.container.AbstractContainer;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.opengis.display.primitive.Graphic;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author axel
 */
public class ContextContainer3D extends AbstractContainer {

    public ContextContainer3D(ReferencedCanvas canvas) {
    super(canvas);
    }

    @Override
    public GeneralEnvelope getGraphicsEnvelope() {
        return null;
    }

    @Override
    protected void updateObjectiveCRS(CoordinateReferenceSystem crs) throws TransformException {

    }

    @Override
    public synchronized Graphic add(Graphic graphic) throws IllegalArgumentException {
        return super.add(graphic);
    }





}
