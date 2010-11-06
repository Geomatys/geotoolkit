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

package org.geotoolkit.filter.accessor;

import java.util.Collections;
import java.util.Iterator;

import org.geotoolkit.feature.DefaultName;

import org.jaxen.FunctionCallException;
import org.jaxen.JaxenConstants;
import org.jaxen.Navigator;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;
import org.opengis.feature.Attribute;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;
import org.opengis.filter.identity.Identifier;

/**
 * xpath navigator for features.
 *
 * Element == ComplexType
 * Attribut == Property
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class JaxenFeatureNavigator implements Navigator{

    private static final String EMPTY = "";

    @Override
    public String getElementNamespaceUri(Object o) {
        final Property prop = (Property) o;
        return prop.getName().getNamespaceURI();
    }

    @Override
    public String getElementName(Object o) {
        final Property prop = (Property) o;
        return prop.getName().getLocalPart();
    }

    @Override
    public String getElementQName(Object o) {
        final Property prop = (Property) o;
        return DefaultName.toJCRExtendedForm(prop.getName());
    }

    @Override
    public String getAttributeNamespaceUri(Object o) {
        //final Identifier id = (Identifier) o;
        return null;
    }

    @Override
    public String getAttributeName(Object o) {
        //final Identifier id = (Identifier) o;
        return "Id";
    }

    @Override
    public String getAttributeQName(Object o) {
        //final Identifier id = (Identifier) o;
        return "Id";
    }

    @Override
    public boolean isDocument(Object o) {
        return o instanceof ComplexAttribute;
    }

    @Override
    public boolean isElement(Object o) {
        return o instanceof Property;
    }

    @Override
    public boolean isAttribute(Object o) {
        return o instanceof Identifier;
    }

    @Override
    public boolean isNamespace(Object o) {
        return false;
    }

    @Override
    public boolean isComment(Object o) {
        return false;
    }

    @Override
    public boolean isText(Object o) {
        return false;
    }

    @Override
    public boolean isProcessingInstruction(Object o) {
        return false;
    }

    @Override
    public String getCommentStringValue(Object o) {
        throw new UnsupportedOperationException("Not supported, should never be called, " + o);
    }

    @Override
    public String getElementStringValue(Object o) {
        final Property property = (Property) o;
        final Object value = property.getValue();
        return (value==null)? EMPTY : value.toString();
    }

    @Override
    public String getAttributeStringValue(Object o) {
        final Identifier property = (Identifier) o;
        return property.getID().toString();
    }

    @Override
    public XPath parseXPath(String string) throws SAXPathException {
        throw new UnsupportedOperationException("Not supported, should never be called");
//        return new JaxenFeatureXPath(string);
    }

    ////////////////////////////////////////////////////////////////////////////
    // NOT NEEDED //////////////////////////////////////////////////////////////

    @Override
    public String getNamespaceStringValue(Object o) {
        throw new UnsupportedOperationException("Not supported, should never be called");
    }

    @Override
    public String getTextStringValue(Object o) {
        throw new UnsupportedOperationException("Not supported, should never be called");
    }

    @Override
    public String getNamespacePrefix(Object o) {
        throw new UnsupportedOperationException("Not supported, should never be called");
    }

    @Override
    public String getProcessingInstructionTarget(Object o) {
        throw new UnsupportedOperationException("Not supported, should never be called");
    }

    @Override
    public String getProcessingInstructionData(Object o) {
        throw new UnsupportedOperationException("Not supported, should never be called");
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITERATORS ///////////////////////////////////////////////////////////////

    @Override
    public Iterator getChildAxisIterator(Object o) throws UnsupportedAxisException {
        if(o instanceof ComplexAttribute){
            final ComplexAttribute ca = (ComplexAttribute) o;
            return ca.getProperties().iterator();
        }

        return null;
    }

    @Override
    public Iterator getDescendantAxisIterator(Object o) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator getParentAxisIterator(Object o) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator getAncestorAxisIterator(Object o) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator getFollowingSiblingAxisIterator(Object o) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator getPrecedingSiblingAxisIterator(Object o) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator getFollowingAxisIterator(Object o) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator getPrecedingAxisIterator(Object o) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator getAttributeAxisIterator(Object o) throws UnsupportedAxisException {
        if(o instanceof Attribute){
            final Attribute att = (Attribute) o;
            final Identifier id = att.getIdentifier();
            if(id != null){
                return Collections.singleton(id).iterator();
            }
        }

        return JaxenConstants.EMPTY_ITERATOR;
    }

    @Override
    public Iterator getNamespaceAxisIterator(Object o) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator getSelfAxisIterator(Object o) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator getDescendantOrSelfAxisIterator(Object o) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator getAncestorOrSelfAxisIterator(Object o) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }



    //INTERFACE ////////////////////////////////////////////////////////////////



    @Override
    public Object getDocument(String string) throws FunctionCallException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getDocumentNode(Object o) {
        if(o instanceof ComplexAttribute){
            return o;
        }
        return null;
    }

    @Override
    public Object getParentNode(Object o) throws UnsupportedAxisException {
        throw new UnsupportedAxisException("Not supported. Expression on feature can only be forward.");
    }

    @Override
    public String translateNamespacePrefixToUri(String string, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getElementById(Object o, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public short getNodeType(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
