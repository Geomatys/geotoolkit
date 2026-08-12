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

import java.util.List;
import javax.xml.namespace.QName;
import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.opengis.filter.Filter;

/**
 * To retrieve feature instances matching a filter on a single feature type.
 *
 * <p>Only a single {@code typeName} is supported (unlike the WFS 2.0 {@code GetFeature}
 * request, which allows several queries in one call).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetFeature extends RequestParameters {

    private WFSVersion version;
    private QName typeName;
    private Filter filter;
    private Integer maxFeatures;
    private List<String> propertyNames;
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

    public GetFeature version(WFSVersion version) {
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

    public GetFeature typeName(QName typeName) {
        setTypeName(typeName);
        return this;
    }

    /**
     * Filter to apply, expressed in the version-independent {@link Filter} model. It is converted
     * to the version-specific OGC filter encoding before being sent. (optional)
     *
     * @return the filter
     */
    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public GetFeature filter(Filter filter) {
        setFilter(filter);
        return this;
    }

    /**
     * Maximum number of features to return. (optional)
     *
     * @return the maxFeatures
     */
    public Integer getMaxFeatures() {
        return maxFeatures;
    }

    public void setMaxFeatures(Integer maxFeatures) {
        this.maxFeatures = maxFeatures;
    }

    public GetFeature maxFeatures(Integer maxFeatures) {
        setMaxFeatures(maxFeatures);
        return this;
    }

    /**
     * Subset of properties to return. If left {@code null} or empty, all properties are returned.
     *
     * @return the propertyNames
     */
    public List<String> getPropertyNames() {
        return propertyNames;
    }

    public void setPropertyNames(List<String> propertyNames) {
        this.propertyNames = propertyNames;
    }

    public GetFeature propertyNames(List<String> propertyNames) {
        setPropertyNames(propertyNames);
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

    public GetFeature outputFormat(String outputFormat) {
        setOutputFormat(outputFormat);
        return this;
    }

}
