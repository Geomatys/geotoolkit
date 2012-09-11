/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.googlemaps;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import org.geotoolkit.client.AbstractServerFactory;
import org.geotoolkit.client.map.CachedPyramidSet;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ResourceInternationalString;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.*;

/**
 * Google Static Map Server factory.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StaticGoogleServerFactory extends AbstractServerFactory implements CoverageStoreFactory{
    
    /** factory identification **/
    public static final String NAME = "googleStaticMaps";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }
    
    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);
    
    
    public static final ParameterDescriptorGroup PARAMETERS =
            new DefaultParameterDescriptorGroup("GSParameters",
                IDENTIFIER,URL,SECURITY,IMAGE_CACHE,NIO_QUERIES);

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
        return new ResourceInternationalString("org/geotoolkit/googlemaps/bundle", "coverageDescription");
    }

    @Override
    public CharSequence getDisplayName() {
        return new ResourceInternationalString("org/geotoolkit/googlemaps/bundle", "coverageTitle");
    }

    @Override
    public StaticGoogleMapsServer create(ParameterValueGroup params) throws DataStoreException {
        checkCanProcessWithError(params);
        final StaticGoogleMapsServer server = new StaticGoogleMapsServer(params);
        
        try{
            final ParameterValue val = params.parameter(NIO_QUERIES.getName().getCode());
            boolean useNIO = Boolean.TRUE.equals(val.getValue());
            server.setUserProperty(CachedPyramidSet.PROPERTY_NIO, useNIO);
        }catch(ParameterNotFoundException ex){}
        
        return server;
    }

    @Override
    public StaticGoogleMapsServer create(Map<String, ? extends Serializable> params) throws DataStoreException {
        return (StaticGoogleMapsServer) super.create(params);
    }

    @Override
    public CoverageStore createNew(Map<String, ? extends Serializable> params) throws DataStoreException {
        try{
            return createNew(FeatureUtilities.toParameter(params,getParametersDescriptor()));
        }catch(InvalidParameterValueException ex){
            throw new DataStoreException(ex);
        }
    }

    @Override
    public CoverageStore createNew(ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Can not create new Google Static coverage store.");
    }
    
}
