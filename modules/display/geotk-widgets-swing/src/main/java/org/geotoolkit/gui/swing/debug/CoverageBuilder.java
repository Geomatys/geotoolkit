
package org.geotoolkit.gui.swing.debug;

import java.awt.image.BandedSampleModel;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.CoverageFactoryFinder;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.util.NumberRange;
import org.opengis.geometry.Envelope;


/**
 *
 * @author johann sorel
 */
public class CoverageBuilder {

    public static GridCoverage2D createCoverage() throws IOException {
        final int width = 400;
        final int height = 400;
        final int numBands = 2;
        final float minimum = 0;
        final float maximum = 100;
        
        final SampleModel sm = new BandedSampleModel(DataBuffer.TYPE_FLOAT, width, height, numBands);
        final ColorModel cm = PaletteFactory.getDefault().getContinuousPalette("rainbow", minimum, maximum, DataBuffer.TYPE_FLOAT, numBands, 0).getColorModel();
        final WritableRaster data = cm.createCompatibleWritableRaster(width, height);
        
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                final double amplitude = ((double) y)/height * (maximum - minimum) + minimum;
                final double angle = ((double) x)/width * (2 * Math.PI);
                data.setSample(x, y, 0, amplitude * Math.cos(angle));
                data.setSample(x, y, 1, amplitude * Math.sin(angle));
            }
        }
        
        final GeneralEnvelope envelope = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        envelope.setRange(0, -20, 20);
        envelope.setRange(1, -20, 20);
        GridSampleDimension[] bands = new GridSampleDimension[numBands];
        final Category category1 = new Category("U", null, NumberRange.create(0, 255), NumberRange.create(minimum,maximum)).geophysics(true);
        final Category category2 = new Category("V", null, NumberRange.create(0, 255), NumberRange.create(minimum,maximum)).geophysics(true);
        bands[0] = new GridSampleDimension(null, new Category[]{category1}, null);
        bands[1] = new GridSampleDimension(null, new Category[]{category2}, null);
        
        GridCoverageFactory factory = CoverageFactoryFinder.getGridCoverageFactory(null);
        GridCoverage2D coverage = factory.create("Test", data, envelope, bands);
        return coverage;
    }
    
    
}
