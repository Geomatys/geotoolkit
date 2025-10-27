
package org.geotoolkit.pending.demo.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.MemoryFeatureSet;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.pending.demo.Demos;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Literal;
import org.opengis.filter.ValueReference;


public class FilterDemo {

    private static final FilterFactory FF = FilterUtilities.FF;

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
            if(filter.test(candidate)){
                System.out.println(candidate);
            }
        }
    }

    private static Filter attributeFilter(){
        final ValueReference property = FF.property("name");
        final Literal value = FF.literal("robert");
        final Filter filter = FF.equal(property, value);
        return filter;
    }

    private static Filter idFilter(){
        final Set<Filter<Object>> ids = new HashSet<>();
        ids.add(FF.resourceId("id-1"));
        ids.add(FF.resourceId("id-4"));
        final Filter filter = FF.or(ids);
        return filter;
    }

    private static Filter combinedFilter(){
        final Filter ageBetween = FF.between(FF.property("age"), FF.literal(3), FF.literal(30));
        final Filter jobType = FF.like(FF.property("job"), "*developer*");
        final Filter combined = FF.and(ageBetween,jobType);
        return combined;
    }

    private static Filter bboxFilter(){
        final GeneralEnvelope env = new GeneralEnvelope(2);
        env.setRange(0, 10, 30);
        env.setRange(1, 0, 50);
        final Filter bbox = FF.bbox(FF.property("localisation"), env);
        return bbox;
    }

    public static FeatureSet createSampleCollection(){

        final GeometryFactory gf = org.geotoolkit.geometry.jts.JTS.getFactory();

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

        return new MemoryFeatureSet(null, type, Arrays.asList(feature1,feature2,feature3,feature4));
    }
}
