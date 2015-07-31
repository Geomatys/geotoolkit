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
package org.geotoolkit.db.mysql;

import java.util.Collections;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.geotoolkit.db.AbstractJDBCFeatureStoreFactory;
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
 * @module pending
 */
public class MySQLFeatureStoreFactory extends AbstractJDBCFeatureStoreFactory {

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
    public static final ParameterDescriptor<Integer> PORT = createFixedPort(3306);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("MySQLParameters",
                IDENTIFIER,HOST,PORT,DATABASE,TABLE,USER,PASSWORD,NAMESPACE,
                DATASOURCE,MAXCONN,MINCONN,VALIDATECONN,FETCHSIZE,MAXWAIT,SIMPLETYPE);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    protected SQLDialect createSQLDialect(final JDBCFeatureStore featureStore) {
        return new MySQLDialect((DefaultJDBCFeatureStore)featureStore);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
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
    protected String getValidationQuery() {
        return "select now()";
    }

    @Override
    protected String getJDBCURLDatabaseName() {
        return "mysql";
    }

    @Override
    protected MySQLFeatureStore toFeatureStore(ParameterValueGroup params, String factoryId) {
        //add versioning support
        return new MySQLFeatureStore(params, factoryId);
    }
    
    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.VECTOR, true, true, true, false, DefaultFactoryMetadata.GEOMS_NONE);
    }
    
}
