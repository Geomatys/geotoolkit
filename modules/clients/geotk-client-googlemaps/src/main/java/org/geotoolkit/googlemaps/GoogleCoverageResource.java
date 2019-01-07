/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.googlemaps;

import java.util.Collection;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.multires.MultiResolutionModel;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.googlemaps.model.GoogleMapsPyramidSet;
import org.geotoolkit.storage.coverage.AbstractPyramidalCoverageResource;
import org.opengis.util.GenericName;

/**
 * GoogleMaps coverage reference.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GoogleCoverageResource extends AbstractPyramidalCoverageResource {

    private final GoogleMapsPyramidSet set;

    GoogleCoverageResource(final StaticGoogleMapsClient server, final GenericName name, boolean cacheImage) throws DataStoreException{
        super(server,name,0);
        this.set = new GoogleMapsPyramidSet(this,cacheImage);
    }

    public GetMapRequest createGetMap() {
        return new DefaultGetMap( (StaticGoogleMapsClient)store, getIdentifier().tip().toString());
    }

    public GoogleMapsPyramidSet getPyramidSet() {
        return set;
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        final GridCoverageReader reader = acquireReader();
        try {
            return reader.getGridGeometry(getImageIndex());
        } finally {
            recycle(reader);
        }
    }

    @Override
    public Collection<Pyramid> getModels() throws DataStoreException {
        return set.getPyramids();
    }

    @Override
    public MultiResolutionModel createModel(MultiResolutionModel template) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

}
