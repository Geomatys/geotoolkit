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

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;

import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.display.canvas.ReferencedCanvas;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;


/**
 * A graphic implementation with default support for Coordinate Reference System (CRS) management.
 * This class provides some methods specific to the GeotoolKit implementation of graphic primitive.
 * The {@link org.geotoolkit.display.canvas.ReferencedCanvas} expects instances of this class.
 *
 * @module pending
 * @since 2.3
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 */
public interface ReferencedGraphic extends Graphic {
    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * canvas {@linkplain ReferencedCanvas#getObjectiveCRS objective CRS} changed.
     */
    public static final String OBJECTIVE_CRS_PROPERTY = "objectiveCRS";
    
    /**
     * The name of the {@linkplain PropertyChangeEvent property change event}
     * fired when the {@linkplain ReferencedCanvas#getEnvelope canvas envelope} or
     * {@linkplain ReferencedGraphic#getEnvelope graphic envelope} changed.
     */
    public static final String ENVELOPE_PROPERTY = "envelope";
    
    ReferencedCanvas getCanvas();

    /**
     * Returns an envelope that completly encloses the graphic. Note that there is no guarantee
     * that the returned envelope is the smallest bounding box that encloses the graphic, only
     * that the graphic lies entirely within the indicated envelope.
     * <p>
     * The default implementation returns a {@linkplain GeneralEnvelope#setToNull null envelope}.
     * Subclasses should compute their envelope and invoke {@link #setEnvelope} as soon as they can.
     *
     * @see #setEnvelope
     */
    Envelope getEnvelope();

    boolean intersects(final Envelope candidateEnvelope);
    
    /**
     * Use to grab an ReferencedGraphic at some position.
     * 
     * @param point : point in display crs
     * @return ReferencedGraphic, can be this object or a child object
     */
    List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics);

}
