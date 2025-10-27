package org.geotoolkit.benchmarks;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.wrapper.jts.Factory;
import org.apache.sis.geometry.wrapper.jts.JTS;
import org.apache.sis.image.processing.isoline.Isolines;
import org.apache.sis.storage.MemoryGridCoverageResource;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.coverage.isoline.IsolineDescriptor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.parameter.ParameterValueGroup;
import org.apache.sis.coverage.grid.PixelInCell;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Limitations:
 * <ul>
 *     <li>Work only with greyscale byte image</li>
 *     <li>does <em>not</em> measure influence of space conversion on isoline process.
 *     Iit just use an identity transform, considering only image space.</li>
 *     <li>Input image is shared among all threads</li>
 * </ul>
 */
@Warmup(iterations = 2, time = 10)
@Measurement(iterations = 4, time = 10)
@Fork(1)
@Threads(8)
public class IsolineComparison {

    @State(Scope.Benchmark)
    public static class Input {

        @Param({ "3", "7", "13"})
        int nbLevels;

        RenderedImage image;
        double[] isolineLevels;

        @Setup
        public void init() throws Exception {
            try (final InputStream rawStream = IsolineComparison.class.getResourceAsStream("/images/canyon-relief-inversion-grey.jpg")) {
                // Note: we do not close image input stream ourselves, because read does it, and image input streams do
                // not support subsequent calls to close().
                image = ImageIO.read(new MemoryCacheImageInputStream(rawStream));
            }

            if (nbLevels < 1) throw new ExceptionInInitializerError("At least one level should be set");
            else if (nbLevels > 256) throw new ExceptionInInitializerError("Too much levels given");

            final double step = 256d / nbLevels;
            isolineLevels = IntStream.rangeClosed(1, nbLevels)
                    .mapToDouble(i -> i * step)
                    .toArray();
        }
    }

    @State(Scope.Thread)
    public static class GeotkProcess {

        @Param({ "SIS_MARCHING_SQUARE", "GEOTK_MARCHING_SQUARE" })
        public String method;

        public Process process;

        @Setup
        public void setup(Input input) {
            final GridCoverage coverage = new GridCoverageBuilder()
                    .setValues(input.image)
                    .setDomain(new GridGeometry(
                            new GridExtent(input.image.getWidth(), input.image.getHeight()),
                            PixelInCell.CELL_CENTER, MathTransforms.identity(2),
                            CommonCRS.defaultGeographic()))
                    .build();
            final Parameters params = Parameters.castOrWrap(IsolineDescriptor.INSTANCE.getInputDescriptor().createValue());
            params.getOrCreate(IsolineDescriptor.COVERAGE_REF).setValue(new MemoryGridCoverageResource(null, coverage, null));
            params.getOrCreate(IsolineDescriptor.INTERVALS).setValue(input.isolineLevels);
            params.getOrCreate(IsolineDescriptor.FEATURE_NAME).setValue("isolines");
            params.getOrCreate(IsolineDescriptor.METHOD).setValue(method);
            process = IsolineDescriptor.INSTANCE.createProcess(params);
        }
    }

    /**
     * Run SIS isoline, and only that. It is not directly comparable to Geotk approach, as they use different geometry
     * libraries.
     */
    @Benchmark
    public Isolines[] sisIsolinesRaw(Input input) throws Exception {
        final Isolines[] result = Isolines.generate(input.image, new double[][]{input.isolineLevels}, MathTransforms.identity(2));
        if (result == null || result.length < 1) throw new RuntimeException("Invalid result");
        return result;
    }

