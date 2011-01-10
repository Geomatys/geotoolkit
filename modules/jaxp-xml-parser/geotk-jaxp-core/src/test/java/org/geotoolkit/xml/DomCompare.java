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
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.util.DomUtilities;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
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
    public static void compare(final Object expected, final Object result)
            throws ParserConfigurationException, SAXException, IOException{
        final Document expectedDoc = DomUtilities.read(expected);
        final Document resultDoc = DomUtilities.read(result);

        compareNode(expectedDoc.getDocumentElement(),
                    resultDoc.getDocumentElement());

    }

    public static void compareNode(final Node expected, final Node result){
        compareNode(expected, result, null, 0);
    }

    /**
     * Compare two nodes.
     *
     * @param expected
     * @param result
     * @param previousNodes : list of previous nodes as String, for better output message.
     * @param nodeIndex : index of the tested node.
     */
    public static void compareNode(final Node expected, final Node result, List<String> previousNodes, final int nodeIndex){
        if(previousNodes == null){
            previousNodes = new ArrayList<String>();
        }

        final StringBuilder sb = new StringBuilder();
        sb.append('[').append(nodeIndex).append(']');
        toString(expected, sb);
        previousNodes.add(sb.toString());

        final String errorMessage = errorMessage(expected, result, previousNodes);

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
                compareNode(att,resAtt,previousNodes, i);
            }
        }


        //check text value for types :
        //TEXT_NODE, CDATA_SECTION_NODE, COMMENT_NODE, PROCESSING_INSTRUCTION_NODE

        if(expected instanceof Text || expected instanceof CDATASection
                || expected instanceof Comment || expected instanceof ProcessingInstruction){
            assertEquals(errorMessage,expected.getTextContent().trim(), result.getTextContent().trim());
        }

        //check child nodes recursivly
        final List<Node> expectedChilds = removeEmptyTextElements(expected.getChildNodes());
        final List<Node> resultChilds = removeEmptyTextElements(result.getChildNodes());
        assertEquals(errorMessage, expectedChilds.size(), resultChilds.size());
        for(int i=0,n=expectedChilds.size();i<n;i++){
            compareNode(expectedChilds.get(i), resultChilds.get(i), previousNodes, i);
        }

        previousNodes.remove(previousNodes.size()-1);
    }

    private static List<Node> removeEmptyTextElements(final NodeList lst){
        final List<Node> nodes = new ArrayList<Node>();
        if(lst != null){
            for(int i=0,n=lst.getLength();i<n;i++){
                final Node no = lst.item(i);
                if(no instanceof Text){
                    final Text t = (Text) no;
                    final String value = t.getWholeText();
                    for(char c : value.toCharArray()){
                        if(c != '\n' && c != ' '){
                            nodes.add(no);
                            continue;
                        }
                    }
                    continue;
                }
                nodes.add(no);
            }
        }
        return nodes;
    }

    private static String errorMessage(final Node expected, final Node result, final List<String> previousNodes){
        final StringBuilder sb = new StringBuilder("Error comparing nodes : \n");
        toString(expected, sb);
        sb.append('\n');
        toString(result, sb);
        sb.append('\n');
        sb.append("Node Stack : \n");
        for(String str : previousNodes){
            sb.append(str).append('\n');
        }
        return sb.toString();
    }

    private static String toString(final Node node){
        final StringBuilder sb = new StringBuilder();
        toString(node, sb);
        return sb.toString();
    }

    private static void toString(final Node node, final StringBuilder sb){
        if(node == null){
            sb.append("null");
            return;
        }
        sb.append(node.getNamespaceURI()).append(':').append(node.getNodeName());
        if(node instanceof Text || node instanceof CDATASection
                || node instanceof Comment || node instanceof ProcessingInstruction){
            sb.append(" Text=").append(node.getTextContent()).append(' ');
        }
        if(node.getChildNodes().getLength() == 1){
            //potential text child
            final Node n = node.getChildNodes().item(0);
            if(n instanceof Text || n instanceof CDATASection
                    || n instanceof Comment || n instanceof ProcessingInstruction){
                sb.append(", First child Text=").append(n.getTextContent()).append(' ');
            }
        }


        sb.append('(');
        if(node.getChildNodes() != null){
            sb.append(" nbChild=").append(node.getChildNodes().getLength());
        }
        if(node.getAttributes() != null){
            sb.append(" nbAtt=").append(node.getAttributes().getLength());
        }
        sb.append(") [");
        NamedNodeMap atts = node.getAttributes();
        if(atts != null){
            for(int i=0;i<atts.getLength();i++){
                sb.append(atts.item(i)).append("  ");
            }
        }
        sb.append(']');
    }

}
