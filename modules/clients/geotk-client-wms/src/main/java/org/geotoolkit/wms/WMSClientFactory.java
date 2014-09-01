/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Johann Sorel
 *    (C) 2012-2014, Geomatys
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
package org.geotoolkit.wms;

import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.geotoolkit.client.AbstractClientFactory;
import org.geotoolkit.client.CoverageClientFactory;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.FactoryMetadata;
import org.geotoolkit.wms.xml.WMSVersion;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.storage.DefaultFactoryMetadata;

/**
 * WMS Server factory.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class WMSClientFactory extends AbstractClientFactory implements CoverageClientFactory{

    /** factory identification **/
    public static final String NAME = "wms";
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
        final String code = "version";
        final CharSequence remarks = I18N_VERSION;
        final Map<String,Object> params = new HashMap<String, Object>();
        params.put(DefaultParameterDescriptor.NAME_KEY, code);
        params.put(DefaultParameterDescriptor.REMARKS_KEY, remarks);
        final List<String> validValues =  new ArrayList<String>();
        for(WMSVersion version : WMSVersion.values()){
            validValues.add(version.getCode());
        }

        VERSION = new DefaultParameterDescriptor<String>(params, String.class,
                validValues.toArray(new String[validValues.size()]),
                WMSVersion.auto.getCode(), null, null, null, true);
    }

    public static final ParameterDescriptorGroup PARAMETERS =
            new DefaultParameterDescriptorGroup("WMSParameters", IDENTIFIER,URL,VERSION,SECURITY,TIMEOUT);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS;
    }

    @Override
    public CharSequence getDescription() {
        return new ResourceInternationalString("org/geotoolkit/wms/bundle", "coverageDescription");
    }

    @Override
    public CharSequence getDisplayName() {
        return new ResourceInternationalString("org/geotoolkit/wms/bundle", "coverageTitle");
    }

    @Override
    public WebMapClient open(ParameterValueGroup params) throws DataStoreException {
        checkCanProcessWithError(params);
        return new WebMapClient(params);
    }

    @Override
    public WebMapClient open(Map<String, ? extends Serializable> params) throws DataStoreException {
        return (WebMapClient) super.open(params);
    }

    @Override
    public CoverageStore create(Map<String, ? extends Serializable> params) throws DataStoreException {
        try{
            return create(FeatureUtilities.toParameter(params,getParametersDescriptor()));
        }catch(InvalidParameterValueException ex){
            throw new DataStoreException(ex);
        }
    }

    @Override
    public CoverageStore create(ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Can not create new WMS coverage store.");
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.GRID, false);
    }
}
