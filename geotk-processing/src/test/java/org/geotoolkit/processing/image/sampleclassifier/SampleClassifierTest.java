package org.geotoolkit.processing.image.sampleclassifier;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import org.apache.sis.image.PlanarImage;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.process.ProcessException;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;

import static org.junit.Assert.*;


/**
 * TODO : add test for multi-tiled images
 * @author Alexis Manin (Geomatys)
 */
public class SampleClassifierTest {

    @Test
    public void classify() throws ProcessException {
        final double[] samples = {
             -1,    0.2,    0.4,    1.3,
            1.7,    1.9,      2,    1.3,
              4,    2.2,    1.1,    1.3,
             12,     13,    2.5,    1.3
        };
        final int[] expectedClassif = {
            127,      0,   0,    1,
              1,      1,   2,    1,
            127,      2,   1,    1,
            127,    127,   2,    1
        };

        final RenderedImage source = createGrayScale(4, 4, samples);

        final SampleClassifier classifier = new SampleClassifier();
        classifier.setImage(source);
        classifier.setFillValue((byte)127);
        classifier.addInterval(0, 1, (byte) 0);
        classifier.addInterval(1, 2, (byte) 1);
        classifier.addInterval(2, 3, (byte) 2);

        executeAndCheck(classifier, expectedClassif);
    }

    @Test
    public void classifyWithHoles() throws ProcessException {
        final double[] samples = {
            2, 0, 0, 0, 4, 0, 0,
            3, 2, 2, 0, 1, 1, 5,
            7, 7, 7, 8, 8, 8, 2,
            9, 9, 9, 9, 9, 9, 9,
            7, 6, 5, 4, 3, 2, 1
        };
        final int[] expectedClassif = {
            1, 3, 3, 3, 1, 3, 3,
            1, 1, 1, 3, 9, 9, 1,
            9, 9, 9, 2, 2, 2, 1,
            9, 9, 9, 9, 9, 9, 9,
            9, 1, 1, 1, 1, 1, 9
        };

        final RenderedImage source = createGrayScale(7, 5, samples);

        final SampleClassifier classifier = new SampleClassifier();
        classifier.setImage(source);
        classifier.setFillValue((byte)9);
        classifier.addInterval(2, 7, (byte) 1);
        classifier.addInterval(8, 9, (byte) 2);
        classifier.addInterval(0, 1, (byte) 3);

        executeAndCheck(classifier, expectedClassif);
    }

    @Test
    public void classifyTiledImage() throws ProcessException {
        final BufferedImage tile00 = createGrayScale(3, 3, new double[]{
            0, 9, 2,
            1, 2, 7,
            0, 4, 2
        });
        final BufferedImage tile01 = createGrayScale(3, 3, new double[]{
            0, 0, 0,
            1, 2, 1,
            0, 4, 4
        });
        final BufferedImage tile02 = createGrayScale(3, 3, new double[]{
            0, 9, 2,
            1, 3, 7,
            0, 4, 2
        });
        final BufferedImage tile10 = createGrayScale(3, 3, new double[]{
            2, 1, 4,
            3, 4, 9,
            2, 6, 4
        });
        final BufferedImage tile11 = createGrayScale(3, 3, new double[]{
            0, 9, 2,
            1, 2, 7,
            0, 4, 2
        });
        final BufferedImage tile12 = createGrayScale(3, 3, new double[]{
            9, 8, 1,
            0, 1, 6,
            9, 3, 1
        });
        final BufferedImage tile20 = createGrayScale(3, 3, new double[]{
            2, 1, 4,
            3, 4, 9,
            2, 6, 4
        });
        final BufferedImage tile21 = createGrayScale(3, 3, new double[]{
            0, 9, 2,
            1, 2, 7,
            0, 4, 2
        });
        final BufferedImage tile22 = createGrayScale(3, 3, new double[]{
            9, 8, 1,
            0, 1, 6,
            9, 3, 1
        });

        final int[] expectedClassif = new int[] {
            1, 9, 2, 1, 1, 1, 1, 9, 2,
            1, 2, 9, 1, 2, 1, 1, 2, 9,
            1, 2, 2, 1, 2, 2, 1, 2, 2,
            2, 1, 2, 1, 9, 2, 9, 9, 1,
            2, 2, 9, 1, 2, 9, 1, 1, 3,
            2, 3, 2, 1, 2, 2, 9, 2, 1,
            2, 1, 2, 1, 9, 2, 9, 9, 1,
            2, 2, 9, 1, 2, 9, 1, 1, 3,
            2, 3, 2, 1, 2, 2, 9, 2, 1
        };

        final RenderedImage[] tiles = {
            tile00, tile01, tile02,
            tile10, tile11, tile12,
            tile20, tile21, tile22
        };
        final RenderedImage source = new PlanarImage() {
            @Override
            public Raster getTile(int tileX, int tileY) {
                final int idx = tileY*getNumXTiles() + tileX;
                return tiles[idx].getTile(0, 0);
            }

            @Override
            public int getTileHeight() {
                return 3;
            }

            @Override
            public int getTileWidth() {
                return 3;
            }

            @Override
            public int getNumYTiles() {
                return 3;
            }

            @Override
            public int getNumXTiles() {
                return 3;
            }

            @Override
            public int getWidth() {
                return 9;
            }

            @Override
            public int getHeight() {
                return 9;
            }

            @Override
            public ColorModel getColorModel() {
                return tile00.getColorModel();
            }

            @Override
            public SampleModel getSampleModel() {
                return tile00.getSampleModel();
            }
        };

        final SampleClassifier classifier = new SampleClassifier();
        classifier.setImage(source);
        classifier.setFillValue((byte)9);
        classifier.addInterval(0, 2, (byte) 1);
        classifier.addInterval(2, 5, (byte) 2);
        classifier.addInterval(5, 7, (byte) 3);

        executeAndCheck(classifier, expectedClassif);
    }

    private void executeAndCheck(final SampleClassifier preparedProcess, final int[] expectedClassif) throws ProcessException {
        final RenderedImage source = preparedProcess.getImage();
        final ParameterValueGroup output = preparedProcess.call();

        final Object value = output.parameter(SampleClassifierDescriptor.IMAGE.getName().getCode()).getValue();
        assertTrue("Output image should be an image, but is "+value, value instanceof RenderedImage);

        final RenderedImage outImage = (RenderedImage) value;
        assertEquals("Output image width differs from input", source.getWidth(), outImage.getWidth());
        assertEquals("Output image height differs from input", source.getHeight(), outImage.getHeight());

        int[] pixels = outImage.getData().getPixels(0, 0, outImage.getWidth(), outImage.getHeight(), (int[])null);
        assertArrayEquals("Classification differs from expected result", expectedClassif, pixels);
    }

    public static BufferedImage createGrayScale(final int width, final int height, final double[] samples) {
        final WritableRaster data = BufferedImages.createRaster(width, height, 1, DataBuffer.TYPE_FLOAT, new Point(0, 0));
        data.setPixels(0, 0, width, height, samples);
        final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        final ComponentColorModel cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, DataBuffer.TYPE_FLOAT);
        return new BufferedImage(cm, data, true, null);
    }
}
