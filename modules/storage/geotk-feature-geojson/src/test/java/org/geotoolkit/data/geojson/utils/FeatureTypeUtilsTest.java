package org.geotoolkit.data.geojson.utils;

import com.vividsolutions.jts.geom.Polygon;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.referencing.CRS;
import org.junit.Test;
import org.geotoolkit.feature.type.*;
import org.opengis.util.FactoryException;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class FeatureTypeUtilsTest {

    @Test
    public void writeReadFTTest() throws Exception {

        File featureTypeFile = File.createTempFile("complexFT", ".json");
        featureTypeFile.createNewFile();
        featureTypeFile.deleteOnExit();

        FeatureType featureType = createComplexType();
        FeatureTypeUtils.writeFeatureType(featureType, featureTypeFile);

        assertTrue(featureTypeFile.length() > 0);

        FeatureType readFeatureType = FeatureTypeUtils.readFeatureType(featureTypeFile);

        assertNotNull(readFeatureType);
        assertNotNull(readFeatureType.getGeometryDescriptor());
        assertNotNull(readFeatureType.getCoordinateReferenceSystem());

        testFeatureTypes(featureType, readFeatureType);
    }

    private void testFeatureTypes(FeatureType expected, FeatureType result) {

        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getUserData(), result.getUserData());
        assertEquals(expected.getDescription(), result.getDescription());
        testDescriptors(expected.getDescriptors(), result.getDescriptors());

    }

    private void testDescriptors(Collection<PropertyDescriptor> expected, Collection<PropertyDescriptor> result) {

        for (PropertyDescriptor exp : expected) {
            for (PropertyDescriptor res : result) {
                if (exp.getName().equals(res.getName())) {
                    assertEquals(exp.getMaxOccurs(), res.getMaxOccurs());
                    assertEquals(exp.getMinOccurs(), res.getMinOccurs());
                    testUserMap(exp.getUserData(), res.getUserData());
                    testType(exp.getType(), res.getType());
                }
            }
        }
    }

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

    private void testType(PropertyType expType, PropertyType resType) {
        if (expType instanceof ComplexType) {
            testDescriptors(((ComplexType) expType).getDescriptors(), ((ComplexType) resType).getDescriptors());
        } else if (expType instanceof AttributeType) {
            assertTrue(((AttributeType) resType).getBinding().isAssignableFrom(((AttributeType) expType).getBinding()));
            if (expType instanceof GeometryType) {
                assertTrue(CRS.equalsIgnoreMetadata(((GeometryType) expType).getCoordinateReferenceSystem(),
                        ((GeometryType) resType).getCoordinateReferenceSystem()));
            }
        }
    }

    public static FeatureType createComplexType() throws FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();

        ftb.setName("complexAtt1");
        ftb.add("longProp2", Long.class);
        ftb.add("stringProp2", String.class);
        final ComplexType complexAtt1 = ftb.buildType();

        ftb.reset();
        ftb.setName("complexAtt2");
        ftb.add("longProp2", Long.class);
        ftb.add("dateProp", Date.class);
        final ComplexType complexAtt2 = ftb.buildType();
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("date", new Date());
        userMap.put("unit", SI.KILOMETRE);

        ftb.reset();
        ftb.setName("complexFT");
        ftb.add("longProp", Long.class);
        ftb.add("stringProp", String.class);
        ftb.add("integerProp", Integer.class);
        ftb.add("booleanProp", Boolean.class);
        ftb.add("dateProp", Date.class);

        AttributeDescriptor complexAtt1Desc = adb.create(complexAtt1, DefaultName.valueOf("complexAtt1"),1,1,false,null);
        AttributeDescriptor complexAtt2Desc = adb.create(complexAtt2, DefaultName.valueOf("complexAtt2"),0,Integer.MAX_VALUE,false,userMap);
        ftb.add(complexAtt1Desc);
        ftb.add(complexAtt2Desc);
        ftb.add("geom", Polygon.class, CommonCRS.WGS84.geographic());
        ftb.setDescription(new SimpleInternationalString("Description"));
        return ftb.buildFeatureType();
    }
}
