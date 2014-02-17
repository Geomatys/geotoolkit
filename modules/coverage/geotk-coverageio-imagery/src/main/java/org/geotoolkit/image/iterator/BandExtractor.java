package org.geotoolkit.image.iterator;

import java.awt.*;
import java.awt.image.RenderedImage;

/**
 * A row major iterator which browse only specified bands.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 30/05/13
 */
public class BandExtractor extends RowMajorIterator {

    protected final int[] roi;

    int position;
    /**
     * Create row-major rendered image iterator.
     *
     * @param renderedImage image which will be follow by iterator.
     * @param subArea       Rectangle which represent image sub area iteration.
     * @throws IllegalArgumentException
     *          if subArea don't intersect image.
     */
    public BandExtractor(RenderedImage renderedImage, Rectangle subArea, int[] bandsToKeep) {
        super(renderedImage, subArea);

        final int srcNumBands = super.getNumBands();
        for (int band : bandsToKeep) {
            if (band >= srcNumBands) {
                throw new IllegalArgumentException("Given bands does not match. Source image contains only "+
                        srcNumBands +", but band n." + band + " is requested.");
            }
        }
        roi = bandsToKeep;
        position = bandsToKeep.length;
    }

    @Override
    public boolean next() {
        if(++position >= roi.length) {
            band = rasterNumBand-1;
            if(super.next()) {
                position = 0;
            } else {
                return false;
            }
        }

        band = roi[position];
        return true;
    }

    @Override
    public void moveTo(int x, int y, int b) {
        position = b;
        super.moveTo(x, y, roi[b]);
    }

    @Override
    public int getNumBands() {
        return roi.length;
    }
}
