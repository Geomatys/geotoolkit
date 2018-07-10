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
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.CoverageWriter;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.content.CoverageDescription;
import org.geotoolkit.storage.coverage.GridCoverageResource;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class QueryableAggregate extends WMSAggregate implements GridCoverageResource {

    final AbstractLayer layer;
    final WMSCoverageResource queryableResource;

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
        queryableResource = new WMSCoverageResource(client, layer.getName());
    }

    @Override
    public NamedIdentifier getIdentifier() {
        return name;
    }

    @Override
    public int getImageIndex() {
        return queryableResource.getImageIndex();
    }

    @Override
    public CoverageDescription getCoverageDescription() {
        return queryableResource.getCoverageDescription();
    }

    @Override
    public boolean isWritable() throws DataStoreException {
        return queryableResource.isWritable();
    }

    @Override
    public DataStore getStore() {
        return queryableResource.getStore();
    }

    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        return queryableResource.acquireReader();
    }

    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        return queryableResource.acquireWriter();
    }

    @Override
    public void recycle(CoverageReader reader) {
        queryableResource.recycle(reader);
    }

    @Override
    public void recycle(CoverageWriter writer) {
        queryableResource.recycle(writer);
    }

    @Override
    public Image getLegend() throws DataStoreException {
        return queryableResource.getLegend();
    }

    @Override
    public void addStorageListener(StorageListener listener) {
        queryableResource.addStorageListener(listener);
    }

    @Override
    public void removeStorageListener(StorageListener listener) {
        queryableResource.removeStorageListener(listener);
    }

    @Override
    public Envelope getEnvelope() throws DataStoreException {
        return queryableResource.getEnvelope();
    }
}
