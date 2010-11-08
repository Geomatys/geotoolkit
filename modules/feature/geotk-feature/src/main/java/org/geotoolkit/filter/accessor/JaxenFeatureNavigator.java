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
import org.jaxen.util.AncestorAxisIterator;
import org.jaxen.util.AncestorOrSelfAxisIterator;
import org.jaxen.util.DescendantAxisIterator;
import org.jaxen.util.DescendantOrSelfAxisIterator;
import org.jaxen.util.FollowingAxisIterator;
import org.jaxen.util.FollowingSiblingAxisIterator;
import org.jaxen.util.PrecedingAxisIterator;
import org.jaxen.util.PrecedingSiblingAxisIterator;
import org.jaxen.util.SelfAxisIterator;

import org.opengis.feature.Attribute;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
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
        if(o instanceof Property){
            final Property candidate = (Property) o;
            return candidate.getName().getNamespaceURI();
        }else if(o instanceof PropertyDescriptor){
            final PropertyDescriptor candidate = (PropertyDescriptor) o;
            return candidate.getName().getNamespaceURI();
        }else if(o instanceof PropertyType){
            final PropertyType candidate = (PropertyType) o;
            return candidate.getName().getNamespaceURI();
        }
        return null;
    }

    @Override
    public String getElementName(Object o) {
        if(o instanceof Property){
            final Property candidate = (Property) o;
            return candidate.getName().getLocalPart();
        }else if(o instanceof PropertyDescriptor){
            final PropertyDescriptor candidate = (PropertyDescriptor) o;
            return candidate.getName().getLocalPart();
        }else if(o instanceof PropertyType){
            final PropertyType candidate = (PropertyType) o;
            return candidate.getName().getLocalPart();
        }
        return null;
    }

    @Override
    public String getElementQName(Object o) {
        if(o instanceof Property){
            final Property candidate = (Property) o;
            return DefaultName.toJCRExtendedForm(candidate.getName());
        }else if(o instanceof PropertyDescriptor){
            final PropertyDescriptor candidate = (PropertyDescriptor) o;
            return DefaultName.toJCRExtendedForm(candidate.getName());
        }else if(o instanceof PropertyType){
            final PropertyType candidate = (PropertyType) o;
            return DefaultName.toJCRExtendedForm(candidate.getName());
        }
        return null;
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
        return o instanceof ComplexAttribute || o instanceof ComplexType;
    }

    @Override
    public boolean isElement(Object o) {
        return o instanceof Property || o instanceof PropertyType || o instanceof PropertyDescriptor;
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
        if(o instanceof Property){
            final Property candidate = (Property) o;
            final Object value = candidate.getValue();
            return (value==null)? EMPTY : value.toString();
        }else if(o instanceof PropertyDescriptor){
            final PropertyDescriptor candidate = (PropertyDescriptor) o;
            return DefaultName.toJCRExtendedForm(candidate.getName());
        }else if(o instanceof PropertyType){
            final PropertyType candidate = (PropertyType) o;
            return DefaultName.toJCRExtendedForm(candidate.getName());
        }
        return null;
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
            final ComplexAttribute candidate = (ComplexAttribute) o;
            return candidate.getProperties().iterator();
        }else if(o instanceof PropertyDescriptor){
            final PropertyDescriptor ca = (PropertyDescriptor) o;
            final PropertyType type = ca.getType();
            if(type instanceof ComplexType){
                final ComplexType ct = (ComplexType) type;
                return ct.getDescriptors().iterator();
            }else{
                return JaxenConstants.EMPTY_ITERATOR;
            }
        }else if(o instanceof PropertyType){
            final PropertyType type = (PropertyType) o;
            if(type instanceof ComplexType){
                final ComplexType ct = (ComplexType) type;
                return ct.getDescriptors().iterator();
            }else{
                return JaxenConstants.EMPTY_ITERATOR;
            }
        }

        return JaxenConstants.EMPTY_ITERATOR;
    }

    @Override
    public Iterator getDescendantAxisIterator(Object o) throws UnsupportedAxisException {
        return new DescendantAxisIterator(o, this);
    }

    @Override
    public Iterator getParentAxisIterator(Object o) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator getAncestorAxisIterator(Object o) throws UnsupportedAxisException {
        return new AncestorAxisIterator(o, this);
    }

    @Override
    public Iterator getFollowingSiblingAxisIterator(Object o) throws UnsupportedAxisException {
        return new FollowingSiblingAxisIterator(o, this);
    }

    @Override
    public Iterator getPrecedingSiblingAxisIterator(Object o) throws UnsupportedAxisException {
        return new PrecedingSiblingAxisIterator(o, this);
    }

    @Override
    public Iterator getFollowingAxisIterator(Object o) throws UnsupportedAxisException {
        return new FollowingAxisIterator(o, this);
    }

    @Override
    public Iterator getPrecedingAxisIterator(Object o) throws UnsupportedAxisException {
        return new PrecedingAxisIterator(o, this);
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
        return new SelfAxisIterator(o);
    }

    @Override
    public Iterator getDescendantOrSelfAxisIterator(Object o) throws UnsupportedAxisException {
        return new DescendantOrSelfAxisIterator(o, this);
    }

    @Override
    public Iterator getAncestorOrSelfAxisIterator(Object o) throws UnsupportedAxisException {
        return new AncestorOrSelfAxisIterator(o, this);
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
        }else if(o instanceof PropertyDescriptor){
            return o;
        }else if(o instanceof PropertyType){
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
