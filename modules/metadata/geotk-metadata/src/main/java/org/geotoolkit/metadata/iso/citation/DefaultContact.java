/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Address;
import org.opengis.metadata.citation.Contact;
import org.opengis.metadata.citation.Telephone;
import org.opengis.metadata.citation.OnlineResource;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Information required to enable contact with the responsible person and/or organization.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "CI_Contact_Type", propOrder={
    "phone",
    "address",
    "onlineResource",
    "hoursOfService",
    "contactInstructions"
})
@XmlRootElement(name = "CI_Contact")
public class DefaultContact extends MetadataEntity implements Contact {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 3283637180253117382L;

    /**
     * Contact informations for the <A HREF="http://www.iso.org/">International Organization for
     * Standardization</A>.
     *
     * @since 3.19
     */
    static final Contact ISO;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnlineResource.ISO);
        c.freeze();
        ISO = c;
    }

    /**
     * Contact informations for the <A HREF="http://www.opengeospatial.org">Open Geospatial consortium</A>.
     * "Open Geospatial consortium" is the new name for "OpenGIS consortium".
     *
     * @see DefaultOnlineResource#OGC
     */
    static final Contact OGC;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnlineResource.OGC);
        c.freeze();
        OGC = c;
    }

    /**
     * Contact informations for the <A HREF="http://www.opengis.org">OpenGIS consortium</A>.
     * "OpenGIS consortium" is the old name for "Open Geospatial consortium".
     *
     * @see DefaultOnlineResource#OPEN_GIS
     */
    static final Contact OPEN_GIS;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnlineResource.OPEN_GIS);
        c.freeze();
        OPEN_GIS = c;
    }

    /**
     * Contact informations for the <A HREF="http://www.iho.int">International hydrographic organization</A>.
     */
    static final Contact IHO;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnlineResource.IHO);
        c.freeze();
        IHO = c;
    }

    /**
     * Contact informations for the
     * <A HREF="http://www.epsg.org">European Petroleum Survey Group</A>.
     *
     * @see DefaultOnlineResource#EPSG
     */
    static final Contact EPSG;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnlineResource.EPSG);
        c.freeze();
        EPSG = c;
    }

    /**
     * Contact informations for the
     * <A HREF="http://www.unidata.ucar.edu/software/netcdf-java/">NetCDF</A> library.
     *
     * @see DefaultOnlineResource#NETCDF
     *
     * @since 3.08
     */
    static final Contact NETCDF;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnlineResource.NETCDF);
        c.freeze();
        NETCDF = c;
    }

    /**
     * Contact informations for the
     * <A HREF="http://www.remotesensing.org/geotiff/geotiff.html">GeoTIFF</A> group.
     *
     * @see DefaultOnlineResource#GEOTIFF
     */
    static final Contact GEOTIFF;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnlineResource.GEOTIFF);
        c.freeze();
        GEOTIFF = c;
    }

    /**
     * Contact informations for the <A HREF="http://trac.osgeo.org/proj">Proj.4</A> project.
     *
     * @see DefaultOnlineResource#PROJ4
     *
     * @since 3.20
     */
    static final Contact PROJ4;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnlineResource.PROJ4);
        c.freeze();
        PROJ4 = c;
    }

    /**
     * Contact informations for <A HREF="http://www.esri.com">ESRI</A>.
     *
     * @see DefaultOnlineResource#ESRI
     */
    static final Contact ESRI;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnlineResource.ESRI);
        c.freeze();
        ESRI = c;
    }

    /**
     * Contact informations for <A HREF="http://www.oracle.com">Oracle</A>.
     *
     * @see DefaultOnlineResource#ORACLE
     */
    static final Contact ORACLE;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnlineResource.ORACLE);
        c.freeze();
        ORACLE = c;
    }

    /**
     * Contact informations for <A HREF="http://postgis.refractions.net">PostGIS</A>.
     *
     * @see DefaultOnlineResource#POSTGIS
     *
     * @since 2.4
     */
    static final Contact POSTGIS;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnlineResource.POSTGIS);
        c.freeze();
        POSTGIS = c;
    }

    /**
     * Contact informations for <A HREF="http://www.sun.com/">Sun Microsystems</A>.
     *
     * @see DefaultOnlineResource#SUN_MICROSYSTEMS
     */
    static final Contact SUN_MICROSYSTEMS;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnlineResource.SUN_MICROSYSTEMS);
        c.freeze();
        SUN_MICROSYSTEMS = c;
    }

    /**
     * Contact informations for the <A HREF="http://www.geotoolkit.org">Geotoolkit.org</A> project.
     *
     * @see DefaultOnlineResource#GEOTOOLKIT
     */
    static final Contact GEOTOOLKIT;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnlineResource.GEOTOOLKIT);
        c.freeze();
        GEOTOOLKIT = c;
    }

    /**
     * Contact informations for the <A HREF="http://www.geotools.org">GeoTools</A> project.
     *
     * @see DefaultOnlineResource#GEOTOOLS
     */
    static final Contact GEOTOOLS;
    static {
        final DefaultContact c = new DefaultContact(DefaultOnlineResource.GEOTOOLS);
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
    private OnlineResource onlineResource;

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
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultContact(final Contact source) {
        super(source);
    }

    /**
     * Constructs a contact initialized to the specified online resource.
     *
     * @param resource The on-line information that can be used to contact the individual or
     *        organization, or {@code null} if none.
     */
    public DefaultContact(final OnlineResource resource) {
        if (resource != null) {
            setOnlineResource(resource);
        }
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultContact castOrCopy(final Contact object) {
        return (object == null) || (object instanceof DefaultContact)
                ? (DefaultContact) object : new DefaultContact(object);
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
    public synchronized OnlineResource getOnlineResource() {
        return onlineResource;
    }

    /**
     * Sets on-line information that can be used to contact the individual or organization.
     *
     * @param newValue The new online resource.
     */
    public synchronized void setOnlineResource(final OnlineResource newValue) {
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
