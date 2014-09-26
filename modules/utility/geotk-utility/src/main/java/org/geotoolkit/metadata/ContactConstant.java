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
import org.opengis.metadata.citation.Contact;
import org.opengis.metadata.citation.OnLineFunction;
import org.apache.sis.metadata.iso.citation.DefaultContact;
import org.apache.sis.metadata.iso.citation.DefaultOnlineResource;


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
final class ContactConstant {
    private ContactConstant() {
    }

    static DefaultOnlineResource resource(final String linkage) {
        final DefaultOnlineResource r = new DefaultOnlineResource();
        r.setLinkage(URI.create(linkage));
        r.setFunction(OnLineFunction.INFORMATION);
        return r;
    }

    /**
     * Contact informations for the <A HREF="http://www.iso.org/">International Organization for
     * Standardization</A>.
     *
     * @since 3.19
     */
    static final Contact ISO;
    static {
        final DefaultContact c = new DefaultContact(resource("http://www.iso.org/"));
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
        final DefaultContact c = new DefaultContact(resource("http://www.opengeospatial.org/"));
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
        final DefaultContact c = new DefaultContact(resource("http://www.opengis.org"));
        c.freeze();
        OPEN_GIS = c;
    }

    /**
     * Contact informations for the <A HREF="http://www.iho.int">International hydrographic organization</A>.
     */
    static final Contact IHO;
    static {
        final DefaultContact c = new DefaultContact(resource("http://www.iho.int"));
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
        final DefaultContact c = new DefaultContact(resource("http://www.epsg.org"));
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
        final DefaultContact c = new DefaultContact(resource("http://www.unidata.ucar.edu/software/netcdf-java"));
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
        final DefaultContact c = new DefaultContact(resource("http://www.remotesensing.org/geotiff"));
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
        final DefaultContact c = new DefaultContact(resource("http://trac.osgeo.org/proj"));
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
        final DefaultContact c = new DefaultContact(resource("http://www.esri.com"));
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
        final DefaultContact c = new DefaultContact(resource("http://www.oracle.com"));
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
        final DefaultContact c = new DefaultContact(resource("http://postgis.refractions.net"));
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
        final DefaultContact c = new DefaultContact(resource("http://java.sun.com"));
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
        final DefaultContact c = new DefaultContact(resource("http://www.geotoolkit.org"));
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
        final DefaultContact c = new DefaultContact(resource("http://www.geotools.org"));
        c.freeze();
        GEOTOOLS = c;
    }
}
