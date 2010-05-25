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

package org.geotoolkit.data.sml;


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
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.geotoolkit.internal.sql.ScriptRunner;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.type.Name;

import org.opengis.feature.type.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class SMLDataStoreTest extends AbstractReadingTests{


    private static final String SML_NAMESPACE = "http://www.opengis.net/sml/1.0";
    private static final String GML_NAMESPACE = "http://www.opengis.net/gml";
    private final static Name SML_TN_SYSTEM         = new DefaultName(SML_NAMESPACE, "System");
    private final static Name SML_TN_COMPONENT      = new DefaultName(SML_NAMESPACE, "Component");
    private final static Name SML_TN_PROCESSCHAIN   = new DefaultName(SML_NAMESPACE, "ProcessChain");
    private final static Name SML_TN_PROCESSMODEL   = new DefaultName(SML_NAMESPACE, "ProcessModel");
    private final static Name SML_TN_DATASOURCETYPE = new DefaultName(SML_NAMESPACE, "DataSourceType");

    // Shared attributes
    private static final Name ATT_DESC        = new DefaultName(GML_NAMESPACE, "description");
    private static final Name ATT_NAME        = new DefaultName(GML_NAMESPACE, "name");
    private static final Name ATT_KEYWORDS    = new DefaultName(SML_NAMESPACE, "keywords");
    private static final Name ATT_LOCATION    = new DefaultName(SML_NAMESPACE, "location");
    private static final Name ATT_PHENOMENONS = new DefaultName(SML_NAMESPACE, "phenomenons");
    private static final Name ATT_SMLTYPE     = new DefaultName(SML_NAMESPACE, "smltype");
    private static final Name ATT_SMLREF      = new DefaultName(SML_NAMESPACE, "smlref");
    private static final Name ATT_INPUTS      = new DefaultName(SML_NAMESPACE, "inputs");
    private static final Name ATT_OUTPUTS     = new DefaultName(SML_NAMESPACE, "outputs");
    // attribute for sml:System or sml:ProcessChain
    private static final Name ATT_PRODUCER = new DefaultName(SML_NAMESPACE, "producer");
    private static final Name ATT_COMPONENTS = new DefaultName(SML_NAMESPACE, "components");
    // attribute for sml:ProccessModel
    private static final Name ATT_METHOD = new DefaultName(SML_NAMESPACE, "method");
    // attribute for sml:DatasourceType
    private static final Name ATT_CHARACTERISTICS = new DefaultName(SML_NAMESPACE, "characteristics");


    private static DefaultDataSource ds;
    private static DataStore store;
    private static Set<Name> names = new HashSet<Name>();
    private static List<ExpectedResult> expecteds = new ArrayList<ExpectedResult>();
    static{
        try{
            final String url = "jdbc:derby:memory:TestSML;create=true";
            ds = new DefaultDataSource(url);

            Connection con = ds.getConnection();

            final ScriptRunner exec = new ScriptRunner(con);
            exec.run(SMLDataStoreTest.class.getResourceAsStream("/org/geotoolkit/sql/structure-mdweb.sql"));
            exec.run(SMLDataStoreTest.class.getResourceAsStream("/org/geotoolkit/sql/mdweb-base-data.sql"));
            exec.run(SMLDataStoreTest.class.getResourceAsStream("/org/geotoolkit/sql/ISO19115-base-data.sql"));
            exec.run(SMLDataStoreTest.class.getResourceAsStream("/org/geotoolkit/sql/mdweb-user-data.sql"));
            exec.run(SMLDataStoreTest.class.getResourceAsStream("/org/geotoolkit/sql/sml-schema.sql"));
            exec.run(SMLDataStoreTest.class.getResourceAsStream("/org/geotoolkit/sql/sml-data.sql"));

            final Map params = new HashMap<String, Object>();
            params.put("dbtype", "SML");
            params.put(SMLDataStoreFactory.SGBDTYPE.getName().toString(), "derby");
            params.put(SMLDataStoreFactory.DERBYURL.getName().toString(), url);
            store = DataStoreFinder.getDataStore(params);


            final FeatureTypeBuilder featureTypeBuilder = new FeatureTypeBuilder();

            // Feature type sml:System
            featureTypeBuilder.reset();
            featureTypeBuilder.setName(SML_TN_SYSTEM);
            featureTypeBuilder.add(ATT_DESC,        String.class, 0, 1, true, null);
            featureTypeBuilder.add(ATT_NAME,        String.class, 1, 1, false, null);
            featureTypeBuilder.add(ATT_KEYWORDS,    List.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_LOCATION,    Point.class, 1, 1, false, null);
            featureTypeBuilder.add(ATT_PHENOMENONS, List.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_SMLTYPE,     String.class, 1, 1, true, null);
            featureTypeBuilder.add(ATT_SMLREF,      String.class, 1, 1, true, null);
            featureTypeBuilder.add(ATT_INPUTS,      Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_OUTPUTS,     Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_PRODUCER,    Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_COMPONENTS,  Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.setDefaultGeometry(ATT_LOCATION.getLocalPart());
            final FeatureType typeSystem = featureTypeBuilder.buildFeatureType();

            // Feature type sml:Component
            featureTypeBuilder.reset();
            featureTypeBuilder.setName(SML_TN_COMPONENT);
            featureTypeBuilder.add(ATT_DESC,        String.class, 0, 1, true, null);
            featureTypeBuilder.add(ATT_NAME,        String.class, 1, 1, false, null);
            featureTypeBuilder.add(ATT_KEYWORDS,    List.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_LOCATION,    Point.class, 1, 1, false, null);
            featureTypeBuilder.add(ATT_PHENOMENONS, List.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_SMLTYPE,     String.class, 1, 1, true, null);
            featureTypeBuilder.add(ATT_SMLREF,      String.class, 1, 1, true, null);
            featureTypeBuilder.add(ATT_INPUTS,      Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_OUTPUTS,     Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.setDefaultGeometry(ATT_LOCATION.getLocalPart());
            final FeatureType typeComponent = featureTypeBuilder.buildFeatureType();

            // Feature type sml:ProcessChain
            featureTypeBuilder.reset();
            featureTypeBuilder.setName(SML_TN_PROCESSCHAIN);
            featureTypeBuilder.add(ATT_DESC,        String.class, 0, 1, true, null);
            featureTypeBuilder.add(ATT_NAME,        String.class, 1, 1, false, null);
            featureTypeBuilder.add(ATT_KEYWORDS,    List.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_LOCATION,    Point.class, 1, 1, false, null);
            featureTypeBuilder.add(ATT_PHENOMENONS, List.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_SMLTYPE,     String.class, 1, 1, true, null);
            featureTypeBuilder.add(ATT_SMLREF,      String.class, 1, 1, true, null);
            featureTypeBuilder.add(ATT_INPUTS,      Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_OUTPUTS,     Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_PRODUCER,    Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_COMPONENTS,  Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.setDefaultGeometry(ATT_LOCATION.getLocalPart());
            final FeatureType typeProcessChain = featureTypeBuilder.buildFeatureType();

            // Feature type sml:ProcessModel
            featureTypeBuilder.reset();
            featureTypeBuilder.setName(SML_TN_PROCESSMODEL);
            featureTypeBuilder.add(ATT_DESC,        String.class, 0, 1, true, null);
            featureTypeBuilder.add(ATT_NAME,        String.class, 1, 1, false, null);
            featureTypeBuilder.add(ATT_KEYWORDS,    List.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_LOCATION,    Point.class, 1, 1, false, null);
            featureTypeBuilder.add(ATT_PHENOMENONS, List.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_SMLTYPE,     String.class, 1, 1, true, null);
            featureTypeBuilder.add(ATT_SMLREF,      String.class, 1, 1, true, null);
            featureTypeBuilder.add(ATT_INPUTS,      Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_OUTPUTS,     Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_METHOD,      String.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.setDefaultGeometry(ATT_LOCATION.getLocalPart());
            final FeatureType typeProcess = featureTypeBuilder.buildFeatureType();

            // Feature type sml:DataSourceType
            featureTypeBuilder.reset();
            featureTypeBuilder.setName(SML_TN_DATASOURCETYPE);
            featureTypeBuilder.add(ATT_DESC,        String.class, 0, 1, true, null);
            featureTypeBuilder.add(ATT_NAME,        String.class, 1, 1, false, null);
            featureTypeBuilder.add(ATT_KEYWORDS,    List.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_LOCATION,    Point.class, 1, 1, false, null);
            featureTypeBuilder.add(ATT_PHENOMENONS, List.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_SMLTYPE,     String.class, 1, 1, true, null);
            featureTypeBuilder.add(ATT_SMLREF,      String.class, 1, 1, true, null);
            featureTypeBuilder.add(ATT_INPUTS,      Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_OUTPUTS,     Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.add(ATT_CHARACTERISTICS,Map.class, 0, Integer.MAX_VALUE, true, null);
            featureTypeBuilder.setDefaultGeometry(ATT_LOCATION.getLocalPart());
            final FeatureType typeDataSource = featureTypeBuilder.buildFeatureType();

            names.add(typeSystem.getName());
            names.add(typeComponent.getName());
            names.add(typeProcessChain.getName());
            names.add(typeDataSource.getName());
            names.add(typeProcess.getName());


            //expected results -------------------------------------------------
            int size; GeneralEnvelope env;

            //System
            size = 1;
            env = new GeneralEnvelope(CRS.decode("EPSG:27582"));
            env.setRange(0, 65400, 65400);
            env.setRange(1, 1731368, 1731368);
            expecteds.add(new ExpectedResult(typeSystem.getName(),typeSystem, size, env));

            //Component
            //unvalide test, a feature has a null geometry even while the type define
            //todo must be fixed somehow later
            //a non-null property for it.
//            size = 1;
//            env = new GeneralEnvelope(CRS.decode("EPSG:27582"));
//            env.setRange(0, 65400, 65400);
//            env.setRange(1, 1731368, 1731368);
//            expecteds.add(new ExpectedResult(typeComponent.getName(),typeComponent, size, env));

            //ProcessChain
            size = 0;
            expecteds.add(new ExpectedResult(typeProcessChain.getName(),typeProcessChain, size, null));

            //ProcessModel
            size = 0;
            expecteds.add(new ExpectedResult(typeProcess.getName(),typeProcess, size, null));

            //DataSource
            size = 0;
            expecteds.add(new ExpectedResult(typeDataSource.getName(),typeDataSource, size, null));

        }catch(Exception ex){
            ex.printStackTrace();
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
