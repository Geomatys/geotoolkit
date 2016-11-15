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
import java.io.InputStream;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.data.AbstractReadingTests;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.om.netcdf.NetcdfObservationStoreFactory;
import org.geotoolkit.util.NamesExt;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.nio.IOUtilities;
import org.opengis.util.GenericName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class NetCDFFeatureStoreTest extends AbstractReadingTests{

    private static FeatureStore store;
    private static final Set<GenericName> names = new HashSet<>();
    private static final List<AbstractReadingTests.ExpectedResult> expecteds = new ArrayList<>();
    static{
        try{
            final Path f = IOUtilities.getResourceAsPath("org/geotoolkit/sql/test-trajectories.nc");
            final Map params = new HashMap<>();
            params.put(NetcdfObservationStoreFactory.IDENTIFIER.getName().toString(), "observationFile");
            params.put(NetcdfObservationStoreFactory.FILE_PATH.getName().toString(), f.toUri().toURL());

            store = (FeatureStore) DataStores.open(params);

            final String nsOM = "http://www.opengis.net/sampling/1.0";
            final String nsGML = "http://www.opengis.net/gml";
            final GenericName name = NamesExt.create(nsOM, "test-trajectories");
            names.add(name);

            final FeatureTypeBuilder featureTypeBuilder = new FeatureTypeBuilder();
            featureTypeBuilder.setName(name);
            featureTypeBuilder.addAttribute(String.class).setName(NamesExt.create(nsGML, "id")).addRole(AttributeRole.IDENTIFIER_COMPONENT);
            featureTypeBuilder.addAttribute(String.class).setName(NamesExt.create(nsGML, "description"));
            featureTypeBuilder.addAttribute(String.class).setName(NamesExt.create(nsGML, "name")).setMinimumOccurs(1).setMaximumOccurs(Integer.MAX_VALUE);
            featureTypeBuilder.addAttribute(String.class).setName(NamesExt.create(nsOM, "sampledFeature")).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
            featureTypeBuilder.addAttribute(Geometry.class).setName(NamesExt.create(nsOM, "position")).addRole(AttributeRole.DEFAULT_GEOMETRY);

            int size = 4;
            GeneralEnvelope env = new GeneralEnvelope(CRS.forCode("EPSG:27582"));
            env.setRange(0, -51.78333, 27.816);
            env.setRange(1, -19.802, 128.6);

            final AbstractReadingTests.ExpectedResult res = new AbstractReadingTests.ExpectedResult(name,
                    featureTypeBuilder.build(), size, env);
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
//                IOUtilities.deleteRecursively(fdb.toPath());
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
    protected Set<GenericName> getExpectedNames() {
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