/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.dif.xml.v102;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 *
 *                 * Email moved to the end of list * Phone and Fax were merged into the new
 * Phone field
 *
 * | DIF 9 | ECHO 10 | UMM | DIF 10 | Notes | | ------------| -------------- |
 * ------------- | ------------ | -------------------------------------------- |
 * | Personnel | ContactPerson | ContactPerson | Contact_Group | Added | | | | >
 * Last_Name | > LastName | > LastName | > Name | Just map to last name | | >
 * Address | > Address | > Address | > Address | | | > Phone | > Phone | > Phone
 * | > Phone | Fax and Phon in DIF 9 merged with Phone type | | > Email | >
 * Email | > Email | > Email | |
 *
 *
 *
 * <p>
 * Classe Java pour ContactGroupType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette
 * classe.
 *
 * <pre>
 * &lt;complexType name="ContactGroupType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Address" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}AddressType" minOccurs="0"/>
 *         &lt;element name="Phone" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}PhoneType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uuid" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}UuidType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContactGroupType", propOrder = {
    "name",
    "address",
    "phone",
    "email"
})
public class ContactGroupType {

    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Address")
    protected AddressType address;
    @XmlElement(name = "Phone")
    protected List<PhoneType> phone;
    @XmlElement(name = "Email")
    protected List<String> email;
    @XmlAttribute(name = "uuid")
    protected String uuid;

    public ContactGroupType() {

    }

    public ContactGroupType(String name, AddressType address, PhoneType phone, String email) {
        this.name = name;
        this.address = address;
        this.phone = new ArrayList<>();
        this.phone.add(phone);
        this.email = new ArrayList<>();
        this.email.add(email);
    }

    /**
     * Obtient la valeur de la propriété name.
     *
     * @return possible object is {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété address.
     *
     * @return possible object is {@link AddressType }
     *
     */
    public AddressType getAddress() {
        return address;
    }

    /**
     * Définit la valeur de la propriété address.
     *
     * @param value allowed object is {@link AddressType }
     *
     */
    public void setAddress(AddressType value) {
        this.address = value;
    }

    /**
     * Gets the value of the phone property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the phone property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPhone().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PhoneType }
     *
     *
     */
    public List<PhoneType> getPhone() {
        if (phone == null) {
            phone = new ArrayList<PhoneType>();
        }
        return this.phone;
    }

    /**
     * Gets the value of the email property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the email property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEmail().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link String }
     *
     *
     */
    public List<String> getEmail() {
        if (email == null) {
            email = new ArrayList<String>();
        }
        return this.email;
    }

    /**
     * Obtient la valeur de la propriété uuid.
     *
     * @return possible object is {@link String }
     *
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Définit la valeur de la propriété uuid.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

}
