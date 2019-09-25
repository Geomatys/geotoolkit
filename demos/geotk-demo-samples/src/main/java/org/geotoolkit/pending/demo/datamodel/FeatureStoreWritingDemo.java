

package org.geotoolkit.pending.demo.datamodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableFeatureSet;
import org.geotoolkit.data.DefiningFeatureSet;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.pending.demo.Demos;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.Identifier;

public class FeatureStoreWritingDemo {

    private static final FilterFactory FF = DefaultFactories.forBuildin(FilterFactory.class);

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
        final MemoryFeatureStore store = new MemoryFeatureStore();
        final WritableFeatureSet resource = (WritableFeatureSet) store.add(new DefiningFeatureSet(type, null));


        ////////////////////////////////////////////////////////////////////////////////
        // ADDING RECORDS //////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////

        //passing a collection -----------------------------------------------------------
        //used to copy values from one featurestore to another
        List<Feature> toAdd = new ArrayList<>();

        Feature feature = type.newInstance();
        feature.setPropertyValue("name","speedy");
        feature.setPropertyValue("length",78);
        feature.setPropertyValue("position",gf.createPoint(new Coordinate(-12, -31)));
        toAdd.add(feature);
        //and so on add features in the collection ...

        //and finally store them
        resource.add(toAdd.iterator());


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

    }

}
