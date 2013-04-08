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

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.OnlineResource;

import org.geotoolkit.metadata.iso.MetadataEntity;


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
 */
@ThreadSafe
@XmlType(name = "CI_OnlineResource_Type", propOrder={
    "linkage",
    "protocol",
    "applicationProfile",
    "name",
    "description",
    "function"
})
@XmlRootElement(name = "CI_OnlineResource")
public class DefaultOnlineResource extends MetadataEntity implements OnlineResource {
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
        final DefaultOnlineResource r;
        ISO = r = new DefaultOnlineResource("http://www.iso.org/");
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
        final DefaultOnlineResource r;
        OGC = r = new DefaultOnlineResource("http://www.opengeospatial.org/");
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
        final DefaultOnlineResource r;
        OPEN_GIS = r = new DefaultOnlineResource("http://www.opengis.org");
        r.freeze();
    }

    /**
     * The online resources for the <A HREF="http://www.iho.int">International hydrographic organization</A>.
     */
    static final OnlineResource IHO;
    static {
        final DefaultOnlineResource r;
        IHO = r = new DefaultOnlineResource("http://www.iho.int");
        r.freeze();
    }

    /**
     * The online resources for the
     * <A HREF="http://www.epsg.org">European Petroleum Survey Group</A>.
     */
    static final OnlineResource EPSG;
    static {
        final DefaultOnlineResource r;
        EPSG = r = new DefaultOnlineResource("http://www.epsg.org");
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
        final DefaultOnlineResource r;
        NETCDF = r = new DefaultOnlineResource("http://www.unidata.ucar.edu/software/netcdf-java");
        r.freeze();
    }

    /**
     * The online resources for the
     * <A HREF="http://www.remotesensing.org/geotiff/geotiff.html">GeoTIFF</A> group.
     */
    public static final OnlineResource GEOTIFF;
    static {
        final DefaultOnlineResource r;
        GEOTIFF = r = new DefaultOnlineResource("http://www.remotesensing.org/geotiff");
        r.freeze();
    }

    /**
     * The online resources for the <A HREF="http://trac.osgeo.org/proj">Proj.4</A> project.
     *
     * @since 3.20
     */
    static final OnlineResource PROJ4;
    static {
        final DefaultOnlineResource r;
        PROJ4 = r = new DefaultOnlineResource("http://trac.osgeo.org/proj");
        r.freeze();
    }

    /**
     * The online resources for <A HREF="http://www.esri.com">ESRI</A>.
     */
    static final OnlineResource ESRI;
    static {
        final DefaultOnlineResource r;
        ESRI = r = new DefaultOnlineResource("http://www.esri.com");
        r.freeze();
    }

    /**
     * The online resources for <A HREF="http://www.oracle.com">Oracle</A>.
     */
    static final OnlineResource ORACLE;
    static {
        final DefaultOnlineResource r;
        ORACLE = r = new DefaultOnlineResource("http://www.oracle.com");
        r.freeze();
    }

    /**
     * The online resources for <A HREF="http://postgis.refractions.net">PostGIS</A>.
     *
     * @since 2.4
     */
    static final OnlineResource POSTGIS;
    static {
        final DefaultOnlineResource r;
        POSTGIS = r = new DefaultOnlineResource("http://postgis.refractions.net");
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
        final DefaultOnlineResource r;
        SUN_MICROSYSTEMS = r = new DefaultOnlineResource("http://java.sun.com");
        r.freeze();
    }

    /**
     * The online resources for the <A HREF="http://www.geotoolkit.org">Geotoolkit.org</A> project.
     */
    static final OnlineResource GEOTOOLKIT;
    static {
        final DefaultOnlineResource r;
        GEOTOOLKIT = r = new DefaultOnlineResource("http://www.geotoolkit.org");
        r.freeze();
    }

    /**
     * The online resources for the <A HREF="http://www.geotools.org">GeoTools</A> project.
     */
    static final OnlineResource GEOTOOLS;
    static {
        final DefaultOnlineResource r;
        GEOTOOLS = r = new DefaultOnlineResource("http://www.geotools.org");
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
        final DefaultOnlineResource r;
        WMS = r = new DefaultOnlineResource("http://portal.opengis.org/files/?artifact_id=5316");
        r.setFunction(OnLineFunction.DOWNLOAD);
        r.freeze();
    }

    /**
     * Name of an application profile that can be used with the online resource.
     */
    private String applicationProfile;

    /**
     * Name of the online resources.
     */
    private String name;

    /**
     * Detailed text description of what the online resource is/does.
     */
    private InternationalString description;

    /**
     * Code for function performed by the online resource.
     */
    private OnLineFunction function;

    /**
     * Location (address) for on-line access using a Uniform Resource Locator address or
     * similar addressing scheme such as http://www.statkart.no/isotc211.
     */
    private URI linkage;

    /**
     * The connection protocol to be used.
     */
    private String protocol;

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
    public static DefaultOnlineResource castOrCopy(final OnlineResource object) {
        return (object == null) || (object instanceof DefaultOnlineResource)
                ? (DefaultOnlineResource) object : new DefaultOnlineResource(object);
    }

    /**
     * Returns the name of an application profile that can be used with the online resource.
     * Returns {@code null} if none.
     */
    @Override
    @XmlElement(name = "applicationProfile")
    public synchronized String getApplicationProfile() {
        return applicationProfile;
    }

    /**
     * Sets the name of an application profile that can be used with the online resource.
     *
     * @param newValue The new application profile.
     */
    public synchronized void setApplicationProfile(final String newValue) {
        checkWritePermission();
        applicationProfile = newValue;
    }

    /**
     * Name of the online resource. Returns {@code null} if none.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "name")
    public synchronized String getName() {
        return name;
    }

    /**
     * Sets the name of the online resource.
     *
     * @param newValue The new name.
     *
     * @since 2.4
     */
    public synchronized void setName(final String newValue) {
        checkWritePermission();
        name = newValue;
    }

    /**
     * Returns the detailed text description of what the online resource is/does.
     * Returns {@code null} if none.
     */
    @Override
    @XmlElement(name = "description")
    public synchronized InternationalString getDescription() {
        return description;
    }

    /**
     * Sets the detailed text description of what the online resource is/does.
     *
     * @param newValue The new description.
     */
    public synchronized void setDescription(final InternationalString newValue) {
        checkWritePermission();
        description = newValue;
    }

    /**
     * Returns the code for function performed by the online resource.
     * Returns {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "function")
    public synchronized OnLineFunction getFunction() {
        return function;
    }

    /**
     * Sets the code for function performed by the online resource.
     *
     * @param newValue The new function.
     */
    public synchronized void setFunction(final OnLineFunction newValue) {
        checkWritePermission();
        function = newValue;
    }

    /**
     * Returns the location (address) for on-line access using a Uniform Resource Locator address or
     * similar addressing scheme such as http://www.statkart.no/isotc211.
     */
    @Override
    @XmlElement(name = "linkage", required = true)
    public synchronized URI getLinkage() {
        return linkage;
    }

    /**
     * Sets the location (address) for on-line access using a Uniform Resource Locator address or
     * similar addressing scheme such as http://www.statkart.no/isotc211.
     *
     * @param newValue The new linkage.
     */
    public synchronized void setLinkage(final URI newValue) {
        checkWritePermission();
        linkage = newValue;
    }

    /**
     * Returns the connection protocol to be used.
     * Returns {@code null} if none.
     */
    @Override
    @XmlElement(name = "protocol")
    public synchronized String getProtocol() {
        return protocol;
    }

    /**
     * Returns the connection protocol to be used.
     *
     * @param newValue The new protocol.
     *
     * @since 3.04
     */
    public synchronized void setProtocol(final String newValue) {
        checkWritePermission();
        protocol = newValue;
    }
}
