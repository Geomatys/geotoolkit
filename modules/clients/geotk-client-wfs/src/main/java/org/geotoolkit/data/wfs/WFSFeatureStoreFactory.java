/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.client.AbstractClientFactory;
import static org.geotoolkit.client.AbstractClientFactory.createVersionDescriptor;
import org.geotoolkit.client.ClientFactory;
import org.geotoolkit.data.AbstractFeatureStoreFactory;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.*;

/**
 * FeatureStore factory for WFS client.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WFSFeatureStoreFactory extends AbstractFeatureStoreFactory implements ClientFactory{

    /** factory identification **/
    public static final String NAME = "wfs";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

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
            new ParameterBuilder().addName("WFSParameters").createGroup(
                IDENTIFIER, AbstractClientFactory.URL, VERSION, AbstractClientFactory.SECURITY,
                LONGITUDE_FIRST,POST_REQUEST,AbstractClientFactory.TIMEOUT);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.datastoreDescription);
    }

    @Override
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.datastoreTitle);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public WebFeatureClient open(Map<String, ? extends Serializable> params) throws DataStoreException {
        return (WebFeatureClient)super.open(params);
    }

    @Override
    public WebFeatureClient open(ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        return new WebFeatureClient(params);
    }

    @Override
    public WebFeatureClient create(Map<String, ? extends Serializable> params) throws DataStoreException {
        return (WebFeatureClient)super.create(params);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public WebFeatureClient create(final ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Can not create any new WFS DataStore");
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.VECTOR, true, false, true, false, GEOMS_ALL);
    }
}
