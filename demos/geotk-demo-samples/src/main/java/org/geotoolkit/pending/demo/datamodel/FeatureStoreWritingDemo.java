

package org.geotoolkit.pending.demo.datamodel;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.util.HashSet;
import java.util.Set;
import org.apache.sis.feature.builder.AttributeRole;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.Identifier;

public class FeatureStoreWritingDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) throws  DataStoreException {
        Demos.init();

        final GeometryFactory gf = new GeometryFactory();


        //start by creating a memory featurestore for this test -----------------------------
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Fish");
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Integer.class).setName("length");
        ftb.addAttribute(Point.class).setName("position").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType type = ftb.build();


        //create the featurestore ---------------------------------------------------------
        final FeatureStore store = new MemoryFeatureStore();
        store.createFeatureType(type);


        ////////////////////////////////////////////////////////////////////////////////
        // ADDING RECORDS //////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////

        //working directly on the featurestore --------------------------------------------
        //best performance
        final FeatureWriter writer = store.getFeatureWriter(QueryBuilder.filtered(type.getName().toString(),Filter.EXCLUDE));
        Feature feature = writer.next();
        feature.setPropertyValue("name","sam");
        feature.setPropertyValue("length",30);
        feature.setPropertyValue("position",gf.createPoint(new Coordinate(20, 30)));
        writer.write();

        feature = writer.next();
        feature.setPropertyValue("name","tomy");
        feature.setPropertyValue("length",5);
        feature.setPropertyValue("position",gf.createPoint(new Coordinate(41, 56)));
        writer.write();

        //and so on write features ...

        writer.close();


        //passing a collection -----------------------------------------------------------
        //used to copy values from one featurestore to another
        FeatureCollection toAdd = FeatureStoreUtilities.collection("collectionID", type);

        feature = type.newInstance();
        feature.setPropertyValue("name","speedy");
        feature.setPropertyValue("length",78);
        feature.setPropertyValue("position",gf.createPoint(new Coordinate(-12, -31)));
        toAdd.add(feature);
        //and so on add features in the collection ...

        //and finally store them
        store.addFeatures(type.getName().toString(), toAdd);


        //From a the session -----------------------------------------------------------
        final Session session = store.createSession(true);
        toAdd = FeatureStoreUtilities.collection("collectionID", type);

        feature = type.newInstance();
        feature.setPropertyValue("name","ginette");
        feature.setPropertyValue("length",74);
        feature.setPropertyValue("position",gf.createPoint(new Coordinate(56, 101)));
        toAdd.add(feature);
        //and so on add features in the collection ...

        session.addFeatures(type.getName().toString(), toAdd);
        //so far thoses features are only visible in the session, don't forget to commit
        session.commit();


        //On a FeatureCollection like normal java ----------------------------------------
        FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(type.getName()));

        feature = type.newInstance();
        feature.setPropertyValue("name","marcel");
        feature.setPropertyValue("length",125);
        feature.setPropertyValue("position",gf.createPoint(new Coordinate(-79, 2)));

        col.add(feature);

        session.commit();

        System.out.println(col);


        ////////////////////////////////////////////////////////////////////////////////
        // REMOVING RECORDS ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////

        //on the featurestore ------------------------------------------------------------
        Set<Identifier> ids = new HashSet<Identifier>();
        ids.add(new DefaultFeatureId("Fish.1"));
        store.removeFeatures(type.getName().toString(), FF.id(ids));

        //same thing on the session and normal java way on the collection.
        //to remove everything use
        store.removeFeatures(type.getName().toString(), Filter.INCLUDE);

        System.out.println("Number of features = " + col.size());

    }

}
