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

import org.geotoolkit.googlemaps.model.GoogleMapsPyramidSet;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.AbstractPyramidalCoverageResource;
import org.geotoolkit.storage.coverage.PyramidSet;
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
        return new DefaultGetMap( (StaticGoogleMapsClient)store, getName().tip().toString());
    }

    @Override
    public PyramidSet getPyramidSet() throws DataStoreException {
        return set;
    }

}
