
package org.geotoolkit.pending.demo.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.pending.demo.Demos;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.Identifier;


public class FilterDemo {

    private static final FilterFactory FF = DefaultFactories.forBuildin(FilterFactory.class);

    public static void main(String[] args) throws DataStoreException {
        Demos.init();

        final FeatureSet collection = createSampleCollection();
        System.out.println(collection);

        testFilter(collection, attributeFilter());
        testFilter(collection, idFilter());
        testFilter(collection, combinedFilter());
        testFilter(collection, bboxFilter());

    }

    private static void testFilter(FeatureSet collection, Filter filter) throws DataStoreException{
        System.out.println("\n==============================================================\n");
        System.out.println(filter);
        System.out.println('\n');

        final Iterator<Feature> ite = collection.features(false).iterator();
        while(ite.hasNext()){
            final Feature candidate = ite.next();
            if(filter.evaluate(candidate)){
                System.out.println(candidate);
            }
        }
    }

    private static Filter attributeFilter(){
        final PropertyName property = FF.property("name");
        final Literal value = FF.literal("robert");
        final Filter filter = FF.equals(property, value);
        return filter;
    }

    private static Filter idFilter(){
        final Set<Identifier> ids = new HashSet<Identifier>();
        ids.add(FF.featureId("id-1"));
        ids.add(FF.featureId("id-4"));
        final Filter filter = FF.id(ids);
        return filter;
    }

    private static Filter combinedFilter(){
        final Filter ageBetween = FF.between(FF.property("age"), FF.literal(3), FF.literal(30));
        final Filter jobType = FF.like(FF.property("job"), "*developer*");
        final Filter combined = FF.and(ageBetween,jobType);
        return combined;
    }

    private static Filter bboxFilter(){
        final Filter bbox = FF.bbox("localisation", 10, 0, 30, 50, null);
        return bbox;
    }

    public static FeatureSet createSampleCollection(){

        final GeometryFactory gf = new GeometryFactory();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("person");
        ftb.addAttribute(String.class).setName("id").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Integer.class).setName("age");
        ftb.addAttribute(String.class).setName("job");
        ftb.addAttribute(Point.class).setName("localisation").setCRS(CommonCRS.WGS84.normalizedGeographic());
        final FeatureType type = ftb.build();

        final Feature feature1 = type.newInstance();
        feature1.setPropertyValue("id","id-1");
        feature1.setPropertyValue("name","marcel");
        feature1.setPropertyValue("age",18);
        feature1.setPropertyValue("job","developer");
        feature1.setPropertyValue("localisation",gf.createPoint(new Coordinate(5, 2)));

        final Feature feature2 = type.newInstance();
        feature2.setPropertyValue("id","id-2");
        feature2.setPropertyValue("name","janine");
        feature2.setPropertyValue("age",27);
        feature2.setPropertyValue("job","advanced developer");
        feature2.setPropertyValue("localisation",gf.createPoint(new Coordinate(17, 39)));

        final Feature feature3 = type.newInstance();
        feature3.setPropertyValue("id","id-3");
        feature3.setPropertyValue("name","robert");
        feature3.setPropertyValue("age",5);
        feature3.setPropertyValue("job","student");
        feature3.setPropertyValue("localisation",gf.createPoint(new Coordinate(-9, 5)));

        final Feature feature4 = type.newInstance();
        feature4.setPropertyValue("id","id-4");
        feature4.setPropertyValue("name","hector");
        feature4.setPropertyValue("age",48);
        feature4.setPropertyValue("job","manager");
        feature4.setPropertyValue("localisation",gf.createPoint(new Coordinate(22, 7)));

        return FeatureStoreUtilities.collection(feature1,feature2,feature3,feature4);
    }

}
