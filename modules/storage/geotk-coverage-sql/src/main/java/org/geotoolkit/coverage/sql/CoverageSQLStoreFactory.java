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

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
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
@StoreMetadataExt(resourceTypes = ResourceType.GRID, canWrite = true)
public class CoverageSQLStoreFactory extends DataStoreFactory {

    /** factory identification **/
    public static final String NAME = "coverage-sql";

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

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


    public static final ParameterDescriptorGroup PARAMETERS = new ParameterBuilder()
            .addName(NAME).addName("CoverageDatabase").createGroup(
            IDENTIFIER,HOST,PORT,DATABASE,SCHEMA,USER,PASSWORD,ROOTDIRECTORY);

    @Override
    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.coverageSQLDescription);
    }

    @Override
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.coverageSQLTitle);
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS;
    }

    @Override
    public CoverageSQLStore open(ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        return new CoverageSQLStore(params);
    }

}
