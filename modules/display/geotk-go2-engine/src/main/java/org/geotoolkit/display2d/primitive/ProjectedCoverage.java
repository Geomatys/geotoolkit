/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.display2d.primitive;

import java.io.IOException;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotools.coverage.io.CoverageReadParam;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 *
 *
 * @author Johann Sorel (Geomatys)
 */
public interface ProjectedCoverage {

    CoverageMapLayer getCoverageLayer();

    GridCoverage2D getCoverage(CoverageReadParam param) throws FactoryException,IOException,TransformException;

    ProjectedGeometry getEnvelopeGeometry();

    GridCoverage2D getElevationCoverage(CoverageReadParam param) throws FactoryException,IOException,TransformException;

}
