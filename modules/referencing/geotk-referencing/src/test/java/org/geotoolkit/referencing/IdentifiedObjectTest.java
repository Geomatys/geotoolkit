/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing;

import java.util.Map;
import java.util.HashMap;
import java.util.Locale;

import org.opengis.test.Validators;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.parameter.InvalidParameterValueException;

import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.test.referencing.ReferencingTestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the creation of {@link AbstractIdentifiedObject} and a few subclasses.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 */
public final strictfp class IdentifiedObjectTest extends ReferencingTestBase {
    /**
     * Tests {@link NamedIdentifier} attributes. Useful for making sure that the
     * hash code enumerated in the switch statement in the constructor have
     * the correct value.
     */
    @Test
    public void testIdentifier() {
        final Map<String,Object> properties = new HashMap<>();
        assertNull(properties.put("code",          "This is a code"));
        assertNull(properties.put("authority",     "This is an authority"));
        assertNull(properties.put("version",       "This is a version"));
        assertNull(properties.put("dummy",         "Doesn't matter"));
        assertNull(properties.put("remarks",       "There is remarks"));
        assertNull(properties.put("remarks_fr",    "Voici des remarques"));
        assertNull(properties.put("remarks_fr_CA", "Pareil"));

        NamedIdentifier identifier = new NamedIdentifier(properties);
        Validators.validate((ReferenceIdentifier) identifier);
        Validators.validate((GenericName) identifier);

        assertEquals("code",          "This is a code",        identifier.getCode());
        assertEquals("authority",     "This is an authority",  identifier.getAuthority().getTitle().toString());
        assertEquals("version",       "This is a version",     identifier.getVersion());
        assertEquals("remarks",       "There is remarks",      identifier.getRemarks().toString(Locale.ENGLISH));
        assertEquals("remarks_fr",    "Voici des remarques",   identifier.getRemarks().toString(Locale.FRENCH));
        assertEquals("remarks_fr_CA", "Pareil",                identifier.getRemarks().toString(Locale.CANADA_FRENCH));
        assertEquals("remarks_fr_BE", "Voici des remarques",   identifier.getRemarks().toString(new Locale("fr", "BE")));

        // Following produces warnings, which we ignore.
        if (true) {
            final Object old = properties.put("remarks", new SimpleInternationalString("Overrides remarks"));
            identifier = new NamedIdentifier(properties);
            assertEquals("remarks", "Overrides remarks", identifier.getRemarks().toString(Locale.ENGLISH));
            assertNotNull(properties.put("remarks", old)); // Restore the previous value.
        }

        assertNotNull(properties.remove("authority"));
        assertNull(properties.put("AutHOrITY", new DefaultCitation("An other authority")));
        identifier = new NamedIdentifier(properties);
        Validators.validate((ReferenceIdentifier) identifier);
        Validators.validate((GenericName) identifier);

        assertEquals("authority", "An other authority", identifier.getAuthority().getTitle().toString(Locale.ENGLISH));
        assertNotNull(properties.remove("AutHOrITY"));
        assertNull(properties.put("authority", Locale.CANADA));
        try {
            identifier = new NamedIdentifier(properties);
            fail(identifier.toString());
        } catch (InvalidParameterValueException exception) {
            // This is the expected exception
        }
    }

    /**
     * Tests {@link IdentifiedObject}.
     */
    @Test
    public void testIdentifiedObject() {
        final Map<String,Object> properties = new HashMap<>();
        assertNull(properties.put("name",             "This is a name"));
        assertNull(properties.put("remarks",          "There is remarks"));
        assertNull(properties.put("remarks_fr",       "Voici des remarques"));
        assertNull(properties.put("dummy",            "Doesn't matter"));
        assertNull(properties.put("dummy_fr",         "Rien d'intéressant"));
        assertNull(properties.put("local",            "A custom localized string"));
        assertNull(properties.put("local_fr",         "Une chaîne personalisée"));
        assertNull(properties.put("anchorPoint",      "Anchor point"));
        assertNull(properties.put("realizationEpoch", "Realization epoch"));
        assertNull(properties.put("validArea",        "Valid area"));

        final Map<String,Object> remaining = new HashMap<>();
        final AbstractIdentifiedObject reference = new AbstractIdentifiedObject(
                properties, remaining, new String[] {"local"});
        Validators.validate(reference);

        assertEquals("name",       "This is a name",         reference.getName().getCode());
        assertEquals("remarks",    "There is remarks",       reference.getRemarks().toString(null));
        assertEquals("remarks_fr", "Voici des remarques",    reference.getRemarks().toString(Locale.FRENCH));

        // Check extra properties
        assertEquals("Size:",    6,                    remaining.size());
        assertEquals("dummy",    "Doesn't matter",     remaining.get("dummy"));
        assertEquals("dummy_fr", "Rien d'intéressant", remaining.get("dummy_fr"));
        assertEquals("local",    "A custom localized string", ((InternationalString) remaining.get("local")).toString(null));
        assertEquals("local_fr", "Une chaîne personalisée",   ((InternationalString) remaining.get("local")).toString(Locale.FRENCH));
        assertFalse ("local_fr", remaining.containsKey("local_fr"));

        // Check the case of some special property keys
        assertEquals("anchorPoint",      "Anchor point",      remaining.get("anchorPoint"));
        assertEquals("realizationEpoch", "Realization epoch", remaining.get("realizationEpoch"));
        assertEquals("validArea",        "Valid area",        remaining.get("validArea"));
    }

    /**
     * Tests {@link AbstractReferenceSystem}.
     */
    @Test
    public void testReferenceSystem() {
        final Map<String,Object> properties = new HashMap<>();
        assertNull(properties.put("name",       "This is a name"));
        assertNull(properties.put("scope",      "This is a scope"));
        assertNull(properties.put("scope_fr",   "Valide dans ce domaine"));
        assertNull(properties.put("remarks",    "There is remarks"));
        assertNull(properties.put("remarks_fr", "Voici des remarques"));

        final AbstractReferenceSystem reference = new AbstractReferenceSystem(properties);
        assertEquals("name",          "This is a name",         reference.getName()   .getCode());
        assertEquals("scope",         "This is a scope",        reference.getScope()  .toString(null));
        assertEquals("scope_fr",      "Valide dans ce domaine", reference.getScope()  .toString(Locale.FRENCH));
        assertEquals("remarks",       "There is remarks",       reference.getRemarks().toString(null));
        assertEquals("remarks_fr",    "Voici des remarques",    reference.getRemarks().toString(Locale.FRENCH));
    }
}
