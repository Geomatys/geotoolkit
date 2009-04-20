package org.geotoolkit.internal.jaxb.backend.v130;

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
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wms}Title" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}OnlineResource" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}LogoURL" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
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
