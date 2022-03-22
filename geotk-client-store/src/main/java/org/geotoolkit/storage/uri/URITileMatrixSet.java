/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.storage.uri;

import java.net.URI;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.multires.AbstractTileMatrixSet;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.storage.multires.WritableTileMatrix;
import org.geotoolkit.storage.multires.WritableTileMatrixSet;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 * Tile matrix set with resources located by URI.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class URITileMatrixSet extends AbstractTileMatrixSet implements WritableTileMatrixSet {

    protected final URI root;
    protected final URITileFormat tileFormat;
    protected final ClientSecurity security;

    /**
     * @param tilematrixSetId not null
     * @param root not null
     * @param format not null
     * @param crs not null
     * @throws DataStoreException
     */
    public URITileMatrixSet(GenericName tilematrixSetId, URI root, ClientSecurity security, URITileFormat format, CoordinateReferenceSystem crs) throws DataStoreException {
        super(tilematrixSetId, crs);
        ArgumentChecks.ensureNonNull("root", root);
        ArgumentChecks.ensureNonNull("crs", crs);
        ArgumentChecks.ensureNonNull("format", format);
        this.root = root;
        this.tileFormat = format;
        this.security = security;
    }

    public URI getFolder() {
        return root;
    }

    public URITileFormat getTileFormat() {
        return tileFormat;
    }

    @Override
    public WritableTileMatrix createTileMatrix(TileMatrix template) throws DataStoreException {
        throw new DataStoreException("Unsupported operation");
    }

    @Override
    public void deleteTileMatrix(String tilematrixId) throws DataStoreException {
        throw new DataStoreException("Unsupported operation");
    }

}
