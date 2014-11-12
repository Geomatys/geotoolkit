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
package org.geotoolkit.coverage.sql;

import java.util.Collections;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.geotoolkit.coverage.AbstractCoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStore;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Wrap coverage-sql as a coverage-store.
 * TODO : temporary binding waiting for CoverageStore interface to be revisited
 * and integrated in geotk.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CoverageSQLStoreFactory extends AbstractCoverageStoreFactory {

    /** factory identification **/
    public static final String NAME = "coverage-sql";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
        IDENTIFICATION.freeze();
    }

    public static final ParameterDescriptor<String> IDENTIFIER = AbstractCoverageStoreFactory.createFixedIdentifier(NAME);

     /** parameter for database host */
    public static final ParameterDescriptor<String> HOST =
             new DefaultParameterDescriptor<String>("host","Host",String.class,"localhost",true);

    /** parameter for database port */
    public static final ParameterDescriptor<Integer> PORT =
             new DefaultParameterDescriptor<Integer>("port","Port",Integer.class,5432,true);

    /** parameter for database instance */
    public static final ParameterDescriptor<String> DATABASE =
             new DefaultParameterDescriptor<String>("database","Database",String.class,null,false);

    /** parameter for database schema */
    public static final ParameterDescriptor<String> SCHEMA =
             new DefaultParameterDescriptor<String>("schema","Schema",String.class,null,false);

    /** parameter for database user */
    public static final ParameterDescriptor<String> USER =
             new DefaultParameterDescriptor<String>("user","user login",String.class,null,true);

    /** parameter for database password */
    public static final ParameterDescriptor<String> PASSWORD =
             new DefaultParameterDescriptor<String>("password","user password",String.class,null,true);
    
    /** parameter for rootDirectory password */
    public static final ParameterDescriptor<String> ROOTDIRECTORY =
             new DefaultParameterDescriptor<String>("rootDirectory","local data directory root",String.class,null,true);

    
    public static final ParameterDescriptorGroup PARAMETERS = new DefaultParameterDescriptorGroup("CoverageDatabase", 
            IDENTIFIER,HOST,PORT,DATABASE,SCHEMA,USER,PASSWORD,ROOTDIRECTORY);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public CharSequence getDescription() {
        return new ResourceInternationalString("org/geotoolkit/coverage/bundle", "coverageSQLDescription");
    }

    @Override
    public CharSequence getDisplayName() {
        return new ResourceInternationalString("org/geotoolkit/coverage/bundle", "coverageSQLTitle");
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS;
    }

    @Override
    public CoverageStore open(ParameterValueGroup params) throws DataStoreException {
        checkCanProcessWithError(params);
        return new CoverageSQLStore(params);
    }

    @Override
    public CoverageStore create(ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

}
