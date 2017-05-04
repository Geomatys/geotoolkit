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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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

import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Attribute;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociation;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Property;
import org.opengis.feature.PropertyType;
import org.opengis.filter.identity.Identifier;
import org.opengis.util.GenericName;

/**
 * xpath navigator for features.
 *
 * Element == ComplexType
 * Attribut == Property
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
final class JaxenFeatureNavigator implements Navigator{

    private static final String EMPTY = "";

    @Override
    public String getElementNamespaceUri(final Object o) {
        if(o instanceof Fake){
            final Fake candidate = (Fake) o;
            return getNamespace(candidate.name);
        }else if(o instanceof Feature){
            final Feature candidate = (Feature) o;
            return getNamespace(candidate.getType().getName());
        }else if(o instanceof Property){
            final Property candidate = (Property) o;
            return getNamespace(candidate.getName());
        }else if(o instanceof PropertyType){
            final PropertyType candidate = (PropertyType) o;
            return getNamespace(candidate.getName());
        }
        return null;
    }

    private String getNamespace(GenericName candidate){
        final String ns = NamesExt.getNamespace(candidate);
        if(ns==null || ns.isEmpty()){
            return null;
        }else{
            return ns;
        }
    }

    @Override
    public String getElementName(final Object o) {
        String str = null;
        if(o instanceof Fake){
            final Fake candidate = (Fake) o;
            str = candidate.name.tip().toString();
        }else if(o instanceof Feature){
            final Feature candidate = (Feature) o;
            str = candidate.getType().getName().tip().toString();
        }else if(o instanceof Property){
            final Property candidate = (Property) o;
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
        if(o instanceof Fake){
            final Fake candidate = (Fake) o;
            return NamesExt.toExpandedString(candidate.name);
        }else if(o instanceof Feature){
            final Feature candidate = (Feature) o;
            return NamesExt.toExpandedString(candidate.getType().getName());
        }else if(o instanceof Property){
            final Property candidate = (Property) o;
            return NamesExt.toExpandedString(candidate.getName());
        }else if(o instanceof PropertyType){
            final PropertyType candidate = (PropertyType) o;
            return NamesExt.toExpandedString(candidate.getName());
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
        if(o instanceof Fake){
            final Fake candidate = (Fake) o;
            str = candidate.name.tip().toString();
        }else if(o instanceof Property){
            final Property candidate = (Property) o;
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
    public boolean isDocument(Object o) {
        if(o instanceof Fake) o = ((Fake)o).value;
        return o instanceof Feature || o instanceof FeatureType ;
    }

    @Override
    public boolean isElement(final Object o) {
        return o instanceof Property || o instanceof PropertyType || o instanceof Feature || o instanceof Fake;
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
    public String getElementStringValue(Object o) {
        if(o instanceof Fake) o = ((Fake)o).value;

        if(o instanceof Property){
            final Property candidate = (Property) o;
            if(candidate instanceof Attribute && ((Attribute)candidate).getType().getMaximumOccurs()>1){
                final Collection values = ((Attribute)candidate).getValues();
                return (values.isEmpty())? EMPTY : values.iterator().next().toString();
            }else{
                final Object value = candidate.getValue();
                return (value==null)? EMPTY : value.toString();
            }
        }else if(o instanceof PropertyType){
            final PropertyType candidate = (PropertyType) o;
            return NamesExt.toExpandedString(candidate.getName());
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
    public Iterator getChildAxisIterator(Object o) throws UnsupportedAxisException {
        if(o instanceof Fake) o = ((Fake)o).value;

        if(o instanceof Feature){
            final Feature candidate = (Feature) o;
            return new PropIterator(candidate,false);
        }else if(o instanceof FeatureAssociation){
            final FeatureAssociation ct = (FeatureAssociation) o;
            final Collection<Feature> features = ct.getValues();
            return features.iterator();
        }else if(o instanceof FeatureType){
            final FeatureType ct = (FeatureType) o;
            return new PropTypeIterator(ct.getProperties(true).iterator(),false);
        }else if(o instanceof FeatureAssociationRole){
            final FeatureAssociationRole ct = (FeatureAssociationRole) o;
            try {
                return new PropTypeIterator(ct.getValueType().getProperties(true).iterator(),false);
            } catch(IllegalStateException ex) {
                //TODO : can happen when the feature type relations are uncomplete
                //may be normal or a bug
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
    public Iterator getAttributeAxisIterator(Object o) throws UnsupportedAxisException {
        if(o instanceof Fake) o = ((Fake)o).value;

        if(o instanceof Feature){
            final Feature att = (Feature) o;
            return new PropIterator(att, true);
        } else if(o instanceof PropertyType){
            final PropertyType type = (PropertyType) o;
            if(type instanceof FeatureAssociationRole){
                final FeatureAssociationRole ct = (FeatureAssociationRole) type;
                return new PropTypeIterator(ct.getValueType().getProperties(true).iterator(), true);
            }else{
                return JaxenConstants.EMPTY_ITERATOR;
            }
        } else if(o instanceof FeatureType){
            final FeatureType ct = (FeatureType) o;
            return new PropTypeIterator(ct.getProperties(true).iterator(), true);
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
        if(o instanceof Feature){
            return o;
        }else if(o instanceof FeatureType){
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

    private static class PropTypeIterator implements Iterator{

        private final Iterator ite;
        private final boolean attributes;
        private Object next = null;

        public PropTypeIterator(Iterator<? extends PropertyType> ite, boolean attributes) {
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

                GenericName name = null;
                if(candidate instanceof PropertyType){
                    name = ((PropertyType)candidate).getName();
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


    private static class PropIterator implements Iterator{

        private final Feature feature;
        private final Iterator ite;
        private Object next = null;

        public PropIterator(Feature f, boolean attributes) {
            this.feature = f;

            //we must unloop multi-attributes and multi-association
            final List props = new ArrayList<>();
            final Iterator<? extends PropertyType> pite = f.getType().getProperties(true).iterator();
            while(pite.hasNext() && next==null){
                final PropertyType candidate = pite.next();
                final GenericName gname = candidate.getName();
                final String name = candidate.getName().toString();

                final boolean isAtt = candidate.getName().tip().toString().startsWith("@");
                if((attributes && !isAtt) || (!attributes && isAtt)){
                    continue;
                }

//                if(candidate instanceof AttributeType && ((AttributeType)candidate).getMaximumOccurs()>1){
//                    for(Object o : ((Collection)values)){
//                        props.add(new Fake(gname,o));
//                    }
//                }else
                if(candidate instanceof FeatureAssociationRole && ((FeatureAssociationRole)candidate).getMaximumOccurs()>1){
                    final FeatureAssociation complete = (FeatureAssociation)feature.getProperty(name);
                    final Collection<? extends Feature> values = (Collection)complete.getValues();
                    for(Feature o : values){
                        props.add(new Fake(complete,gname,o));
                    }
                }else{
                    props.add(feature.getProperty(name));
                }
            }

            ite = props.iterator();
        }

        @Override
        public boolean hasNext() {
            return ite.hasNext();
        }

        @Override
        public Object next() {
            return ite.next();
        }

        @Override
        public void remove() {
        }

    }

    public static final class Fake{
        public final GenericName name;
        public final Feature value;
        public final FeatureAssociation complete;

        public Fake(FeatureAssociation complete, GenericName name, Feature value) {
            this.complete = complete;
            this.name = name;
            this.value = value;
        }

    }

}
