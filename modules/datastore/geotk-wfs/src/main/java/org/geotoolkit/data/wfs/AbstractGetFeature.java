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

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.geotoolkit.sld.xml.XMLUtilities;
import org.opengis.filter.Filter;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractGetFeature extends AbstractRequest implements GetFeatureRequest{

    protected final String version;

    private String typeName = null;
    private Filter filter = null;

    protected AbstractGetFeature(String serverURL, String version){
        super(serverURL);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getTypeName() {
        return requestParameters.get("TYPENAME");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTypeName(String type) {
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
    public URL getURL() throws MalformedURLException {
        requestParameters.put("SERVICE",    "WFS");
        requestParameters.put("REQUEST",    "DescribeFeatureType");
        requestParameters.put("VERSION",    version);

        if(typeName != null){
            requestParameters.put("TYPENAME",typeName);
        }

        if(filter != null){
            final XMLUtilities util = new XMLUtilities();

            final StringWriter writer = new StringWriter();
            try {
                util.writeFilter(writer, filter, org.geotoolkit.sld.xml.Specification.Filter.V_1_1_0);
            } catch (JAXBException ex) {
                Logger.getLogger(AbstractGetFeature.class.getName()).log(Level.SEVERE, null, ex);
            }

            final String strFilter = writer.toString();

            requestParameters.put("FILTER",strFilter);
        }

        return super.getURL();
    }


}
