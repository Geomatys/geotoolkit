
package org.geotoolkit.feature;

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
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.parameter.DefaultParameterDescriptor;
import org.apache.sis.parameter.DefaultParameterDescriptorGroup;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.test.Assert;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.metadata.acquisition.GeometryType;
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

    @Test
    public void findDefaultGeometry() {
        FeatureTypeBuilder builder = new FeatureTypeBuilder();
        builder.setName("base type");
        builder.addAttribute(String.class).setName("first");
        builder.addAttribute(Float.class).setName("second");
        builder.addAttribute(Geometry.class).setName("secondary_geometry");

        final FeatureType baseType = builder.build();
        // There's only one geometry here, no confusion possible.
        Assert.assertNotNull("We should find the only geometry defined in the feature type.", FeatureExt.getDefaultGeometryAttribute(baseType));

        builder = new FeatureTypeBuilder(baseType);
        builder.setName("with sis convention");
        builder.addAttribute(Geometry.class).setName("main_geometry").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType conventionedType = builder.build();
        // We should find main geometry as we defined a convention for it.
        AttributeType<?> defaultGeom = FeatureExt.getDefaultGeometryAttribute(conventionedType);
        Assert.assertNotNull("We should find one geomeetry attribute", defaultGeom);
        Assert.assertTrue("We should have found the attribute attached to SIS convention.", defaultGeom instanceof AttributeType);

        builder = new FeatureTypeBuilder(baseType);
        builder.setName("without sis convention");
        builder.addAttribute(Geometry.class).setName("main_geometry").setCRS(CommonCRS.WGS84.normalizedGeographic());
        Assert.assertNull("We should not find any geometry as there's multiple geometric attributes but no convention defined.", FeatureExt.getDefaultGeometryAttribute(builder.build()));

        // We also test we find the geometry after reprojection, and it's the good one, the reprojected.
        final ReprojectFeatureType reprojected = new ReprojectFeatureType(conventionedType, CommonCRS.WGS84.geographic());
        defaultGeom = FeatureExt.getDefaultGeometryAttribute(reprojected);
        Assert.assertNotNull("We should find one geomeetry attribute", defaultGeom);
        Assert.assertTrue("We should have found the attribute attached to SIS convention.", defaultGeom instanceof AttributeType);
        // Check we've got a definition matching reprojection
        AttributeType<?> crsCharacteristic = defaultGeom.characteristics().get(AttributeConvention.CRS_CHARACTERISTIC.toString());
        Assert.assertNotNull("No CRS characteristic found in returned geometry", crsCharacteristic);
        Assert.assertEquals("CRS defined in returned geometry is not correct !", CommonCRS.WGS84.geographic(), crsCharacteristic.getDefaultValue());
    }

    @Test
    public void testSameProperties() {
        final String geometryName = "geom";
        final String stringName = "this is a string";

        /* We build a super-type with only one attribute. This attribute will be
         * inherited by the first feature type to test, but the second one will
         * redefine. It means that when super-types will be tested, both checked
         * types will have the parent attribute. But if we ignore super types,
         * only one of the two attributes have it.
         */
        FeatureTypeBuilder builder = new FeatureTypeBuilder();
        builder.setName("parent");
        builder.addAttribute(String.class).setName(stringName);
        final FeatureType parentType = builder.build();

        /* We define a type with sis convention, and another without it. It allows
         * us to check the "ignore conventions" flag.
         */
        builder = new FeatureTypeBuilder();
        builder.setName("with convention and super type.");
        builder.setSuperTypes(parentType);
        builder.addAttribute(GeometryType.LINEAR).setName(geometryName).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType withConvention = builder.build();

        builder = new FeatureTypeBuilder();
        builder.setName("without convention nor super type");
        builder.addAttribute(GeometryType.LINEAR).setName(geometryName);
        builder.addAttribute(String.class).setName(stringName);
        final FeatureType withoutConvention = builder.build();

        Assert.assertFalse("Tested types should not be equal as conventions are checked.", FeatureExt.sameProperties(withConvention, withoutConvention, true));
        Assert.assertTrue("Tested types should not be equal as conventions are checked.", FeatureExt.sameProperties(withConvention, withoutConvention, true, true));
        Assert.assertFalse("Tested types should not be equal as super-types are ignored.", FeatureExt.sameProperties(withConvention, withoutConvention, false, true));
    }
}
