/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.data;

import java.util.List;
import junit.framework.TestCase;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory;

/**
 * Generic schema manipulation tests
 * Tests schema modifications
 *
 * @author Johann Sorel (Geomatys)
 * todo make generic tests
 */
public abstract class AbstractModelTests extends TestCase{

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    protected abstract DataStore getDataStore();

    protected abstract List<Class> getSupportedGeometryTypes();

    protected abstract List<Class> getSupportedAttributTypes();


    @Test
    public void testDataStore(){
        final DataStore store = getDataStore();
        assertNotNull(store);
    }

    @Test
    public void testSchemaCreation() throws Exception{
        final DataStore store = getDataStore();
        final List<Class> geometryBindings = getSupportedAttributTypes();
        final List<Class> bindinds = getSupportedAttributTypes();
        final SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        

        for(final Class geomType : geometryBindings){
            final String name = "testname";
            sftb.reset();
            sftb.setName(name);
            
            sftb.add("att_geometry", geomType, DefaultGeographicCRS.WGS84);
            sftb.setDefaultGeometry("att_geometry");
            
            for(int i=0; i<bindinds.size(); i++){
                sftb.add("att"+i, bindinds.get(i));
            }
            
            final SimpleFeatureType sft = sftb.buildFeatureType();

            store.createSchema(sft.getName(), sft);

            final SimpleFeatureType type = (SimpleFeatureType) store.getFeatureType(name);
            assertNotNull(type);
            assertEquals(sft, type);

            //delete the created schema
            Name nsname = null;
            for(Name n : store.getNames()){
                if(n.getLocalPart().equalsIgnoreCase(name)){
                    nsname = n;
                    break;
                }
            }

            readAndWriteTest(store, nsname);


            assertNotNull(nsname);
            store.deleteSchema(nsname);

            try{
                store.getFeatureType(nsname);
                throw new Exception("Should have raised an error.");
            }catch(DataStoreException ex){
                //ok
            }
            
        }

    }

    private void readAndWriteTest(DataStore store, Name name){
        //todo test creating a few features
    }

    @Test
    public void testUpdateSchemas(){
        final DataStore store = getDataStore();
        //todo, must find a way to test this in a correct way.
    }


}
