/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.metadata.FeatureTypeList;
import org.opengis.metadata.SpatialAttributeSupplement;


/**
 * Spatial attributes in the application schema for the feature types.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.0
 *
 * @since 2.1
 * @module
 */
@XmlRootElement(name = "MD_SpatialAttributeSupplement")
public class DefaultSpatialAttributeSupplement extends MetadataEntity
        implements SpatialAttributeSupplement
{
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = 273337004694210422L;

    /**
     * Provides information about the list of feature types with the same spatial representation.
     */
    private Collection<FeatureTypeList> featureTypeList;

    /**
     * Construct an initially empty spatial attribute supplement.
     */
    public DefaultSpatialAttributeSupplement() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultSpatialAttributeSupplement(final SpatialAttributeSupplement source) {
        super(source);
    }

    /**
     * Creates a spatial attribute supplement initialized to the given values.
     *
     * @param featureTypeList Information about the list of feature types with
     *          the same spatial representation.
     */
    public DefaultSpatialAttributeSupplement(final Collection<? extends FeatureTypeList> featureTypeList) {
        setFeatureTypeList(featureTypeList);
    }

    /**
     * Provides information about the list of feature types with the same spatial representation.
     */
    @Override
    @XmlElement(name = "featureTypeList", required = true)
    public synchronized Collection<FeatureTypeList> getFeatureTypeList() {
        return featureTypeList = nonNullCollection(featureTypeList, FeatureTypeList.class);
    }

    /**
     * Sets information about the list of feature types with the same spatial representation.
     *
     * @param newValues The new feature type list.
     */
    public synchronized void setFeatureTypeList(
            final Collection<? extends FeatureTypeList> newValues)
    {
        featureTypeList = copyCollection(newValues, featureTypeList, FeatureTypeList.class);
    }
}
