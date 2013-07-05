/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.data.mysql;

import java.io.IOException;

import java.util.Collections;
import org.geotoolkit.data.AbstractFeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.jdbc.DefaultJDBCFeatureStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.jdbc.JDBCFeatureStore;
import org.geotoolkit.jdbc.JDBCDataStoreFactory;
import org.geotoolkit.jdbc.dialect.SQLDialect;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.apache.sis.util.iso.ResourceInternationalString;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MySQLFeatureStoreFactory extends JDBCDataStoreFactory {

    /** factory identification **/
    public static final String NAME = "mysql";
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
     * Parameter for database port
     */
    public static final ParameterDescriptor<Integer> PORT =
             new DefaultParameterDescriptor<Integer>("port","Port",Integer.class,3306,true);

    /**
     * Wheter a prepared statements based dialect should be used, or not
     */
    public static final ParameterDescriptor<Boolean> PREPARED_STATEMENTS =
             new DefaultParameterDescriptor<Boolean>("preparedStatements","Use prepared statements",Boolean.class,false,false);


    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("MySQLParameters",
                IDENTIFIER,HOST,PORT,DATABASE,SCHEMA,USER,PASSWORD,NAMESPACE,
                DATASOURCE,MAXCONN,MINCONN,VALIDATECONN,FETCHSIZE,MAXWAIT,PREPARED_STATEMENTS);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    protected SQLDialect createSQLDialect(final JDBCFeatureStore dataStore) {
        return new MySQLDialect(dataStore);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    protected String getDatabaseID() {
        return "mysql";
    }

    @Override
    public CharSequence getDisplayName() {
        return new ResourceInternationalString("org/geotoolkit/mysql/bundle", "datastoreTitle");
    }

    @Override
    public CharSequence getDescription() {
        return new ResourceInternationalString("org/geotoolkit/mysql/bundle", "datastoreDescription");
    }

    @Override
    protected String getDriverClassName() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public JDBCFeatureStore open(final ParameterValueGroup params)
        throws DataStoreException {
        checkCanProcessWithError(params);
        JDBCFeatureStore featureStore = super.open(params);

        final MySQLDialect dialect;

        // setup the ps dialect if need be
        final Boolean usePs = (Boolean) params.parameter(PREPARED_STATEMENTS.getName().toString()).getValue();
        if(Boolean.TRUE.equals(usePs)) {
            dialect = new MySQLPSDialect(featureStore);
            featureStore.setDialect(dialect);
        }else{
            dialect = (MySQLDialect) featureStore.getDialect();
        }

        return featureStore;
    }

    @Override
    protected String getValidationQuery() {
        return "select now()";
    }

    @Override
    protected String getJDBCUrl(final ParameterValueGroup params) throws IOException {
        final String host = (String) params.parameter(HOST.getName().toString()).getValue();
        final Integer port = (Integer) params.parameter(PORT.getName().toString()).getValue();
        final String db = (String) params.parameter(DATABASE.getName().toString()).getValue();
        return "jdbc:mysql://" + host + ":" + port + "/" + db;
    }

}
