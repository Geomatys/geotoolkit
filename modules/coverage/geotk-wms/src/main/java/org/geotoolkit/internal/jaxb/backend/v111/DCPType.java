package org.geotoolkit.internal.jaxb.backend.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.internal.jaxb.backend.AbstractDCP;


/**
 * <p>Java class for anonymous complex type.
 * 
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "http"
})
@XmlRootElement(name = "DCPType")
public class DCPType extends AbstractDCP {

    @XmlElement(name = "HTTP", required = true)
    private HTTP http;

    /**
     * An empty constructor used by JAXB.
     */
     DCPType() {
     }

    /**
     * Build a new DCP object.
     */
    public DCPType(final HTTP http) {
        this.http = http;
    }
    
    /**
     * Gets the value of the http property.
     * 
     */
    public HTTP getHTTP() {
        return http;
    }

}
