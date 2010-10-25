/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.data.memory;

import java.util.Set;
import org.geotoolkit.data.FeatureCollection;
import org.opengis.feature.Feature;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.StorageCountListener;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.storage.DataStoreException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import static org.junit.Assert.*;

/**
 *
 * @author geoadmin
 */
public class MemoryWritingTest {

    private final FeatureType type;
    private final MemoryDataStore store;

    public MemoryWritingTest() throws DataStoreException {

        store = new MemoryDataStore();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("att", String.class);
        type = ftb.buildFeatureType();

        store.createSchema(type.getName(), type);

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testIdEvent() throws DataStoreException {

        StorageCountListener listener = new StorageCountListener();


        //test on the datastore with feature writer ////////////////////////////
        store.addStorageListener(listener);

        final FeatureWriter writer = store.getFeatureWriterAppend(type.getName());
        final Feature feature = writer.next();
        feature.getProperty("att").setValue("ii");
        writer.write();
        writer.close();

        assertEquals(1, listener.numContentEvent);
        assertNotNull(listener.lastContentEvent);

        Id ids = listener.lastContentEvent.getIds();
        assertNotNull(ids);
        Set<Object> objects = ids.getIDs();
        assertEquals(1, objects.size());
        Object obj = objects.iterator().next();
        assertNotNull(obj);
        assertEquals("test.0", obj);
        store.removeStorageListener(listener);

        //test on a feature collection /////////////////////////////////////////
        listener = new StorageCountListener();
        Session session = store.createSession(false);
        FeatureCollection fc = session.getFeatureCollection(QueryBuilder.all(type.getName()));
        fc.addStorageListener(listener);

        Feature newFeature = FeatureUtilities.defaultFeature(type, "myID");
        fc.add(newFeature);


        assertEquals(1, listener.numContentEvent);
        assertNotNull(listener.lastContentEvent);

        ids = listener.lastContentEvent.getIds();
        assertNotNull(ids);
        objects = ids.getIDs();
        assertEquals(1, objects.size());
        obj = objects.iterator().next();
        assertNotNull(obj);
        assertEquals("myID", obj);
        store.removeStorageListener(listener);

    }
}
