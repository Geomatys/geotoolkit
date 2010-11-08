
package org.geotoolkit.pending.demo.filter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.util.HashSet;
import java.util.Set;

import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.Identifier;


public class FilterDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) {

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
        ftb.add("name", String.class);
        ftb.add("age", Integer.class);
        ftb.add("job", String.class);
        ftb.add("localisation", Point.class, DefaultGeographicCRS.WGS84);
        final FeatureType type = ftb.buildFeatureType();

        final Feature feature1 = FeatureUtilities.defaultFeature(type, "id-1");
        feature1.getProperty("name").setValue("marcel");
        feature1.getProperty("age").setValue(18);
        feature1.getProperty("job").setValue("developer");
        feature1.getProperty("localisation").setValue(gf.createPoint(new Coordinate(5, 2)));

        final Feature feature2 = FeatureUtilities.defaultFeature(type, "id-2");
        feature2.getProperty("name").setValue("janine");
        feature2.getProperty("age").setValue(27);
        feature2.getProperty("job").setValue("advanced developer");
        feature2.getProperty("localisation").setValue(gf.createPoint(new Coordinate(17, 39)));

        final Feature feature3 = FeatureUtilities.defaultFeature(type, "id-3");
        feature3.getProperty("name").setValue("robert");
        feature3.getProperty("age").setValue(5);
        feature3.getProperty("job").setValue("student");
        feature3.getProperty("localisation").setValue(gf.createPoint(new Coordinate(-9, 5)));

        final Feature feature4 = FeatureUtilities.defaultFeature(type, "id-4");
        feature4.getProperty("name").setValue("hector");
        feature4.getProperty("age").setValue(48);
        feature4.getProperty("job").setValue("manager");
        feature4.getProperty("localisation").setValue(gf.createPoint(new Coordinate(22, 7)));

        final FeatureCollection collection = DataUtilities.collection(feature1,feature2,feature3,feature4);
        return collection;
    }

}