    /**
     * Same as {@link #sisIsolinesRaw(Input)}, but force a conversion of result geometries, to measure the impact of
     * mapping isoline geometries to JTS. This is important to measure, as it will be heavily used on production.
     */
    @Benchmark
    public void sisIsolinesToJTS(Input input, Blackhole blackhole) throws Exception {
        final GeometryFactory factory = Factory.INSTANCE.factory(false);
        final Isolines[] result = Isolines.generate(input.image, new double[][]{input.isolineLevels}, MathTransforms.identity(2));
        for (Isolines iso : result) {
            iso.polylines().forEach((scale, geometry) -> {
                final Geometry geom = JTS.fromAWT(factory, geometry, 1);
                if (geom == null) throw new RuntimeException("NULL GEOMETRY !");
                blackhole.consume(geom);
            });
        }
    }

    /**
     * Measure Geotoolkit isolines. Note that the process object that launches the computation is prepared before-hand
     * in a state object, to avoid polluting final measures.
     * Also, this benchmark has 2 branches:
     * <ul>
     *     <li>Using SIS method. It allows to get an idea of the overhead caused by the process
     *     (argument preparation, Feature conversion, etc.) compared to {@link #sisIsolinesToJTS(Input, Blackhole)}.</li>
     *     <li>Using Geok method. It allows to compare SIS performance gain compared to Geotk.</li>
     * </ul>
     */
    @Benchmark
    public ParameterValueGroup geotkIsolines(GeotkProcess process) throws Exception {
        return process.process.call();
    }

    /**
     * Debug app: execute all isoline algorithms benchmarked, and save results in temporary images.
     * It helps to compare results visually.
     */
    public static void main(String[] args) throws Exception {
        final Input input = new Input();
        input.nbLevels = 4;
        input.init();
        // override automatic levels for clearer results
        input.isolineLevels = new double[]{ 10, 50, 210, 250 };

        // Run SIS isoline process
        var canvas = createCanvas(input.image);
        Isolines[] result = new IsolineComparison().sisIsolinesRaw(input);
        Arrays.stream(result)
                .flatMap(map -> map.polylines().values().stream())
                .forEach(canvas.painter::draw);

        final Path directSISImage = save(canvas, "sis-direct-isoline");
        System.out.println("SIS FILE: "+ directSISImage);

        // Re-run SIS, but this time, forcing a conversion back and forth to JTS
        canvas = createCanvas(input.image);
        result = new IsolineComparison().sisIsolinesRaw(input);
        final GeometryFactory factory = Factory.INSTANCE.factory(false);
        Arrays.stream(result)
                .flatMap(map -> map.polylines().values().stream())
                .map(shape -> JTS.fromAWT(factory, shape, 1))
                .map(JTS::asShape)
                .forEach(canvas.painter::draw);

        final Path sisConvertedImage = save(canvas, "sis-converted-isoline");
        System.out.println("SIS WITH CONVERSION FILE: "+ sisConvertedImage);


        // Run Geotk isoline process
        canvas = createCanvas(input.image);
        final GeotkProcess process = new GeotkProcess();
        process.method = IsolineDescriptor.Method.GEOTK_MARCHING_SQUARE.name();
        process.setup(input);
        final FeatureSet features = Parameters.castOrWrap(process.process.call()).getMandatoryValue(IsolineDescriptor.FCOLL);
        features.features(false)
                .map(it -> (Geometry) it.getPropertyValue("sis:geometry"))
                .map(JTS::asShape)
                .forEach(canvas.painter::draw);


        final Path geotkImage = save(canvas, "geotk-isoline");
        System.out.println("GEOTK FILE: "+ geotkImage);
    }

    record Canvas(RenderedImage image, Graphics2D painter) {}
    private static Canvas createCanvas(final RenderedImage input) {

        final BufferedImage canvas = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = canvas.createGraphics();
        g2d.drawRenderedImage(input, null);
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(1));

        return new Canvas(canvas, g2d);
    }

    private static Path save(Canvas canvas, String title) throws IOException {
        canvas.painter.dispose();
        Path tempFile = Files.createTempFile(title, ".jpeg");
        ImageIO.write(canvas.image, "JPEG", tempFile.toFile());
        return tempFile;
    }
}
