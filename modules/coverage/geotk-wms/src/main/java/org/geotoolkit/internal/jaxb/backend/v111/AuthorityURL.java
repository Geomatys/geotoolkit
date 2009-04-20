package org.geotoolkit.internal.jaxb.backend.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
"onlineResource"
})
@XmlRootElement(name = "AuthorityURL")
public class AuthorityURL {

    @XmlElement(name = "OnlineResource", required = true)
    private OnlineResource onlineResource;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    private String name;

    /**
     * An empty constructor used by JAXB.
     */
    AuthorityURL() {
    }

    /**
     * An empty constructor used by JAXB.
     */
    public AuthorityURL(final String name, OnlineResource onlineResource) {
        this.name = name;
        this.onlineResource = onlineResource;
    }

    /**
     * Gets the value of the onlineResource property.
     * 
     */
    public OnlineResource getOnlineResource() {
        return onlineResource;
    }

   /**
    * Gets the value of the name property.
    */
    public String getName() {
        return name;
    }
}
