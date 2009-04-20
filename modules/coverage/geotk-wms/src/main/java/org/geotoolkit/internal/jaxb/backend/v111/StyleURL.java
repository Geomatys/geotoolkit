package org.geotoolkit.internal.jaxb.backend.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "format",
    "onlineResource"
})
@XmlRootElement(name = "StyleURL")
public class StyleURL {

    @XmlElement(name = "Format", required = true)
    private String format;
    @XmlElement(name = "OnlineResource", required = true)
    private OnlineResource onlineResource;

    /**
     * An empty constructor used by JAXB.
     */
     StyleURL() {
     }

    /**
     * Build a new Contact person primary object.
     */
    public StyleURL(final String format, final OnlineResource onlineResource) {
        this.format         = format;
        this.onlineResource = onlineResource;
    }
    
    /**
     * Gets the value of the format property.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Gets the value of the onlineResource property.
     * 
     */
    public OnlineResource getOnlineResource() {
        return onlineResource;
    }
}
