/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2013, Geomatys
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
package org.geotoolkit.db.postgres;

import java.util.Collections;
import org.geotoolkit.db.AbstractJDBCFeatureStoreFactory;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.geotoolkit.db.JDBCFeatureStore;
import org.geotoolkit.db.dialect.SQLDialect;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.ResourceInternationalString;
import static org.geotoolkit.data.AbstractFeatureStoreFactory.GEOMS_ALL;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * PostgreSQL/PostGIS  feature store factory.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PostgresFeatureStoreFactory extends AbstractJDBCFeatureStoreFactory{

    /** factory identification **/
    public static final String NAME = "postgresql";
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
     * Parameter for loose bbox filter.
     */
    public static final ParameterDescriptor<Boolean> LOOSEBBOX = new ParameterBuilder()
            .addName("Loose bbox")
            .addName(new ResourceInternationalString("org/geotoolkit/db/postgres/bundle", "lbbox"))
            .setRemarks(new ResourceInternationalString("org/geotoolkit/db/postgres/bundle", "lbbox_remarks"))
            .setRequired(false)
            .create(Boolean.class, Boolean.TRUE);

    /**
     * Parameter for database port.
     */
    public static final ParameterDescriptor<Integer> PORT = createFixedPort(5432);


    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("PostgresParameters",
                IDENTIFIER,HOST,PORT,DATABASE,SCHEMA,TABLE,USER,PASSWORD,NAMESPACE,
                DATASOURCE,MAXCONN,MINCONN,VALIDATECONN,FETCHSIZE,MAXWAIT,LOOSEBBOX,SIMPLETYPE);
    
    
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
    
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    protected String getJDBCURLDatabaseName() {
        return "postgresql";
    }

    @Override
    protected String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    protected SQLDialect createSQLDialect(JDBCFeatureStore dataStore) {
        return new PostgresDialect((DefaultJDBCFeatureStore)dataStore);
    }

    @Override
    protected String getValidationQuery() {
        return "select now()";
    }

    @Override
    protected DefaultJDBCFeatureStore toFeatureStore(ParameterValueGroup params, String factoryId) {
        //add versioning support
        return new PostgresFeatureStore(params, factoryId);
    }
    
    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.VECTOR, true, false, true, false, GEOMS_ALL);
    }

}
