/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.data.AbstractReadingTests;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.om.OMDataStoreFactory;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.geotoolkit.internal.sql.ScriptRunner;
import org.geotoolkit.storage.DataStoreException;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.opengis.feature.type.Name;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OMDataStoreTest extends AbstractReadingTests{

    private static DefaultDataSource ds;
    private static DataStore store;
    private static Set<Name> names = new HashSet<Name>();
    private static List<ExpectedResult> expecteds = new ArrayList<ExpectedResult>();

    @BeforeClass
    public static void setUpClass() throws Exception {
        final String url = "jdbc:derby:memory:TestOM;create=true";
        ds = new DefaultDataSource(url);

        Connection con = ds.getConnection();

        final ScriptRunner exec = new ScriptRunner(con);
        exec.run(OMDataStoreTest.class.getResourceAsStream("org/geotoolkit/sql/structure-observations.sql"));
        exec.run(OMDataStoreTest.class.getResourceAsStream("org/geotoolkit/sql/sos-data.sql"));

        final Map params = new HashMap<String, Object>();
        params.put("dbtype", "OM");
        params.put(OMDataStoreFactory.SGBDTYPE.getName().toString(), "derby");
        params.put(OMDataStoreFactory.DERBYURL.getName().toString(), url);

        store = DataStoreFinder.getDataStore(params);

        final String ns = "http://www.opengis.net/sampling/1.0";
        names.add(new DefaultName(ns, "SamplingPoint"));
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (ds != null) {
            ds.shutdown();
        }
        File dlog = new File("derby.log");
        if (dlog.exists()) {
            dlog.delete();
        }
    }

    @Override
    protected DataStore getDataStore() {
        return store;
    }

    @Override
    protected Set<Name> getExpectedNames() {
        return names;
    }

    @Override
    protected List<ExpectedResult> getReaderTests() {
        return expecteds;
    }

}
