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

import java.util.Arrays;
import java.util.Collection;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.geotoolkit.data.FileFeatureStoreFactory;
import org.geotoolkit.storage.DataStore;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

/**
 * GML featurestore factory.
 *
 * @author Johann Sorel (Geomatys)
 */
@StoreMetadataExt(
        resourceTypes = ResourceType.VECTOR,
        canCreate = false,
        canWrite = false,
        geometryTypes ={Geometry.class,
                        Point.class,
                        LineString.class,
                        Polygon.class,
                        MultiPoint.class,
                        MultiLineString.class,
                        MultiPolygon.class})
public class GMLFeatureStoreFactory extends AbstractFileFeatureStoreFactory {

    /** factory identification **/
    public static final String NAME = "gml";
    public static final String MIME_TYPE = "application/gml+xml";

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

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
            new ParameterBuilder().addName(NAME).addName("GMLParameters").createGroup(
                IDENTIFIER, PATH,SPARSE,XSD,XSD_TYPE_NAME,LONGITUDE_FIRST);

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
        ensureCanProcess(params);
        final Boolean sparse = Parameters.castOrWrap(params).getValue(SPARSE);
        if(sparse){
            return new GMLSparseFeatureStore(params);
        }else{
            return new GMLFeatureStore(params);
        }
    }

    @Override
    public DataStore create(final ParameterValueGroup params) throws DataStoreException {
        return open(params);
    }

    @Override
    public Collection<String> getSuffix() {
        return Arrays.asList("gml");
    }

    @Override
    public boolean canProcess(ParameterValueGroup params) {
        Boolean sparse = null;
        try{
            ParameterValue<?> parameter = params.parameter(SPARSE.getName().getCode());
            if(parameter!=null && parameter.getValue() instanceof Boolean){
                sparse = (Boolean) parameter.getValue();
            }
        }catch(ParameterNotFoundException ex){
        }
        if(sparse != null && sparse){
            return true;
        }else{
            return super.canProcess(params);
        }
    }



}
