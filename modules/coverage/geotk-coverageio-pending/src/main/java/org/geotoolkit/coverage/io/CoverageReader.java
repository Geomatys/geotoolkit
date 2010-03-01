/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.coverage.io;

import java.io.IOException;

import org.geotoolkit.coverage.grid.GridCoverage2D;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * Coverage Reader provide a convinient way to obtain a gridCoverage with 
 * different parameters.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 *
 * @deprecated Replaced by {@link GridCoverageReader}.
 */
@Deprecated
public interface CoverageReader {
    
    /**
     * Obtain a GridCoverage matching the requested parameters.
     * 
     * @param param : coverage parameters, resolution ...
     * @return GridCoverage2D
     * @throws org.opengis.referencing.FactoryException
     * @throws org.opengis.referencing.operation.TransformException
     * @throws java.io.IOException
     */
    public GridCoverage2D read(CoverageReadParam param)
            throws FactoryException, TransformException, IOException;
    
    /**
     * Get the envelope of the coverage.
     * @return Referenced Envelope
     */
    public Envelope getCoverageBounds();
    
}
