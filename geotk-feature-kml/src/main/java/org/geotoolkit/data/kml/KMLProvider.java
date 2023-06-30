/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017-2023, Geomatys
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

package org.geotoolkit.data.kml;

import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.internal.storage.xml.AbstractProvider;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.ProviderOnFileSystem;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

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
public final class KMLProvider extends AbstractProvider implements ProviderOnFileSystem {

    public static final String NAME = "kml";
    public static final String MIME_TYPE_KML = "application/vnd.google-earth.kml+xml";
    public static final String MIME_TYPE_KMZ = "application/vnd.google-earth.kmz";

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
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        final ProbeResult result = FeatureStoreUtilities.probe(this, connector, MIME_TYPE);
        if (result.isSupported()) {
            //SHP and SHX files have the same signature, we only want to match on the SHP file.
            final Path path = connector.getStorageAs(Path.class);
            final String ext = IOUtilities.extension(path);
            if ("kml".equalsIgnoreCase(ext)) {
                return new ProbeResult(true, MIME_TYPE_KML, null);
            } else if ("kmz".equalsIgnoreCase(ext)) {
                return new ProbeResult(true, MIME_TYPE_KMZ, null);
            }
        }
        return result;
    }

    @Override
    public DataStore open(StorageConnector connector) throws DataStoreException {
        final URI uri = connector.commit(URI.class, NAME);
        if (uri == null) {
            throw new DataStoreException("Unsupported parameters.");
        }
        return new KMLStore(uri);
    }

    @Override
    public Collection<String> getSuffix() {
        return List.of("kml", "kmz");
    }

}
