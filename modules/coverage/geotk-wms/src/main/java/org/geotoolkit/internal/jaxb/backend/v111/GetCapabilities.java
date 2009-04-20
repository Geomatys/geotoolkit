

package org.geotoolkit.internal.jaxb.backend.v111;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.internal.jaxb.backend.AbstractOperation;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "format",
    "dcpType"
})
@XmlRootElement(name = "GetCapabilities")
public class GetCapabilities extends AbstractOperation {

    @XmlElement(name = "Format", required = true)
    private List<Format> format;
    @XmlElement(name = "DCPType", required = true)
    private List<DCPType> dcpType;

    /**
     * Gets the value of the format property.
     * 
     */
    public List<Format> getFormat() {
        if (format == null) {
            format = new ArrayList<Format>();
        }
        return this.format;
    }

    /**
     * Gets the value of the dcpType property.
     * 
     */
    public List<DCPType> getDCPType() {
        if (dcpType == null) {
            dcpType = new ArrayList<DCPType>();
        }
        return this.dcpType;
    }

}
