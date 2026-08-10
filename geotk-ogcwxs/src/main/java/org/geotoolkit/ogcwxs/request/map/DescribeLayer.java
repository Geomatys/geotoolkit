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
package org.geotoolkit.ogcwxs.request.map;

import java.util.List;
import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.geotoolkit.wms.xml.WMSVersion;

/**
 * To retrieve, for the given layers, a description of the underlying data feature types or coverages,
 * usable to build a WFS or WCS request.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DescribeLayer extends RequestParameters {

    private WMSVersion version;
    private List<String> layers;

    /**
     * The WMS version to request. Defaults to {@link WMSVersion#v130} if left {@code null}.
     *
     * @return the version
     */
    public WMSVersion getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public void setVersion(WMSVersion version) {
        this.version = version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public DescribeLayer version(WMSVersion version) {
        setVersion(version);
        return this;
    }

    /**
     * Layer(s) to describe. (required)
     *
     * @return the layers
     */
    public List<String> getLayers() {
        return layers;
    }

    /**
     * @param layers the layers to set
     * @see #getLayers()
     */
    public void setLayers(List<String> layers) {
        this.layers = layers;
    }

    /**
     * @param layers the layers to set
     * @see #getLayers()
     */
    public DescribeLayer layers(List<String> layers) {
        setLayers(layers);
        return this;
    }

}
