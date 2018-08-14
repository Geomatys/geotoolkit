/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.osm;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.data.FileFeatureStoreFactory;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * OSM XML featurestore factory.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@StoreMetadataExt(
        resourceTypes = ResourceType.VECTOR,
        canWrite = true,
        geometryTypes ={Geometry.class,
                        Point.class,
                        LineString.class,
                        Polygon.class,
                        MultiPoint.class,
                        MultiLineString.class,
                        MultiPolygon.class})
public class OSMMemoryFeatureStoreFactory extends AbstractFileFeatureStoreFactory {

    /** factory identification **/
    public static final String NAME = "osm-xml";
    public static final String MIME_TYPE = "application/x-osm+xml";

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName(NAME).addName("OSMMemoryParameters").createGroup(IDENTIFIER, PATH);

    @Override
    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.datastoreDescription);
    }

    @Override
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.datastoreTitle);
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        return FileFeatureStoreFactory.probe(this, connector, MIME_TYPE);
    }

    @Override
    public OSMMemoryFeatureStore open(final ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        final URI uri = (URI) params.parameter(PATH.getName().toString()).getValue();

        final String path = uri.toString();
        final int slash = Math.max(0, path.lastIndexOf('/') + 1);
        int dot = path.indexOf('.', slash);
        if (dot < 0) {
            dot = path.length();
        }
        final String name = path.substring(slash, dot);
        try {
            return new OSMMemoryFeatureStore(params, IOUtilities.toPath(uri));
        } catch (Exception ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public OSMMemoryFeatureStore create(final ParameterValueGroup params) throws DataStoreException {
        return open(params);
    }

    @Override
    public Collection<String> getSuffix() {
        return Arrays.asList("osm");
    }

}
