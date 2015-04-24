package org.geotoolkit.data.geojson.utils;

import com.vividsolutions.jts.geom.Polygon;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.junit.Test;
import org.opengis.util.FactoryException;
import org.apache.sis.measure.Units;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;

import static org.junit.Assert.*;
import org.opengis.feature.FeatureType;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class FeatureTypeUtilsTest extends org.geotoolkit.test.TestBase {

    @Test
    public void writeReadFTTest() throws Exception {

        Path featureTypeFile = Files.createTempFile("complexFT", ".json");

        FeatureType featureType = createComplexType();
        FeatureTypeUtils.writeFeatureType(featureType, featureTypeFile);

        assertTrue(Files.size(featureTypeFile) > 0);

        FeatureType readFeatureType = FeatureTypeUtils.readFeatureType(featureTypeFile);

        assertNotNull(readFeatureType);
        assertTrue(FeatureExt.hasAGeometry(readFeatureType));
        assertNotNull(FeatureExt.getCRS(readFeatureType));

        testFeatureTypes(featureType, readFeatureType);
    }

    private void testFeatureTypes(FeatureType expected, FeatureType result) {
        assertEquals(expected, result);
    }

//    private void testDescriptors(Collection<PropertyDescriptor> expected, Collection<PropertyDescriptor> result) {
//
//        for (PropertyDescriptor exp : expected) {
//            for (PropertyDescriptor res : result) {
//                if (exp.getName().equals(res.getName())) {
//                    assertEquals(exp.getMaxOccurs(), res.getMaxOccurs());
//                    assertEquals(exp.getMinOccurs(), res.getMinOccurs());
//                    testUserMap(exp.getUserData(), res.getUserData());
//                    testType(exp.getType(), res.getType());
//                }
//            }
//        }
//    }

    /**
     * Test toString value of maps entries key/value.
     * Because we lost the java type in JSON serialization
     *
     * @param expUserData
     * @param resUserData
     */
    private void testUserMap(Map<Object, Object> expUserData, Map<Object, Object> resUserData) {

        for (Map.Entry<Object, Object> expEntry : expUserData.entrySet()) {
            String key = expEntry.getKey().toString();
            String value = expEntry.getValue().toString();
            assertTrue(resUserData.containsKey(key));
            assertEquals(value, resUserData.get(key).toString());
        }
    }

//    private void testType(PropertyType expType, PropertyType resType) {
//        if (expType instanceof ComplexType) {
//            testDescriptors(((ComplexType) expType).getDescriptors(), ((ComplexType) resType).getDescriptors());
//        } else if (expType instanceof AttributeType) {
//            assertTrue(((AttributeType) resType).getBinding().isAssignableFrom(((AttributeType) expType).getBinding()));
//            if (expType instanceof GeometryType) {
//                assertTrue(Utilities.equalsIgnoreMetadata(((GeometryType) expType).getCoordinateReferenceSystem(),
//                        ((GeometryType) resType).getCoordinateReferenceSystem()));
//            }
//        }
//    }

    public static FeatureType createComplexType() throws FactoryException {
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.setName("complexAtt1");
        ftb.addAttribute(Long.class).setName("longProp2");
        ftb.addAttribute(String.class).setName("stringProp2");
        final FeatureType complexAtt1 = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("complexAtt2");
        ftb.addAttribute(Long.class).setName("longProp2");
        ftb.addAttribute(Date.class).setName("dateProp");
        final FeatureType complexAtt2 = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("complexFT");
        ftb.addAttribute(Polygon.class).setName("geometry").setCRS(CommonCRS.WGS84.geographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Long.class).setName("longProp");
        ftb.addAttribute(String.class).setName("stringProp");
        ftb.addAttribute(Integer.class).setName("integerProp");
        ftb.addAttribute(Boolean.class).setName("booleanProp");
        ftb.addAttribute(Date.class).setName("dateProp");

        ftb.addAssociation(complexAtt1).setName("complexAtt1");
        ftb.addAssociation(complexAtt2).setName("complexAtt2").setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        ftb.setDescription(new SimpleInternationalString("Description"));
        return ftb.build();
    }
}
