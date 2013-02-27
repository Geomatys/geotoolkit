/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.metadata;

import org.apache.sis.metadata.TypeValuePolicy;
import org.apache.sis.metadata.KeyNamePolicy;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;

import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.quality.Completeness;
import org.opengis.coverage.grid.RectifiedGrid;

import org.geotoolkit.test.Depend;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.quality.AbstractCompleteness;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link MetadataStandard} class. Unless otherwise specified,
 * the tests use the {@link MetadataStandard#ISO_19115} constant.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 2.4
 */
@Depend(PropertyAccessorTest.class)
public final strictfp class MetadataStandardTest {
    /**
     * Tests the conversion between interface types and implementation types.
     */
    @Test
    public void testTypeConversions() {
        final MetadataStandard std = MetadataStandard.ISO_19115;

        assertEquals(Citation.class, std.getInterface(DefaultCitation.class));
        assertEquals(DefaultCitation.class, std.getImplementation(Citation.class));

        assertEquals(AbstractCompleteness.class, std.getImplementation(Completeness.class));
        assertEquals(Completeness.class, std.getInterface(AbstractCompleteness.class));
    }

    /**
     * Tests the shallow copy methods without skipping null values. For this test, we need
     * to use a class that doesn't have any {@code getIdentifiers()} method inherited from
     * ISO 19115. The class will inherit the {@code getIdentifiers()} method defined by Geotk
     * in the parent class, which doesn't have corresponding {@code setIdentifiers(...)} method.
     *
     * @since 3.20
     */
    @Test
    public void testShallowCopy() {
        final AbstractCompleteness source = new AbstractCompleteness();
        final AbstractCompleteness target = new AbstractCompleteness();
        source.setMeasureDescription(new SimpleInternationalString("Some description"));
        target.getStandard().shallowCopy(source, target, false);
        assertEquals("Some description", target.getMeasureDescription().toString());
        assertEquals(source, target);

        source.setMeasureDescription(null);
        target.getStandard().shallowCopy(source, target, true);
        assertEquals("Measure description should not have been removed, since we skipped null values.",
                "Some description", target.getMeasureDescription().toString());
        assertFalse(target.isEmpty()); // Opportunist test.

        target.getStandard().shallowCopy(source, target, false);
        assertNull("Measure description should have been removed.", target.getMeasureDescription());
        assertTrue(target.isEmpty()); // Opportunist test.
    }

    /**
     * Tests the shallow equals and copy methods.
     */
    @Test
    public void testEquals() {
        final MetadataStandard std = MetadataStandard.ISO_19115;
        Citation citation = Citations.EPSG;
        assertFalse(std.shallowEquals(citation, Citations.GEOTIFF, ComparisonMode.STRICT, true ));
        assertFalse(std.shallowEquals(citation, Citations.GEOTIFF, ComparisonMode.STRICT, false));
        assertTrue (std.shallowEquals(citation, Citations.EPSG,    ComparisonMode.STRICT, false));

        citation = new DefaultCitation();
        std.shallowCopy(Citations.EPSG, citation, true);
        assertFalse(std.shallowEquals(citation, Citations.GEOTIFF, ComparisonMode.STRICT, true ));
        assertFalse(std.shallowEquals(citation, Citations.GEOTIFF, ComparisonMode.STRICT, false));
        assertTrue (std.shallowEquals(citation, Citations.EPSG,    ComparisonMode.STRICT, false));

        try {
            std.shallowCopy(citation, Citations.EPSG, true);
            fail("Citations.EPSG should be unmodifiable.");
        } catch (UnmodifiableMetadataException e) {
            // This is the expected exception.
        }
    }

    /**
     * Tests the {@link PropertyMap} implementation.
     */
    @Test
    public void testMap() {
        final Citation citation = new DefaultCitation(Citations.EPSG);
        final Map<String,Object> map = MetadataStandard.ISO_19115.asMap(citation);
        assertFalse(map.isEmpty());
        assertTrue (map.size() > 1);

        final Set<String> keys = map.keySet();
        assertTrue ("Property exists and should be defined.",            keys.contains("title"));
        assertFalse("Property exists but undefined for Citations.EPSG.", keys.contains("ISBN"));
        assertFalse("Property do not exists.",                           keys.contains("dummy"));

        final String s = keys.toString();
        assertTrue (s.indexOf("title")       >= 0);
        assertTrue (s.indexOf("identifiers") >= 0);
        assertFalse(s.indexOf("ISBN")        >= 0);

        final Object identifiers = map.get("identifiers");
        assertTrue(identifiers instanceof Collection<?>);
        assertTrue(PropertyAccessorTest.containsEPSG(identifiers));

        final Map<String,Object> copy = new HashMap<>(map);
        assertEquals(map, copy);

        // Note: AbstractCollection do not defines hashCode(); we have to wraps in a HashSet.
        final int hashCode = citation.hashCode();
        assertEquals("hashCode() should be as in a Set.", hashCode, new HashSet<>(map .values()).hashCode());
        assertEquals("hashCode() should be as in a Set.", hashCode, new HashSet<>(copy.values()).hashCode());

        map.remove("identifiers");
        final int newHashCode = citation.hashCode();
        assertFalse(map.equals(copy));
        assertFalse(hashCode == newHashCode);
        assertEquals(newHashCode, new HashSet<>(map.values()).hashCode());
    }

    /**
     * Tests the {@link MetadataStandard#ISO_19123} constant. Getters shall
     * be accessible even if there is no implementation on the classpath.
     */
    @Test
    public void testISO_19123() {
        final MetadataStandard std = MetadataStandard.ISO_19123;
        assertFalse(std.isMetadata(Citation.class));
        assertFalse(std.isMetadata(DefaultCitation.class));
        assertTrue (std.isMetadata(RectifiedGrid.class));
        /*
         * Ensure that the getters have been found.
         */
        final Map<String, String> names = std.asNameMap(RectifiedGrid.class, KeyNamePolicy.JAVABEANS_PROPERTY, KeyNamePolicy.UML_IDENTIFIER);
        assertFalse("Getters should have been found even if there is no implementation.", names.isEmpty());
        assertEquals("dimension", names.get("dimension"));
        assertEquals("cells", names.get("cell"));
        /*
         * Ensure that the type are recognized, especially RectifiedGrid.getOffsetVectors()
         * which is of type List<double[]>.
         */
        Map<String, Class<?>> types;
        types = std.asTypeMap(RectifiedGrid.class, TypeValuePolicy.PROPERTY_TYPE, KeyNamePolicy.UML_IDENTIFIER);
        assertEquals("The return type is the int primitive type.", Integer.TYPE, types.get("dimension"));
        assertEquals("The offset vectors are stored in a List.",   List.class,   types.get("offsetVectors"));

        types = std.asTypeMap(RectifiedGrid.class, TypeValuePolicy.ELEMENT_TYPE, KeyNamePolicy.UML_IDENTIFIER);
        assertEquals("If the type was elements in a List, it would be the Integer wrapper.",
                Integer.class,  types.get("dimension"));
        assertEquals(double[].class, types.get("offsetVectors"));
    }
}
