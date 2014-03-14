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

import org.geotoolkit.coverage.*;
import org.geotoolkit.googlemaps.model.GoogleMapsPyramidSet;
import org.apache.sis.storage.DataStoreException;
import org.opengis.feature.type.Name;

/**
 * GoogleMaps coverage reference.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GoogleCoverageReference extends AbstractPyramidalCoverageReference{

    private final GoogleMapsPyramidSet set;

    GoogleCoverageReference(final StaticGoogleMapsClient server, final Name name, boolean cacheImage) throws DataStoreException{
        super(server,name);
        this.set = new GoogleMapsPyramidSet(this,cacheImage);
    }

    @Override
    public int getImageIndex() {
        return 0;
    }

    public GetMapRequest createGetMap() {
        return new DefaultGetMap( (StaticGoogleMapsClient)store, name.getLocalPart());
    }

    @Override
    public PyramidSet getPyramidSet() throws DataStoreException {
        return set;
    }

}
