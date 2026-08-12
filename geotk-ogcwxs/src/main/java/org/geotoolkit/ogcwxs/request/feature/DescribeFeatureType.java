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

/**
 * To retrieve, for a set of feature types, a description of their schema.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DescribeFeatureType extends RequestParameters {

    private WFSVersion version;
    private List<QName> typeNames;
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

    public DescribeFeatureType version(WFSVersion version) {
        setVersion(version);
        return this;
    }

    /**
     * Feature types to describe. If left {@code null} or empty, all feature types are described.
     *
     * @return the typeNames
     */
    public List<QName> getTypeNames() {
        return typeNames;
    }

    public void setTypeNames(List<QName> typeNames) {
        this.typeNames = typeNames;
    }

    public DescribeFeatureType typeNames(List<QName> typeNames) {
        setTypeNames(typeNames);
        return this;
    }

    /**
     * Requested schema format. (optional, server-dependent default)
     *
     * @return the outputFormat
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public DescribeFeatureType outputFormat(String outputFormat) {
        setOutputFormat(outputFormat);
        return this;
    }

}
