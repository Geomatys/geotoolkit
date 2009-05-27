/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.map;

import java.io.IOException;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotools.coverage.io.CoverageReadParam;
import org.geotools.coverage.io.CoverageReader;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * Wrapper class around a Gridcoverage2D.
 *
 * @author Johann Sorel (Geomtays)
 */
public class SimpleCoverageReader implements CoverageReader{

    private final GridCoverage2D coverage;

    public SimpleCoverageReader(GridCoverage2D coverage){
        this.coverage = coverage;
    }

    @Override
    public GridCoverage2D read(CoverageReadParam param) throws FactoryException, TransformException, IOException {
        return coverage;
    }

    @Override
    public Envelope getCoverageBounds() {
        return coverage.getEnvelope();
    }

}
