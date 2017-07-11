/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import static org.junit.Assert.assertTrue;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.util.GenericName;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;


import static org.geotoolkit.db.postgres.PostgresFeatureStoreFactory.*;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataStores;
import static org.junit.Assert.*;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PostgresSpatialQueryTest extends org.geotoolkit.test.TestBase {


    private PostgresFeatureStore store;

    public PostgresSpatialQueryTest(){
    }

    private static ParameterValueGroup params;

    /**
     * <p>Find JDBC connection parameters in specified file at
     * "/home/.geotoolkit.org/test-pgfeature.properties".<br>
     * If properties file doesn't find all tests are skipped.</p>
     *
     * <p>To lunch tests user should create file with this architecture<br>
     * for example : <br>
     * database   = junit    (table name)<br>
     * port       = 5432     (port number)<br>
     * schema     = public   (schema name)<br>
     * user       = postgres (user login)<br>
     * password   = postgres (user password)<br>
     * simpletype = false <br>
     * namespace  = no namespace</p>
     * @throws IOException
     */
    @BeforeClass
    public static void beforeClass() throws IOException {
        String path = System.getProperty("user.home");
        path += "/.geotoolkit.org/test-pgfeature.properties";
        final File f = new File(path);
        Assume.assumeTrue(f.exists());
        final Properties properties = new Properties();
        properties.load(new FileInputStream(f));
        params = Parameters.toParameter((Map)properties, PARAMETERS_DESCRIPTOR, false);
    }

    private void reload(boolean simpleType) throws DataStoreException, VersioningException {
        if(store != null){
            store.close();
        }

        //open in complex type to delete all types
        ParametersExt.getOrCreateValue(params, PostgresFeatureStoreFactory.SIMPLETYPE.getName().getCode()).setValue(false);
        store = (PostgresFeatureStore) DataStores.open(params);
        for(GenericName n : store.getNames()){
            VersionControl vc = store.getVersioning(n.toString());
            vc.dropVersioning();
            store.deleteFeatureType(n.toString());
        }
        assertTrue(store.getNames().isEmpty());
        store.close();

        //reopen the way it was asked
        ParametersExt.getOrCreateValue(params, PostgresFeatureStoreFactory.SIMPLETYPE.getName().getCode()).setValue(simpleType);
        store = (PostgresFeatureStore) DataStores.open(params);
        assertTrue(store.getNames().isEmpty());
    }

    /**
     * Test reading envelope on a table with no geometry field.
     */
    @Test
    public void noGeomEnvelopeQuery() throws DataStoreException, VersioningException{
        reload(true);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("nogeomtable");
        ftb.addAttribute(String.class).setName("field");
        FeatureType ft = ftb.build();

        store.createFeatureType(ft);

        //test env reading all fields
        Envelope env = store.getEnvelope(QueryBuilder.all(ft.getName()));
        assertNull(env);

        //test env reading no fields
        final QueryBuilder qb = new QueryBuilder(ft.getName().toString());
        qb.setProperties(new String[0]);
        qb.setHints(new Hints(HintsPending.FEATURE_HIDE_ID_PROPERTY, Boolean.TRUE));
        env = store.getEnvelope(qb.buildQuery());
        assertNull(env);
    }

    /**
     * Test reading envelope on a table with no geometry or id field.
     */
    @Test
    public void noGeomNoIdEnvelopeQuery() throws DataStoreException, VersioningException, SQLException{
        reload(true);

        final Connection cnx = store.getDataSource().getConnection();
        cnx.createStatement().executeUpdate("CREATE TABLE \"noGeomNoIdTable\" (field VARCHAR(255));");

        store.refreshMetaModel();

        final FeatureType ft = store.getFeatureType("noGeomNoIdTable");

        //test env reading all fields
        Envelope env = store.getEnvelope(QueryBuilder.all(ft.getName()));
        assertNull(env);

        //test env reading no fields
        final QueryBuilder qb = new QueryBuilder(ft.getName().toString());
        qb.setProperties(new String[0]);
        qb.setHints(new Hints(HintsPending.FEATURE_HIDE_ID_PROPERTY, Boolean.TRUE));
        env = store.getEnvelope(qb.buildQuery());
        assertNull(env);
    }


}
