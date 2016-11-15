
package org.geotoolkit.data.memory;

import java.util.Set;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.StorageCountListener;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.apache.sis.storage.DataStoreException;
import org.junit.Test;
import org.opengis.filter.Id;
import static org.junit.Assert.*;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.apache.sis.internal.feature.AttributeConvention;
import org.opengis.filter.Filter;

/**
 *
 * @author geoadmin
 */
public class MemoryWritingTest extends org.geotoolkit.test.TestBase {

    private final FeatureType type;
    private final MemoryFeatureStore store;

    public MemoryWritingTest() throws DataStoreException {

        store = new MemoryFeatureStore();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("att");
        type = ftb.build();

        store.createFeatureType(type);

    }

    @Test
    public void testIdEvent() throws DataStoreException {

        StorageCountListener listener = new StorageCountListener();


        //test on the featurestore with feature writer ////////////////////////////
        store.addStorageListener(listener);

        final FeatureWriter writer = store.getFeatureWriter(QueryBuilder.filtered(type.getName().toString(),Filter.EXCLUDE));
        final Feature feature = writer.next();
        feature.setPropertyValue("att","ii");
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

        Feature newFeature = type.newInstance();
        newFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "myID");
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
