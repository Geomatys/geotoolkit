package org.geotoolkit.image.io.large;

import java.awt.image.Raster;

/**
 * Contain {@link java.awt.image.Raster} and different raster properties.
 *
 * @author Remi Marechal (Geomatys).
 */
class LargeRaster {
    private final int gridX;
    private final int gridY;
    private final long weight;
    private final Raster raster;

    /**
     * Object to wrap {@link java.awt.image.Raster} and different raster properties.
     *
     * @param gridX raster position in X direction.
     * @param gridY raster position in Y direction.
     * @param weight raster weight.
     * @param raster
     */
    LargeRaster(int gridX, int gridY, long weight, Raster raster) {
        this.gridX  = gridX;
        this.gridY  = gridY;
        this.weight = weight;
        this.raster = raster;
    }

    /**
     * Return stocked {@link java.awt.image.Raster} mosaic coordinate in X direction.
     *
     * @return stocked {@link java.awt.image.Raster} mosaic coordinate in X direction.
     */
    int getGridX() {
        return gridX;
    }

    /**
     * Return stocked {@link java.awt.image.Raster} mosaic coordinate in Y direction.
     *
     * @return stocked {@link java.awt.image.Raster} mosaic coordinate in Y direction.
     */
    int getGridY() {
        return gridY;
    }

    /**
     * Return stocked {@link java.awt.image.Raster}.
     *
     * @return stocked {@link java.awt.image.Raster}.
     */
    Raster getRaster() {
        return raster;
    }

    /**
     * Return stocked {@link java.awt.image.Raster} weight.
     *
     * @return stocked {@link java.awt.image.Raster} weight.
     */
    long getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LargeRaster)) return false;
        LargeRaster lr = (LargeRaster) obj;
        return (gridX == lr.getGridX() && gridY == lr.getGridY() && raster == lr.getRaster());
    }
}
