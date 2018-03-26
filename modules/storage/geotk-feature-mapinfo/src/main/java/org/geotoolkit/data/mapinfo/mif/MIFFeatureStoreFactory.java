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
package org.geotoolkit.data.mapinfo.mif;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.geotoolkit.data.FileFeatureStoreFactory;
import org.geotoolkit.nio.IOUtilities;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.storage.ResourceType;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.storage.StoreMetadataExt;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 21/02/13
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
public class MIFFeatureStoreFactory extends AbstractFileFeatureStoreFactory implements FileFeatureStoreFactory {

    public final static Logger LOGGER = Logging.getLogger("org.geotoolkit.data.mapinfo.mif");

    /** factory identification **/
    public static final String NAME = "MIF-MID";
    public static final String MIME_TYPE = "application/x-mifmid";
    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName(NAME).addName("MIFParameters").createGroup(IDENTIFIER, PATH);

    @Override
    public CharSequence getDisplayName() {
        return NAME;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<String> getSuffix() {
        return Arrays.asList("mif");
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        return FileFeatureStoreFactory.probe(this, connector, MIME_TYPE);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MIFFeatureStore open(ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        final URI filePath = (URI) params.parameter(PATH.getName().toString()).getValue();

        // Try to open a stream to ensure we've got an existing file.
        try (InputStream stream = IOUtilities.open(filePath)){
            //do nothing (stream can be created)
        } catch (IOException ex) {
            throw new DataStoreException("Can't reach data pointed by given URI.", ex);
        }

        return new MIFFeatureStore(filePath);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MIFFeatureStore create(ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        final URI filePath = (URI) params.parameter(PATH.getName().toString()).getValue();

        return new MIFFeatureStore(filePath);
    }

}
