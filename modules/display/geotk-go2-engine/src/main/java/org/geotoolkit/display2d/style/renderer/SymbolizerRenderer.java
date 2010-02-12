/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display2d.style.renderer;

import java.util.Iterator;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;


/**
 * A symbolizer renderer is capable to paint a given symbolizer on a java2d
 * canvas.
 *
 * To perform some visual intersection test using the hit methods.
 *
 * And you can generate glyphs using the glyph method.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface SymbolizerRenderer {

    /**
     * Paint the graphic object using the cached symbolizer and the rendering parameters.
     *
     * @param graphic : cached graphic representation of a feature
     * @throws PortrayalException
     */
    void portray(ProjectedFeature graphic) throws PortrayalException;

    /**
     * Paint in one iteration a complete set of features.
     * 
     * @param graphics : iterator over all graphics to render
     * @throws PortrayalException
     */
    void portray(Iterator<ProjectedFeature> graphics) throws PortrayalException;

    /**
     * Paint the graphic object using the cached symbolizer and the rendering parameters.
     *
     * @param graphic : cached graphic representation of a coverage
     * @throws PortrayalException
     */
    void portray(ProjectedCoverage graphic) throws PortrayalException;

    /**
     * Test if the graphic object hit the given search area.
     *
     * @param graphic : cached graphic representation of a feature
     * @param mask : search area, it can represent a mouse position or a particular shape
     * @param filter : the type of searching, intersect or within
     * @return true if the searcharea hit this graphic object, false otherwise.
     */
    boolean hit(ProjectedFeature graphic, SearchAreaJ2D mask, VisitFilter filter);

    /**
     * Test if the graphic object hit the given search area.
     *
     * @param graphic : cached graphic representation of a coverage
     * @param mask : search area, it can represent a mouse position or a particular shape
     * @param filter : the type of searching, intersect or within
     * @return true if the searcharea hit this graphic object, false otherwise.
     */
    boolean hit(ProjectedCoverage graphic, SearchAreaJ2D mask, VisitFilter filter);

}
