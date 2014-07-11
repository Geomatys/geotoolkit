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
import javax.xml.bind.annotation.XmlTransient;
import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.OnlineResource;


/**
 * Information about on-line sources from which the dataset, specification, or
 * community profile name and extended metadata elements can be obtained.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@XmlTransient
public class DefaultOnlineResource extends org.apache.sis.metadata.iso.citation.DefaultOnlineResource {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 5412370008274334799L;

    /**
     * The online resources for the <A HREF="http://www.iso.org/">International Organization for
     * Standardization</A>.
     *
     * @since 3.19
     */
    static final OnlineResource ISO;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        ISO = r = create("http://www.iso.org/");
        r.freeze();
    }

    /**
     * The online resources for the <A HREF="http://www.opengeospatial.org">Open Geospatial Consortium</A>.
     * "Open Geospatial consortium" is the new name for "OpenGIS consortium".
     *
     * @see #OPEN_GIS
     */
    static final OnlineResource OGC;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        OGC = r = create("http://www.opengeospatial.org/");
        r.freeze();
    }

    /**
     * The online resources for the <A HREF="http://www.opengis.org">OpenGIS consortium</A>.
     * "OpenGIS consortium" is the old name for "Open Geospatial consortium".
     *
     * @see #OGC
     */
    static final OnlineResource OPEN_GIS;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        OPEN_GIS = r = create("http://www.opengis.org");
        r.freeze();
    }

    /**
     * The online resources for the <A HREF="http://www.iho.int">International hydrographic organization</A>.
     */
    static final OnlineResource IHO;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        IHO = r = create("http://www.iho.int");
        r.freeze();
    }

    /**
     * The online resources for the
     * <A HREF="http://www.epsg.org">European Petroleum Survey Group</A>.
     */
    static final OnlineResource EPSG;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        EPSG = r = create("http://www.epsg.org");
        r.freeze();
    }

    /**
     * The online resources for the
     * <A HREF="http://www.unidata.ucar.edu/software/netcdf-java">NetCDF library</A>.
     *
     * @since 3.08
     */
    static final OnlineResource NETCDF;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        NETCDF = r = create("http://www.unidata.ucar.edu/software/netcdf-java");
        r.freeze();
    }

    /**
     * The online resources for the
     * <A HREF="http://www.remotesensing.org/geotiff/geotiff.html">GeoTIFF</A> group.
     */
    public static final OnlineResource GEOTIFF;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        GEOTIFF = r = create("http://www.remotesensing.org/geotiff");
        r.freeze();
    }

    /**
     * The online resources for the <A HREF="http://trac.osgeo.org/proj">Proj.4</A> project.
     *
     * @since 3.20
     */
    static final OnlineResource PROJ4;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        PROJ4 = r = create("http://trac.osgeo.org/proj");
        r.freeze();
    }

    /**
     * The online resources for <A HREF="http://www.esri.com">ESRI</A>.
     */
    static final OnlineResource ESRI;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        ESRI = r = create("http://www.esri.com");
        r.freeze();
    }

    /**
     * The online resources for <A HREF="http://www.oracle.com">Oracle</A>.
     */
    static final OnlineResource ORACLE;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        ORACLE = r = create("http://www.oracle.com");
        r.freeze();
    }

    /**
     * The online resources for <A HREF="http://postgis.refractions.net">PostGIS</A>.
     *
     * @since 2.4
     */
    static final OnlineResource POSTGIS;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        POSTGIS = r = create("http://postgis.refractions.net");
        r.freeze();
    }

    /**
     * The online resources for <A HREF="http://java.sun.com/">Sun Microsystems</A>.
     * This online resources point to the Java developer site.
     *
     * @since 2.2
     */
    static final OnlineResource SUN_MICROSYSTEMS;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        SUN_MICROSYSTEMS = r = create("http://java.sun.com");
        r.freeze();
    }

    /**
     * The online resources for the <A HREF="http://www.geotoolkit.org">Geotoolkit.org</A> project.
     */
    static final OnlineResource GEOTOOLKIT;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        GEOTOOLKIT = r = create("http://www.geotoolkit.org");
        r.freeze();
    }

    /**
     * The online resources for the <A HREF="http://www.geotools.org">GeoTools</A> project.
     */
    static final OnlineResource GEOTOOLS;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        GEOTOOLS = r = create("http://www.geotools.org");
        r.freeze();
    }

    /**
     * The download link for <A HREF="http://portal.opengis.org/files/?artifact_id=5316">Web Map
     * Service</A> specification. The download link may change in future Geotk versions in order
     * to point toward the latest specification.
     *
     * @since 2.2
     */
    static final OnlineResource WMS;
    static {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r;
        WMS = r = create("http://portal.opengis.org/files/?artifact_id=5316");
        r.setFunction(OnLineFunction.DOWNLOAD);
        r.freeze();
    }

    private static org.apache.sis.metadata.iso.citation.DefaultOnlineResource create(final String linkage) {
        final org.apache.sis.metadata.iso.citation.DefaultOnlineResource r =
                new org.apache.sis.metadata.iso.citation.DefaultOnlineResource();
        r.setLinkage(URI.create(linkage));
        r.setFunction(OnLineFunction.INFORMATION);
        return r;
    }

    /**
     * Creates an initially empty on line resource.
     */
    public DefaultOnlineResource() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultOnlineResource(final OnlineResource source) {
        super(source);
    }

    /**
     * Creates an on line resource initialized to the given URI.
     * This method is private for now since, if this constructor was public, some
     * users may expect a string argument to be for the description text instead.
     * Furthermore, a public method should not hide the {@code URISyntaxException}
     * and should not set a function.
     */
    private DefaultOnlineResource(final String linkage) {
        setLinkage(URI.create(linkage));
        setFunction(OnLineFunction.INFORMATION);
    }

    /**
     * Creates an on line resource initialized to the given URI.
     *
     * @param linkage The location for on-line access using a Uniform Resource Locator address,
     *        or {@code null} if none.
     */
    public DefaultOnlineResource(final URI linkage) {
        if (linkage != null) {
            setLinkage(linkage);
        }
    }
}
