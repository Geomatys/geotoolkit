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
package org.geotoolkit.metadata;

import java.net.URI;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.citation.ResponsibleParty;
import org.apache.sis.metadata.iso.citation.DefaultOnlineResource;
import org.apache.sis.metadata.iso.citation.DefaultResponsibleParty;
import org.apache.sis.util.iso.SimpleInternationalString;


/**
 * Identification of, and means of communication with, person(s) and
 * organizations associated with the dataset.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.21
 *
 * @since 2.1
 * @module
 */
@SuppressWarnings("deprecation")
final class PartyConstant {
    private PartyConstant() {
    }

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
            final org.apache.sis.metadata.iso.citation.DefaultContact contact =
                    new org.apache.sis.metadata.iso.citation.DefaultContact(resource);
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
        r.setContactInfo(ContactConstant.ISO);
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
        r.setContactInfo(ContactConstant.OGC);
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
        r.setContactInfo(ContactConstant.OPEN_GIS);
        r.freeze();
        OPEN_GIS = r;
    }

    /**
     * The <A HREF="http://www.iho.int">International hydrographic organization</A>.
     */
    static final ResponsibleParty IHO;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.RESOURCE_PROVIDER);
        r.setOrganisationName(new SimpleInternationalString("International hydrographic organization"));
        r.setContactInfo(ContactConstant.IHO);
        r.freeze();
        IHO = r;
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
        r.setContactInfo(ContactConstant.EPSG);
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
        r.setContactInfo(ContactConstant.NETCDF);
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
        r.setContactInfo(ContactConstant.GEOTIFF);
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
        r.setContactInfo(ContactConstant.PROJ4);
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
        r.setContactInfo(ContactConstant.ESRI);
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
        r.setContactInfo(ContactConstant.ORACLE);
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
        r.setContactInfo(ContactConstant.POSTGIS);
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
        r.setContactInfo(ContactConstant.SUN_MICROSYSTEMS);
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
        r.setContactInfo(ContactConstant.GEOTOOLKIT);
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
        r.setContactInfo(ContactConstant.GEOTOOLS);
        r.freeze();
        GEOTOOLS = r;
    }
}
