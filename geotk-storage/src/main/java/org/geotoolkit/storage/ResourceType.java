/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016-2018, Geomatys
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
 */
package org.geotoolkit.storage;

/**
 * A set of possible data types which could be handled by a {@link org.apache.sis.storage.DataStore}. The information can
 * be retrieved from the {@link org.geotoolkit.storage.FactoryMetadata}.
 *
 * @author Alexis Manin (Geomatys)
 * @author Benjamin Garcia (Geomatys)
 */
public enum ResourceType {
    /**
     * A discrete coverage made of quadrilateral cells.
     */
    GRID(true),

    /**
     * A {@linkplain #GRID} coverage where cells are regrouped in tiles, and different set of tiles are pre-computed for different resolutions.
     */
    PYRAMID(true),

    /**
     * Other coverage not define
     */
    COVERAGE(true),

    /**
     * {@link org.opengis.feature.Feature} data.
     */
    VECTOR(false),

    /**
     * Observation data retrieved from sensors.
     */
    SENSOR(false),

    /**
     * Descriptive information.
     */
    METADATA(false),

    /**
     * Unspecified type.
     */
    OTHER(false);

    private final boolean coverageType;

    private ResourceType(boolean coverageType) {
        this.coverageType = coverageType;
    }

    public boolean isCoverageType() {
        return coverageType;
    }

}
