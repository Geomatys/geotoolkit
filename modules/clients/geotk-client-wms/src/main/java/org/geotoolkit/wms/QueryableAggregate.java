package org.geotoolkit.wms;

import java.awt.Image;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.storage.coverage.CoverageResource;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.util.GenericName;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class QueryableAggregate extends WMSAggregate implements CoverageResource {

    final AbstractLayer layer;
    final WMSCoverageResource queryableResource;

    final GenericName name;

    public QueryableAggregate(final WebMapClient client, final AbstractLayer layer) throws CoverageStoreException {
        super(client, layer);
        if (layer.getName() == null) {
            throw new CoverageStoreException("Cannot create a queryable resource over an unnamed layer.");
        } else if (!layer.isQueryable()) {
            throw new CoverageStoreException("Cannot create a queryable resource over an unqueryable layer.");
        }
        this.layer = layer;
        name = Names.createScopedName(null, ":", layer.getName());
        queryableResource = new WMSCoverageResource(client, layer.getName());
    }

    @Override
    public GenericName getName() {
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
    public CoverageStore getStore() {
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
    public void recycle(GridCoverageWriter writer) {
        queryableResource.recycle(writer);
    }

    @Override
    public Image getLegend() throws DataStoreException {
        return queryableResource.getLegend();
    }

    @Override
    public Identifier getIdentifier() {
        return queryableResource.getIdentifier();
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
