/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.provider;

import java.util.*;

import org.opengis.util.GenericName;
import org.opengis.util.FactoryException;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;
import org.apache.sis.util.ArraysExt;

import org.geotoolkit.util.Deprecable;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.test.referencing.ReferencingTestBase;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.geotoolkit.referencing.Commons.*;


/**
 * Compares the hard-coded codes declared in the {@code provider} package with the codes found
 * in an authority factory. This is primarily used for comparing the EPSG codes against those
 * declared in the EPSG database, but this could be extended to other authorities as well.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.03
 */
public final strictfp class ConformanceTest extends ReferencingTestBase {
    /**
     * Deprecated method names to ignore.
     */
    private static final String[] IGNORE = {
        "Krovak Oblique Conic Conformal"  // Since EPSG 7.6, the name is only "Krovak".
    };

    /**
     * Tests the conformance of EPSG codes.
     *
     * @throws FactoryException If an error occurred while querying the database.
     */
    @Test
    public void testEPSG() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());
        assumeTrue(isEpsgDatabaseUpToDate());
        run(Citations.EPSG, AuthorityFactoryFinder.getCoordinateOperationAuthorityFactory("EPSG", null));
    }

    /**
     * Tests the conformance of GeoTIFF codes.
     *
     * @throws FactoryException If an error occurred while querying the database.
     */
    @Test
    public void testGeoTIFF() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());
        run(Citations.GEOTIFF, null);
    }

    /**
     * Tests the conformance with the given authority.
     *
     * @param  authority The authority to test.
     * @param  factory The factory for the operation method, or {@code null} if none.
     * @throws FactoryException If an error occurred while querying the database.
     */
    private static void run(final Citation authority, final CoordinateOperationAuthorityFactory factory)
            throws FactoryException
    {
        /*
         * Get all known names and codes for the given authority. For
         * each name or code, we remember the method that declare it.
         *
         * Only one OperationMethod can be associated to a given numerical identifier,
         * but many OperationMethods can be associated to the same name or alias because
         * some are ambiguous (e.g. "Bursa-Wolf").
         */
        final Map<String, OperationMethod> codes = new LinkedHashMap<String, OperationMethod>();
        final Map<String, Map<OperationMethod,OperationMethod>> names =
                new LinkedHashMap<String, Map<OperationMethod,OperationMethod>>();
        final MathTransformFactory mtFactory = FactoryFinder.getMathTransformFactory(null);
skip:   for (final OperationMethod method : mtFactory.getAvailableMethods(SingleOperation.class)) {
            for (final ReferenceIdentifier id : method.getIdentifiers()) {
                if (Citations.identifierMatches(authority, id.getAuthority())) {
                    final String code = id.getCode().trim();
                    if (code.equals("9602") && codes.containsKey(code)) {
                        /*
                         * Exclude this special case because EPSG defines a single OperationMethod
                         * for both "Ellipsoid_To_Geocentric" and "Geocentric_To_Ellipsoid", while
                         * OGC and Geotk define two distinct operations. This name appears twice.
                         */
                        continue skip;
                    }
                    assertTrue("Not a code: "   + code, isNumber(code));
                    assertNull("Defined twice:" + code, codes.put(code, method));
                }
            }
            final Collection<GenericName> aliases = method.getAlias();
            assertTrue("In Geotk implementation, the aliases shall contain the primary name as a " +
                    "GenericName in addition of ReferenceIdentifier", aliases.contains(method.getName()));
            for (final GenericName alias : aliases) {
                if (Citations.identifierMatches(authority, alias.head().toString())) {
                    final String name = alias.tip().toString().trim();
                    if (ArraysExt.contains(IGNORE, name)) {
                        assertTrue(name, ((Deprecable) alias).isDeprecated());
                        continue;
                    }
                    assertFalse("Not a name: " + name, isNumber(name));
                    Map<OperationMethod,OperationMethod> methods = names.get(name);
                    if (methods == null) {
                        methods = new IdentityHashMap<OperationMethod,OperationMethod>();
                        assertNull(name, names.put(name, methods));
                    }
                    assertNull("MathTransformFactory.getAvailableMethods(...) did not retuned a Set." +
                            " Duplicated OperationMethod is: " + name, methods.put(method, method));
                }
            }
        }
        assertFalse(codes.isEmpty());
        assertFalse(names.isEmpty());
        assertTrue(Collections.disjoint(codes.keySet(), names.keySet()));
        if (factory == null) {
            return;
        }
        /*
         * Now compares the method names with the one declared in the EPSG database.
         * We will run this block twice. This first execution performs the test without
         * changing the map (because we need to read some informations more than once).
         * The second execution remove the entries that we have processed, in order to
         * see if there is any left at the end of this block.
         */
        boolean clean = false;
        do {
            for (final Map.Entry<String,OperationMethod> entry : codes.entrySet()) {
                final OperationMethod epsgMethod, geotkMethod;
                final String code, name, message;
                code        = entry.getKey();
                geotkMethod = entry.getValue();
                epsgMethod  = factory.createOperationMethod(code);
                name        = epsgMethod.getName().getCode();
                message     = "Name \"" + name + "\" for code " + code;
                assertNameIsKnown(message, geotkMethod, names.get(name), clean);
                /*
                 * Checks the aliases. This is basically the same test that the one
                 * we did for the primary name.
                 */
                for (final GenericName alias : epsgMethod.getAlias()) {
                    final String epsgAlias = alias.tip().toString();
                    assertNameIsKnown(message + " alias \"" + epsgAlias + '"',
                            geotkMethod, names.get(epsgAlias), clean);
                }
            }
        } while ((clean = !clean) == true);
        /*
         * Clean and ensure there is no remaining name.
         */
        for (final Iterator<Map<OperationMethod,OperationMethod>> it=names.values().iterator(); it.hasNext();) {
            if (it.next().isEmpty()) {
                it.remove();
            }
        }
        assertTrue("Unknown names: " + names.keySet(), names.isEmpty());
    }

    /**
     * Returns {@code true} if the given string is a number.
     */
    private static boolean isNumber(final String code) {
        for (int i=code.length(); --i>=0;) {
            final char c = code.charAt(i);
            if ((c < '0' || c > '9') && c!='-' && c!='+' && c!='.') {
                return false;
            }
        }
        return true;
    }

    /**
     * Asserts that a name is declared for the given operation method.
     *
     * @param message     The message to emit in case of failure.
     * @param geotkMethod The operation method which is expected to have a name.
     * @param sameNames   The set of operations having the expected name.
     * @param clean       {@code true} if this method is invoked in the cleaning phase.
     */
    private static void assertNameIsKnown(final String message, final OperationMethod geotkMethod,
            final Map<OperationMethod,OperationMethod> operationsForName, final boolean clean)
    {
        assertNotNull(message + ": name is undeclared.", operationsForName);
        final OperationMethod opForName;
        if (clean) {
            opForName = operationsForName.remove(geotkMethod);
            if (opForName == null) {
                return; // This is normal during the cleaning phase.
            }
        } else {
            opForName = operationsForName.get(geotkMethod);
        }
        assertNotNull(message + ": name is declared for another operation.", opForName);
        assertSame(message + ": inconsistency in this test suite.", geotkMethod, opForName);
    }
}
