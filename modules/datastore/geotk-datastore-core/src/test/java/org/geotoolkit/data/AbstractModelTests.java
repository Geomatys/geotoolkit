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
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.storage.DataStoreException;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory;

/**
 * Generic schema manipulation tests
 * Tests schema modifications
 *
 * @author Johann Sorel (Geomatys)
 * todo make more generic tests
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
        final List<Class> geometryBindings = getSupportedGeometryTypes();
        final List<Class> bindinds = getSupportedAttributTypes();
        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        final Session session = store.createSession(true);
        

        for(final Class geomType : geometryBindings){

            //create the schema ------------------------------------------------
            final String name = "testname";
            sftb.reset();
            sftb.setName(name);            
            sftb.add("att_geometry", geomType, DefaultGeographicCRS.WGS84);
            sftb.setDefaultGeometry("att_geometry");            
            for(int i=0; i<bindinds.size(); i++){
                sftb.add("att"+i, bindinds.get(i));
            }            
            final SimpleFeatureType sft = sftb.buildSimpleFeatureType();

            //add listeners
            SimpleListener storeListen = new SimpleListener();
            SimpleListener sessionListen = new SimpleListener();
            store.addStorageListener(storeListen);
            session.addStorageListener(sessionListen);

            store.createSchema(sft.getName(), sft);

            final SimpleFeatureType type = (SimpleFeatureType) store.getFeatureType(name);
            assertNotNull(type);
            assertEquals(sft, type);

            //check listeners
//            assertEquals(1, storeListen.numManageEvent);
//            assertEquals(1, sessionListen.numManageEvent);
//            assertEquals(0, storeListen.numContentEvent);
//            assertEquals(0, sessionListen.numContentEvent);
//            assertNotNull(storeListen.lastManagementEvent);
//            assertNotNull(sessionListen.lastManagementEvent);
//            assertNull(storeListen.lastContentEvent);
//            assertNull(sessionListen.lastContentEvent);
//            assertEquals(StorageManagementEvent.Type.ADD, storeListen.lastManagementEvent.getType());
//            assertEquals(StorageManagementEvent.Type.ADD, sessionListen.lastManagementEvent.getType());
//            assertEquals(name, storeListen.lastManagementEvent.getFeatureTypeName().getLocalPart());
//            assertEquals(name, sessionListen.lastManagementEvent.getFeatureTypeName().getLocalPart());
//            assertEquals(sft, storeListen.lastManagementEvent.getNewFeatureType());
//            assertEquals(sft, sessionListen.lastManagementEvent.getNewFeatureType());
//            assertEquals(null, storeListen.lastManagementEvent.getOldFeatureType());
//            assertEquals(null, sessionListen.lastManagementEvent.getOldFeatureType());

            store.removeStorageListener(storeListen);
            session.removeStorageListener(sessionListen);


            //delete the created schema ----------------------------------------
            Name nsname = null;
            for(Name n : store.getNames()){
                if(n.getLocalPart().equalsIgnoreCase(name)){
                    nsname = n;
                    break;
                }
            }

            assertNotNull(nsname);
            readAndWriteTest(store, nsname);

            //add listeners
            storeListen = new SimpleListener();
            sessionListen = new SimpleListener();
            store.addStorageListener(storeListen);
            session.addStorageListener(sessionListen);

            store.deleteSchema(nsname);

            //check listeners
//            assertEquals(1, storeListen.numManageEvent);
//            assertEquals(1, sessionListen.numManageEvent);
//            assertEquals(0, storeListen.numContentEvent);
//            assertEquals(0, sessionListen.numContentEvent);
//            assertNotNull(storeListen.lastManagementEvent);
//            assertNotNull(sessionListen.lastManagementEvent);
//            assertNull(storeListen.lastContentEvent);
//            assertNull(sessionListen.lastContentEvent);
//            assertEquals(StorageManagementEvent.Type.DELETE, storeListen.lastManagementEvent.getType());
//            assertEquals(StorageManagementEvent.Type.DELETE, sessionListen.lastManagementEvent.getType());
//            assertEquals(name, storeListen.lastManagementEvent.getFeatureTypeName().getLocalPart());
//            assertEquals(name, sessionListen.lastManagementEvent.getFeatureTypeName().getLocalPart());
//            assertEquals(null, storeListen.lastManagementEvent.getNewFeatureType());
//            assertEquals(null, sessionListen.lastManagementEvent.getNewFeatureType());
//            assertEquals(sft, storeListen.lastManagementEvent.getOldFeatureType());
//            assertEquals(sft, sessionListen.lastManagementEvent.getOldFeatureType());

            store.removeStorageListener(storeListen);
            session.removeStorageListener(sessionListen);

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


    private static class SimpleListener implements StorageListener{

        public int numManageEvent = 0;
        public int numContentEvent = 0;
        public StorageManagementEvent lastManagementEvent = null;
        public StorageContentEvent lastContentEvent = null;

        @Override
        public void structureChanged(StorageManagementEvent event) {
            numManageEvent++;
            this.lastManagementEvent = event;
        }

        @Override
        public void contentChanged(StorageContentEvent event) {
            numContentEvent++;
            this.lastContentEvent = event;
        }

    }

}
