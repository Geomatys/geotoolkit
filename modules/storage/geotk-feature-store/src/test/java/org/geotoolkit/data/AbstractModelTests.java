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
import org.geotoolkit.feature.FeatureTypeExt;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.session.Session;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.event.StoreEvent;

/**
 * Generic schema manipulation tests
 * Tests schema modifications
 *
 * @author Johann Sorel (Geomatys)
 * todo make more generic tests
 */
public abstract class AbstractModelTests {

    protected abstract FeatureStore getDataStore();

    protected abstract List<Class> getSupportedGeometryTypes();

    protected abstract List<Class> getSupportedAttributTypes();


    @Test
    public void testDataStore(){
        final FeatureStore store = getDataStore();
        assertNotNull(store);
    }

    @Test
    public void testSchemaCreation() throws Exception{
        final FeatureStore store = getDataStore();
        final List<Class> geometryBindings = getSupportedGeometryTypes();
        final List<Class> bindinds = getSupportedAttributTypes();
        FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        final Session session = store.createSession(true);


        for(final Class geomType : geometryBindings){

            //create the schema ------------------------------------------------
            final String name = "testname";
            sftb = new FeatureTypeBuilder();
            sftb.setName(name);
            sftb.addAttribute(geomType).setName("att_geometry").setCRS(CommonCRS.WGS84.geographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
            for(int i=0; i<bindinds.size(); i++){
                sftb.addAttribute(bindinds.get(i)).setName("att"+i);
            }
            final FeatureType sft = sftb.build();

            //add listeners
            StorageCountListener storeListen = new StorageCountListener();
            StorageCountListener sessionListen = new StorageCountListener();
            store.addListener(StoreEvent.class, storeListen);
            session.addListener(StoreEvent.class, sessionListen);

            store.createFeatureType(sft);

            final FeatureType type = store.getFeatureType(name);
            assertNotNull(type);
            assertTrue(FeatureTypeExt.equalsIgnoreConvention(sft, type));

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

            store.removeListener(StoreEvent.class, storeListen);
            session.removeListener(StoreEvent.class, sessionListen);


            //delete the created schema ----------------------------------------
            GenericName nsname = null;
            for(GenericName n : store.getNames()){
                if(n.tip().toString().equalsIgnoreCase(name)){
                    nsname = n;
                    break;
                }
            }

            assertNotNull(nsname);
            readAndWriteTest(store, nsname);

            //add listeners
            storeListen = new StorageCountListener();
            sessionListen = new StorageCountListener();
            store.addListener(StoreEvent.class, storeListen);
            session.addListener(StoreEvent.class, sessionListen);

            store.deleteFeatureType(nsname.toString());

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

            store.removeListener(StoreEvent.class, storeListen);
            session.removeListener(StoreEvent.class, sessionListen);

            try{
                store.getFeatureType(nsname.toString());
                throw new Exception("Should have raised an error.");
            }catch(DataStoreException ex){
                //ok
            }

        }

    }

    private void readAndWriteTest(final FeatureStore store, final GenericName name){
        //todo test creating a few features
    }

    @Test
    public void testUpdateSchemas(){
        final FeatureStore store = getDataStore();
        //todo, must find a way to test this in a correct way.
    }

}
