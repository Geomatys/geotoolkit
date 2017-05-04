
package org.geotoolkit.pending.demo.filter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.util.HashSet;
import java.util.Set;
import org.apache.sis.feature.builder.AttributeRole;

import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.referencing.CommonCRS;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.Identifier;


public class FilterDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) {
        Demos.init();

        final FeatureCollection collection = createSampleCollection();
        System.out.println(collection);

        testFilter(collection, attributeFilter());
        testFilter(collection, idFilter());
        testFilter(collection, combinedFilter());
        testFilter(collection, bboxFilter());

    }

    private static void testFilter(FeatureCollection collection, Filter filter){
        System.out.println("\n==============================================================\n");
        System.out.println(filter);
        System.out.println('\n');

        final FeatureIterator ite = collection.iterator();
        try{
            while(ite.hasNext()){
                final Feature candidate = ite.next();
                if(filter.evaluate(candidate)){
                    System.out.println(candidate);
                }
            }
        }finally{
            ite.close();
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
        ids.add(new DefaultFeatureId("id-1"));
        ids.add(new DefaultFeatureId("id-4"));
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

    public static FeatureCollection createSampleCollection(){

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

        final FeatureCollection collection = FeatureStoreUtilities.collection(feature1,feature2,feature3,feature4);
        return collection;
    }

}
