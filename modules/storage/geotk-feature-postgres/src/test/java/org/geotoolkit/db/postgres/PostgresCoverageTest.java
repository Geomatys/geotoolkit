/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import static org.geotoolkit.db.postgres.PostgresFeatureStoreFactory.PARAMETERS_DESCRIPTOR;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.geotoolkit.coverage.grid.Coverage;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PostgresCoverageTest extends org.geotoolkit.test.TestBase {

    private PostgresFeatureStore store;

    public PostgresCoverageTest(){
    }

    private static Parameters params;

    /**
     * <p>Find JDBC connection parameters in specified file at
     * "/home/.geotoolkit.org/test-pgfeature.properties".<br/>
     * If properties file doesn't find all tests are skipped.</p>
     *
     * <p>To lunch tests user should create file with this architecture<br/>
     * for example : <br/>
     * database   = junit    (table name)<br/>
     * port       = 5432     (port number)<br/>
     * schema     = public   (schema name)<br/>
     * user       = postgres (user login)<br/>
     * password   = postgres (user password)<br/>
     * simpletype = false <br/>
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
        params = Parameters.castOrWrap(org.geotoolkit.parameter.Parameters.toParameter((Map)properties, PARAMETERS_DESCRIPTOR, false));
    }

    private void reload(boolean simpleType) throws DataStoreException, VersioningException {
        if(store != null){
            store.close();
        }

        //open in complex type to delete all types
        params.getOrCreate(PostgresFeatureStoreFactory.SIMPLETYPE).setValue(false);
        store = (PostgresFeatureStore) DataStores.open(params);
        while(!store.getNames().isEmpty()){ // we get the list each type because relations may delete multiple types each time
            final GenericName n = store.getNames().iterator().next();
            final VersionControl vc = store.getVersioning(n.toString());
            vc.dropVersioning();
            store.deleteFeatureType(n.toString());
        }
        assertTrue(store.getNames().isEmpty());
        store.close();

        //reopen the way it was asked
        params.getOrCreate(PostgresFeatureStoreFactory.SIMPLETYPE).setValue(simpleType);
        store = (PostgresFeatureStore) DataStores.open(params);
        assertTrue(store.getNames().isEmpty());
    }

    @After
    public void disposeStore() {
        if (store != null) {
            store.close();
        }
    }

    @Test
    public void testCoverageTypeCreation() throws DataStoreException, VersioningException {
        reload(true);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("testCovereage");
        ftb.addAttribute(Coverage.class).setName("coverage");
        final FeatureType covType = ftb.build();

        store.createFeatureType(covType);
        assertEquals(1, store.getNames().size());

        final FeatureType resType = store.getFeatureType(store.getNames().iterator().next().toString());
        assertEquals(resType.getName().tip().toString(), covType.getName().tip().toString());

        //we expect one more field for id
        final ArrayList<? extends PropertyType> descs = new ArrayList<>(resType.getProperties(true));

        int index=2;
        PropertyType desc;
        desc = descs.get(index++);
        assertEquals("coverage", desc.getName().tip().toString());
        assertEquals(Coverage.class, ((AttributeType)desc).getValueClass());

    }

}
