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

import org.jaxen.ContextSupport;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.jaxen.UnresolvableException;
import org.jaxen.expr.DefaultNameStep;
import org.jaxen.expr.DefaultXPathFactory;
import org.jaxen.expr.PredicateSet;
import org.jaxen.expr.Step;
import org.jaxen.expr.iter.IterableAxis;
import org.jaxen.saxpath.Axis;

/**
 * The xpath as specified in OGC Feature is a little different about how to behave
 * when asking for a property without namespace.<br/>
 * In XPath a property name without namespace means it's the default namespace.<br/>
 * In Feature a property name without namespace means all properties which localPart
 * match this name.<br/>
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class FeatureXPathFactory extends DefaultXPathFactory{

    static final FeatureXPathFactory INSTANCE = new FeatureXPathFactory();

    @Override
    public Step createNameStep(final int axis, final String prefix, final String localName) throws JaxenException {
        final IterableAxis iter = getIterableAxis( axis );
        return new FeatureNameStep( iter, prefix,localName, createPredicateSet() );
    }

    private static final class FeatureNameStep extends DefaultNameStep{

        private FeatureNameStep(final IterableAxis axis, final String prefix,
                                final String localName, final PredicateSet predicateSet){
            super(axis,prefix,localName,predicateSet);
        }

        private boolean hasPrefix(){
            final String prefix = getPrefix();
            return (prefix != null && prefix.length() > 0);
        }

        private boolean hasNamespace(final String uri) {
            return (uri != null && uri.length() > 0);
        }

        /**
         * Checks whether the node matches this step.
         *
         * @param node  the node to check
         * @param contextSupport  the context support
         * @return true if matches
         * @throws JaxenException
         */
        @Override
        public boolean matches(final Object node, final ContextSupport contextSupport) throws JaxenException {

            final Navigator nav = contextSupport.getNavigator();

            final String nodeName;
            final String nodeUri;

            if (nav.isElement(node)) {
                nodeName = nav.getElementName(node);
                nodeUri = nav.getElementNamespaceUri(node);
            } else if (nav.isText(node)) {
                return false;
            } else if (nav.isAttribute(node)) {
                if (getAxis() != Axis.ATTRIBUTE) {
                    return false;
                }
                nodeName = nav.getAttributeName(node);
                nodeUri = nav.getAttributeNamespaceUri(node);

            } else if (nav.isDocument(node)) {
                return false;
            } else if (nav.isNamespace(node)) {
                if (getAxis() != Axis.NAMESPACE) {
                    // Only works for namespace::*
                    return false;
                }
                nodeName = nav.getNamespacePrefix(node);
                nodeUri = null;
            } else {
                return false;
            }

            String myUri = null;
            if (hasPrefix()) {
                myUri = contextSupport.translateNamespacePrefixToUri(getPrefix());
                if (myUri == null) {
                    throw new UnresolvableException("Cannot resolve namespace prefix '" + getPrefix() + "'");
                }
            } else if (isMatchesAnyName()) {
                return true;
            }

            // If we map to a non-empty namespace and the node does not
            // or vice-versa, fail-fast.
            if (myUri != null && hasNamespace(myUri) != hasNamespace(nodeUri)) {
                return false;
            }

            // To fail-fast, we check the equality of
            // local-names first.  Shorter strings compare
            // quicker.
            if (isMatchesAnyName() || nodeName.equals(getLocalName())) {
                return matchesNamespaceURIs(myUri, nodeUri);
            }

            return false;
        }

        @Override
        protected boolean matchesNamespaceURIs(final String uri1, final String uri2) {
            if(uri1 == null){
                return true;
            }
            return super.matchesNamespaceURIs(uri1, uri2);
        }

    }

}
