package org.geotoolkit.internal.jaxb.backend.v130;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.internal.jaxb.backend.AbstractOperation;


/**
 * 
 *         For each operation offered by the server, list the available output
 *         formats and the online resource.
 *       
 * 
 * <p>Java class for OperationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OperationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wms}Format" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.opengis.net/wms}DCPType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OperationType", propOrder = {
    "format",
    "dcpType"
})
public class OperationType extends AbstractOperation {

    @XmlElement(name = "Format", required = true)
    private List<String> format   = new ArrayList<String>();
    @XmlElement(name = "DCPType", required = true)
    private List<DCPType> dcpType = new ArrayList<DCPType>();

    /**
     * An empty constructor used by JAXB.
     */
     OperationType() {
     }

    /**
     * Build a new Contact person primary object.
     */
    public OperationType(final List<String> format, DCPType... dcpTypes) {
        this.format  = format;
        for (final DCPType element : dcpTypes) {
            this.dcpType.add(element);
        }
    }
    /**
     * Gets the value of the format property.
     * 
     */
    public List<String> getFormat() {
        return Collections.unmodifiableList(format);
    }

    /**
     * Gets the value of the dcpType property.
     */
    public List<DCPType> getDCPType() {
        return Collections.unmodifiableList(dcpType);
    }

}
