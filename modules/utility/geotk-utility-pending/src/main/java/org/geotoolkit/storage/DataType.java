package org.geotoolkit.storage;

/**
 * A set of possible data types which could be handled by a {@link org.apache.sis.storage.DataStore}. The information can
 * be retrieved from the {@link org.geotoolkit.storage.FactoryMetadata}.
 *
 * @author Alexis Manin (Geomatys)
 * @author Benjamin Garcia (Geomatys)
 */
public enum DataType {
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

    private DataType(boolean coverageType) {
        this.coverageType = coverageType;
    }

    public boolean isCoverageType() {
        return coverageType;
    }

}
