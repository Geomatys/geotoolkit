/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2019, Geomatys
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

package org.geotoolkit.data.wfs;

import java.net.URL;
import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.client.AbstractClientProvider;
import static org.geotoolkit.client.AbstractClientProvider.createVersionDescriptor;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.parameter.*;

/**
 * DataStore provider for WFS.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@StoreMetadata(
        formatName = WFSProvider.NAME,
        capabilities = {Capability.READ,Capability.WRITE},
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
public class WFSProvider extends DataStoreProvider {

    /** factory identification **/
    public static final String NAME = "wfs";

    /**
     * Version, Mandatory.
     */
    public static final ParameterDescriptor<String> VERSION;
    static{
        final WFSVersion[] values = WFSVersion.values();
        final String[] validValues =  new String[values.length];
        for(int i=0;i<values.length;i++){
            validValues[i] = values[i].getCode();
        }
        VERSION = createVersionDescriptor(validValues, WFSVersion.v110.getCode());
    }
    /**
     * Optional -post request
     */
    public static final ParameterDescriptor<Boolean> POST_REQUEST = new ParameterBuilder()
            .addName("post")
            .addName(Bundle.formatInternational(Bundle.Keys.post))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.postRemarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.FALSE);
    /**
     * Optional use true CRS axis ordering.
     */
    public static final ParameterDescriptor<Boolean> LONGITUDE_FIRST = new ParameterBuilder()
            .addName("longitudeFirst")
            .addName(Bundle.formatInternational(Bundle.Keys.longitudeFirst))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.longitudeFirstRemarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.FALSE);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName(NAME).createGroup(AbstractClientProvider.URL, VERSION, AbstractClientProvider.SECURITY,
                LONGITUDE_FIRST,POST_REQUEST,AbstractClientProvider.TIMEOUT);

    @Override
    public String getShortName() {
        return NAME;
    }

    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.datastoreDescription);
    }

    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.datastoreTitle);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public WebFeatureClient open(ParameterValueGroup params) throws DataStoreException {
        return new WebFeatureClient(params);
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        return ProbeResult.UNSUPPORTED_STORAGE;
    }

    @Override
    public WebFeatureClient open(StorageConnector connector) throws DataStoreException {
        final URL url = connector.getStorageAs(URL.class);
        final Parameters parameters = Parameters.castOrWrap(PARAMETERS_DESCRIPTOR.createValue());
        parameters.getOrCreate(AbstractClientProvider.URL).setValue(url);
        return open(parameters);
    }

}
