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

import javax.xml.namespace.QName;
import org.geotoolkit.client.Request;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;


/**
 * WFS GetFeature mutable request interface.
 * The request shall be correctly configured before calling the getURL method.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface GetFeatureRequest extends Request{

    /**
     * @return QName : requested type name, can be null
     * if not yet configured.
     */
    QName getTypeName();

    /**
     * @param type : requested type name, must not be null
     */
    void setTypeName(QName type);

    /**
     * @return Filter : the request filter, null for no filter
     */
    Filter getFilter();

    /**
     * @param filter : the request filter, null for no filter
     */
    void setFilter(Filter filter);

    /**
     * @return Integer : maximum features returned by this request,
     *  null for no limit.
     */
    Integer getMaxFeatures();

    /**
     * @param max : maximum features returned by this request,
     *  null for no limit.
     */
    void setMaxFeatures(Integer max);

    /**
     * @return String[] : array of requested properties,
     *  null if all properties, empty for only the id.
     */
    Name[] getPropertyNames();

    /**
     * @param properties : array of requested properties,
     *  null if all properties, empty for only the id.
     */
    void setPropertyNames(Name[] properties);

    /**
     * Return the output format to use for the response.
     * text/xml; subtype=gml/3.1.1 must be supported.
     * Other output formats are possible as well as long as their MIME
     * type is advertised in the capabilities document.
     *
     * @return The current outputFormat
     */
    String getOutputFormat();

    /**
     * Set the output format to use for the response.
     * text/xml; subtype=gml/3.1.1 must be supported.
     * Other output formats are possible as well as long as their MIME
     * type is advertised in the capabilities document.
     *
     * @param outputFormat The current outputFormat
     */
    void setOutputFormat(String outputFormat);
}
