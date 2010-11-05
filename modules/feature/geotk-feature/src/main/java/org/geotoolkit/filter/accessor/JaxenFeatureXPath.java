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

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.SimpleNamespaceContext;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class JaxenFeatureXPath extends BaseXPath {

    private static final JaxenFeatureNavigator NAVIGATOR = new JaxenFeatureNavigator();

    public static JaxenFeatureXPath create(String path) throws JaxenException{
        final Map<String,String> prefixes = new HashMap<String, String>();
        path = replaceNamespaces(path, prefixes);

        final NamespaceContext nsc = new SimpleNamespaceContext(prefixes);
        final JaxenFeatureXPath xpath = new JaxenFeatureXPath(path);
        xpath.setNamespaceContext(nsc);
        return xpath;
    }

    private JaxenFeatureXPath(String path) throws JaxenException {
        super(path,NAVIGATOR);
    }

    @Override
    public Object evaluate(Object context) throws JaxenException{
        final List answer = selectNodes(context);

        if (answer != null && answer.size() == 1){
            return answer.get(0);
        }
        return answer;
    }

    /**
     * Replaces all {namespace} by prefixes and fill the xpath namespace context.
     */
    private static String replaceNamespaces(String candidate, Map<String,String> prefixes) throws JaxenException{

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

}
