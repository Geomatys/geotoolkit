/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.test.xml;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link DomComparator} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 */
public final strictfp class DomComparatorTest {
    /**
     * Tests the {@link DomComparator#ignoredAttributes} and {@link DomComparator#ignoredNodes}
     * sets.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testIgnore() throws Exception {
        final DomComparator cmp = new DomComparator(
            "<body>\n" +
            "  <form id=\"MyForm\">\n" +
            "    <table cellpading=\"1\">\n" +
            "      <tr><td>foo</td></tr>\n" +
            "    </table>\n" +
            "  </form>\n" +
            "</body>",
            "<body>\n" +
            "  <form id=\"MyForm\">\n" +
            "    <table cellpading=\"2\">\n" +
            "      <tr><td>foo</td></tr>\n" +
            "    </table>\n" +
            "  </form>\n" +
            "</body>");

        ensureFail("Should fail because the \"cellpading\" attribute value is different.", cmp);

        // Following comparison should not fail anymore.
        cmp.ignoredAttributes.add("cellpading");
        cmp.compare();

        cmp.ignoredAttributes.clear();
        cmp.ignoredAttributes.add("bgcolor");
        ensureFail("The \"cellpading\" attribute should not be ignored anymore.", cmp);

        // Ignore the table node, which contains the faulty attribute.
        cmp.ignoredNodes.add("table");
        cmp.compare();

        // Ignore the form node and all its children.
        cmp.ignoredNodes.clear();
        cmp.ignoredNodes.add("form");
        cmp.compare();
    }

    /**
     * Ensures that the call to {@link DomComparator#compare()} fails. This method is
     * invoked in order to test that the comparator rightly detected an error that we
     * were expected to detect.
     *
     * @param message The message for JUnit if the comparison does not fail.
     * @param cmp The comparator on which to invoke {@link DomComparator#compare()}.
     */
    private static void ensureFail(final String message, final DomComparator cmp) {
        try {
            cmp.compare();
        } catch (AssertionError e) {
            return;
        }
        fail(message);
    }
}
