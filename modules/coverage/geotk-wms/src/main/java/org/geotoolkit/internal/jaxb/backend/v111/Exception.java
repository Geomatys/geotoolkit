package org.geotoolkit.internal.jaxb.backend.v111;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "format"
})
@XmlRootElement(name = "Exception")
public class Exception {

    @XmlElement(name = "Format", required = true)
    private List<String> format = new ArrayList<String>();

    /**
     * An empty constructor used by JAXB.
     */
     Exception() {
     }

    /**
     * Build a new Contact person primary object.
     */
    public Exception(final String... formats) {
        for (final String element : formats) {
            this.format.add(element);
        }
    }
    /**
     * Gets the value of the format property.
     * 
     */
    public List<String> getFormat() {
        return Collections.unmodifiableList(format);
    }

}
