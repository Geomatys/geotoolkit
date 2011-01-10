

package org.geotoolkit.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.internal.jaxb.text.GenericNameAdapter;
import org.geotoolkit.util.Utilities;
import org.opengis.service.ServiceType;
import org.opengis.util.LocalName;


/**
 * <p>Java class for SV_ServiceType_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SV_ServiceType_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SV_ServiceType_Type")
public class ServiceTypeImpl implements ServiceType {

    @XmlJavaTypeAdapter(GenericNameAdapter.class)
    @XmlElement
    private LocalName serviceType;

    public ServiceTypeImpl() {

    }

    public ServiceTypeImpl(final LocalName serviceType) {
        this.serviceType = serviceType;
    }

    public LocalName getServiceType() {
        return serviceType;
    }

    public void setServiceType(final LocalName serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ServiceTypeImpl) {
            final ServiceTypeImpl that = (ServiceTypeImpl) object;
            return Utilities.equals(this.serviceType, that.serviceType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.serviceType != null ? this.serviceType.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ServiceType]\n");
        if ( serviceType != null) {
            sb.append("serviceType:").append(serviceType);
        }
        return sb.toString();
    }

}
