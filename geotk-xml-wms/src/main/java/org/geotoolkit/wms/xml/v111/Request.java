/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wms.xml.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractRequest;

/**
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getCapabilities",
    "getMap",
    "getFeatureInfo",
    "describeLayer",
    "getLegendGraphic",
    "getStyles",
    "putStyles"
})
@XmlRootElement(name = "Request")
public class Request implements AbstractRequest {

    @XmlElement(name = "GetCapabilities", required = true)
    private GetCapabilities getCapabilities;
    @XmlElement(name = "GetMap", required = true)
    private GetMap getMap;
    @XmlElement(name = "GetFeatureInfo")
    private GetFeatureInfo getFeatureInfo;
    @XmlElement(name = "DescribeLayer")
    private DescribeLayer describeLayer;
    @XmlElement(name = "GetLegendGraphic")
    private GetLegendGraphic getLegendGraphic;
    @XmlElement(name = "GetStyles")
    private GetStyles getStyles;
    @XmlElement(name = "PutStyles")
    private PutStyles putStyles;

    public Request() {

    }

    public Request(final Request that) {
        if (that != null) {
            if (that.getCapabilities != null) {
                this.getCapabilities  = new GetCapabilities(that.getCapabilities);
            }
            if (that.describeLayer != null) {
                this.describeLayer    = new DescribeLayer(that.describeLayer);
            }
            if (that.getFeatureInfo != null) {
                this.getFeatureInfo   = new GetFeatureInfo(that.getFeatureInfo);
            }
            if (that.getLegendGraphic != null) {
                this.getLegendGraphic = new GetLegendGraphic(that.getLegendGraphic);
            }
            if (that.getMap != null) {
                this.getMap           = new GetMap(that.getMap);
            }
            if (that.getStyles != null) {
                this.getStyles        = new GetStyles(that.getStyles);
            }
            if (that.putStyles != null) {
                this.putStyles        = new PutStyles(that.putStyles);
            }
        }
    }

    public Request(final GetCapabilities getCapabilities, final GetMap getMap, final GetFeatureInfo getFeatureInfo, final DescribeLayer describeLayer, final GetLegendGraphic getLegendGraphic,
            final GetStyles getStyles, final PutStyles putStyles) {
        this.describeLayer    = describeLayer;
        this.getCapabilities  = getCapabilities;
        this.getFeatureInfo   = getFeatureInfo;
        this.getLegendGraphic = getLegendGraphic;
        this.getMap           = getMap;
        this.getStyles        = getStyles;
        this.putStyles        = putStyles;
    }

    /**
     * Gets the value of the getCapabilities property.
     */
    @Override
    public GetCapabilities getGetCapabilities() {
        return getCapabilities;
    }

    /**
     * Gets the value of the getMap property.
     */
    @Override
    public GetMap getGetMap() {
        return getMap;
    }

    /**
     * Gets the value of the getFeatureInfo property.
     */
    @Override
    public GetFeatureInfo getGetFeatureInfo() {
        return getFeatureInfo;
    }


    /**
     * Gets the value of the describeLayer property.
     *
     */
    public DescribeLayer getDescribeLayer() {
        return describeLayer;
    }



    /**
     * Gets the value of the getLegendGraphic property.
     */
    public GetLegendGraphic getGetLegendGraphic() {
        return getLegendGraphic;
    }



    /**
     * Gets the value of the getStyles property.
     *
     */
    public GetStyles getGetStyles() {
        return getStyles;
    }



    /**
     * Gets the value of the putStyles property.
     */
    public PutStyles getPutStyles() {
        return putStyles;
    }

    /**
     * update all the dcp ur with the specified one.
     */
    @Override
    public void updateURL(final String url) {
        if (getCapabilities != null) {
            getCapabilities.updateURL(url);
        }
        if (getFeatureInfo != null) {
            getFeatureInfo.updateURL(url);
        }
        if (describeLayer != null) {
            describeLayer.updateURL(url);
        }
        if (getLegendGraphic != null) {
            getLegendGraphic.updateURL(url);
        }
        if (getStyles != null) {
            getStyles.updateURL(url);
        }
        if (getMap != null) {
            getMap.updateURL(url);
        }
        if (putStyles != null) {
            putStyles.updateURL(url);
        }
    }

    @Override
    public Request clone() {
        return new Request(this);
    }
}
