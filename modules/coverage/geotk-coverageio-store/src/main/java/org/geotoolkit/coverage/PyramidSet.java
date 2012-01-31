/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.coverage;

import java.util.Collection;
import java.util.List;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Define a collection of pyramid for a give data.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface PyramidSet {
    
    /**
     * Additional hint : to specify the mime type.
     */
    public static final String HINT_FORMAT = "format";
    
    /**
     * @return unique id.
     */
    String getId();
    
    /**
     * 
     * @return Collection of pyramid, each pyramid has a different CRS.
     */
    Collection<Pyramid> getPyramids();
    
    /**
     * List of format mime types handle by this pyramid set.
     * @return List<String>
     */
    List<String> getFormats();
    
    /**
     * This envelope is not exact, it is approximative.
     * @return global envelope of all pyramids
     */
    Envelope getEnvelope();
    
}
