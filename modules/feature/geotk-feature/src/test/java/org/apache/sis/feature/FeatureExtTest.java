
package org.apache.sis.feature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.parameter.DefaultParameterDescriptor;
import org.apache.sis.parameter.DefaultParameterDescriptorGroup;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.geometry.jts.JTS;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureExtTest {

    private static final GeometryFactory GF = new GeometryFactory();

    @Test
    public void parameterToFeatureTest(){

        final GeneralParameterDescriptor att1 = new DefaultParameterDescriptor(
                Collections.singletonMap("name", "att1"), 1, 1, String.class, null, null, "testString");
        final GeneralParameterDescriptor att2 = new DefaultParameterDescriptor(
                Collections.singletonMap("name", "att2"), 0, 21, Integer.class, null, null, null);
        final ParameterDescriptorGroup group = new DefaultParameterDescriptorGroup(
                Collections.singletonMap("name", "group"), 1, 1, att1,att2);

        final ParameterValueGroup params = group.createValue();
        params.parameter("att1").setValue("value1");
        params.parameter("att2").setValue(45);

        final Feature feature = FeatureExt.toFeature(params);

        //expected feature
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("group");
        ftb.addAttribute(String.class).setName("att1").setDefaultValue("testString");
        ftb.addAttribute(Integer.class).setName("att2").setMinimumOccurs(0).setMaximumOccurs(21);
        final FeatureType expectedType = ftb.build();
        final Feature expectedFeature = expectedType.newInstance();
        expectedFeature.setPropertyValue("att1", "value1");
        expectedFeature.setPropertyValue("att2", 45);

        assertEquals(expectedFeature, feature);
    }

    @Test
    public void featureToMapTest(){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("group");
        ftb.addAttribute(String.class).setName("att1").setDefaultValue("testString");
        ftb.addAttribute(Integer.class).setName("att2").setMinimumOccurs(0).setMaximumOccurs(21);
        final FeatureType type = ftb.build();
        final Feature feature = type.newInstance();
        feature.setPropertyValue("att1", "value1");
        feature.setPropertyValue("att2", 45);

        final Map<String, Object> map = FeatureExt.toMap(feature);
        assertEquals(2, map.size());
        assertEquals("value1", map.get("att1"));
        assertEquals(45, map.get("att2"));

    }

    @Test
    public void mapToFeatureTest(){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("group");
        ftb.addAttribute(String.class).setName("att1").setDefaultValue("testString");
        ftb.addAttribute(Integer.class).setName("att2").setMinimumOccurs(0).setMaximumOccurs(21);
        final FeatureType type = ftb.build();
        final Feature expectedFeature = type.newInstance();
        expectedFeature.setPropertyValue("att1", "value1");
        expectedFeature.setPropertyValue("att2", 45);

        final Map<String, Object> map = new HashMap<>();
        map.put("att1", "value1");
        map.put("att2", 45);

        final Feature feature = FeatureExt.toFeature(map, type);

        assertEquals(expectedFeature, feature);
    }

    @Test
    public void samePropertiesTest() {
        FeatureTypeBuilder builder = new FeatureTypeBuilder();
        builder.setName("base type")
                .addAttribute(String.class).setName("first").setDefaultValue("this is a test");
        builder.addAttribute(Point.class).setName("I'm a geometry !").addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType baseType = builder.build();

        final FeatureType challenger = builder.setName("challenger type").build();
        assertTrue("We should detect that both feature types have got the same properties", FeatureExt.sameProperties(baseType, challenger, false));

        builder = new FeatureTypeBuilder();
        builder.setSuperTypes(baseType)
                .setName("a child")
                .addAttribute(Boolean.class).setName("a boolean");

        final FeatureType firstChild = builder.build();
        final FeatureType secondChild = builder.setSuperTypes(challenger).build();
        assertTrue("We should detect that both feature types have got the same properties (inherited ones included).", FeatureExt.sameProperties(firstChild, secondChild, true));

        FeatureType thirdChild = builder.setSuperTypes(firstChild).build();
        assertFalse("We should detect a difference in inherited properties", FeatureExt.sameProperties(firstChild, thirdChild, true));
    }

    @Test
    public void getEnvelopeTest() {

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("type");
        ftb.addAttribute(Geometry.class).setName("geom").addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType featureType = ftb.build();
        final Feature feature = featureType.newInstance();

        //case geometry is not set
        assertNull(FeatureExt.getEnvelope(feature));

        //case geometry has no crs
        Point geom = GF.createPoint(new Coordinate(10, 20));
        feature.setPropertyValue("geom", geom);
        final GeneralEnvelope envNoCrs = new GeneralEnvelope(2);
        envNoCrs.setRange(0, 10, 10);
        envNoCrs.setRange(1, 20, 20);
        assertEquals(envNoCrs, FeatureExt.getEnvelope(feature));

        //case geometry has a crs
        geom = GF.createPoint(new Coordinate(10, 20));
        JTS.setCRS(geom, CommonCRS.WGS84.normalizedGeographic());
        feature.setPropertyValue("geom", geom);
        final GeneralEnvelope envCrs = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        envCrs.setRange(0, 10, 10);
        envCrs.setRange(1, 20, 20);
        assertEquals(envCrs, FeatureExt.getEnvelope(feature));

    }
}
