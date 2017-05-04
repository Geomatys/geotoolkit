
package org.geotoolkit.pending.demo.filter.customfunction;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.util.Date;

import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.pending.demo.filter.customaccessor.Pojo;
import org.apache.sis.referencing.CommonCRS;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;


public class IMRFunctionDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) {
        Demos.init();

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
        ftb.addAttribute(String.class).setName("family");
        ftb.addAttribute(Integer.class).setName("depth");
        ftb.addAttribute(Point.class).setName("localisation").setCRS(CommonCRS.WGS84.normalizedGeographic());
        final FeatureType type = ftb.build();

        final Feature feature1 = type.newInstance();
        feature1.setPropertyValue("family","seashell");
        feature1.setPropertyValue("depth",1200);
        feature1.setPropertyValue("localisation",gf.createPoint(new Coordinate(5, 2)));

        return feature1;
    }

}
