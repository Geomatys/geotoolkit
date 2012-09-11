/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
import org.geotoolkit.display.canvas.AbstractReferencedCanvas2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;


/**
 * A graphic implementation with default support for Coordinate Reference System (CRS) management.
 * This class provides some methods specific to the GeotoolKit implementation of graphic primitive.
 * The {@link org.geotoolkit.display.canvas.ReferencedCanvas} expects instances of this class.
 *
 * @module pending
 * @since 2.3
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractReferencedGraphic extends AbstractGraphic implements ReferencedGraphic {
    
    /**
     * Constructs a new graphic.
     */
    protected AbstractReferencedGraphic(final AbstractReferencedCanvas2D canvas)
            throws IllegalArgumentException {
        super(canvas);
    }

    @Override
    public AbstractReferencedCanvas2D getCanvas() {
        return (AbstractReferencedCanvas2D) super.getCanvas();
    }

    @Override
    public Envelope getEnvelope() {
        return null;
    }

    @Override
    public boolean intersects(final Envelope candidateEnvelope) {
        final Envelope graphicEnv = getEnvelope();
        if(graphicEnv == null){
            return true;
        }
                
        final CoordinateReferenceSystem graphicCRS = graphicEnv.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem candidateCRS = candidateEnvelope.getCoordinateReferenceSystem();
        
        
        if(CRS.equalsIgnoreMetadata(graphicCRS, candidateCRS)){
            final GeneralEnvelope genv = new GeneralEnvelope(graphicCRS);
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
    @Override
    public abstract List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics);

}
