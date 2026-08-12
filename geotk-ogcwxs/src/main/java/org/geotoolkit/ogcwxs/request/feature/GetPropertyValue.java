/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.request.feature;

import javax.xml.namespace.QName;
import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.opengis.filter.Filter;

/**
 * To retrieve the value of a single property, for feature instances matching a filter on a
 * single feature type. Only supported by WFS 2.0+.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetPropertyValue extends RequestParameters {

    private WFSVersion version;
    private QName typeName;
    private String valueReference;
    private Filter filter;
    private Integer maxFeatures;
    private String outputFormat;

    /**
     * The WFS version to request. Defaults to {@link WFSVersion#v202} if left {@code null}.
     *
     * @return the version
     */
    public WFSVersion getVersion() {
        return version;
    }

    public void setVersion(WFSVersion version) {
        this.version = version;
    }

    public GetPropertyValue version(WFSVersion version) {
        setVersion(version);
        return this;
    }

    /**
     * Feature type to query.
     *
     * @return the typeName
     */
    public QName getTypeName() {
        return typeName;
    }

    public void setTypeName(QName typeName) {
        this.typeName = typeName;
    }

    public GetPropertyValue typeName(QName typeName) {
        setTypeName(typeName);
        return this;
    }

    /**
     * XPath expression identifying the property whose value is requested.
     *
     * @return the valueReference
     */
    public String getValueReference() {
        return valueReference;
    }

    public void setValueReference(String valueReference) {
        this.valueReference = valueReference;
    }

    public GetPropertyValue valueReference(String valueReference) {
        setValueReference(valueReference);
        return this;
    }

    /**
     * Filter to apply, expressed in the version-independent {@link Filter} model. (optional)
     *
     * @return the filter
     */
    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public GetPropertyValue filter(Filter filter) {
        setFilter(filter);
        return this;
    }

    /**
     * Maximum number of features to consider. (optional)
     *
     * @return the maxFeatures
     */
    public Integer getMaxFeatures() {
        return maxFeatures;
    }

    public void setMaxFeatures(Integer maxFeatures) {
        this.maxFeatures = maxFeatures;
    }

    public GetPropertyValue maxFeatures(Integer maxFeatures) {
        setMaxFeatures(maxFeatures);
        return this;
    }

    /**
     * Requested output format. (optional, server-dependent default)
     *
     * @return the outputFormat
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public GetPropertyValue outputFormat(String outputFormat) {
        setOutputFormat(outputFormat);
        return this;
    }

}
