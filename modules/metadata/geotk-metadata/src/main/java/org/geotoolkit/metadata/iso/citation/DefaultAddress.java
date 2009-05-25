/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.metadata.iso.citation;

import java.util.Collection;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Address;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Location of the responsible individual or organization.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@XmlType(propOrder={
    "deliveryPoints",
    "city",
    "administrativeArea",
    "postalCode",
    "country",
    "electronicMailAddresses"
})
@XmlRootElement(name = "CI_Address")
public class DefaultAddress extends MetadataEntity implements Address {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 2278687294173262546L;

    /**
     * State, province of the location.
     */
    private InternationalString administrativeArea;

    /**
     * The city of the location
     */
    private InternationalString city;

   /**
     * Country of the physical address.
     */
    private InternationalString country;

    /**
     * ZIP or other postal code.
     */
    private String postalCode;

    /**
     * Address line for the location (as described in ISO 11180, Annex A).
     */
    private Collection<String> deliveryPoints;

    /**
     * Address of the electronic mailbox of the responsible organization or individual.
     */
    private Collection<String> electronicMailAddresses;

    /**
     * Constructs an initially empty address.
     */
    public DefaultAddress() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultAddress(final Address source) {
        super(source);
    }

    /**
     * Return the state, province of the location.
     * Returns {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "administrativeArea")
    public InternationalString getAdministrativeArea() {
        return administrativeArea;
    }

    /**
     * Sets the state, province of the location.
     *
     * @param newValue The new administrative area.
     */
    public synchronized void setAdministrativeArea(final InternationalString newValue) {
        checkWritePermission();
        administrativeArea = newValue;
    }

    /**
     * Returns the city of the location.
     * Returns {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "city")
    public InternationalString getCity() {
        return city;
    }

    /**
     * Sets the city of the location.
     *
     * @param newValue The new city.
     */
    public synchronized void setCity(final InternationalString newValue) {
        checkWritePermission();
        city = newValue;
    }

    /**
     * Returns the country of the physical address.
     * Returns {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "country")
    public InternationalString getCountry() {
        return country;
    }

    /**
     * set the country of the physical address.
     *
     * @param newValue The new country.
     */
    public synchronized void setCountry(final InternationalString newValue) {
        checkWritePermission();
        country = newValue;
    }

    /**
     * Returns the address line for the location (as described in ISO 11180, Annex A).
     */
    @Override
    @XmlElement(name = "deliveryPoint")
    public synchronized Collection<String> getDeliveryPoints() {
        return xmlOptional(deliveryPoints = nonNullCollection(deliveryPoints, String.class));
    }

    /**
     * Sets the address line for the location (as described in ISO 11180, Annex A).
     *
     * @param newValues The new delivery points.
     */
    public synchronized void setDeliveryPoints(
            final Collection<? extends String> newValues)
    {
        deliveryPoints = copyCollection(newValues, deliveryPoints, String.class);
    }

    /**
     * Returns the address of the electronic mailbox of the responsible organization or individual.
     */
    @Override
    @XmlElement(name = "electronicMailAddress")
    public synchronized Collection<String> getElectronicMailAddresses() {
        return xmlOptional(electronicMailAddresses = nonNullCollection(electronicMailAddresses, String.class));
    }

    /**
     * Sets the address of the electronic mailbox of the responsible organization or individual.
     *
     * @param newValues The new electronic mail addresses.
     */
    public synchronized void setElectronicMailAddresses(
            final Collection<? extends String> newValues)
    {
        electronicMailAddresses = copyCollection(newValues, electronicMailAddresses, String.class);
    }

    /**
     * Returns ZIP or other postal code.
     * Returns {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "postalCode")
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets ZIP or other postal code.
     *
     * @param newValue The new postal code.
     */
    public synchronized void setPostalCode(final String newValue) {
        checkWritePermission();
        postalCode = newValue;
    }

    /**
     * Sets the {@code xmlMarshalling} flag to {@code true}, since the marshalling
     * process is going to be done. This method is automatically called by JAXB
     * when the marshalling begins.
     *
     * @param marshaller Not used in this implementation.
     */
    @SuppressWarnings("unused")
    private void beforeMarshal(Marshaller marshaller) {
        xmlMarshalling(true);
    }

    /**
     * Sets the {@code xmlMarshalling} flag to {@code false}, since the marshalling
     * process is finished. This method is automatically called by JAXB when the
     * marshalling ends.
     *
     * @param marshaller Not used in this implementation
     */
    @SuppressWarnings("unused")
    private void afterMarshal(Marshaller marshaller) {
        xmlMarshalling(false);
    }
}
