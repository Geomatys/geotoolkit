package org.geotoolkit.image;

import java.awt.*;
import java.awt.image.*;
import java.util.Vector;

/**
 * Simple {@link RenderedImage} implementation that wrap {@code origin} {@code RenderedImage} and
 * override his {@link ColorModel}.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class RecolorRenderedImage implements RenderedImage {

    private RenderedImage origin;
    private ColorModel recolor;

    public RecolorRenderedImage(RenderedImage origin, ColorModel recolor) {
        this.origin = origin;
        this.recolor = recolor;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Vector<RenderedImage> getSources() {
        return origin.getSources();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getProperty(String name) {
        return origin.getProperty(name);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getPropertyNames() {
        return origin.getPropertyNames();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ColorModel getColorModel() {
        return recolor;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SampleModel getSampleModel() {
        return origin.getSampleModel();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getWidth() {
        return origin.getWidth();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getHeight() {
        return origin.getHeight();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getMinX() {
        return origin.getMinX();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getMinY() {
        return origin.getMinY();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getNumXTiles() {
        return origin.getNumXTiles();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getNumYTiles() {
        return origin.getNumYTiles();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getMinTileX() {
        return origin.getMinTileX();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getMinTileY() {
        return origin.getMinTileY();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getTileWidth() {
        return origin.getTileWidth();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getTileHeight() {
        return origin.getTileHeight();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getTileGridXOffset() {
        return origin.getTileGridXOffset();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getTileGridYOffset() {
        return origin.getTileGridYOffset();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Raster getTile(int tileX, int tileY) {
        return origin.getTile(tileX, tileY);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Raster getData() {
        return origin.getData();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Raster getData(Rectangle rect) {
        return origin.getData(rect);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public WritableRaster copyData(WritableRaster raster) {
        return origin.copyData(raster);
    }
}
