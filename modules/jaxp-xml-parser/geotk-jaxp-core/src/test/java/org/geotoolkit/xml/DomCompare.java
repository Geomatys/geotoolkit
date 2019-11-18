/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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


package org.geotoolkit.xml;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sis.test.xml.DocumentComparator;
import org.xml.sax.SAXException;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DomCompare {
    /**
     * convenient method to test XML comparison by ignoring irrelevant details
     * like formatting, different attribute order, comments ...
     *
     * @param expected  the expected structure (File,Stream,Document)
     * @param result    the obtained result (File,Stream,Document)
     */
    public static void compare(final Object expected, final Object result)
            throws ParserConfigurationException, SAXException, IOException
    {
        final DocumentComparator comparator = new DocumentComparator(expected, result);
        comparator.ignoredAttributes.add("http://www.w3.org/2000/xmlns:*");
        comparator.ignoredAttributes.add("http://www.w3.org/2001/XMLSchema-instance:schemaLocation");
        comparator.compare();
    }
}
