/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Address;
import org.opengis.metadata.citation.Contact;
import org.opengis.metadata.citation.Telephone;
import org.opengis.metadata.citation.OnLineResource;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Information required to enable contact with the responsible person and/or organization.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(propOrder={
    "phone",
    "address",
    "onlineResource",
    "hoursOfService",
    "contactInstructions"
})
@XmlRootElement(name = "CI_Contact")
public class DefaultContact extends MetadataEntity implements Contact {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 3283637180253117382L;

    /**
     * Contact informations for the <A HREF="http://www.opengeospatial.org">Open Geospatial consortium</A>.
     * "Open Geospatial consortium" is the new name for "OpenGIS consortium".
     *
     * @see DefaultOnLineResource#OGC
     */
    public static final Contact OGC;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnLineResource.OGC);
        c.freeze();
        OGC = c;
    }

    /**
     * Contact informations for the <A HREF="http://www.opengis.org">OpenGIS consortium</A>.
     * "OpenGIS consortium" is the old name for "Open Geospatial consortium".
     *
     * @see DefaultOnLineResource#OPEN_GIS
     */
    public static final Contact OPEN_GIS;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnLineResource.OPEN_GIS);
        c.freeze();
        OPEN_GIS = c;
    }

    /**
     * Contact informations for the
     * <A HREF="http://www.epsg.org">European Petroleum Survey Group</A>.
     *
     * @see DefaultOnLineResource#EPSG
     */
    public static final Contact EPSG;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnLineResource.EPSG);
        c.freeze();
        EPSG = c;
    }

    /**
     * Contact informations for the
     * <A HREF="http://www.remotesensing.org/geotiff/geotiff.html">GeoTIFF</A> group.
     *
     * @see DefaultOnLineResource#GEOTIFF
     */
    public static final Contact GEOTIFF;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnLineResource.GEOTIFF);
        c.freeze();
        GEOTIFF = c;
    }

    /**
     * Contact informations for <A HREF="http://www.esri.com">ESRI</A>.
     *
     * @see DefaultOnLineResource#ESRI
     */
    public static final Contact ESRI;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnLineResource.ESRI);
        c.freeze();
        ESRI = c;
    }

    /**
     * Contact informations for <A HREF="http://www.oracle.com">Oracle</A>.
     *
     * @see DefaultOnLineResource#ORACLE
     */
    public static final Contact ORACLE;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnLineResource.ORACLE);
        c.freeze();
        ORACLE = c;
    }

    /**
     * Contact informations for <A HREF="http://postgis.refractions.net">PostGIS</A>.
     *
     * @see DefaultOnLineResource#POSTGIS
     *
     * @since 2.4
     */
    public static final Contact POSTGIS;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnLineResource.POSTGIS);
        c.freeze();
        POSTGIS = c;
    }

    /**
     * Contact informations for <A HREF="http://www.sun.com/">Sun Microsystems</A>.
     *
     * @see DefaultOnLineResource#SUN_MICROSYSTEMS
     *
     * @since 2.2
     */
    public static final Contact SUN_MICROSYSTEMS;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnLineResource.SUN_MICROSYSTEMS);
        c.freeze();
        SUN_MICROSYSTEMS = c;
    }

    /**
     * Contact informations for the <A HREF="http://www.geotoolkit.org">Geotoolkit.org</A> project.
     *
     * @see DefaultOnLineResource#GEOTOOLKIT
     */
    public static final Contact GEOTOOLKIT;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnLineResource.GEOTOOLKIT);
        c.freeze();
        GEOTOOLKIT = c;
    }

    /**
     * Contact informations for the <A HREF="http://www.geotools.org">GeoTools</A> project.
     *
     * @see DefaultOnLineResource#GEOTOOLS
     */
    public static final Contact GEOTOOLS;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnLineResource.GEOTOOLS);
        c.freeze();
        GEOTOOLS = c;
    }

    /**
     * Supplemental instructions on how or when to contact the individual or organization.
     */
    private InternationalString contactInstructions;

    /**
     * Time period (including time zone) when individuals can contact the organization or
     * individual.
     */
    private InternationalString hoursOfService;

    /**
     * On-line information that can be used to contact the individual or organization.
     */
    private OnLineResource onlineResource;

    /**
     * Physical and email address at which the organization or individual may be contacted.
     */
    private Address address;

    /**
     * Telephone numbers at which the organization or individual may be contacted.
     */
    private Telephone phone;

    /**
     * Constructs an initially empty contact.
     */
    public DefaultContact() {
        // empty constructor. Use set methods and call freeze
        // before returning this instance to client code
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultContact(final Contact source) {
        super(source);
    }

    /**
     * Constructs a contact initialized to the specified online resource.
     *
     * @param resource The on-line information that can be used to contact the individual or organization.
     */
    public DefaultContact(final OnLineResource resource) {
        setOnlineResource(resource);
    }

    /**
     * Returns the physical and email address at which the organization or individual may be contacted.
     * Returns {@code null} if none.
     */
    @Override
    @XmlElement(name = "address")
    public synchronized Address getAddress() {
        return address;
    }

    /**
     * Sets the physical and email address at which the organization or individual may be contacted.
     *
     * @param newValue The new address.
     */
    public synchronized void setAddress(final Address newValue) {
        checkWritePermission();
        address = newValue;
    }

    /**
     * Returns supplemental instructions on how or when to contact the individual or organization.
     * Returns {@code null} if none.
     */
    @Override
    @XmlElement(name = "contactInstructions")
    public synchronized InternationalString getContactInstructions() {
        return contactInstructions;
    }

    /**
     * Sets supplemental instructions on how or when to contact the individual or organization.
     *
     * @param newValue The new contact instructions.
     */
    public synchronized void setContactInstructions(final InternationalString newValue) {
        checkWritePermission();
        contactInstructions = newValue;
    }

    /**
     * Return on-line information that can be used to contact the individual or organization.
     * Returns {@code null} if none.
     */
    @Override
    @XmlElement(name = "onlineResource")
    public synchronized OnLineResource getOnlineResource() {
        return onlineResource;
    }

    /**
     * Sets on-line information that can be used to contact the individual or organization.
     *
     * @param newValue The new online resource.
     */
    public synchronized void setOnlineResource(final OnLineResource newValue) {
        checkWritePermission();
        onlineResource = newValue;
    }

    /**
     * Returns telephone numbers at which the organization or individual may be contacted.
     * Returns {@code null} if none.
     */
    @Override
    @XmlElement(name = "phone")
    public synchronized Telephone getPhone() {
        return phone;
    }

    /**
     * Sets telephone numbers at which the organization or individual may be contacted.
     *
     * @param newValue The new telephone.
     */
    public synchronized void setPhone(final Telephone newValue) {
        checkWritePermission();
        phone = newValue;
    }

    /**
     * Returns time period (including time zone) when individuals can contact the organization or
     * individual. Returns {@code null} if none.
     */
    @Override
    @XmlElement(name = "hoursOfService")
    public synchronized InternationalString getHoursOfService() {
        return hoursOfService;
    }

    /**
     * Sets time period (including time zone) when individuals can contact the organization or
     * individual.
     *
     * @param newValue The new hours of service.
     */
    public synchronized void setHoursOfService(final InternationalString newValue) {
        checkWritePermission();
        hoursOfService = newValue;
    }
}
