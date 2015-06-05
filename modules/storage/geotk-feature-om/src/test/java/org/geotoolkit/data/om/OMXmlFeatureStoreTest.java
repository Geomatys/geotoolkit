/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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


package org.geotoolkit.data.om;

import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.data.AbstractReadingTests;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.feature.type.Name;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OMXmlFeatureStoreTest extends AbstractReadingTests{
    
    private static FeatureStore store;
    private static final Set<Name> names = new HashSet<>();
    private static final List<AbstractReadingTests.ExpectedResult> expecteds = new ArrayList<>();
    static{
        try{

            final File f = FileUtilities.getFileFromResource("org/geotoolkit/sql/observation1.xml");
            final Map params = new HashMap<>();
            params.put(OMXmlFeatureStoreFactory.FILE_PATH.getName().toString(), f);

            store = FeatureStoreFinder.open(params);

            final String nsOM = "http://www.opengis.net/sampling/1.0";
            final String nsGML = "http://www.opengis.net/gml";
            final Name name = DefaultName.create(nsOM, "observation1");
            names.add(name);

            final FeatureTypeBuilder featureTypeBuilder = new FeatureTypeBuilder();
            featureTypeBuilder.setName(name);
            featureTypeBuilder.add(DefaultName.create(nsGML, "description"),String.class,0,1,true,null);
            featureTypeBuilder.add(DefaultName.create(nsGML, "name"),String.class,1,Integer.MAX_VALUE,false,null);
            featureTypeBuilder.add(DefaultName.create(nsOM, "sampledFeature"),String.class,0,Integer.MAX_VALUE,true,null);
            featureTypeBuilder.add(DefaultName.create(nsOM, "position"),Geometry.class,1,1,false,null);
            featureTypeBuilder.setDefaultGeometry(DefaultName.create(nsOM, "position"));

            int size = 1;
            GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:27582"));
            env.setRange(0, 65400.0, 65400.0);
            env.setRange(1, 1731368.0, 1731368.0);

            final AbstractReadingTests.ExpectedResult res = new AbstractReadingTests.ExpectedResult(name,
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
    protected FeatureStore getDataStore() {
        return store;
    }

    @Override
    protected Set<Name> getExpectedNames() {
        return names;
    }

    @Override
    protected List<AbstractReadingTests.ExpectedResult> getReaderTests() {
        return expecteds;
    }

    public static InputStream getResourceAsStream(final String url) {
        final ClassLoader cl = getContextClassLoader();
        return cl.getResourceAsStream(url);
    }
    
    public static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }
}
