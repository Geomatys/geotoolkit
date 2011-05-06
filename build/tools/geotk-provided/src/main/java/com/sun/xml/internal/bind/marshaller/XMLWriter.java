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
package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.Writer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;


/**
 * A placeholder for an internal class from Sun JDK 6. Sun's internal package are not visible at
 * compile time, while they are visible at runtime. This placeholder is used only in order to
 * allows some classes to extend the Sun's class at compile-time. It will not be used at run-time;
 * the "real" Sun's class will be used instead since it come first in the classpath.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 */
public class XMLWriter extends XMLFilterImpl {
    public XMLWriter(Writer writer, String string, CharacterEscapeHandler ceh) {
    }

    public XMLWriter(Writer writer, String string) {
    }

    public void reset() {
        throw new UnsupportedOperationException("Not expected to be used at runtime.");
    }

    public void flush() throws IOException {
        throw new UnsupportedOperationException("Not expected to be used at runtime.");
    }

    public void setOutput(Writer writer, String string) {
        throw new UnsupportedOperationException("Not expected to be used at runtime.");
    }

    public void setXmlDecl(boolean bln) {
        throw new UnsupportedOperationException("Not expected to be used at runtime.");
    }

    public void setHeader(String string) {
        throw new UnsupportedOperationException("Not expected to be used at runtime.");
    }

    public void startElement(String string, String string1) throws SAXException {
        throw new UnsupportedOperationException("Not expected to be used at runtime.");
    }

    public void startElement(String string) throws SAXException {
        throw new UnsupportedOperationException("Not expected to be used at runtime.");
    }

    public void endElement(String string, String string1) throws SAXException {
        throw new UnsupportedOperationException("Not expected to be used at runtime.");
    }

    public void endElement(String string) throws SAXException {
        throw new UnsupportedOperationException("Not expected to be used at runtime.");
    }

    public void dataElement(String string, String string1, String string2, Attributes atrbts, String string3) throws SAXException {
        throw new UnsupportedOperationException("Not expected to be used at runtime.");
    }

    public void dataElement(String string, String string1, String string2) throws SAXException {
        throw new UnsupportedOperationException("Not expected to be used at runtime.");
    }

    public void dataElement(String string, String string1) throws SAXException {
        throw new UnsupportedOperationException("Not expected to be used at runtime.");
    }

    public void characters(String string) throws SAXException {
        throw new UnsupportedOperationException("Not expected to be used at runtime.");
    }
}
