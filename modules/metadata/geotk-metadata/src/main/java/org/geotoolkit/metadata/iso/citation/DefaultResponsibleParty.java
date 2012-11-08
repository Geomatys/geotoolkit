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

import java.net.URI;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.citation.Role;
import org.opengis.metadata.citation.Contact;
import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.util.InternationalString;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.util.SimpleInternationalString;


/**
 * Identification of, and means of communication with, person(s) and
 * organizations associated with the dataset.
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
@XmlType(name = "CI_ResponsibleParty_Type", propOrder={
    "individualName",
    "organisationName",
    "positionName",
    "contactInfo",
    "role"
})
@XmlRootElement(name = "CI_ResponsibleParty")
public class DefaultResponsibleParty extends MetadataEntity implements ResponsibleParty {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -2477962229031486552L;

    /**
     * Creates a responsible party metadata entry for OGC involvement.
     * The organisation name is automatically set to "Open Geospatial Consortium".
     *
     * @param  role     The OGC role (point of contact, owner, etc.) for a resource.
     * @param  resource The URI to the resource, or {@code null} if none.
     * @return Responsible party describing OGC involvement.
     *
     * @since 2.2
     */
    static ResponsibleParty OGC(final Role role, final OnlineResource resource) {
        final DefaultResponsibleParty ogc = new DefaultResponsibleParty(role);
        ogc.setOrganisationName(OGC.getOrganisationName());
        if (resource != null) {
            final DefaultContact contact = new DefaultContact(resource);
            contact.freeze();
            ogc.setContactInfo(contact);
        }
        ogc.freeze();
        return ogc;
    }

    /**
     * Creates a responsible party metadata entry for OGC involvement.
     * The organisation name is automatically set to "Open Geospatial Consortium".
     *
     * @param  role           The OGC role (point of contact, owner, etc.) for a resource.
     * @param  function       The OGC function (information, download, etc.) for a resource.
     * @param  onlineResource The URI to the resource.
     * @return Responsible party describing OGC involvement.
     */
    static ResponsibleParty OGC(final Role role,
                                final OnLineFunction function,
                                final URI onlineResource)
    {
        final DefaultOnlineResource resource = new DefaultOnlineResource(onlineResource);
        resource.setFunction(function);
        resource.freeze();
        return OGC(role, resource);
    }

    /**
     * Creates a responsible party metadata entry for OGC involvement.
     * The organisation name is automatically set to "Open Geospatial Consortium".
     *
     * @param  role           The OGC role (point of contact, owner, etc.) for a resource.
     * @param  function       The OGC function (information, download, etc.) for a resource.
     * @param  onlineResource The URI on the resource.
     * @return Responsible party describing OGC involvement.
     */
    static ResponsibleParty OGC(final Role role,
                                final OnLineFunction function,
                                final String onlineResource)
    {
        return OGC(role, function, URI.create(onlineResource));
    }

    /**
     * The <A HREF="http://www.iso.org/">International Organization for Standardization</A>
     * responsible party.
     *
     * @since 3.19
     */
    static final ResponsibleParty ISO;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.RESOURCE_PROVIDER);
        r.setOrganisationName(new SimpleInternationalString("International Organization for Standardization"));
        r.setContactInfo(DefaultContact.ISO);
        r.freeze();
        ISO = r;
    }

    /**
     * The <A HREF="http://www.opengeospatial.org">Open Geospatial consortium</A> responsible party.
     * "Open Geospatial consortium" is the new name for "OpenGIS consortium".
     *
     * @see DefaultContact#OGC
     */
    static final ResponsibleParty OGC;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.RESOURCE_PROVIDER);
        r.setOrganisationName(new SimpleInternationalString("Open Geospatial Consortium"));
        r.setContactInfo(DefaultContact.OGC);
        r.freeze();
        OGC = r;
    }

    /**
     * The <A HREF="http://www.opengis.org">OpenGIS consortium</A> responsible party.
     * "OpenGIS consortium" is the old name for "Open Geospatial consortium".
     *
     * @see DefaultContact#OPEN_GIS
     *
     * @deprecated To be replaced by a database (constants are becoming too numerous)
     */
    @Deprecated
    public static final ResponsibleParty OPEN_GIS;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.RESOURCE_PROVIDER);
        r.setOrganisationName(new SimpleInternationalString("OpenGIS consortium"));
        r.setContactInfo(DefaultContact.OPEN_GIS);
        r.freeze();
        OPEN_GIS = r;
    }

    /**
     * The <A HREF="http://www.epsg.org">European Petroleum Survey Group</A> responsible party.
     *
     * @see DefaultContact#EPSG
     *
     * @deprecated To be replaced by a database (constants are becoming too numerous)
     */
    @Deprecated
    public static final ResponsibleParty EPSG;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.PRINCIPAL_INVESTIGATOR);
        r.setOrganisationName(new SimpleInternationalString("European Petroleum Survey Group"));
        r.setContactInfo(DefaultContact.EPSG);
        r.freeze();
        EPSG = r;
    }

    /**
     * The <A HREF="http://www.unidata.ucar.edu/software/netcdf-java">NETCDF</A> responsible party.
     *
     * @see DefaultContact#NETCDF
     *
     * @since 3.08
     */
    static final ResponsibleParty NETCDF;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.PRINCIPAL_INVESTIGATOR);
        r.setOrganisationName(new SimpleInternationalString("NETCDF"));
        r.setContactInfo(DefaultContact.NETCDF);
        r.freeze();
        NETCDF = r;
    }

    /**
     * The <A HREF="http://www.remotesensing.org/geotiff/geotiff.html">GeoTIFF</A> responsible
     * party.
     *
     * @see DefaultContact#GEOTIFF
     */
    static final ResponsibleParty GEOTIFF;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.PRINCIPAL_INVESTIGATOR);
        r.setOrganisationName(new SimpleInternationalString("GeoTIFF"));
        r.setContactInfo(DefaultContact.GEOTIFF);
        r.freeze();
        GEOTIFF = r;
    }

    /**
     * The <A HREF="http://trac.osgeo.org/proj">Proj.4</A> responsible party.
     *
     * @see DefaultContact#PROJ4
     *
     * @since 3.20
     */
    static final ResponsibleParty PROJ4;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.PRINCIPAL_INVESTIGATOR);
        r.setOrganisationName(new SimpleInternationalString("PROJ4"));
        r.setContactInfo(DefaultContact.PROJ4);
        r.freeze();
        PROJ4 = r;
    }

    /**
     * The <A HREF="http://www.esri.com">ESRI</A> responsible party.
     *
     * @see DefaultContact#ESRI
     *
     * @deprecated To be replaced by a database (constants are becoming too numerous)
     */
    @Deprecated
    public static final ResponsibleParty ESRI;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.OWNER);
        r.setOrganisationName(new SimpleInternationalString("ESRI"));
        r.setContactInfo(DefaultContact.ESRI);
        r.freeze();
        ESRI = r;
    }

    /**
     * The <A HREF="http://www.oracle.com">Oracle</A> responsible party.
     *
     * @see DefaultContact#ORACLE
     */
    static final ResponsibleParty ORACLE;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.OWNER);
        r.setOrganisationName(new SimpleInternationalString("Oracle"));
        r.setContactInfo(DefaultContact.ORACLE);
        r.freeze();
        ORACLE = r;
    }

    /**
     * The <A HREF="http://postgis.refractions.net">PostGIS</A> responsible party.
     *
     * @see DefaultContact#POSTGIS
     *
     * @since 2.4
     */
    static final ResponsibleParty POSTGIS;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.PRINCIPAL_INVESTIGATOR);
        r.setOrganisationName(new SimpleInternationalString("PostGIS"));
        r.setContactInfo(DefaultContact.POSTGIS);
        r.freeze();
        POSTGIS = r;
    }

    /**
     * The <A HREF="http://www.sun.com/">Sun Microsystems</A> party.
     *
     * @see DefaultContact#SUN_MICROSYSTEMS
     *
     * @since 2.2
     */
    static final ResponsibleParty SUN_MICROSYSTEMS;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.PRINCIPAL_INVESTIGATOR);
        r.setOrganisationName(new SimpleInternationalString("Sun Microsystems"));
        r.setContactInfo(DefaultContact.SUN_MICROSYSTEMS);
        r.freeze();
        SUN_MICROSYSTEMS = r;
    }

    /**
     * The <A HREF="http://www.geotoolkit.org">Geotoolkit.org</A> project.
     *
     * @see DefaultContact#GEOTOOLKIT
     *
     * @deprecated To be replaced by a database (constants are becoming too numerous)
     */
    @Deprecated
    public static final ResponsibleParty GEOTOOLKIT;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.PRINCIPAL_INVESTIGATOR);
        r.setOrganisationName(new SimpleInternationalString("Geotoolkit.org"));
        r.setContactInfo(DefaultContact.GEOTOOLKIT);
        r.freeze();
        GEOTOOLKIT = r;
    }

    /**
     * The <A HREF="http://www.geotools.org">GeoTools</A> project.
     *
     * @see DefaultContact#GEOTOOLS
     */
    static final ResponsibleParty GEOTOOLS;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.PRINCIPAL_INVESTIGATOR);
        r.setOrganisationName(new SimpleInternationalString("GeoTools"));
        r.setContactInfo(DefaultContact.GEOTOOLS);
        r.freeze();
        GEOTOOLS = r;
    }

    /**
     * Name of the responsible person- surname, given name, title separated by a delimiter.
     */
    private String individualName;

    /**
     * Name of the responsible organization.
     */
    private InternationalString organisationName;

    /**
     * Role or position of the responsible person
     */
    private InternationalString positionName;

    /**
     * Address of the responsible party.
     */
    private Contact contactInfo;

    /**
     * Function performed by the responsible party.
     */
    private Role role;

    /**
     * Constructs an initially empty responsible party.
     */
    public DefaultResponsibleParty() {
    }

    /**
     * Constructs a new responsible party initialized to the values specified by the given object.
     * This constructor performs a shallow copy (i.e. each source attributes are reused without
     * copying them).
     *
     * @param source The metadata to copy, or {@code null} if none.
     * @since 2.2
     */
    public DefaultResponsibleParty(final ResponsibleParty source) {
        super(source);
    }

    /**
     * Constructs a responsibility party with the given role.
     *
     * @param role The function performed by the responsible party, or {@code null} if none.
     */
    public DefaultResponsibleParty(final Role role) {
        if (role != null) {
            setRole(role);
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
    public static DefaultResponsibleParty castOrCopy(final ResponsibleParty object) {
        return (object == null) || (object instanceof DefaultResponsibleParty)
                ? (DefaultResponsibleParty) object : new DefaultResponsibleParty(object);
    }

    /**
     * Returns the name of the responsible person- surname, given name, title separated by a delimiter.
     * Only one of {@code individualName}, {@link #getOrganisationName organisationName}
     * and {@link #getPositionName positionName} should be provided.
     */
    @Override
    @XmlElement(name = "individualName")
    public synchronized String getIndividualName() {
        return individualName;
    }

    /**
     * Sets the name of the responsible person- surname, given name, title separated by a delimiter.
     * Only one of {@code individualName}, {@link #getOrganisationName organisationName}
     * and {@link #getPositionName positionName} should be provided.
     *
     * @param newValue The new individual name.
     */
    public synchronized void setIndividualName(final String newValue) {
        checkWritePermission();
        individualName = newValue;
    }

    /**
     * Returns the name of the responsible organization. Only one of
     * {@link #getIndividualName individualName}, {@code organisationName}
     * and {@link #getPositionName positionName} should be provided.
     */
    @Override
    @XmlElement(name = "organisationName")
    public synchronized InternationalString getOrganisationName() {
        return organisationName;
    }

    /**
     * Sets the name of the responsible organization. Only one of
     * {@link #getIndividualName individualName}, {@code organisationName}
     * and {@link #getPositionName positionName} should be provided.
     *
     * @param newValue The new organisation name.
     */
    public synchronized void setOrganisationName(final InternationalString newValue) {
        checkWritePermission();
        organisationName = newValue;
    }

    /**
     * Returns the role or position of the responsible person Only one of
     * {@link #getIndividualName individualName}, {@link #getOrganisationName organisationName}
     * and {@code positionName} should be provided.
     */
    @Override
    @XmlElement(name = "positionName")
    public synchronized InternationalString getPositionName() {
        return positionName;
    }

    /**
     * set the role or position of the responsible person Only one of
     * {@link #getIndividualName individualName}, {@link #getOrganisationName organisationName}
     * and {@code positionName} should be provided.
     *
     * @param newValue The new position name.
     */
    public synchronized void setPositionName(final InternationalString newValue) {
        checkWritePermission();
        positionName = newValue;
    }

    /**
     * Returns the address of the responsible party.
     */
    @Override
    @XmlElement(name = "contactInfo")
    public synchronized Contact getContactInfo() {
        return contactInfo;
    }

    /**
     * Sets the address of the responsible party.
     *
     * @param newValue The new contact info.
     */
    public synchronized void setContactInfo(final Contact newValue) {
        checkWritePermission();
        contactInfo = newValue;
    }

    /**
     * Returns the function performed by the responsible party.
     */
    @Override
    @XmlElement(name = "role", required = true)
    public synchronized Role getRole() {
        return role;
    }

    /**
     * Sets the function performed by the responsible party.
     *
     * @param newValue The new role.
     */
    public synchronized void setRole(final Role newValue) {
        checkWritePermission();
        role = newValue;
    }
}
