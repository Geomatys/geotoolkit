package org.geotoolkit.internal.jaxb.backend.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "contactPerson",
    "contactOrganization"
})
@XmlRootElement(name = "ContactPersonPrimary")
public class ContactPersonPrimary {

    @XmlElement(name = "ContactPerson", required = true)
    private String contactPerson;
    @XmlElement(name = "ContactOrganization", required = true)
    private String contactOrganization;

    /**
     * An empty constructor used by JAXB.
     */
     ContactPersonPrimary() {
     }

    /**
     * Build a new Contact person primary object.
     */
    public ContactPersonPrimary(final String contactPerson, final String contactOrganization) {
        this.contactOrganization = contactOrganization;
        this.contactPerson       = contactPerson;
    }
    
    /**
     * Gets the value of the contactPerson property.
     * 
     */
    public String getContactPerson() {
        return contactPerson;
    }

    /**
     * Gets the value of the contactOrganization property.
     */
    public String getContactOrganization() {
        return contactOrganization;
    }
}
