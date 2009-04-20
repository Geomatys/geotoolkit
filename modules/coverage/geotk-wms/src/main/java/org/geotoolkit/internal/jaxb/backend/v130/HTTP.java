package org.geotoolkit.internal.jaxb.backend.v130;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.internal.jaxb.backend.AbstractHTTP;


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
 *         &lt;element ref="{http://www.opengis.net/wms}Get"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Post" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "get",
    "post"
})
@XmlRootElement(name = "HTTP")
public class HTTP extends AbstractHTTP {

    @XmlElement(name = "Get", required = true)
    private Get get;
    @XmlElement(name = "Post")
    private Post post;

    /**
     * An empty constructor used by JAXB.
     */
     HTTP() {
     }

    /**
     * Build a new HTTP object.
     */
    public HTTP(final Get get, final Post post) {
        this.get  = get;
        this.post = post;
    }
    
    
    /**
     * Gets the value of the get property.
     * 
     */
    public Get getGet() {
        return get;
    }

    /**
     * Gets the value of the post property.
     * 
     */
    public Post getPost() {
        return post;
    }
}
