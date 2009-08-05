/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.metadata.iso.spatial;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.metadata.spatial.GeometricObjects;
import org.opengis.metadata.spatial.TopologyLevel;
import org.opengis.metadata.spatial.VectorSpatialRepresentation;


/**
 * Information about the vector spatial objects in the dataset.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@XmlType(name = "MD_VectorSpatialRepresentation", propOrder={
    "topologyLevel",
    "geometricObjects"
})
@XmlRootElement(name = "MD_VectorSpatialRepresentation")
public class DefaultVectorSpatialRepresentation extends AbstractSpatialRepresentation
        implements VectorSpatialRepresentation
{
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 5643234643524810592L;

    /**
     * Code which identifies the degree of complexity of the spatial relationships.
    */
    private TopologyLevel topologyLevel;

    /**
     * Information about the geometric objects used in the dataset.
     */
    private Collection<GeometricObjects> geometricObjects;

    /**
     * Constructs an initially empty vector spatial representation.
     */
    public DefaultVectorSpatialRepresentation() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultVectorSpatialRepresentation(final VectorSpatialRepresentation source) {
        super(source);
    }

    /**
     * Returns the code which identifies the degree of complexity of the spatial relationships.
     */
    @Override
    @XmlElement(name = "topologyLevel")
    public TopologyLevel getTopologyLevel() {
        return topologyLevel;
    }

    /**
     * Sets the code which identifies the degree of complexity of the spatial relationships.
     *
     * @param newValue The new topology level.
     */
    public synchronized void setTopologyLevel(final TopologyLevel newValue) {
        checkWritePermission();
        topologyLevel = newValue;
    }

    /**
     * Returns information about the geometric objects used in the dataset.
     */
    @Override
    @XmlElement(name = "geometricObjects")
    public synchronized Collection<GeometricObjects> getGeometricObjects() {
        return xmlOptional(geometricObjects = nonNullCollection(geometricObjects, GeometricObjects.class));
    }

    /**
     * Sets information about the geometric objects used in the dataset.
     *
     * @param newValues The new geometric objects.
     */
    public synchronized void setGeometricObjects(final Collection<? extends GeometricObjects> newValues) {
        geometricObjects = copyCollection(newValues, geometricObjects, GeometricObjects.class);
    }
}
