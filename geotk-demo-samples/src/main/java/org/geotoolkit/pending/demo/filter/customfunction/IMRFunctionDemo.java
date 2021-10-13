
package org.geotoolkit.pending.demo.filter.customfunction;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import java.util.Date;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.pending.demo.filter.customaccessor.Pojo;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.filter.FilterUtilities;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Expression;


public class IMRFunctionDemo {

    private static final FilterFactory FF = FilterUtilities.FF;

    public static void main(String[] args) {
        Demos.init();

        final Pojo pojo = aPOJO();
        final Feature feature = aFeature();

        final Expression param1 = FF.literal(45);
        final Expression param2 = FF.property("depth");
        final Expression imrFunction = FF.function(IMRFunctionFactory.DENSITY, param1, param2);

        System.out.println(imrFunction.apply(pojo));
        System.out.println(imrFunction.apply(feature));
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
