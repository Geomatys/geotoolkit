
package org.apache.sis.feature;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.parameter.DefaultParameterDescriptor;
import org.apache.sis.parameter.DefaultParameterDescriptorGroup;
import static org.junit.Assert.assertEquals;
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

}
