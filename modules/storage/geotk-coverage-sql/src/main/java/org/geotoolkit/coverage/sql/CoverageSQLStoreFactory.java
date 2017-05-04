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
import org.geotoolkit.storage.coverage.AbstractCoverageStoreFactory;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.storage.DataStore;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
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
 * @module
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
    public static final ParameterDescriptor<String> HOST = new ParameterBuilder()
            .addName("host")
            .setRemarks("Host")
            .setRequired(true)
            .create(String.class,"localhost");

    /** parameter for database port */
    public static final ParameterDescriptor<Integer> PORT = new ParameterBuilder()
            .addName("port")
            .setRemarks("Port")
            .setRequired(true)
            .create(Integer.class,5432);

    /** parameter for database instance */
    public static final ParameterDescriptor<String> DATABASE = new ParameterBuilder()
            .addName("database")
            .setRemarks("Database")
            .setRequired(false)
            .create(String.class,null);

    /** parameter for database schema */
    public static final ParameterDescriptor<String> SCHEMA = new ParameterBuilder()
            .addName("schema")
            .setRemarks("Schema")
            .setRequired(false)
            .create(String.class,null);

    /** parameter for database user */
    public static final ParameterDescriptor<String> USER = new ParameterBuilder()
            .addName("user")
            .setRemarks("user login")
            .setRequired(true)
            .create(String.class,null);

    /** parameter for database password */
    public static final ParameterDescriptor<String> PASSWORD = new ParameterBuilder()
            .addName("password")
            .setRemarks("user password")
            .setRequired(true)
            .create(String.class,null);

    /** parameter for rootDirectory password */
    public static final ParameterDescriptor<String> ROOTDIRECTORY = new ParameterBuilder()
            .addName("rootDirectory")
            .setRemarks("local data directory root")
            .setRequired(true)
            .create(String.class,null);


    public static final ParameterDescriptorGroup PARAMETERS = new ParameterBuilder().addName("CoverageDatabase").createGroup(
            IDENTIFIER,HOST,PORT,DATABASE,SCHEMA,USER,PASSWORD,ROOTDIRECTORY,NAMESPACE);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.coverageSQLDescription);
    }

    @Override
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.coverageSQLTitle);
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS;
    }

    @Override
    public CoverageSQLStore open(ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        return new CoverageSQLStore(params);
    }

    @Override
    public CoverageSQLStore create(ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.GRID, true, false, true);
    }

}
