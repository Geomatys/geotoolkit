/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.display.primitive;

import java.util.List;
import java.util.logging.Level;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.canvas.Canvas;
import org.geotoolkit.referencing.CRS;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class SpatialNode extends SceneNode{

    public SpatialNode(Canvas canvas) {
        super(canvas);
    }

    public SpatialNode(Canvas canvas, boolean allowChildren) {
        super(canvas, allowChildren);
    }

    /**
     * Returns an envelope that completely encloses the graphic. Note that there is no guarantee
     * that the returned envelope is the smallest bounding box that encloses the graphic, only
     * that the graphic lies entirely within the indicated envelope.
     * <p>
     * The default implementation returns a {@linkplain GeneralEnvelope#setToNull null envelope}.
     * Subclasses should compute their envelope and invoke {@link #setEnvelope} as soon as they can.
     *
     * @see #setEnvelope
     */
    public abstract Envelope getEnvelope();

    public boolean intersects(final Envelope candidateEnvelope){
        final Envelope graphicEnv = getEnvelope();
        if(graphicEnv == null){
            return true;
        }

        final CoordinateReferenceSystem graphicCRS = graphicEnv.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem candidateCRS = candidateEnvelope.getCoordinateReferenceSystem();


        if(CRS.equalsIgnoreMetadata(graphicCRS, candidateCRS)){
            final GeneralEnvelope genv = new GeneralEnvelope(graphicEnv);
            return genv.intersects(candidateEnvelope, true);
        }else{
            try {
                final GeneralEnvelope genv = new GeneralEnvelope(CRS.transform(graphicEnv, candidateCRS));
                return genv.intersects(candidateEnvelope, true);
            } catch (TransformException ex) {
                getLogger().log(Level.FINE, ex.getMessage(),ex);
                //we tryed
                return true;
            }
        }
    }

    /**
     * Use to grab an ReferencedGraphic at some position.
     *
     * @param point : point in display crs
     * @return ReferencedGraphic, can be this object or a child object
     */
    public abstract List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics);

}
