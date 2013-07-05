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
package org.geotoolkit.metadata.iso;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.FeatureTypeList;


/**
 * List of names of feature types with the same spatial representation (same as spatial attributes).
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
@ThreadSafe
@XmlType(propOrder={
    "spatialObject",
    "spatialSchemaName"
})
@XmlRootElement(name = "MD_FeatureTypeList")
public class DefaultFeatureTypeList extends MetadataEntity implements FeatureTypeList {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 5417914796207743856L;

    /**
     * Instance of a type defined in the spatial schema.
     */
    private String spatialObject;

    /**
     * Name of the spatial schema used.
     */
    private String spatialSchemaName;

    /**
     * Construct an initially empty feature type list.
     */
    public DefaultFeatureTypeList() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultFeatureTypeList(final FeatureTypeList source) {
        super(source);
    }

    /**
     * Creates a feature type list initialized to the given values.
     *
     * @param spatialObject     The instance of a type defined in the spatial schema.
     * @param spatialSchemaName The name of the spatial schema used.
     */
    public DefaultFeatureTypeList(final String spatialObject,
                                  final String spatialSchemaName)
    {
        setSpatialObject    (spatialObject    );
        setSpatialSchemaName(spatialSchemaName);
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
    public static DefaultFeatureTypeList castOrCopy(final FeatureTypeList object) {
        return (object == null) || (object instanceof DefaultFeatureTypeList)
                ? (DefaultFeatureTypeList) object : new DefaultFeatureTypeList(object);
    }

    /**
     * Instance of a type defined in the spatial schema.
     */
    @Override
    @XmlElement(name = "spatialObject", required = true)
    public synchronized String getSpatialObject() {
        return spatialObject;
    }

    /**
     * Sets the instance of a type defined in the spatial schema.
     *
     * @param newValue The new spatial object.
     */
    public synchronized void setSpatialObject(final String newValue) {
        checkWritePermission();
        spatialObject = newValue;
    }

    /**
     * Name of the spatial schema used.
     */
    @Override
    @XmlElement(name = "spatialSchemaName", required = true)
    public synchronized String getSpatialSchemaName() {
        return spatialSchemaName;
    }

    /**
     * Sets the name of the spatial schema used.
     *
     * @param newValue The new spatial schema.
     */
    public synchronized void setSpatialSchemaName(final String newValue) {
        checkWritePermission();
        spatialSchemaName = newValue;
    }
}
