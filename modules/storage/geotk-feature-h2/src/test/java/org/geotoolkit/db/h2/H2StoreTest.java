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
package org.geotoolkit.db.h2;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import java.util.Collections;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.referencing.CRS;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class H2StoreTest {
    
    /**
     * TODO reuse JDBC feature store test classes.
     * 
     * @throws DataStoreException
     * @throws FactoryException 
     */
    @Test
    public void createAndReadTest() throws DataStoreException, FactoryException{
        
        final CoordinateReferenceSystem crs = CRS.decode("EPSG:4326",true);
        
        final BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:h2:mem:test");
        ds.setUsername("user");
        ds.setPassword("pwd");
        
        final ParameterValueGroup params = H2FeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(H2FeatureStoreFactory.USER, params).setValue("user");
        Parameters.getOrCreate(H2FeatureStoreFactory.PASSWORD, params).setValue("pwd");
        Parameters.getOrCreate(H2FeatureStoreFactory.PORT, params).setValue(5555);
        Parameters.getOrCreate(H2FeatureStoreFactory.DATABASE, params).setValue("sirs");
        Parameters.getOrCreate(H2FeatureStoreFactory.HOST, params).setValue("localhost");
        Parameters.getOrCreate(H2FeatureStoreFactory.SIMPLETYPE, params).setValue(Boolean.FALSE);
        Parameters.getOrCreate(H2FeatureStoreFactory.DATASOURCE, params).setValue(ds);
        
        final FeatureStore store = new H2FeatureStoreFactory().create(params);
        
        
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("route");
        AttributeDescriptor add = ftb.add("id", String.class);
        add.getUserData().put(HintsPending.PROPERTY_IS_IDENTIFIER,Boolean.TRUE);
        ftb.add("geom", LineString.class, crs);
        final FeatureType featureType = ftb.buildFeatureType();
        
        
        store.createFeatureType(ftb.getName(), featureType);
        
        for(Name n : store.getNames()){
            final FeatureType ft = store.getFeatureType(n);
            System.out.println(store.getFeatureType(n));
            
            
            final GeometryFactory GF = new GeometryFactory();
            final LineString geom = GF.createLineString(new Coordinate[]{new Coordinate(10, 20),new Coordinate(30, 40)});
            
            final Feature f = FeatureUtilities.defaultFeature(ft, "id-0");
            f.setPropertyValue("id", "test");
            f.setPropertyValue("geom", geom);
            
            store.addFeatures(n, Collections.singleton(f));
            
            final FeatureCollection res = store.createSession(true).getFeatureCollection(QueryBuilder.all(n));
            for(Feature rf : res){
                System.out.println(rf);
            }
            
            
        }
        
    }
    
}
