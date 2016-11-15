
package org.geotoolkit.pending.demo.datamodel;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import java.util.Date;
import org.apache.sis.feature.builder.AttributeRole;
import org.geotoolkit.util.NamesExt;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.referencing.CRS;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

public class FeatureTypeBuilderDemo {

    public static void main(String[] args) throws NoSuchAuthorityCodeException, FactoryException {
        Demos.init();

        System.out.println(createSimpleType());
        System.out.println(createComplexType());
    }

    public static FeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException{
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Fish");
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Integer.class).setName("length");
        ftb.addAttribute(LineString.class).setName("lastPosition").setCRS( CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Date.class).setName("lastPositionDate");
        ftb.addAttribute(Float.class).setName("direction");
        final FeatureType sft = ftb.build();
        return sft;
    }

    public static FeatureType createComplexType() throws NoSuchAuthorityCodeException, FactoryException{
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        //track point type
        ftb.setName("FishTrackPoint");
        ftb.addAttribute(Point.class).setName("location").setCRS(CRS.forCode("EPSG:3395"));
        ftb.addAttribute(Date.class).setName("time");
        final FeatureType trackPointType = ftb.build();

        //fish type
        ftb = new FeatureTypeBuilder();
        ftb.setName("Fish");
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(String.class).setName("code");
        final FeatureType fishType = ftb.build();

        //fish track type
        ftb = new FeatureTypeBuilder();
        ftb.setName("FishTrack");
        ftb.addAttribute(Long.class).setName("trackNumber");
        ftb.addAssociation(fishType).setName("fish");
        ftb.addAssociation(trackPointType).setName("trackpoints").setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        final FeatureType ft = ftb.build();
        return ft;
    }

}
