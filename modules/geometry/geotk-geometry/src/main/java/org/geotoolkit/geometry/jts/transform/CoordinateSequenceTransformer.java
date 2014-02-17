/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry.jts.transform;


import com.vividsolutions.jts.geom.CoordinateSequence;

import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;


/**
 * Interface that should be implemented by classes able to apply the provided
 * {@linkplain MathTransform transformation} to a
 * {@linkplain CoordinateSequence coordinate sequence}.
 *
 * @module pending
 * @since 2.1
 * @version $Id$
 * @author Andrea Aime
 */
public interface CoordinateSequenceTransformer {
    /**
     * Returns a transformed coordinate sequence.
     *
     * @param  sequence The sequence to transform.
     * @throws TransformException if at least one coordinate can't be transformed.
     */
    public CoordinateSequence transform(CoordinateSequence sequence, int minpoints) throws TransformException;
    
}
