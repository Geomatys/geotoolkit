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

import com.vividsolutions.jts.geom.Point;

import java.sql.Connection;
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
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.geotoolkit.internal.sql.ScriptRunner;
import org.geotoolkit.referencing.CRS;

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
    static{
        try{
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

            final String nsOM = "http://www.opengis.net/sampling/1.0";
            final String nsGML = "http://www.opengis.net/gml";
            final Name name = new DefaultName(nsOM, "SamplingPoint");
            names.add(name);

            final FeatureTypeBuilder featureTypeBuilder = new FeatureTypeBuilder();
            featureTypeBuilder.setName(name);
            featureTypeBuilder.add(new DefaultName(nsGML, "description"),String.class,0,1,true,null);
            featureTypeBuilder.add(new DefaultName(nsGML, "name"),String.class,1,Integer.MAX_VALUE,false,null);
            featureTypeBuilder.add(new DefaultName(nsOM, "sampledFeature"),String.class,1,Integer.MAX_VALUE,true,null);
            featureTypeBuilder.add(new DefaultName(nsOM, "position"),Point.class,1,1,false,null);
            featureTypeBuilder.setDefaultGeometry(new DefaultName(nsOM, "position"));

            int size = 2;
            GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:27582"));
            env.setRange(0, 65400, 65400);
            env.setRange(1, 1731368, 1731368);

            final ExpectedResult res = new ExpectedResult(name,
                    featureTypeBuilder.buildFeatureType(), size, env);
            expecteds.add(res);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

//    @Override
//    public void tearDown() {
//        try{
//            if (ds != null) {
//                ds.shutdown();
//            }
//
//            File fdb = new File("TestOM");
//            if(fdb.exists()){
//                FileUtilities.deleteDirectory(fdb);
//            }
//
//            File dlog = new File("derby.log");
//            if (dlog.exists()) {
//                dlog.delete();
//            }
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }
//    }

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
