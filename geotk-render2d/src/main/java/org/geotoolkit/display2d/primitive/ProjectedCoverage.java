/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2013, Geomatys
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

import java.util.logging.Level;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.GridCoverageStack;
import org.opengis.filter.Expression;

/**
 * Convenient representation of a coverage for rendering.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ProjectedCoverage implements ProjectedObject<MapLayer> {

    private final MapLayer layer;

    public ProjectedCoverage(final MapLayer layer) {
        this.layer = layer;
    }

    public void clearObjectiveCache(){
    }

    public void clearDisplayCache(){
    }

    /**
     * Get the original CoverageMapLayer from where the feature is from.
     *
     * @return CoverageMapLayer
     */
    @Override
    public MapLayer getLayer() {
        return layer;
    }

    /**
     * Return internal coverage.
     *
     * @param param : expected output geometry.
     * @return A coverage covering requested area, or at least a piece of it, in case queried area is not completely
     * contained in this data. Never null.
     *
     * @throws DataStoreException if reading fails for specified geometry. The error can be a {@link org.apache.sis.coverage.grid.DisjointExtentException}
     * if queried area is completely outside this coverage extent.
     */
    public GridCoverage getCoverage(final GridGeometry param, int... bands) throws DataStoreException {
        Resource resource = layer.getData();
        if (resource instanceof GridCoverageResource) {
            GridCoverage result = ((GridCoverageResource)resource).read(param, (bands == null || bands.length < 1)? null : bands);
            if (result instanceof GridCoverageStack) {
                Logging.getLogger("org.geotoolkit.display2d.primitive").log(Level.WARNING, "Coverage reader return more than one slice.");
            }
            while (result instanceof GridCoverageStack) {
                //pick the first slice
                result = ((GridCoverageStack)result).coverageAtIndex(0);
            }
            return result;
        } else {
            throw new DataStoreException("Resource is not a coverage" + resource);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isVisible() {
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setVisible(final boolean visible) {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
    }

    @Override
    public MapLayer getCandidate() {
        return layer;
    }

    @Override
    public ProjectedGeometry getGeometry(Expression name) {
        throw new UnsupportedOperationException("Not supported.");
    }

}
