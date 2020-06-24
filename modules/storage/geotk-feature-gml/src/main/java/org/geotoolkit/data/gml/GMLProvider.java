/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

package org.geotoolkit.data.gml;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.storage.ProviderOnFileSystem;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.geotoolkit.storage.feature.FileFeatureStoreFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * GML data store provider.
 *
 * @author Johann Sorel (Geomatys)
 */
@StoreMetadata(
        formatName = GMLProvider.NAME,
        capabilities = {Capability.READ,Capability.WRITE,Capability.CREATE},
        resourceTypes = FeatureSet.class)
@StoreMetadataExt(
        resourceTypes = ResourceType.VECTOR,
        geometryTypes ={Geometry.class,
                        Point.class,
                        LineString.class,
                        Polygon.class,
                        MultiPoint.class,
                        MultiLineString.class,
                        MultiPolygon.class})
public class GMLProvider extends DataStoreProvider implements ProviderOnFileSystem {

    /** factory identification **/
    public static final String NAME = "gml";
    public static final String MIME_TYPE = "application/gml+xml";

    public static final ParameterDescriptor<URI> PATH = new ParameterBuilder()
            .addName(LOCATION)
            .setRequired(true)
            .create(URI.class, null);

    /**
     * Open a folder of sparsed features
     */
    public static final ParameterDescriptor<Boolean> SPARSE = new ParameterBuilder()
            .addName("sparse")
            .addName(Bundle.formatInternational(Bundle.Keys.paramSparseAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramSparseRemarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.FALSE);

    /**
     * Open a store where gml files may not exist, in this case the given xsd is used
     * to list the possible types.
     */
    public static final ParameterDescriptor<String> XSD = new ParameterBuilder()
            .addName("xsd")
            .addName(Bundle.formatInternational(Bundle.Keys.paramXSDAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramXSDRemarks))
            .setRequired(false)
            .create(String.class, null);

    /**
     * Name of the feature type to use in the XSD.
     */
    public static final ParameterDescriptor<String> XSD_TYPE_NAME = new ParameterBuilder()
            .addName("xsdtypename")
            .addName(Bundle.formatInternational(Bundle.Keys.paramXSDTypeNameAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramXSDTypeNameRemarks))
            .setRequired(false)
            .create(String.class, null);

    public static final ParameterDescriptor<Boolean> LONGITUDE_FIRST = new ParameterBuilder()
            .addName("longitudeFirst")
            .addName(Bundle.formatInternational(Bundle.Keys.longitudeFirstAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.longitudeFirstRemarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.TRUE);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName(NAME).createGroup(
                PATH,SPARSE,XSD,XSD_TYPE_NAME,LONGITUDE_FIRST);

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

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        return FileFeatureStoreFactory.probe(this, connector, MIME_TYPE);
    }

    @Override
    public DataStore open(final ParameterValueGroup params) throws DataStoreException {
        final Boolean sparse = Parameters.castOrWrap(params).getValue(SPARSE);
        if (sparse) {
            return new GMLSparseStore(params);
        }else{
            return new GMLStore(params);
        }
    }

    @Override
    public DataStore open(StorageConnector connector) throws DataStoreException {
        final URI path = connector.getStorageAs(URI.class);
        try {
            return new GMLStore(path);
        } catch (MalformedURLException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    @Override
    public Collection<String> getSuffix() {
        return Arrays.asList("gml");
    }

}
