/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import java.net.URI;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * Compares the XML document produced by a test method with the expected XML document.
 * The two XML documents are specified at construction time. The comparison is performed
 * by a call to the {@link #compare()} method. The execution is delegated to the various
 * protected methods defined in this class, which can be overridden.
 * <p>
 * By default, this comparator expects the documents to contain the same elements and
 * the same attributes (but the order of attributes may be different). However it is
 * possible to:
 * <p>
 * <ul>
 *   <li>Specify attributes to ignore in comparisons (see {@link #ignoredAttributes})</li>
 * </ul>
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.17
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.test.XMLComparator}.
 */
@Deprecated
public strictfp class DomComparator extends org.apache.sis.test.XMLComparator {
    /**
     * Creates a new comparator for the given root nodes.
     *
     * @param expected The root node of the expected XML document.
     * @param actual   The root node of the XML document to compare.
     */
    public DomComparator(final Node expected, final Node actual) {
        super(expected, actual);
    }

    /**
     * Creates a new comparator for the given inputs. The inputs can be any of the
     * following types:
     * <p>
     * <ul>
     *   <li>{@link Node}; used directly without further processing.</li>
     *   <li>{@link File}, {@link URL} or {@link URI}: the stream is opened and parsed
     *       as a XML document.</li>
     *   <li>{@link String}: The string content is parsed directly as a XML document.
     *       Encoding <strong>must</strong> be UTF-8 (no other encoding is supported
     *       by current implementation of this method).</li>
     * </ul>
     *
     * @param  expected  The expected XML document.
     * @param  actual    The XML document to compare.
     * @throws IOException If the stream can not be read.
     * @throws ParserConfigurationException If a {@link DocumentBuilder} can not be created.
     * @throws SAXException If an error occurred while parsing the XML document.
     */
    public DomComparator(final Object expected, final Object actual)
            throws IOException, ParserConfigurationException, SAXException
    {
        super(expected, actual);
    }
}
