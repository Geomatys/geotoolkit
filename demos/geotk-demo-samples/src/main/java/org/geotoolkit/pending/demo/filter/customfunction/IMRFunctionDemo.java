
package org.geotoolkit.pending.demo.filter.customfunction;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.util.Date;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.pending.demo.filter.customaccessor.Pojo;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;


public class IMRFunctionDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) {

        final Pojo pojo = aPOJO();
        final Feature feature = aFeature();

        final Expression param1 = FF.literal(45);
        final Expression param2 = FF.property("depth");
        final Expression imrFunction = FF.function(IMRFunctionFactory.DENSITY, param1, param2);

        System.out.println(imrFunction.evaluate(pojo));
        System.out.println(imrFunction.evaluate(feature));



    }

    private static Pojo aPOJO(){
        final Pojo myPojo = new Pojo("squid", 1200, new Date());
        return myPojo;
    }

    private static Feature aFeature(){
        final GeometryFactory gf = new GeometryFactory();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("marine-life");
        ftb.add("family", String.class);
        ftb.add("depth", Integer.class);
        ftb.add("localisation", Point.class, DefaultGeographicCRS.WGS84);
        final FeatureType type = ftb.buildFeatureType();

        final Feature feature1 = FeatureUtilities.defaultFeature(type, "id-1");
        feature1.getProperty("family").setValue("seashell");
        feature1.getProperty("depth").setValue(1200);
        feature1.getProperty("localisation").setValue(gf.createPoint(new Coordinate(5, 2)));

        return feature1;
    }

}
