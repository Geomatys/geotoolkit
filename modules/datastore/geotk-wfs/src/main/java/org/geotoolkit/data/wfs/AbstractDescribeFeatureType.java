/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.data.wfs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.xml.namespace.QName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractDescribeFeatureType extends AbstractRequest implements DescribeFeatureTypeRequest{

    protected final String version;

    private List<QName> typeNames;

    protected AbstractDescribeFeatureType(String serverURL, String version){
        super(serverURL);
        this.version = version;
    }

    @Override
    public List<QName> getTypeNames() {
        return typeNames;
    }

    @Override
    public void setTypeNames(List<QName> typeNames) {
        this.typeNames = typeNames;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        requestParameters.put("SERVICE",    "WFS");
        requestParameters.put("REQUEST",    "DescribeFeatureType");
        requestParameters.put("VERSION",    version);

        if(typeNames != null){
            final StringBuilder sbN = new StringBuilder();
            final StringBuilder sbNS = new StringBuilder("{");
            for(QName q : typeNames){
                sbN.append(q.getPrefix()).append(':').append(q.getLocalPart()).append(',');
                sbNS.append("xmlns(").append(q.getPrefix()).append('=').append(q.getNamespaceURI()).append(')').append(',');
            }

            if(sbN.charAt(sbN.length()-1) == ','){
                sbN.deleteCharAt(sbN.length()-1);
            }

            if(sbNS.charAt(sbNS.length()-1) == ','){
                sbNS.deleteCharAt(sbNS.length()-1);
            }

            sbNS.append("}");

            requestParameters.put("TYPENAME",sbN.toString());
            requestParameters.put("NAMESPACE",sbNS.toString());
        }

        return super.getURL();
    }


}
