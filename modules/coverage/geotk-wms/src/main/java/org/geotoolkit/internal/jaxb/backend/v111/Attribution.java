package org.geotoolkit.internal.jaxb.backend.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "title",
    "onlineResource",
    "logoURL"
})
@XmlRootElement(name = "Attribution")
public class Attribution {

    @XmlElement(name = "Title")
    private String title;
    @XmlElement(name = "OnlineResource")
    private OnlineResource onlineResource;
    @XmlElement(name = "LogoURL")
    private LogoURL logoURL;

    /**
     * An empty constructor used by JAXB.
     */
    Attribution() {
    }
    
    /**
     * Build a new Attribution.
     */
    public Attribution(final String title, OnlineResource onlineResource, LogoURL logoURL) {
        this.title          = title;
        this.onlineResource = onlineResource;
        this.logoURL        = logoURL;
    }
    
    /**
     * Gets the value of the title property.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the value of the onlineResource property.
    */
    public OnlineResource getOnlineResource() {
        return onlineResource;
    }

    /**
     * Gets the value of the logoURL property.
     */
    public LogoURL getLogoURL() {
        return logoURL;
    }
}
