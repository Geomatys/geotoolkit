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
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.ImmutableEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.style.MutableStyle;
import org.opengis.geometry.Envelope;

/**
 * Default implementation of the coverage MapLayer.
 * Use MapBuilder to create it.
 * This class is left public only for subclass implementations.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public final class CoverageMapLayer extends AbstractMapLayer {

    private static final ImmutableEnvelope INFINITE = new ImmutableEnvelope(
            new double[] {-180, -90}, new double[] {180, 90}, CommonCRS.WGS84.normalizedGeographic());

    private Query query = null;

    protected CoverageMapLayer(final GridCoverageResource ref, final MutableStyle style){
        super(ref);
        if(ref == null){
            throw new NullArgumentException("Coverage reference can not be null.");
        }
        this.resource = ref;
        setStyle(style);

        trySetName(ref);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GridCoverageResource getResource() {
        return (GridCoverageResource) resource;
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
        final GridCoverageResource ref = getResource();
        // Resource possibly contains the data envelope. We start here, because it's supposed to be the most economic
        // way to get back the envelope.
        // TODO: use an expansible list of strategies, and factorize possible cases in super-class.
        Envelope env = null;
        try {
            env = ref.getEnvelope().orElse(null);
        } catch (DataStoreException e) {
            LOGGER.log(Level.WARNING, "Cannot access resource envelope.", e);
        }
        if (env != null) {
            return env;
        }
        try {
            final GridGeometry geom = ref.getGridGeometry();
            if (geom == null) {
                LOGGER.log(Level.WARNING, "Could not access envelope of layer {0}", getResource().getIdentifier().orElse(null));
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
