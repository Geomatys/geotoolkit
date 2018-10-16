/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2013, Geomatys
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

import java.util.logging.Level;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.query.Query;
import org.apache.sis.geometry.ImmutableEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.style.MutableStyle;
import org.apache.sis.util.NullArgumentException;
import org.opengis.geometry.Envelope;
import org.apache.sis.util.logging.Logging;
import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;
import org.geotoolkit.storage.coverage.GridCoverageResource;

/**
 * Default implementation of the coverage MapLayer.
 * Use MapBuilder to create it.
 * This class is left public only for subclass implementations.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class DefaultCoverageMapLayer extends AbstractMapLayer implements CoverageMapLayer {

    private static final ImmutableEnvelope INFINITE = new ImmutableEnvelope(
            new double[] {-180, -90}, new double[] {180, 90}, CommonCRS.WGS84.normalizedGeographic());

    private final GridCoverageResource ref;
    private Query query = null;

    protected DefaultCoverageMapLayer(final GridCoverageResource ref, final MutableStyle style){
        super(style);
        if(ref == null){
            throw new NullArgumentException("Coverage reference can not be null.");
        }
        this.ref = ref;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GridCoverageResource getCoverageReference() {
        return ref;
    }

    /**
     * Returns the query, may be {@code null}.
     */
    public Query getQuery() {
        return query;
    }

    /**
     * Sets a filter query for this layer.
     *
     * <p>
     * Query filters should be used to reduce searched or displayed feature
     * when rendering or analyzing this layer.
     * </p>
     *
     * @param query the full filter for this layer. can not be null.
     */
    public void setQuery(final Query query) {
        ensureNonNull("query", query);

        final Query oldQuery;
        synchronized (this) {
            oldQuery = getQuery();
            if(query.equals(oldQuery)){
                return;
            }
            this.query = query;
        }
        firePropertyChange(QUERY_PROPERTY, oldQuery, this.query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds() {
        if(ref != null && ref instanceof PyramidalCoverageResource){
            try {
                return ((PyramidalCoverageResource)ref).getPyramidSet().getEnvelope();
            } catch (DataStoreException ex) {
                Logging.getLogger("org.geotoolkit.map").log(Level.SEVERE, null, ex);
            }
        }

        final GridCoverageResource ref = getCoverageReference();
        try {
            GridCoverageReader reader = ref.acquireReader();
            final GeneralGridGeometry geom = reader.getGridGeometry(getCoverageReference().getImageIndex());
            ref.recycle(reader);
            if (geom == null) {
                LOGGER.log(Level.WARNING, "Could not access envelope of layer {0}", getCoverageReference().getIdentifier());
                return INFINITE;
            } else {
                return geom.getEnvelope();
            }
        } catch (DataStoreException ex) {
            LOGGER.log(Level.WARNING, null, ex);
            return INFINITE;
        }
    }

}
