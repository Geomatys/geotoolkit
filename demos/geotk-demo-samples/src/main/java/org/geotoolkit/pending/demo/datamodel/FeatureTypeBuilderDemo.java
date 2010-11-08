
package org.geotoolkit.pending.demo.datamodel;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import java.util.Date;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.referencing.CRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

public class FeatureTypeBuilderDemo {

    public static void main(String[] args) throws NoSuchAuthorityCodeException, FactoryException {
        System.out.println(createSimpleType());
        System.out.println(createComplexType());
    }

    public static SimpleFeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException{
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Fish");
        ftb.add("name", String.class);
        ftb.add("length", Integer.class);
        ftb.add("lastPosition", LineString.class, CRS.decode("EPSG:3395"));
        ftb.add("lastPositionDate", Date.class);
        ftb.add("direction", Float.class);
        ftb.setDefaultGeometry("lastPosition");
        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        return sft;
    }

    public static FeatureType createComplexType() throws NoSuchAuthorityCodeException, FactoryException{
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();

        //track point type
        ftb.setName("FishTrackPoint");
        ftb.add("location", Point.class, CRS.decode("EPSG:3395"));
        ftb.add("time", Date.class);
        final ComplexType trackPointType = ftb.buildType();

        //fish type
        ftb.reset();
        ftb.setName("Fish");
        ftb.add("name", String.class);
        ftb.add("code", String.class);
        final ComplexType fishType = ftb.buildType();

        //fish track type
        ftb.reset();
        ftb.setName("FishTrack");
        ftb.add("trackNumber", Long.class);
        AttributeDescriptor fishDesc = adb.create(fishType, DefaultName.valueOf("fish"),1,1,false,null);
        AttributeDescriptor trackpointsDesc = adb.create(trackPointType, DefaultName.valueOf("trackpoints"),0,Integer.MAX_VALUE,false,null);
        ftb.add(fishDesc);
        ftb.add(trackpointsDesc);
        final FeatureType ft = ftb.buildFeatureType();
        return ft;
    }

}
