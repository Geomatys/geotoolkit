/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.sml.xml.v101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractAddress;
import org.geotoolkit.sml.xml.AbstractContactInfo;
import org.geotoolkit.sml.xml.AbstractPhone;
import org.geotoolkit.util.Utilities;


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
 *         &lt;element name="phone" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="voice" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="facsimile" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="address" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="deliveryPoint" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="administrativeArea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="postalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="electronicMailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}onlineResource" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="hoursOfService" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contactInstructions" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "phone",
    "address",
    "onlineResource",
    "hoursOfService",
    "contactInstructions"
})
@XmlRootElement(name = "contactInfo")
public class ContactInfo implements AbstractContactInfo {

    private ContactInfo.Phone phone;
    private ContactInfo.Address address;
    private List<OnlineResource> onlineResource;
    private String hoursOfService;
    private String contactInstructions;

    public ContactInfo() {

    }

    public ContactInfo(Phone phone, Address address) {
        this.address = address;
        this.phone   = phone;
    }

    /**
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link ContactInfo.Phone }
     *     
     */
    public ContactInfo.Phone getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactInfo.Phone }
     *     
     */
    public void setPhone(ContactInfo.Phone value) {
        this.phone = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link ContactInfo.Address }
     *     
     */
    public ContactInfo.Address getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactInfo.Address }
     *     
     */
    public void setAddress(ContactInfo.Address value) {
        this.address = value;
    }

    /**
     * Gets the value of the onlineResource property.
     */
    public List<OnlineResource> getOnlineResource() {
        if (onlineResource == null) {
            onlineResource = new ArrayList<OnlineResource>();
        }
        return this.onlineResource;
    }

    /**
     * sets the value of the onlineResource property.
     */
    public void setOnlineResource(List<OnlineResource> or) {
        this.onlineResource = or;
    }

    /**
     * Gets the value of the onlineResource property.
     */
    public void setOnlineResource(OnlineResource or) {
        if (onlineResource == null) {
            onlineResource = new ArrayList<OnlineResource>();
        }
        this.onlineResource.add(or);
    }

    /**
     * Gets the value of the hoursOfService property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHoursOfService() {
        return hoursOfService;
    }

    /**
     * Sets the value of the hoursOfService property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHoursOfService(String value) {
        this.hoursOfService = value;
    }

    /**
     * Gets the value of the contactInstructions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactInstructions() {
        return contactInstructions;
    }

    /**
     * Sets the value of the contactInstructions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactInstructions(String value) {
        this.contactInstructions = value;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ContactInfo]").append("\n");
        if (phone != null) {
            sb.append("phone: ").append(phone).append('\n');
        }
        if (address != null) {
            sb.append("address: ").append(address).append('\n');
        }
        if (hoursOfService != null) {
            sb.append("hoursOfService: ").append(hoursOfService).append('\n');
        }
        if (contactInstructions != null) {
            sb.append("contactInstructions: ").append(contactInstructions).append('\n');
        }
        if (onlineResource != null) {
            for (OnlineResource o : onlineResource) {
                sb.append("onlineResource: ").append(o).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof ContactInfo) {
            final ContactInfo that = (ContactInfo) object;
            return Utilities.equals(this.address,             that.address)             &&
                   Utilities.equals(this.contactInstructions, that.contactInstructions) &&
                   Utilities.equals(this.hoursOfService,      that.hoursOfService)      &&
                   Utilities.equals(this.onlineResource,      that.onlineResource)      &&
                   Utilities.equals(this.phone,               that.phone);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.phone != null ? this.phone.hashCode() : 0);
        hash = 23 * hash + (this.address != null ? this.address.hashCode() : 0);
        hash = 23 * hash + (this.onlineResource != null ? this.onlineResource.hashCode() : 0);
        hash = 23 * hash + (this.hoursOfService != null ? this.hoursOfService.hashCode() : 0);
        hash = 23 * hash + (this.contactInstructions != null ? this.contactInstructions.hashCode() : 0);
        return hash;
    }
    
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
     *         &lt;element name="deliveryPoint" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="administrativeArea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="postalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="electronicMailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "deliveryPoint",
        "city",
        "administrativeArea",
        "postalCode",
        "country",
        "electronicMailAddress"
    })
    public static class Address implements AbstractAddress {

        private List<String> deliveryPoint;
        private String city;
        private String administrativeArea;
        private String postalCode;
        private String country;
        private String electronicMailAddress;

        public Address() {

        }
        
        public Address(String deliveryPoint, String city, String administrativeArea, String postalCode, String country,
                String electronicMailAddress) {
            this.administrativeArea = administrativeArea;
            this.city = city;
            this.country = country;
            this.deliveryPoint = new ArrayList<String>();
            if (deliveryPoint != null) {
                this.deliveryPoint.add(deliveryPoint);
            }
            this.electronicMailAddress = electronicMailAddress;
            this.postalCode = postalCode;

        }
        
        /**
         * Gets the value of the deliveryPoint property.
         * 
         */
        public List<String> getDeliveryPoint() {
            if (deliveryPoint == null) {
                deliveryPoint = new ArrayList<String>();
            }
            return this.deliveryPoint;
        }

        /**
         * Gets the value of the city property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCity() {
            return city;
        }

        /**
         * Sets the value of the city property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCity(String value) {
            this.city = value;
        }

        /**
         * Gets the value of the administrativeArea property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAdministrativeArea() {
            return administrativeArea;
        }

        /**
         * Sets the value of the administrativeArea property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAdministrativeArea(String value) {
            this.administrativeArea = value;
        }

        /**
         * Gets the value of the postalCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPostalCode() {
            return postalCode;
        }

        /**
         * Sets the value of the postalCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPostalCode(String value) {
            this.postalCode = value;
        }

        /**
         * Gets the value of the country property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCountry() {
            return country;
        }

        /**
         * Sets the value of the country property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCountry(String value) {
            this.country = value;
        }

        /**
         * Gets the value of the electronicMailAddress property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getElectronicMailAddress() {
            return electronicMailAddress;
        }

        /**
         * Sets the value of the electronicMailAddress property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setElectronicMailAddress(String value) {
            this.electronicMailAddress = value;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[Address]").append("\n");
            if (city != null) {
                sb.append("city: ").append(city).append('\n');
            }
            if (administrativeArea != null) {
                sb.append("administrativeArea: ").append(administrativeArea).append('\n');
            }
            if (postalCode != null) {
                sb.append("postalCode: ").append(postalCode).append('\n');
            }
            if (country != null) {
                sb.append("country: ").append(country).append('\n');
            }
            if (electronicMailAddress != null) {
                sb.append("electronicMailAddress: ").append(electronicMailAddress).append('\n');
            }
            if (deliveryPoint != null) {
                for (String d : deliveryPoint) {
                    sb.append("deliveryPoint: ").append(d).append('\n');
                }
            }
            return sb.toString();
        }

        /**
         * Verify if this entry is identical to specified object.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }

            if (object instanceof Address) {
                final Address that = (Address) object;
                return Utilities.equals(this.administrativeArea, that.administrativeArea) &&
                        Utilities.equals(this.city, that.city) &&
                        Utilities.equals(this.country, that.country) &&
                        Utilities.equals(this.deliveryPoint, that.deliveryPoint) &&
                        Utilities.equals(this.postalCode, that.postalCode) &&
                        Utilities.equals(this.electronicMailAddress, that.electronicMailAddress);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 83 * hash + (this.deliveryPoint != null ? this.deliveryPoint.hashCode() : 0);
            hash = 83 * hash + (this.city != null ? this.city.hashCode() : 0);
            hash = 83 * hash + (this.administrativeArea != null ? this.administrativeArea.hashCode() : 0);
            hash = 83 * hash + (this.postalCode != null ? this.postalCode.hashCode() : 0);
            hash = 83 * hash + (this.country != null ? this.country.hashCode() : 0);
            hash = 83 * hash + (this.electronicMailAddress != null ? this.electronicMailAddress.hashCode() : 0);
            return hash;
        }

    }


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
     *         &lt;element name="voice" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="facsimile" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
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
        "voice",
        "facsimile"
    })
    public static class Phone implements AbstractPhone {

        private List<String> voice;
        private List<String> facsimile;

        public Phone() {
        }

        public Phone(List<String> voice, List<String> facsimile) {
            this.facsimile = facsimile;
            this.voice = voice;
        }

        public Phone(String voice, String facsimile) {
            this.facsimile = new ArrayList<String>();
            this.voice = new ArrayList<String>();
            if (facsimile != null) {
                this.facsimile.add(facsimile);
            }
            if (voice != null) {
                this.voice.add(voice);
            }

        }
        /**
         * Gets the value of the voice property.
        */
        public List<String> getVoice() {
            if (voice == null) {
                voice = new ArrayList<String>();
            }
            return this.voice;
        }

        /**
         * Gets the value of the facsimile property.
         */
        public List<String> getFacsimile() {
            if (facsimile == null) {
                facsimile = new ArrayList<String>();
            }
            return this.facsimile;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[Phone]").append("\n");
            if (voice != null) {
                for (String d : voice) {
                    sb.append("voice: ").append(d).append('\n');
                }
            }
            if (facsimile != null) {
                for (String d : facsimile) {
                    sb.append("facsimile: ").append(d).append('\n');
                }
            }
            return sb.toString();
        }

        /**
         * Verify if this entry is identical to specified object.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }

            if (object instanceof Phone) {
                final Phone that = (Phone) object;
                return Utilities.equals(this.facsimile, that.facsimile) &&
                       Utilities.equals(this.voice, that.voice);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + (this.voice != null ? this.voice.hashCode() : 0);
            hash = 37 * hash + (this.facsimile != null ? this.facsimile.hashCode() : 0);
            return hash;
        }
    }

}
