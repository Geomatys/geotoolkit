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

import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.util.InternationalString;

import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.util.collection.XCollections;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.metadata.KeyNamePolicy.*;


/**
 * Tests the {@link PropertyAccessor} class. Every tests in this class instantiates directly a
 * {@link PropertyAccessor} object by invoking the {@link #createPropertyAccessor(Citation)}
 * method. This class shall not test accessors created indirectly (e.g. the accessors created
 * by {@link MetadataStandard}).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 2.4
 */
public final strictfp class PropertyAccessorTest {
    /**
     * Creates a property accessor for the given citation.
     */
    private static PropertyAccessor createPropertyAccessor(final Citation citation) {
        final Class<?> implementation = citation.getClass();
        final Class<?> type = PropertyAccessor.getStandardType(implementation, "org.opengis.metadata.");
        assertNotNull("The given citation shall be recognized as a metadata implementation.", type);
        assertTrue("The metadata type shall be assignable from the given citation.", type.isInstance(citation));
        return new PropertyAccessor(implementation, type);
    }

    /**
     * Tests the constructor.
     */
    @Test
    public void testConstructor() {
        final Citation citation = Citations.EPSG;
        PropertyAccessor accessor;
        assertNull("No dummy interface expected.",
                PropertyAccessor.getStandardType(citation.getClass(), "org.opengis.dummy."));
        accessor = createPropertyAccessor(citation);
        assertTrue("Count of 'get' methods.", accessor.count() >= 13);
    }

    /**
     * Tests the constructor with a method which override an other method with covariant
     * return type.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-205">GEOTK-205</a>
     *
     * @since 3.20
     */
    @Test
    public void testConstructorWithCovariantReturnType() {
        final Class<?> type = GeographicCRS.class;
        final PropertyAccessor accessor = new PropertyAccessor(type, type);
        assertTrue("Count of 'get' methods.", accessor.count() >= 8);
    }

    /**
     * Tests the {@link PropertyAccessor#indexOf(String)} and
     * {@link PropertyAccessor#name(int, KeyNamePolicy)} methods.
     */
    @Test
    public void testName() {
        final Citation citation = Citations.EPSG;
        final PropertyAccessor accessor = createPropertyAccessor(citation);
        assertEquals("Non-existent property",   -1,  accessor.indexOf("dummy"));
        assertEquals("getTitle() property", "title", accessor.name(accessor.indexOf("title"), JAVABEANS_PROPERTY));
        assertEquals("getTitle() property", "title", accessor.name(accessor.indexOf("TITLE"), JAVABEANS_PROPERTY));
        assertEquals("getTitle() property", "title", accessor.name(accessor.indexOf("title"), UML_IDENTIFIER));
        assertEquals("getISBN() property",  "ISBN",  accessor.name(accessor.indexOf("ISBN" ), JAVABEANS_PROPERTY));
        assertEquals("getISBN() property",  "ISBN",  accessor.name(accessor.indexOf("isbn" ), JAVABEANS_PROPERTY));
        assertEquals("getISBN() property",  "ISBN",  accessor.name(accessor.indexOf("ISBN" ), UML_IDENTIFIER));
        assertNull(accessor.name(-1, JAVABEANS_PROPERTY));
        /*
         * Method name - this is the simplest name(...) implementation.
         */
        assertEquals("getAlternateTitles() property", "getAlternateTitles",
                accessor.name(accessor.indexOf("alternateTitle"), METHOD_NAME));
        /*
         * Note that at the opposite of UML_IDENTIFIER, the value returned
         * by identifier(int) for this property ends with a "s".
         */
        assertEquals("getAlternateTitles() property", "alternateTitles",
                accessor.name(accessor.indexOf("alternatetitle"), JAVABEANS_PROPERTY));
        /*
         * Note that at the opposite of JAVABEANS_PROPERTY, the value returned
         * by identifier(int) for this property does not have a "s".
         */
        assertEquals("getAlternateTitles() property", "alternateTitle",
                accessor.name(accessor.indexOf("alternate title"), UML_IDENTIFIER));
    }

    /**
     * Tests the {@link PropertyAccessor#get(int, Object)} method.
     */
    @Test
    public void testGet() {
        Citation citation = Citations.EPSG;
        final PropertyAccessor accessor = createPropertyAccessor(citation);
        final int index = accessor.indexOf("identifiers");
        assertTrue(index >= 0);
        final Object identifiers = accessor.get(index, citation);
        assertNotNull(identifiers);
        assertTrue(containsEPSG(identifiers));
    }

    /**
     * Returns {@code true} if the specified identifiers contains the {@code "EPSG"} code.
     */
    static boolean containsEPSG(final Object identifiers) {
        assertTrue(identifiers instanceof Collection<?>);
        @SuppressWarnings("unchecked")
        final Collection<Identifier> collection = (Collection<Identifier>) identifiers;
        for (final Identifier id : collection) {
            if (id.getCode().equals("EPSG")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests the {@link PropertyAccessor#set(int, Object, Object, boolean)} method.
     */
    @Test
    public void testSet() {
        Citation citation = new DefaultCitation();
        final PropertyAccessor accessor = createPropertyAccessor(citation);

        // Tries with ISBN, which expect a String.
        Object value = "Random number";
        int index = accessor.indexOf("ISBN");
        assertTrue(index >= 0);
        assertNull(accessor.set(index, citation, value, true));
        assertSame(value, accessor.get(index, citation));
        assertSame(value, citation.getISBN());

        // Tries with the title. Automatic conversion from String to InternationalString expected.
        index = accessor.indexOf("title");
        assertTrue(index >= 0);
        assertNull(accessor.set(index, citation, "A random title", true));
        value = accessor.get(index, citation);
        assertTrue(value instanceof InternationalString);
        assertEquals("A random title", value.toString());
        assertSame(value, citation.getTitle());

        // Tries with an element to be added in a collection.
        index = accessor.indexOf("alternateTitle");
        assertTrue(index >= 0);

        value = accessor.get(index, citation);
        assertTrue(value instanceof Collection<?>);
        assertTrue(((Collection<?>) value).isEmpty());

        value = accessor.set(index, citation, "An other title", true);
        assertTrue(value instanceof Collection<?>);
        assertTrue(((Collection<?>) value).isEmpty());

        value = accessor.set(index, citation, "Yet an other title", true);
        assertTrue(value instanceof Collection<?>);
        assertEquals(1, ((Collection<?>) value).size());

        final Collection<Object> expected = new ArrayList<>();
        assertTrue(expected.add(new SimpleInternationalString("An other title")));
        assertTrue(expected.add(new SimpleInternationalString("Yet an other title")));
        assertEquals(expected, (Collection<?>) accessor.get(index, citation));
    }

    /**
     * Tests the shallow equals and copy methods.
     */
    @Test
    public void testEquals() {
        Citation citation = Citations.EPSG;
        final PropertyAccessor accessor = createPropertyAccessor(citation);
        assertFalse(accessor.shallowEquals(citation, Citations.GEOTIFF, ComparisonMode.STRICT, true ));
        assertFalse(accessor.shallowEquals(citation, Citations.GEOTIFF, ComparisonMode.STRICT, false));
        assertTrue (accessor.shallowEquals(citation, Citations.EPSG,    ComparisonMode.STRICT, false));

        citation = new DefaultCitation();
        assertTrue (accessor.shallowCopy  (Citations.EPSG, citation, true));
        assertFalse(accessor.shallowEquals(citation, Citations.GEOTIFF, ComparisonMode.STRICT, true ));
        assertFalse(accessor.shallowEquals(citation, Citations.GEOTIFF, ComparisonMode.STRICT, false));
        assertTrue (accessor.shallowEquals(citation, Citations.EPSG,    ComparisonMode.STRICT, false));

        final int index = accessor.indexOf("identifiers");
        final Object source = accessor.get(index, Citations.EPSG);
        final Object target = accessor.get(index, citation);
        assertNotNull(source);
        assertNotNull(target);
        assertTrue(source instanceof Collection<?>);
        assertTrue(target instanceof Collection<?>);
        assertNotSame(source, target);
        assertEquals (source, target);
        assertTrue(containsEPSG(target));

        assertEquals(XCollections.copy((Collection<?>) target), accessor.set(index, citation, null, true));
        final Object value = accessor.get(index, citation);
        assertNotNull(value);
        assertTrue(((Collection<?>) value).isEmpty());

        try {
            accessor.shallowCopy(citation, Citations.EPSG, true);
            fail("Citations.EPSG should be unmodifiable.");
        } catch (UnmodifiableMetadataException e) {
            // This is the expected exception.
        }
    }

    /**
     * Tests the hash code computation.
     */
    @Test
    public void testHashCode() {
        final DefaultCitation citation = new DefaultCitation();
        final PropertyAccessor accessor = createPropertyAccessor(citation);
        int hashCode = accessor.hashCode(citation);
        assertEquals("Empty metadata.", 0, hashCode);

        final Date editionDate = new Date();
        citation.setEditionDate(editionDate);
        hashCode = accessor.hashCode(citation);
        assertEquals("Metadata with a single String value.", editionDate.hashCode(), hashCode);

        final Set<Object> set = new HashSet<>();
        assertEquals("By Set.hashCode() contract.", 0, set.hashCode());
        assertTrue(set.add(editionDate));
        assertEquals("Expected Metadata.hashCode() == Set.hashCode().", set.hashCode(), hashCode);

        final InternationalString title = new SimpleInternationalString("Dummy title");
        citation.setTitle(title);
        hashCode = accessor.hashCode(citation);
        assertEquals("Metadata with two values.", editionDate.hashCode() + title.hashCode(), hashCode);
        assertTrue(set.add(title));
        assertEquals("Expected Metadata.hashCode() == Set.hashCode().", set.hashCode(), hashCode);
        assertEquals("CitationsImpl.hashCode() should delegate.", hashCode, citation.hashCode());

        final Collection<Object> values = citation.asMap().values();
        assertEquals(hashCode, new HashSet<>(values).hashCode());
        assertTrue(values.containsAll(set));
        assertTrue(set.containsAll(values));
    }
}
