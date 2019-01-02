package org.geotoolkit.processing.coverage.categorize;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.Collections;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.coverage.memory.MemoryCoverageStore;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.image.sampleclassifier.SampleClassifierTest;
import org.geotoolkit.storage.coverage.DefiningCoverageResource;
import org.geotoolkit.test.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.geotoolkit.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.geotoolkit.storage.coverage.GridCoverageResource;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class CategorizeTest {

    @Test
    public void test2D() throws DataStoreException, ProcessException {
        test2D(false);
    }

    @Test
    public void testSubset2D() throws DataStoreException, ProcessException {
        test2D(true);
    }

    private void test2D(final boolean subset) throws DataStoreException, ProcessException {
        final MemoryCoverageStore store = new MemoryCoverageStore();
        final GridCoverageResource input = store.add(new DefiningCoverageResource("input"));

        final BufferedImage inputImage = SampleClassifierTest.createGrayScale(3, 3, new double[]{
            0, 1, 2,
            2, 1, 0,
            3, 3, 3
        });

        final int[] expectedClassif;
        if (subset) {
            expectedClassif = new int[] {
                1, 1,
                2, 2
            };
        } else {
            expectedClassif = new int[] {
                1, 1, 2,
                2, 1, 1,
                2, 2, 2
            };
        }

        final GridCoverageBuilder builder = new GridCoverageBuilder();
        builder.setRenderedImage(inputImage);
        builder.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        builder.setEnvelope(-20, -20, 10, 10);
        final GridCoverage sourceCvg = builder.build();

        final GridCoverageWriter writer = input.acquireWriter();
        try {
            writer.write(sourceCvg, new GridCoverageWriteParam());
        } finally {
            input.recycle(writer);
        }

        final GridCoverageResource output = store.add(new DefiningCoverageResource("output"));
        final Envelope roi;
        final Categorize process;
        if (subset) {
            final GeneralEnvelope tmpRoi = new GeneralEnvelope(CommonCRS.defaultGeographic());
            tmpRoi.setRange(0, -10, 10);
            tmpRoi.setRange(1, -20, 0);
            roi = tmpRoi;
            process = create(input, output, roi);
        } else {
            roi = sourceCvg.getEnvelope();
            process = create(input, output, null);
        }

        process.call();

        final GridCoverage outCvg;
        final GridCoverageReader outReader = output.acquireReader();
        try {
            outCvg = outReader.read(0, new GridCoverageReadParam());
        } finally {
            output.recycle(outReader);
        }

        final Envelope outEnvelope = outCvg.getEnvelope();
        Assert.assertEquals("Output envelope is not conform to source data.", GeneralEnvelope.castOrCopy(roi), GeneralEnvelope.castOrCopy(outEnvelope));
        final RenderedImage outImage = outCvg.getRenderableImage(0, 1).createDefaultRendering();
        final int[] pixels = outImage.getData().getPixels(0, 0, outImage.getWidth(), outImage.getHeight(), (int[]) null);
        Assert.assertArrayEquals("Classification result", expectedClassif, pixels);
    }

    /**
     * Activate back when we'll have a multi-dimensional datasource for testing.
     */
    @Ignore
    @Test
    public void test3D() throws FactoryException, DataStoreException, ProcessException {

        final CoordinateReferenceSystem inputCrs = new DefaultCompoundCRS(
                Collections.singletonMap("name", "toto"),
                CommonCRS.defaultGeographic(),
                CommonCRS.Vertical.MEAN_SEA_LEVEL.crs()
        );
        final MemoryCoverageStore store = new MemoryCoverageStore();
        final GridCoverageResource input = store.add(new DefiningCoverageResource("input"));

        final double[][] inputs = {
            {
                0, 1, 2,
                3, 4, 5,
                6, 7, 8,
            },
            {
                1, 2, 3,
                4, 5, 6,
                7, 8, 9
            },
            {
                4, 4, 4,
                7, 7, 7,
                8, 8, 8
            }
        };
        final int[][] expectedClassifs = {
            {
                1, 1, 2,
                2, 9, 9,
                3, 3, 4
            },
            {
                1, 2, 2,
                9, 9, 3,
                3, 4, 9
            },
            {
                9, 9, 9,
                3, 3, 3,
                4, 4, 4
            }
        };

        final GridCoverageWriter writer = input.acquireWriter();
        try {
            for (int i = 0; i < inputs.length; i++) {
                final GridCoverageBuilder builder = new GridCoverageBuilder();
                builder.setRenderedImage(SampleClassifierTest.createGrayScale(3, 3, inputs[i]));
                builder.setCoordinateReferenceSystem(inputCrs);
                builder.setEnvelope(-10, -10, i, 10, 10, i);
                final GridCoverage sourceCvg = builder.build();
                writer.write(sourceCvg, new GridCoverageWriteParam());
            }
        } finally {
            input.recycle(writer);
        }

        final GridCoverageResource output = store.add(new DefiningCoverageResource("output"));
        final Categorize process = create(input, output, null);
        process.call();

        final GridCoverageReader outReader = output.acquireReader();
        try {
            final GridCoverageReadParam param = new GridCoverageReadParam();
            final GeneralEnvelope readEnv = new GeneralEnvelope(inputCrs);
            readEnv.setRange(0, -10, 10);
            readEnv.setRange(1, -10, 10);
            for (int i = 0; i < expectedClassifs.length; i++) {
                readEnv.setRange(2, i, i);
                final GridCoverage outCvg = outReader.read(0, param);
                Assert.assertEquals("Output envelope is not conform to source data.", readEnv, outCvg.getEnvelope());
                final RenderedImage outImage = outCvg.getRenderableImage(0, 1).createDefaultRendering();
                final int[] pixels = outImage.getData().getPixels(0, 0, outImage.getWidth(), outImage.getHeight(), (int[]) null);
                Assert.assertArrayEquals("Classification result", expectedClassifs[i], pixels);
            }
        } finally {
            output.recycle(outReader);
        }
    }

    private Categorize create(final GridCoverageResource input, final GridCoverageResource output, final Envelope roi) {
        final Categorize process = new Categorize();
        process.setSource(input);
        process.setDestination(output);
        process.setFillValue((byte) 9);
        process.addInterval(0, 2, (byte) 1);
        process.addInterval(2, 4, (byte) 2);
        process.addInterval(6, 8, (byte) 3);
        process.addInterval(8, 9, (byte) 4);

        if (roi != null) {
            process.setEnvelope(roi);
        }

        return process;
    }
}
