package org.geotoolkit.internal.jaxb.backend.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.internal.jaxb.backend.AbstractRequest;

/**
 * 
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
public class Request extends AbstractRequest {

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

    /**
     * Gets the value of the getCapabilities property.
     */
    public GetCapabilities getGetCapabilities() {
        return getCapabilities;
    }

    /**
     * Gets the value of the getMap property.
     */
    public GetMap getGetMap() {
        return getMap;
    }

    /**
     * Gets the value of the getFeatureInfo property.
     */
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
}