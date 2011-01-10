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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jaxen.Context;
import org.jaxen.ContextSupport;

import org.jaxen.FunctionContext;
import org.jaxen.JaxenException;
import org.jaxen.JaxenHandler;
import org.jaxen.NamespaceContext;
import org.jaxen.Navigator;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.SimpleVariableContext;
import org.jaxen.VariableContext;
import org.jaxen.XPath;
import org.jaxen.XPathFunctionContext;
import org.jaxen.expr.XPathExpr;
import org.jaxen.function.BooleanFunction;
import org.jaxen.function.NumberFunction;
import org.jaxen.function.StringFunction;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.XPathReader;
import org.jaxen.saxpath.helpers.XPathReaderFactory;
import org.jaxen.util.SingletonList;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class JaxenFeatureXPath implements XPath {

    private static final JaxenFeatureNavigator NAVIGATOR = new JaxenFeatureNavigator();

    public static JaxenFeatureXPath create(String path) throws JaxenException{
        final Map<String,String> prefixes = new HashMap<String, String>();
        path = replaceNamespaces(path, prefixes);

        final NamespaceContext nsc = new SimpleNamespaceContext(prefixes);
        final JaxenFeatureXPath xpath = new JaxenFeatureXPath(path);
        xpath.setNamespaceContext(nsc);
        return xpath;
    }

    /** the parsed form of the XPath expression */
    private final XPathExpr xpath;

    /** the support information and function, namespace and variable contexts */
    private ContextSupport support;

    private JaxenFeatureXPath(final String xpathExpr) throws JaxenException {
        try {
            final XPathReader reader = XPathReaderFactory.createReader();
            final JaxenHandler handler = new JaxenHandler();
            handler.setXPathFactory(FeatureXPathFactory.INSTANCE);
            reader.setXPathHandler(handler);
            reader.parse(xpathExpr);
            this.xpath = handler.getXPathExpr();
        } catch (org.jaxen.saxpath.XPathSyntaxException e) {
            throw new org.jaxen.XPathSyntaxException(e);
        } catch (SAXPathException e) {
            throw new JaxenException(e);
        }
    }

    @Override
    public Object evaluate(final Object context) throws JaxenException{
        final List answer = selectNodes(context);
        if (answer != null && answer.size() == 1){
            return answer.get(0);
        }
        return answer;
    }

    /**
     * Replaces all {namespace} by prefixes and fill the xpath namespace context.
     */
    private static String replaceNamespaces(final String candidate, final Map<String,String> prefixes) throws JaxenException{

        int start = candidate.indexOf('{');
        if(start >= 0){
            //we have some namespaces in this expression
            final StringBuilder sb = new StringBuilder();
            int nsNum = 0;
            int end = 0;

            do{
                sb.append(candidate.substring((end==0)?0:end+1,start));
                end = candidate.indexOf('}', start);
                final String namespace = candidate.substring(start+1, end);

                String prefix = prefixes.get(namespace);
                if(prefix == null){
                    //add a new prefix
                    prefix = "ns"+Integer.toString(++nsNum);
                    prefixes.put(namespace, prefix);
                    //the namespace context expect the prefix to be the key
                    //prefix<->namespace are a 1 to1 relation, no danger to do this
                    prefixes.put(prefix, namespace);
                }
                sb.append(prefix).append(':');
                start = candidate.indexOf('{',end);
            }while( start >= 0 );

            //append what remains
            sb.append(candidate.substring(end+1));

            return sb.toString();
        }

        return candidate;
    }

    @Override
    public String valueOf(final Object node) throws JaxenException {
        return stringValueOf( node );
    }

    @Override
    public String stringValueOf(final Object node) throws JaxenException {
        final Context context = getContext( node );
        final Object result = selectSingleNodeForContext( context );
        if ( result == null ){
            return "";
        }
        return StringFunction.evaluate( result, context.getNavigator() );
    }

    @Override
    public boolean booleanValueOf(final Object node) throws JaxenException {
        final Context context = getContext( node );
        final List result = selectNodesForContext( context );
        if ( result == null ) return false;
        return BooleanFunction.evaluate( result, context.getNavigator() ).booleanValue();
    }

    @Override
    public Number numberValueOf(final Object node) throws JaxenException {
        final Context context = getContext( node );
        final Object result = selectSingleNodeForContext( context );
        return NumberFunction.evaluate( result, context.getNavigator() );
    }

    @Override
    public List selectNodes(final Object node) throws JaxenException {
        final Context context = getContext( node );
        return selectNodesForContext( context );
    }

    @Override
    public Object selectSingleNode(final Object node) throws JaxenException {
        final List results = selectNodes( node );
        if ( results.isEmpty() ){
            return null;
        }
        return results.get( 0 );
    }

    @Override
    public void addNamespace(final String prefix, final String uri) throws JaxenException {
        final NamespaceContext nsContext = getNamespaceContext();
        if ( nsContext instanceof SimpleNamespaceContext ){
            ((SimpleNamespaceContext)nsContext).addNamespace( prefix, uri );
            return;
        }
        throw new JaxenException("Operation not permitted while using a non-simple namespace context.");
    }

    @Override
    public void setNamespaceContext(final NamespaceContext namespaceContext) {
        getContextSupport().setNamespaceContext(namespaceContext);
    }

    @Override
    public void setFunctionContext(final FunctionContext functionContext) {
        getContextSupport().setFunctionContext(functionContext);
    }

    @Override
    public void setVariableContext(final VariableContext variableContext) {
        getContextSupport().setVariableContext(variableContext);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return getContextSupport().getNamespaceContext();
    }

    @Override
    public FunctionContext getFunctionContext() {
        return getContextSupport().getFunctionContext();
    }

    @Override
    public VariableContext getVariableContext() {
        return getContextSupport().getVariableContext();
    }

    @Override
    public Navigator getNavigator() {
        return NAVIGATOR;
    }


    /////////////////////////////////////////////////////////////////////////

    private Context getContext(final Object node) {
        if (node instanceof Context) {
            return (Context) node;
        }

        Context fullContext = new Context(getContextSupport());

        if (node instanceof List) {
            fullContext.setNodeSet((List) node);
        } else {
            List list = new SingletonList(node);
            fullContext.setNodeSet(list);
        }

        return fullContext;
    }

    private ContextSupport getContextSupport() {
        if ( support == null ){
            support = new ContextSupport(
                new SimpleNamespaceContext(),
                XPathFunctionContext.getInstance(),
                new SimpleVariableContext(),
                getNavigator()
            );
        }
        return support;
    }

    private List selectNodesForContext(final Context context) throws JaxenException {
        return this.xpath.asList(context);
    }

    private Object selectSingleNodeForContext(final Context context) throws JaxenException {
        final List results = selectNodesForContext(context);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }


}
