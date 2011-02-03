/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import org.geotoolkit.util.DomUtilities;
import org.geotoolkit.util.StringUtilities;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
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
public class DomComparator {

    private final Node expectedDoc;
    private final Node resultDoc;

    public DomComparator(Object expected, Object candidate) throws ParserConfigurationException, SAXException, IOException{
        expectedDoc = (expected instanceof Node) ? (Node)expected : DomUtilities.read(expected);
        resultDoc = (candidate instanceof Node) ? (Node)candidate : DomUtilities.read(candidate);
        assertNotNull(expectedDoc);
        assertNotNull(resultDoc);
    }
    
    public DomComparator(Node expected, Node candidate){
        expectedDoc = expected;
        resultDoc = candidate;
        assertNotNull(expectedDoc);
        assertNotNull(resultDoc);
    }

    public void compare(){
        compareNode(expectedDoc, resultDoc);
    }

    /**
     * Compare two nodes.
     *
     * @param expected
     * @param result
     */
    public void compareNode(final Node expected, final Node result){

        if(expected == null || result == null){
            fail(toErrorMessage(expected,result));
        }

        //check names
        compareNames(expected, result);
        //check attributs
        compareAttributes(expected, result);


        //check text value for types :
        //TEXT_NODE, CDATA_SECTION_NODE, COMMENT_NODE, PROCESSING_INSTRUCTION_NODE

        if(expected instanceof CDATASection){
            compareCDATASectionNode((CDATASection) expected,result);
        }else if(expected instanceof Text){
            compareTextNode((Text) expected,result);
        }else if(expected instanceof Comment){
            compareCommentNode((Comment) expected,result);
        }else if(expected instanceof ProcessingInstruction){
            compareProcessingInstructionNode((ProcessingInstruction) expected,result);
        }

        //check child nodes recursivly if it's not an attribut
        if(expected.getNodeType() != Node.ATTRIBUTE_NODE){
            compareChildren(expected, result);
        }
    }

    protected void compareTextNode(final Text expected, final Node result){
        assertTrue(result instanceof Text);
        assertEquals(toErrorMessage(expected,result),
                expected.getTextContent().trim(),
                result.getTextContent().trim());
    }

    protected void compareCDATASectionNode(final CDATASection expected, final Node result){
        assertTrue(result instanceof CDATASection);
        assertEquals(toErrorMessage(expected,result),
                expected.getTextContent().trim(),
                result.getTextContent().trim());
    }

    protected void compareCommentNode(final Comment expected, final Node result){
        assertTrue(result instanceof Comment);
        assertEquals(toErrorMessage(expected,result),
                expected.getTextContent().trim(),
                result.getTextContent().trim());
    }

    protected void compareProcessingInstructionNode(final ProcessingInstruction expected, final Node result){
        assertTrue(result instanceof ProcessingInstruction);
        assertEquals(toErrorMessage(expected,result),
                expected.getTextContent().trim(),
                result.getTextContent().trim());
    }

    protected void compareChildren(final Node expected, final Node result){
        Node expChild = firstNoneEmpty(expected.getFirstChild(), true);
        Node resChild = firstNoneEmpty(result.getFirstChild(), true);

        if(expChild == null && resChild != null){
            fail(toErrorMessage(expected,result));
        }

        while(expChild != null){
            compareNode(expChild, resChild);
            expChild = firstNoneEmpty(expChild.getNextSibling(), true);
            resChild = firstNoneEmpty(resChild.getNextSibling(), true);
        }

    }

    protected void compareNames(final Node expected, final Node result){
        assertEquals(toErrorMessage(expected,result),
                expected.getNamespaceURI(),result.getNamespaceURI());
        assertEquals(toErrorMessage(expected,result),
                expected.getNodeName(),result.getNodeName());
    }

    protected void compareAttributes(final Node expected, final Node result){

        final NamedNodeMap expectedAttributs = expected.getAttributes();
        final NamedNodeMap resultAttributs = result.getAttributes();
        if(expectedAttributs == null && resultAttributs == null){
            // none have attributs
        } else if(expectedAttributs == null){
            // checkdifferent number of attributs
            if(resultAttributs.getLength() != 0){
                fail(toErrorMessage(expected,result));
            }
            
        } else if(resultAttributs == null){
            // different number of attributs
            if(expectedAttributs.getLength() != 0){
                fail(toErrorMessage(expected,result));
            }

        }else {
            //check attributs
            assertEquals(toErrorMessage(expected,result), expectedAttributs.getLength(), resultAttributs.getLength());
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
    }

    private static Node firstNoneEmpty(Node node, final boolean noAttributeType){
        while(node != null && ((noAttributeType && node.getNodeType() == Node.ATTRIBUTE_NODE) || isEmptyTextNode(node)) ){
            node = node.getNextSibling();
        }
        return node;
    }

    private static boolean isEmptyTextNode(final Node no) {
        if (no instanceof Text) {
            final Text t = (Text) no;
            final String value = t.getTextContent();
            for (char c : value.toCharArray()) {
                if (c != '\n' && c != ' ') {
                    return false;
                }
            }
            return true;
        }
        return false;
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

    protected static String toErrorMessage(Node expected, Node result){
        final StringBuilder sb = new StringBuilder("Error comparing nodes : \n");
        toString(expected, sb);
        sb.append('\n');
        toString(result, sb);

        final List<String> stack = new ArrayList<String>();
        while(expected != null){
            stack.add(String.valueOf(expected.getNodeName()));
            expected = expected.getParentNode();
        }
        Collections.reverse(stack);
        sb.append("\n> Expected stack :\n");
        sb.append(StringUtilities.toStringTree(stack));

        stack.clear();
        while(result != null){
            stack.add(String.valueOf(result.getNodeName()));
            result = result.getParentNode();
        }
        Collections.reverse(stack);
        sb.append("\n> Result stack :\n");
        sb.append(StringUtilities.toStringTree(stack));

        sb.append('\n');
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
