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
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.map.CoverageMapLayer;

import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * Convenient representation of a coverage for rendering.
 * We expect the sub classes to cache information for more efficient rendering.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface ProjectedCoverage extends ProjectedObject<CoverageMapLayer> {

    /**
     * Get the original CoverageMapLayer from where the feature is from.
     *
     * @return CoverageMapLayer
     */
    @Override
    CoverageMapLayer getLayer();

    /**
     * Get a coverage reference.
     *
     * @param param : expected coverage parameters
     * @return GridCoverage2D or null if the requested parameters are out of the coverage area.
     *
     * @throws FactoryException
     * @throws IOException
     * @throws TransformException
     */
    GridCoverage2D getCoverage(GridCoverageReadParam param) throws CoverageStoreException;

    /**
     * Get the projecte geometry representation of the coverage border.
     *
     * @return ProjectedGeometry
     */
    ProjectedGeometry getEnvelopeGeometry();

    /**
     * Get a coverage reference for the elevation model.
     *
     * @param param : expected coverage parameters
     * @return GridCoverage2D or null if the requested parameters are out of the coverage area.
     *
     * @throws FactoryException
     * @throws IOException
     * @throws TransformException
     */
    GridCoverage2D getElevationCoverage(GridCoverageReadParam param) throws CoverageStoreException;


}
