package org.geotoolkit.internal.jaxb.backend.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.internal.jaxb.backend.AbstractProtocol;

/**
 * <p>Java class for anonymous complex type.
 * 
 * 
 * @author Guilhem Legal 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
"onlineResource"
})
@XmlRootElement(name = "Post")
public class Post  extends AbstractProtocol {

    @XmlElement(name = "OnlineResource", required = true)
    private OnlineResource onlineResource;

    /**
     * An empty constructor used by JAXB.
     */
    Post() {
    }

    /**
     * Build a new Post object.
     */
    public Post(final OnlineResource onlineResource) {
        this.onlineResource = onlineResource;
    }

    /**
     * Gets the value of the onlineResource property.
     * 
     */
    public OnlineResource getOnlineResource() {
        return onlineResource;
    }
}
