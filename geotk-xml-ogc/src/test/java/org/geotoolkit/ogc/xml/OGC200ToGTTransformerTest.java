package org.geotoolkit.ogc.xml;

import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.filter.DefaultFilterFactory;
import org.geotoolkit.filter.FilterFactory2;
import org.geotoolkit.ogc.xml.v200.ObjectFactory;
import org.geotoolkit.ogc.xml.v200.PropertyIsLikeType;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class OGC200ToGTTransformerTest {

    @Test
    public void propertyIsLike() {
        var filter = like("*test*", true);

        final Feature data = TEST_DATA_TYPE.newInstance();

        data.setPropertyValue(TEXT_PROPERTY, "first Test (case insensitive)");

        assertFalse("Pattern should not match: case sensitive", filter.test(data));

        filter = like("*test*", false);

        assertTrue("Pattern should match: case insensitive", filter.test(data));

        data.setPropertyValue(TEXT_PROPERTY, "should not match");
        assertFalse(filter.test(data));
    }

    public static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
    private static final String TEXT_PROPERTY = "text";
    private static final FeatureType TEST_DATA_TYPE;
    static {
        var builder = new FeatureTypeBuilder().setName("TEST_DATA_TYPE");
        builder.addAttribute(String.class).setName(TEXT_PROPERTY);
        TEST_DATA_TYPE = builder.build();
    }

    /**
     * Build a "property is like" filter targetting {@link #TEXT_PROPERTY data text property}.
     * Build with multi-character wildcard {@code *}, single character wildcard {@code ?} and escape character {@code \}.
     */
    private static Filter<Feature> like(String pattern, boolean matchCase) {
        final PropertyIsLikeType like = new PropertyIsLikeType(TEXT_PROPERTY, pattern, "*", "?", "\\", matchCase);
        final OGC200toGTTransformer mapper = new OGC200toGTTransformer(DefaultFilterFactory.forFeatures());
        return mapper.visitComparisonOp(OBJECT_FACTORY.createPropertyIsLike(like));
    }
}
