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

package org.geotoolkit.data.kml2;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.internal.storage.xml.AbstractProvider;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@StoreMetadata(
        formatName = KMLProvider.NAME,
        capabilities = {Capability.READ},
        resourceTypes = {FeatureSet.class})
@StoreMetadataExt(
        resourceTypes = ResourceType.VECTOR,
        geometryTypes ={Geometry.class,
                        Point.class,
                        LineString.class,
                        Polygon.class,
                        MultiPoint.class,
                        MultiLineString.class,
                        MultiPolygon.class})
public class KMLProvider extends AbstractProvider {

    public static final String NAME = "kml";
    public static final String MIME_TYPE = "application/vnd.google-earth.kml+xml";

    private static KMLProvider INSTANCE;

    /**
     * Get singleton instance of KML provider.
     *
     * <p>
     * Note : this method is named after Java 9 service loader provider method.
     * {@link https://docs.oracle.com/javase/9/docs/api/java/util/ServiceLoader.html}
     * </p>
     *
     * @return singleton instance of KMLProvider
     */
    public static synchronized KMLProvider provider() {
        if (INSTANCE == null) INSTANCE = new KMLProvider();
        return INSTANCE;
    }

    public KMLProvider(){
        super(NAME, Collections.singletonMap("http://www.opengis.net/kml/2.2", MIME_TYPE), new HashMap<>());
    }

    @Override
    public DataStore open(StorageConnector connector) throws DataStoreException {
        final URI uri = connector.commit(URI.class, NAME);
        if (uri == null) {
            throw new DataStoreException("Unsupported parameters.");
        }
        return new KMLStore(uri);
    }

}
