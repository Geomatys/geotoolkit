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

import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.OnLineResource;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Information about on-line sources from which the dataset, specification, or
 * community profile name and extended metadata elements can be obtained.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@XmlType(propOrder={
    "linkage",
    "protocol",
    "applicationProfile",
    "name",
    "description",
    "function"
})
@XmlRootElement(name = "CI_OnlineResource")
public class DefaultOnLineResource extends MetadataEntity implements OnLineResource {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 5412370008274334799L;

    /**
     * The online resources for the <A HREF="http://www.opengeospatial.org">Open Geospatial Consortium</A>.
     * "Open Geospatial consortium" is the new name for "OpenGIS consortium".
     *
     * @see #OPEN_GIS
     */
    public static final OnLineResource OGC;
    static {
        final DefaultOnLineResource r;
        OGC = r = new DefaultOnLineResource("http://www.opengeospatial.org/");
        r.freeze();
    }

    /**
     * The online resources for the <A HREF="http://www.opengis.org">OpenGIS consortium</A>.
     * "OpenGIS consortium" is the old name for "Open Geospatial consortium".
     *
     * @see #OGC
     */
    public static final OnLineResource OPEN_GIS;
    static {
        final DefaultOnLineResource r;
        OPEN_GIS = r = new DefaultOnLineResource("http://www.opengis.org");
        r.freeze();
    }

    /**
     * The online resources for the
     * <A HREF="http://www.epsg.org">European Petroleum Survey Group</A>.
     */
    public static final OnLineResource EPSG;
    static {
        final DefaultOnLineResource r;
        EPSG = r = new DefaultOnLineResource("http://www.epsg.org");
        r.freeze();
    }

    /**
     * The online resources for the
     * <A HREF="http://www.remotesensing.org/geotiff/geotiff.html">GeoTIFF</A> group.
     */
    public static final OnLineResource GEOTIFF;
    static {
        final DefaultOnLineResource r;
        GEOTIFF = r = new DefaultOnLineResource("http://www.remotesensing.org/geotiff");
        r.freeze();
    }

    /**
     * The online resources for <A HREF="http://www.esri.com">ESRI</A>.
     */
    public static final OnLineResource ESRI;
    static {
        final DefaultOnLineResource r;
        ESRI = r = new DefaultOnLineResource("http://www.esri.com");
        r.freeze();
    }

    /**
     * The online resources for <A HREF="http://www.oracle.com">Oracle</A>.
     */
    public static final OnLineResource ORACLE;
    static {
        final DefaultOnLineResource r;
        ORACLE = r = new DefaultOnLineResource("http://www.oracle.com");
        r.freeze();
    }

    /**
     * The online resources for <A HREF="http://postgis.refractions.net">PostGIS</A>.
     *
     * @since 2.4
     */
    public static final OnLineResource POSTGIS;
    static {
        final DefaultOnLineResource r;
        POSTGIS = r = new DefaultOnLineResource("http://postgis.refractions.net");
        r.freeze();
    }

    /**
     * The online resources for <A HREF="http://java.sun.com/">Sun Microsystems</A>.
     * This online resources point to the Java developper site.
     *
     * @since 2.2
     */
    public static final OnLineResource SUN_MICROSYSTEMS;
    static {
        final DefaultOnLineResource r;
        SUN_MICROSYSTEMS = r = new DefaultOnLineResource("http://java.sun.com");
        r.freeze();
    }

    /**
     * The online resources for the <A HREF="http://www.geotoolkit.org">Geotoolkit</A> project.
     */
    public static final OnLineResource GEOTOOLKIT;
    static {
        final DefaultOnLineResource r;
        GEOTOOLKIT = r = new DefaultOnLineResource("http://www.geotoolkit.org");
        r.freeze();
    }

    /**
     * The online resources for the <A HREF="http://www.geotools.org">GeoTools</A> project.
     */
    public static final OnLineResource GEOTOOLS;
    static {
        final DefaultOnLineResource r;
        GEOTOOLS = r = new DefaultOnLineResource("http://www.geotools.org");
        r.freeze();
    }

    /**
     * The download link for <A HREF="http://portal.opengis.org/files/?artifact_id=5316">Web Map
     * Service</A> specification. The download link may change in future Geotoolkit versions in
     * order to point toward the latest specification.
     *
     * @since 2.2
     */
    public static final OnLineResource WMS;
    static {
        final DefaultOnLineResource r;
        WMS = r = new DefaultOnLineResource("http://portal.opengis.org/files/?artifact_id=5316");
        r.setFunction(OnLineFunction.DOWNLOAD);
        r.freeze();
    }

    /**
     * Name of an application profile that can be used with the online resource.
     */
    private String applicationProfile;

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
     * Name of the online resources.
     */
    private String name;

    /**
     * Creates an initially empty on line resource.
     */
    public DefaultOnLineResource() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultOnLineResource(final OnLineResource source) {
        super(source);
    }

    /**
     * Creates an on line resource initialized to the given URI.
     * This method is private for now since, if this constructor was public, some
     * users may expect a string argument to be for the description text instead.
     * Furthermore, a public method should not catch the {@link URISyntaxException}
     * and should not set a function.
     */
    private DefaultOnLineResource(final String linkage) {
        try {
            setLinkage(new URI(linkage));
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException(exception);
        }
        setFunction(OnLineFunction.INFORMATION);
    }

    /**
     * Creates an on line resource initialized to the given URI.
     *
     * @param linkage The location for on-line access using a Uniform Resource Locator address.
     */
    public DefaultOnLineResource(final URI linkage) {
        setLinkage(linkage);
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
        final URI linkage = this.linkage;
        return (linkage!=null) ? linkage.getScheme() : null;
    }
}
