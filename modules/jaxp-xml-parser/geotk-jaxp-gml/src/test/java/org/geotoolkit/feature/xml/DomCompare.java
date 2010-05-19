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


package org.geotoolkit.feature.xml;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.util.DomUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

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
    public static void compare(Object expected, Object result)
            throws ParserConfigurationException, SAXException, IOException{
        final Document expectedDoc = DomUtilities.read(expected);
        final Document resultDoc = DomUtilities.read(result);

        compareNode(expectedDoc.getDocumentElement(),
                    resultDoc.getDocumentElement());

    }

    public static void compareNode(Node expected, Node result){
        final String errorMessage = errorMessage(expected, result);

        if(expected == null || result == null){
            fail(errorMessage);
        }

        //check names
        assertEquals(errorMessage,expected.getNamespaceURI(),result.getNamespaceURI());
        assertEquals(errorMessage,expected.getNodeName(),result.getNodeName());

        //check attributs
        final NamedNodeMap expectedAttributs = expected.getAttributes();
        final NamedNodeMap resultAttributs = result.getAttributes();
        if(expectedAttributs == null && resultAttributs == null){
            // none have attributs
        }else{
            //check attributs
            assertEquals(errorMessage, expectedAttributs.getLength(), resultAttributs.getLength());
            for(int i=0,n=expectedAttributs.getLength();i<n;i++){
                final Node att = expectedAttributs.item(i);
                final String ns = att.getNamespaceURI();
                final String name = att.getNodeName();
                final Node resAtt;
                if(ns == null){
                    resAtt = resultAttributs.getNamedItem(name);
                }else{
                    resAtt = resultAttributs.getNamedItemNS(ns, name);
                }
                compareNode(att,resAtt);
            }
        }

        

        //check text value
        assertEquals(errorMessage,expected.getTextContent().trim(), result.getTextContent().trim());

        //check child nodes recursivly
        final NodeList expectedChilds = expected.getChildNodes();
        final NodeList resultChilds = result.getChildNodes();
        assertEquals(errorMessage, expectedChilds.getLength(), resultChilds.getLength());
        for(int i=0,n=expectedChilds.getLength();i<n;i++){
            compareNode(expectedChilds.item(i), resultChilds.item(i));
        }
    }

    private static String errorMessage(Node expected, Node result){
        final StringBuilder sb = new StringBuilder("Error comparing nodes : \n");
        toString(expected, sb);
        sb.append('\n');
        toString(result, sb);
        sb.append('\n');
        return sb.toString();
    }

    private static void toString(Node node, StringBuilder sb){
        if(node == null){
            sb.append("null");
            return;
        }
        sb.append(node.getNamespaceURI()).append(':').append(node.getNodeName());
        sb.append('=').append(node.getTextContent());
        sb.append('[');
        NamedNodeMap atts = node.getAttributes();
        if(atts != null){
            for(int i=0;i<atts.getLength();i++){
                sb.append(atts.item(i)).append("  ");
            }
        }
        sb.append(']');
    }

}
