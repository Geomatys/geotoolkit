/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wms;

import java.awt.Image;
import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class QueryableAggregate extends WMSAggregate implements GridCoverageResource, StoreResource {

    final AbstractLayer layer;
    final WMSResource queryableResource;

    final NamedIdentifier name;

    public QueryableAggregate(final WebMapClient client, final AbstractLayer layer) throws CoverageStoreException {
        super(client, layer);
        if (layer.getName() == null) {
            throw new CoverageStoreException("Cannot create a queryable resource over an unnamed layer.");
        } else if (!layer.isQueryable()) {
            throw new CoverageStoreException("Cannot create a queryable resource over an unqueryable layer.");
        }
        this.layer = layer;
        name = new NamedIdentifier(Names.createScopedName(null, ":", layer.getName()));
        queryableResource = new WMSResource(client, layer.getName());
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return queryableResource.getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return queryableResource.getSampleDimensions();
    }

    @Override
    public DataStore getOriginator() {
        return queryableResource.getOriginator();
    }

    public Image getLegend() throws DataStoreException {
        return queryableResource.getLegend();
    }

    @Override
    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
        queryableResource.addListener(eventType, listener);
    }

    @Override
    public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
        queryableResource.removeListener(eventType, listener);
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return queryableResource.getEnvelope();
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        return queryableResource.read(domain, range);
    }
}
