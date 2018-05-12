/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.nmea;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.geotoolkit.data.FileFeatureStoreFactory;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * NMEA feature store.
 *
 * @author Alexis Manin (Geomatys)
 */
@StoreMetadataExt(
        resourceTypes = ResourceType.VECTOR,
        geometryTypes ={Geometry.class,
                        Point.class,
                        LineString.class,
                        Polygon.class,
                        MultiPoint.class,
                        MultiLineString.class,
                        MultiPolygon.class})
public class NMEAFeatureStoreFactory extends AbstractFileFeatureStoreFactory {

    /** factory identification **/
    public static final String NAME = "NMEA";
    public static final String MIME_TYPE = "application/x-nmea";

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);
    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName(NAME).addName("NMEAParameters").createGroup(
                IDENTIFIER, PATH);

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        return FileFeatureStoreFactory.probe(this, connector, MIME_TYPE);
    }

    @Override
    public NMEAFeatureStore open(ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        return new NMEAFeatureStore(params);
    }

    @Override
    public NMEAFeatureStore create(ParameterValueGroup params) throws DataStoreException {
        return open(params);
    }

    @Override
    public Collection<String> getSuffix() {
        return Arrays.asList("txt", "log");
    }

    @Override
    public Collection<byte[]> getSignature() {
        return Collections.singleton(new byte[]{'$'});
    }

}
