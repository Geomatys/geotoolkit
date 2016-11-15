

package org.geotoolkit.pending.demo.datamodel;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.Date;


import org.geotoolkit.pending.demo.Demos;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

public class FeatureDemo {


    private static final GeometryFactory GF = new GeometryFactory();

    public static void main(String[] args) throws NoSuchAuthorityCodeException, FactoryException {
        Demos.init();

        final FeatureType type = FeatureTypeBuilderDemo.createSimpleType();
        final FeatureType cpxtype = FeatureTypeBuilderDemo.createComplexType();

        System.out.println(usingStaticBuilder(cpxtype));
    }

    private static Feature usingStaticBuilder(FeatureType type){
        final Feature sfb = type.newInstance();
        sfb.setPropertyValue("name", "placide");
        sfb.setPropertyValue("length", 12);
        sfb.setPropertyValue("lastPosition", GF.createPoint(new Coordinate(-10, 23)));
        sfb.setPropertyValue("lastPositionDate", new Date());
        sfb.setPropertyValue("direction", 56.498f);
        return sfb;
    }

}
