/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import org.opengis.metadata.FeatureTypeList;

import org.geotoolkit.lang.ThreadSafe;


/**
 * List of names of feature types with the same spatial representation (same as spatial attributes).
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
    "spatialObject",
    "spatialSchemaName"
})
@XmlRootElement(name = "MD_FeatureTypeList")
public class DefaultFeatureTypeList extends MetadataEntity implements FeatureTypeList {
    /**
     * Serial number for interoperability with different versions.
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
     * @param source The metadata to copy.
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
