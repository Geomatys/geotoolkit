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

package org.geotoolkit.filter.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.sis.util.iso.Names;

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

import org.geotoolkit.feature.Attribute;
import org.geotoolkit.feature.ComplexAttribute;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.feature.type.PropertyType;
import org.opengis.filter.identity.Identifier;
import org.opengis.util.GenericName;
import org.opengis.util.NameSpace;

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
    public String getElementNamespaceUri(final Object o) {
        if(o instanceof Property){
            final Property candidate = (Property) o;
            return getNamespace(candidate.getName());
        }else if(o instanceof PropertyDescriptor){
            final PropertyDescriptor candidate = (PropertyDescriptor) o;
            return getNamespace(candidate.getName());
        }else if(o instanceof PropertyType){
            final PropertyType candidate = (PropertyType) o;
            return getNamespace(candidate.getName());
        }
        return null;
    }

    private String getNamespace(GenericName candidate){
        final NameSpace scope = candidate.scope();
        if(scope.isGlobal()){
            return null;
        }else{
            return scope.name().toString();
        }
    }

    @Override
    public String getElementName(final Object o) {
        String str = null;
        if(o instanceof Property){
            final Property candidate = (Property) o;
            str = candidate.getName().toString();
        }else if(o instanceof PropertyDescriptor){
            final PropertyDescriptor candidate = (PropertyDescriptor) o;
            str = candidate.getName().tip().toString();
        }else if(o instanceof PropertyType){
            final PropertyType candidate = (PropertyType) o;
            str = candidate.getName().tip().toString();
        }
        if(str!=null && str.startsWith("@")){
            str = str.substring(1);
        }
        return str;
    }

    @Override
    public String getElementQName(final Object o) {
        if(o instanceof Property){
            final Property candidate = (Property) o;
            return Names.toExpandedString(candidate.getName());
        }else if(o instanceof PropertyDescriptor){
            final PropertyDescriptor candidate = (PropertyDescriptor) o;
            return Names.toExpandedString(candidate.getName());
        }else if(o instanceof PropertyType){
            final PropertyType candidate = (PropertyType) o;
            return Names.toExpandedString(candidate.getName());
        }
        return null;
    }

    @Override
    public String getAttributeNamespaceUri(final Object o) {
        return getElementNamespaceUri(o);
    }

    @Override
    public String getAttributeName(final Object o) {
        String str = null;
        if(o instanceof Property){
            final Property candidate = (Property) o;
            str = candidate.getName().toString();
        }else if(o instanceof PropertyDescriptor){
            final PropertyDescriptor candidate = (PropertyDescriptor) o;
            str = candidate.getName().tip().toString();
        }else if(o instanceof PropertyType){
            final PropertyType candidate = (PropertyType) o;
            str = candidate.getName().tip().toString();
        }
        if(str!=null && str.startsWith("@")){
            str = str.substring(1);
        }
        return str;
    }

    @Override
    public String getAttributeQName(final Object o) {
        //final Identifier id = (Identifier) o;
        return "Id";
    }

    @Override
    public boolean isDocument(final Object o) {
        return o instanceof ComplexAttribute || o instanceof ComplexType;
    }

    @Override
    public boolean isElement(final Object o) {
        return o instanceof Property || o instanceof PropertyType || o instanceof PropertyDescriptor;
    }

    @Override
    public boolean isAttribute(final Object o) {
        return o instanceof Identifier;
    }

    @Override
    public boolean isNamespace(final Object o) {
        return false;
    }

    @Override
    public boolean isComment(final Object o) {
        return false;
    }

    @Override
    public boolean isText(final Object o) {
        return false;
    }

    @Override
    public boolean isProcessingInstruction(final Object o) {
        return false;
    }

    @Override
    public String getCommentStringValue(final Object o) {
        throw new UnsupportedOperationException("Not supported, should never be called, " + o);
    }

    @Override
    public String getElementStringValue(final Object o) {
        if(o instanceof Property){
            final Property candidate = (Property) o;
            final Object value = candidate.getValue();
            return (value==null)? EMPTY : value.toString();
        }else if(o instanceof PropertyDescriptor){
            final PropertyDescriptor candidate = (PropertyDescriptor) o;
            return Names.toExpandedString(candidate.getName());
        }else if(o instanceof PropertyType){
            final PropertyType candidate = (PropertyType) o;
            return Names.toExpandedString(candidate.getName());
        }
        return null;
    }

    @Override
    public String getAttributeStringValue(final Object o) {
        final Identifier property = (Identifier) o;
        return property.getID().toString();
    }

    @Override
    public XPath parseXPath(final String string) throws SAXPathException {
        throw new UnsupportedOperationException("Not supported, should never be called");
//        return new JaxenFeatureXPath(string);
    }

    ////////////////////////////////////////////////////////////////////////////
    // NOT NEEDED //////////////////////////////////////////////////////////////

    @Override
    public String getNamespaceStringValue(final Object o) {
        throw new UnsupportedOperationException("Not supported, should never be called");
    }

    @Override
    public String getTextStringValue(final Object o) {
        throw new UnsupportedOperationException("Not supported, should never be called");
    }

    @Override
    public String getNamespacePrefix(final Object o) {
        throw new UnsupportedOperationException("Not supported, should never be called");
    }

    @Override
    public String getProcessingInstructionTarget(final Object o) {
        throw new UnsupportedOperationException("Not supported, should never be called");
    }

    @Override
    public String getProcessingInstructionData(final Object o) {
        throw new UnsupportedOperationException("Not supported, should never be called");
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITERATORS ///////////////////////////////////////////////////////////////

    @Override
    public Iterator getChildAxisIterator(final Object o) throws UnsupportedAxisException {
        if(o instanceof ComplexAttribute){
            final ComplexAttribute candidate = (ComplexAttribute) o;
            return new PropIterator(candidate.getProperties().iterator(),false);
        }else if(o instanceof PropertyDescriptor){
            final PropertyDescriptor ca = (PropertyDescriptor) o;
            final PropertyType type = ca.getType();
            if(type instanceof ComplexType){
                final ComplexType ct = (ComplexType) type;
                return new PropIterator(ct.getDescriptors().iterator(),false);
            }else{
                return JaxenConstants.EMPTY_ITERATOR;
            }
        }else if(o instanceof PropertyType){
            final PropertyType type = (PropertyType) o;
            if(type instanceof ComplexType){
                final ComplexType ct = (ComplexType) type;
                return new PropIterator(ct.getDescriptors().iterator(),false);
            }else{
                return JaxenConstants.EMPTY_ITERATOR;
            }
        }

        return JaxenConstants.EMPTY_ITERATOR;
    }

    @Override
    public Iterator getDescendantAxisIterator(final Object o) throws UnsupportedAxisException {
        return new DescendantAxisIterator(o, this);
    }

    @Override
    public Iterator getParentAxisIterator(final Object o) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator getAncestorAxisIterator(final Object o) throws UnsupportedAxisException {
        return new AncestorAxisIterator(o, this);
    }

    @Override
    public Iterator getFollowingSiblingAxisIterator(final Object o) throws UnsupportedAxisException {
        return new FollowingSiblingAxisIterator(o, this);
    }

    @Override
    public Iterator getPrecedingSiblingAxisIterator(final Object o) throws UnsupportedAxisException {
        return new PrecedingSiblingAxisIterator(o, this);
    }

    @Override
    public Iterator getFollowingAxisIterator(final Object o) throws UnsupportedAxisException {
        return new FollowingAxisIterator(o, this);
    }

    @Override
    public Iterator getPrecedingAxisIterator(final Object o) throws UnsupportedAxisException {
        return new PrecedingAxisIterator(o, this);
    }

    @Override
    public Iterator getAttributeAxisIterator(final Object o) throws UnsupportedAxisException {
        if(o instanceof Attribute){
            final Attribute att = (Attribute) o;
            final Identifier id = att.getIdentifier();
            if(id != null){
                return Collections.singleton(id).iterator();
            }
        } else if(o instanceof AttributeDescriptor){
            final AttributeDescriptor ca = (AttributeDescriptor) o;
            final PropertyType type = ca.getType();
            if(type instanceof ComplexType){
                final ComplexType ct = (ComplexType) type;
                return new PropIterator(ct.getDescriptors().iterator(), true);
            }else{
                return JaxenConstants.EMPTY_ITERATOR;
            }
        } else if(o instanceof ComplexType){
            final ComplexType ct = (ComplexType) o;
            return new PropIterator(ct.getDescriptors().iterator(), true);
        }

        return JaxenConstants.EMPTY_ITERATOR;
    }

    @Override
    public Iterator getNamespaceAxisIterator(final Object o) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator getSelfAxisIterator(final Object o) throws UnsupportedAxisException {
        return new SelfAxisIterator(o);
    }

    @Override
    public Iterator getDescendantOrSelfAxisIterator(final Object o) throws UnsupportedAxisException {
        return new DescendantOrSelfAxisIterator(o, this);
    }

    @Override
    public Iterator getAncestorOrSelfAxisIterator(final Object o) throws UnsupportedAxisException {
        return new AncestorOrSelfAxisIterator(o, this);
    }


    //INTERFACE ////////////////////////////////////////////////////////////////

    @Override
    public Object getDocument(final String string) throws FunctionCallException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getDocumentNode(final Object o) {
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
    public Object getParentNode(final Object o) throws UnsupportedAxisException {
        throw new UnsupportedAxisException("Not supported. Expression on feature can only be forward.");
    }

    @Override
    public String translateNamespacePrefixToUri(final String string, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getElementById(final Object o, final String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public short getNodeType(final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static class PropIterator implements Iterator{

        private final Iterator ite;
        private final boolean attributes;
        private Object next = null;

        public PropIterator(Iterator ite, boolean attributes) {
            this.ite = ite;
            this.attributes = attributes;
        }

        @Override
        public boolean hasNext() {
            findNext();
            return next!=null;
        }

        @Override
        public Object next() {
            findNext();
            if(next==null) throw new NoSuchElementException();
            Object n = next;
            next = null;
            return n;
        }

        @Override
        public void remove() {

        }

        private void findNext(){
            while(ite.hasNext() && next==null){
                final Object candidate = ite.next();

                Name name = null;
                if(candidate instanceof PropertyDescriptor){
                    name = ((PropertyDescriptor)candidate).getName();
                }else if(candidate instanceof Property){
                    name = ((Property)candidate).getName();
                }

                if(name!=null){
                    if(attributes){
                        next = name.tip().toString().startsWith("@") ? candidate : null;
                    }else{
                        next = !name.tip().toString().startsWith("@") ? candidate : null;
                    }
                }

            }
        }
        
    }

}
