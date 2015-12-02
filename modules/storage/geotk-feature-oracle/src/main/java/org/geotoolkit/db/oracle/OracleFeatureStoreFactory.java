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
package org.geotoolkit.db.oracle;

import java.io.IOException;
import java.util.Collections;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import static org.geotoolkit.data.AbstractFeatureStoreFactory.GEOMS_ALL;
import static org.geotoolkit.data.AbstractFeatureStoreFactory.NAMESPACE;
import static org.geotoolkit.data.AbstractFeatureStoreFactory.createFixedIdentifier;
import org.geotoolkit.db.AbstractJDBCFeatureStoreFactory;
import static org.geotoolkit.db.AbstractJDBCFeatureStoreFactory.DATABASE;
import static org.geotoolkit.db.AbstractJDBCFeatureStoreFactory.HOST;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.geotoolkit.db.JDBCFeatureStore;
import org.geotoolkit.db.dialect.SQLDialect;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class OracleFeatureStoreFactory extends AbstractJDBCFeatureStoreFactory{

    /** factory identification **/
    public static final String NAME = "oracle";
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
     * Parameter for database port.
     */
    public static final ParameterDescriptor<Integer> PORT = createFixedPort(1521);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName("OracleParameters").createGroup(
                IDENTIFIER,HOST,PORT,DATABASE,SCHEMA,TABLE,USER,PASSWORD,NAMESPACE,
                DATASOURCE,MAXCONN,MINCONN,VALIDATECONN,FETCHSIZE,MAXWAIT,SIMPLETYPE);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    protected String getJDBCURLDatabaseName() {
        return "oracle:thin";
    }

    @Override
    protected String getDriverClassName() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    protected SQLDialect createSQLDialect(JDBCFeatureStore featureStore) {
        return new OracleDialect((DefaultJDBCFeatureStore) featureStore);
    }

    @Override
    protected String getValidationQuery() {
        return "select 1 from dual";
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.VECTOR, true, false, true, false, GEOMS_ALL);
    }
    
    @Override
    protected DefaultJDBCFeatureStore toFeatureStore(ParameterValueGroup params, String factoryId) {
        return new OracleFeatureStore(params, factoryId);
    }

    protected String getJDBCUrl(final ParameterValueGroup params) throws IOException {
        final String host = (String) params.parameter(HOST.getName().toString()).getValue();
        final Integer port = (Integer) params.parameter(PORT.getName().toString()).getValue();
        final String db = (String) params.parameter(DATABASE.getName().toString()).getValue();

        final StringBuilder sb = new StringBuilder("jdbc:");
        sb.append(getJDBCURLDatabaseName());
        sb.append(":@");
        sb.append(host);
        if(port != null){
            sb.append(':').append(port);
        }
        if(db != null){
            sb.append(':').append(db);
        }
        return sb.toString();
    }

}
