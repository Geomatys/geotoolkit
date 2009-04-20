package org.geotoolkit.internal.jaxb.backend.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for anonymous complex type.
 * 
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "Identifier")
public class Identifier {

    @XmlValue
    private String value;
    @XmlAttribute(required = true)
    private String authority;

    /**
     * An empty constructor used by JAXB.
     */
     Identifier() {
     }

    /**
     * Build a new Identifier object.
     */
    public Identifier(final String value, final String authority) {
        this.value     = value;
        this.authority = authority;
    }
    
    
    /**
     * Gets the value of the value property.
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the value of the authority property.
     */
    public String getAuthority() {
        return authority;
    }
}
