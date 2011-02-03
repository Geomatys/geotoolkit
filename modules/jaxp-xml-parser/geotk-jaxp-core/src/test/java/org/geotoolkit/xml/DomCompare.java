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
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DomCompare {

    /**
     * Convinient method to test xml comparison by ignoring irrevelant details
     * like formating, different attribut order, comments ...
     *  
     * @param expected : the expected structure (File,Stream,Document)
     * @param result : the obtained result (File,Stream,Document)
     */
    public static void compare(final Object expected, final Object result)
            throws ParserConfigurationException, SAXException, IOException{
        final DomComparator comparator = new DomComparator(expected, result);
        comparator.compare();
    }

    public static void compareNode(final Node expected, final Node result){
        final DomComparator comparator = new DomComparator(expected, result);
        comparator.compare();
    }

}
