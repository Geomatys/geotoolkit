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

import javax.xml.bind.annotation.XmlRootElement;
import org.opengis.metadata.citation.Contact;
import org.opengis.metadata.citation.OnlineResource;


/**
 * Information required to enable contact with the responsible person and/or organization.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.21
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@XmlRootElement(name = "CI_Contact")
public class DefaultContact extends org.apache.sis.metadata.iso.citation.DefaultContact {
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
        final org.apache.sis.metadata.iso.citation.DefaultContact c =
                new org.apache.sis.metadata.iso.citation.DefaultContact(DefaultOnlineResource.ISO);
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
        final org.apache.sis.metadata.iso.citation.DefaultContact c =
                new org.apache.sis.metadata.iso.citation.DefaultContact(DefaultOnlineResource.OGC);
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
        final org.apache.sis.metadata.iso.citation.DefaultContact c =
                new org.apache.sis.metadata.iso.citation.DefaultContact(DefaultOnlineResource.OPEN_GIS);
        c.freeze();
        OPEN_GIS = c;
    }

    /**
     * Contact informations for the <A HREF="http://www.iho.int">International hydrographic organization</A>.
     */
    static final Contact IHO;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultContact c =
                new org.apache.sis.metadata.iso.citation.DefaultContact(DefaultOnlineResource.IHO);
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
        final org.apache.sis.metadata.iso.citation.DefaultContact c =
                new org.apache.sis.metadata.iso.citation.DefaultContact(DefaultOnlineResource.EPSG);
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
        final org.apache.sis.metadata.iso.citation.DefaultContact c =
                new org.apache.sis.metadata.iso.citation.DefaultContact(DefaultOnlineResource.NETCDF);
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
        final org.apache.sis.metadata.iso.citation.DefaultContact c =
                new org.apache.sis.metadata.iso.citation.DefaultContact(DefaultOnlineResource.GEOTIFF);
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
        final org.apache.sis.metadata.iso.citation.DefaultContact c =
                new org.apache.sis.metadata.iso.citation.DefaultContact(DefaultOnlineResource.PROJ4);
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
        final org.apache.sis.metadata.iso.citation.DefaultContact c =
                new org.apache.sis.metadata.iso.citation.DefaultContact(DefaultOnlineResource.ESRI);
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
        final org.apache.sis.metadata.iso.citation.DefaultContact c =
                new org.apache.sis.metadata.iso.citation.DefaultContact(DefaultOnlineResource.ORACLE);
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
        final org.apache.sis.metadata.iso.citation.DefaultContact c =
                new org.apache.sis.metadata.iso.citation.DefaultContact(DefaultOnlineResource.POSTGIS);
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
        final org.apache.sis.metadata.iso.citation.DefaultContact c =
                new org.apache.sis.metadata.iso.citation.DefaultContact(DefaultOnlineResource.SUN_MICROSYSTEMS);
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
        final org.apache.sis.metadata.iso.citation.DefaultContact c =
                new org.apache.sis.metadata.iso.citation.DefaultContact(DefaultOnlineResource.GEOTOOLKIT);
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
        final org.apache.sis.metadata.iso.citation.DefaultContact c =
                new org.apache.sis.metadata.iso.citation.DefaultContact(DefaultOnlineResource.GEOTOOLS);
        c.freeze();
        GEOTOOLS = c;
    }

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
}
