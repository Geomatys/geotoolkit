/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.sld.xml.XMLUtilities;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;


/**
 * Abstract Get feature request.
 * 
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractGetFeature extends AbstractRequest implements GetFeatureRequest{
    protected static final Logger LOGGER = Logging.getLogger(AbstractGetFeature.class);
    protected final String version;

    private QName typeName = null;
    private Filter filter = null;
    private Integer maxFeatures = null;
    private Name[] propertyNames = null;

    protected AbstractGetFeature(String serverURL, String version){
        super(serverURL);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public QName getTypeName() {
        return typeName;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTypeName(QName type) {
        this.typeName = type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Filter getFilter(){
        return filter;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setFilter(Filter filter){
        this.filter = filter;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Integer getMaxFeatures(){
        return maxFeatures;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setMaxFeatures(Integer max){
        maxFeatures = max;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Name[] getPropertyNames() {
        return propertyNames;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setPropertyNames(Name[] properties) {
        this.propertyNames = properties;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        requestParameters.put("SERVICE", "WFS");
        requestParameters.put("REQUEST", "GETFEATURE");
        requestParameters.put("VERSION", version);

        if(maxFeatures != null){
            requestParameters.put("MAXFEATURES", maxFeatures.toString());
        }

        if(typeName != null){
            final StringBuilder sbN = new StringBuilder();
            final StringBuilder sbNS = new StringBuilder();

            String prefix = typeName.getPrefix();
            if (prefix == null || prefix.isEmpty()) {
                prefix = "ut";
            }
            sbN.append(prefix).append(':').append(typeName.getLocalPart()).append(',');
            sbNS.append("xmlns(").append(prefix).append('=').append(typeName.getNamespaceURI()).append(')').append(',');

            if(sbN.length() > 0 && sbN.charAt(sbN.length()-1) == ','){
                sbN.deleteCharAt(sbN.length()-1);
            }

            if(sbNS.length() > 0 && sbNS.charAt(sbNS.length()-1) == ','){
                sbNS.deleteCharAt(sbNS.length()-1);
            }

            requestParameters.put("TYPENAME",sbN.toString());
            requestParameters.put("NAMESPACE",sbNS.toString());
        }

        if(filter != null && filter != Filter.INCLUDE){
            final XMLUtilities util = new XMLUtilities();
            final StringWriter writer = new StringWriter();

            try {
                util.writeFilter(writer, filter, org.geotoolkit.sld.xml.Specification.Filter.V_1_1_0);
            } catch (JAXBException ex) {
                LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }

            final String strFilter = writer.toString();
            try {
                writer.close();
            } catch (IOException ex) {
                LOGGER.log(Level.FINER, ex.getLocalizedMessage(), ex);
            }
            try {
                requestParameters.put("FILTER", URLEncoder.encode(strFilter, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                // Should not occur.
                LOGGER.log(Level.FINEST, ex.getLocalizedMessage(), ex);
            }
        }

        if(propertyNames != null){
            final StringBuilder sb = new StringBuilder();

            for(final Name prop : propertyNames){
                sb.append(prop).append(',');
            }

            if(sb.length() > 0 && sb.charAt(sb.length()-1) == ','){
                sb.deleteCharAt(sb.length()-1);
            }

            requestParameters.put("PROPERTYNAME", sb.toString());
        }

        return super.getURL();
    }
}
