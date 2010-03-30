/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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

package org.geotoolkit.util;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Utils method for dom parsing.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DomUtilities {

    private DomUtilities(){}

    public static Element firstElement(Element parent, String tagName){
        final NodeList lst = parent.getElementsByTagName(tagName);
        if(lst.getLength() > 0){
            return (Element) lst.item(0);
        }else{
            return null;
        }
    }

    public static <T> T textValue(Element parent, String tagName, Class<T> clazz){
        final Element ele = firstElement(parent, tagName);
        if(ele == null) return null;
        final String text = ele.getTextContent();
        if(text == null) return null;
        return Converters.convert(text, clazz);
    }
}
