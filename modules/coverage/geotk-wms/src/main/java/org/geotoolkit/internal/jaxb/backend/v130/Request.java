package org.geotoolkit.internal.jaxb.backend.v130;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.internal.jaxb.backend.AbstractRequest;


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
 *         &lt;element ref="{http://www.opengis.net/wms}GetCapabilities"/>
 *         &lt;element ref="{http://www.opengis.net/wms}GetMap"/>
 *         &lt;element ref="{http://www.opengis.net/wms}GetFeatureInfo" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}_ExtendedOperation" maxOccurs="unbounded" minOccurs="0"/>
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
    "getCapabilities",
    "getMap",
    "getFeatureInfo",
    "extendedOperation"
})
@XmlRootElement(name = "Request")
public class Request extends AbstractRequest {

    @XmlElement(name = "GetCapabilities", required = true)
    private OperationType getCapabilities;
    @XmlElement(name = "GetMap", required = true)
    private OperationType getMap;
    @XmlElement(name = "GetFeatureInfo")
    private OperationType getFeatureInfo;
    @XmlElementRef(name = "_ExtendedOperation", namespace = "http://www.opengis.net/wms", type = JAXBElement.class)
    protected List<JAXBElement<OperationType>> extendedOperation = new ArrayList<JAXBElement<OperationType>>();

    /**
     * An empty constructor used by JAXB.
     */
     Request() {
     }

    /**
     * Build a new Request.
     */
    public Request(final OperationType getCapabilities, final OperationType getMap,
            final OperationType getFeatureInfo, JAXBElement<OperationType>... extendedOperations) {
        this.getCapabilities = getCapabilities;
        this.getFeatureInfo  = getFeatureInfo;
        this.getMap          = getMap;
        for (final JAXBElement<OperationType> element : extendedOperations) {
            this.extendedOperation.add(element);
        }
    }
    /**
     * Gets the value of the getCapabilities property.
     * 
     */
    public OperationType getGetCapabilities() {
        return getCapabilities;
    }

    /**
     * Gets the value of the getMap property.
     * 
     */
    public OperationType getGetMap() {
        return getMap;
    }

    /**
     * Gets the value of the getFeatureInfo property.
     * 
     */
    public OperationType getGetFeatureInfo() {
        return getFeatureInfo;
    }

    /**
     * Gets the value of the extendedOperation property.
     */
    public List<JAXBElement<OperationType>> getExtendedOperation() {
        return Collections.unmodifiableList(extendedOperation);
    }

}
