/*
 *     (C) 2019, Geomatys
 */
package org.geotoolkit.processing.science.drift.v2;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.PixelInCell;
import org.apache.sis.geometries.math.Vector2D;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.privy.AffineTransform2D;
import org.apache.sis.referencing.privy.AxisDirections;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.science.drift.Output;
import org.geotoolkit.processing.science.drift.Weight;
import org.geotoolkit.processing.science.drift.v2.PointBucket.PointReference;
import static org.geotoolkit.processing.science.drift.v2.PredictorDescriptor.*;
import static org.geotoolkit.processing.science.drift.v2.Utilities.setTime;
import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import ucar.ma2.InvalidRangeException;


/**
 * The aim is to use arbitrary UV data source as meteo dataset, instead of hard-
 * coded data used by the original process. Other attempts are:
 *
 * <ol>
 * <li>Increase maintainability by providing better abstractions</li>
 * <li>Ease configuration by using standard process inputs instead of static configuration file</li>
 * <li>Performance increase through abstraction and batch processing.</li>
 * </ol>
 *
 * TODO : better doc.
 *
 * @author Alexis Manin (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 */
public class Predictor extends AbstractProcess {

    /**
     * Where to send debugging info. If non-null, this is usually {@link System#out}.
     * Tip: if non-null, better to have a {@link Weight weights} array of length one,
     * otherwise it become difficult to follow the trajectory.
     */
    private static final PrintStream OUT = null;

    private static final Supplier<Vector2D.Double> RANDOM_NOISE = () -> new Vector2D.Double((float)(Math.random() - 0.5), (float)(Math.random() - 0.5));

    public Predictor(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
    }

    @Override
    protected void execute() throws ProcessException {
        //// TODO: move in prediction context
        final Instant startTime = Instant.ofEpochMilli(inputParameters.getMandatoryValue(START_TIMESTAMP));
        final Instant endTime = Instant.ofEpochMilli(inputParameters.getMandatoryValue(END_TIMESTAMP));

        final MeteoDataset meteo= initData();
        final Origin origin;
        try {
            DirectPosition tmpOrigin = inputParameters.getMandatoryValue(START_POINT);
            tmpOrigin = setTime(tmpOrigin, startTime);
            origin = new Origin(tmpOrigin);
        } catch (FactoryException|TransformException ex) {
            throw new ProcessException("Cannot align start position with meteo coordinate system", this, ex);
        }

        final MeteoDataset.TimeSet initedMeteo = init(meteo, origin.getSource());

        final PredictionContext ctx = initContext(origin.getOrigin2d());
        //// END TODO

        /* Note: we should create it at the end, BUT: If this fails, we cannot store processing results, so it's useless
         * to start any heavy computing before being sure we will be able to store them. Doing tmp file creation
         * beforehand allows to ensure we can write on disk.
         */
        final Path outputFile;
        try {
            outputFile = Files.createTempFile("drift", ".nc");
        } catch (IOException ex) {
            throw new ProcessException("Cannot create a temporary file for result storage", this, ex);
        }

        try {
            // TODO: stream all this sh*t
            List<Output> outputs = compute(startTime, endTime, ctx, initedMeteo);
            write(outputs, ctx.grid.model, startTime, outputFile);
        } catch (Exception ex) {
            try {
                Files.delete(outputFile);
            } catch (Exception bis) {
                ex.addSuppressed(bis);
            }

            if (ex instanceof ProcessException) {
                throw (ProcessException)ex;
            }

            throw new ProcessException("Error while computing", this, ex);
        }

        outputParameters.getOrCreate(OUTPUT_DATA).setValue(outputFile);
    }

    private void write(final List<Output> outputs, final GridGeometry grid, final Instant startTime, final Path outputFile) throws IOException, InvalidRangeException, ProcessException {
        MathTransform grid2Crs = grid.getGridToCRS(PixelInCell.CELL_CENTER);
        if (!(grid2Crs instanceof AffineTransform)) {
            throw new ProcessException("Unsupported case: grid 2 crs is not affine", this);
        }

        final AffineTransform g2c = (AffineTransform) grid2Crs;

        Output.write(
                outputs, grid.getCoordinateReferenceSystem(), startTime.toEpochMilli(),
                g2c.getTranslateX(), g2c.getTranslateY(), g2c.getScaleX(), g2c.getScaleY(),
                outputFile.toString()
        );
    }

    private List<Output> compute(final Instant startTime, final Instant endTime, final PredictionContext ctx, final MeteoDataset.TimeSet meteo) throws ProcessException {
        final double totalSeconds = startTime.until(endTime, ChronoUnit.SECONDS);
        boolean newDay = false;
        Instant nextDay = startTime.plus(1, ChronoUnit.DAYS);

        Instant stepTime = startTime;
        // TODO : We should think about a better way of managing output grid, because here we waste a lot of space.
        final double[] globalProba = new double[ctx.grid.width*ctx.grid.height];
        final double[] dayProba = new double[globalProba.length];
        final List<Output> outputs = new ArrayList<>();
        // When computing point drift, we'll add a point for each weight available. As we don't want this amount to grow
        // past a configured maximum, we have to purge available points before each computing pass.
        final int maxAllowedPoints = ctx.points.maxPts / (ctx.weights.length + 1);
        do {
            final long timePassed = startTime.until(stepTime, ChronoUnit.SECONDS);

            fireProgressing("Drifting: "+stepTime, (float) (timePassed / totalSeconds) * 100f, false);

            MeteoDataset.Snapshot snapshot = meteo.setTime(stepTime)
                    .map(calibration -> calibration.setHorizontalComponent(ctx.grid.model.getEnvelope()))
                    .orElse(null);

            if (snapshot == null) break;

            ctx.points.removeLeastProbable(maxAllowedPoints);
            final double[] stepProba = advance(ctx, snapshot);
            if (stepProba == null)
                break;

            // TODO : add abstraction here : we could reduce loop by iterating only over a rectangle where probabilities
            // have really been updated.
            IntStream.range(0, dayProba.length)
                    .parallel()
                    .forEach(i -> {
                        dayProba[i] += stepProba[i];
                        globalProba[i] += stepProba[i];
                    });

            newDay = stepTime.isAfter(nextDay);
            if (newDay) {
                nextDay = nextDay.plus(1, ChronoUnit.DAYS);
                outputs.add(new Output(dayProba, ctx.grid.width, ctx.grid.height));
                Arrays.fill(dayProba, 0);
            }
        } while ((stepTime = ctx.step(stepTime)).isBefore(endTime));

        if (stepTime.equals(startTime)) {
            throw new ProcessException("No data available for time: "+stepTime, this);
        }
        if (!newDay) {
            outputs.add(new Output(dayProba, ctx.grid.width, ctx.grid.height));
        }

        outputs.add(new Output(globalProba, ctx.grid.width, ctx.grid.height));

        outputParameters.getOrCreate(ACTUAL_END_TIMESTAMP).setValue(stepTime.toEpochMilli());

        return outputs;
    }

    private PredictionContext initContext(final DirectPosition2D origin2d) throws ProcessException {
        // TODO : create point bucket / add origin point into bucket.
        final long timestep = inputParameters.getMandatoryValue(TIMESTEP);
        final int maxPts = inputParameters.getMandatoryValue(MAX_POINTS);

        try {
            final PredictionContext ctx = new PredictionContext(
                    initGrid(origin2d), initWeights(), Duration.ofSeconds(timestep), maxPts
            );
            ctx.points.add(origin2d.getCoordinates(), 1);
            return ctx;
        } catch (NoninvertibleTransformException ex) {
            throw new ProcessException("Cannot initialize output grid", this, ex);
        }
    }

    private GridModel initGrid(final DirectPosition2D origin2d) throws NoninvertibleTransformException, ProcessException {
        final GridExtent extent = new GridExtent(
                inputParameters.getMandatoryValue(TARGET_WIDTH),
                inputParameters.getMandatoryValue(TARGET_HEIGHT)
        );

        // TODO : set proper CRS and transform
        double resolution = inputParameters.doubleValue(TARGET_RESOLUTION);
        if (!Double.isFinite(resolution))
            throw new ProcessException("Input resolution is not a finite number: "+resolution, this);
        // Compute PIXEL CENTER translations, considering the center of output image is th point of origin.
        final double translateX = origin2d.x - (extent.getSize(0) - 1) / 2d * resolution;
        final double translateY = origin2d.y + (extent.getSize(1) - 1) / 2d * resolution;
        final AffineTransform2D grid2crs = new AffineTransform2D(resolution, 0, 0, -resolution, translateX, translateY);
        return new GridModel(
                new GridGeometry(extent, PixelInCell.CELL_CENTER, grid2crs, origin2d.getCoordinateReferenceSystem())
        );
    }

    private Weight[] initWeights() throws ProcessException {
        final String code = PredictorDescriptor.WEIGHTS.getName().getCode();
        Weight[] result = inputParameters.groups(code).stream()
                .map(Parameters::castOrWrap)
                .map(Predictor::readWeight)
                .toArray(size -> new Weight[size]);
        if (result.length < 1) {
            throw new ProcessException("No weight provided as input", this);
        }

        return result;
    }

    private static Weight readWeight(final Parameters weightParam) {
        return new Weight(
                weightParam.doubleValue(CURRENT_WEIGHT),
                weightParam.doubleValue(WIND_WEIGHT),
                weightParam.doubleValue(WEIGHT_PROBABILITY)
        );
    }

    private MeteoDataset initData() throws ProcessException {
        final GridCoverageResource wind = inputParameters.getMandatoryValue(WIND_RESOURCE);
        final GridCoverageResource current = inputParameters.getMandatoryValue(CURRENT_RESOURCE);
        try {
            return new SimpleMeteoDataset(
                    new SimpleUVSource(wind),
                    new SimpleUVSource(current)
            );
        } catch (DataStoreException e) {
            throw new ProcessException("Incompatible data source", this, e);
        }
    }

    /**
     * /!\ Can return null
     *
     * @param ctx
     * @param uv
     * @return The new snapshot of probabilities for given data snapshot. Will be null if we cannot advance anymore.
     *
     * @throws ProcessException
     */
    private double[] advance(final PredictionContext ctx, MeteoDataset.Snapshot uv) throws ProcessException {
        final Vector2D.Double move = new Vector2D.Double();
        final CoordinateReferenceSystem workCrs = ctx.grid.model.getCoordinateReferenceSystem();

        final SingleCRS workHorizontal = CRS.getHorizontalComponent(workCrs);
        if (workHorizontal == null) {
            throw new ProcessException("Cannot identify neither easting nor northing in configured coordinate reference system.", this);
        }

        final int xAxis = AxisDirections.indexOfColinear(workCrs.getCoordinateSystem(), workHorizontal.getCoordinateSystem());
        final int yAxis = xAxis + 1;

        final DirectPosition2D location = new DirectPosition2D(workCrs);

        // TODO: try to parallelize. Point bucket is no synchronized, but it may be
        final HashMap<PointReference, List<PointReference>> movements = new HashMap<>((int)(ctx.points.references.size() * 1.3f));
        final PointReference[] refs = ctx.points.references.toArray(new PointReference[ctx.points.references.size()]);
        for (PointBucket.PointReference ref : refs) {
            ref.read(location);

            final Optional<Vector2D.Double> currentOpt = uv.current.evaluate(location);
            if (!currentOpt.isPresent()) {
                // No more data on current point. All we can do is evince it from processing, hoping that other points
                // are still in the game.
                ctx.points.remove(ref);
                continue;
            }

            final Vector2D.Double current = currentOpt.get();

            final Vector2D.Double wind = uv.wind.evaluate(location)
                    .orElseGet(RANDOM_NOISE);// TODO : should we just ignore wind here ?
             /*
             * At this point (easting, northing) is the projected coordinates in metres and (xStart, yStart)
             * is the same position in grid coordinates. Now compute different possible drift speeds.
             */
            final List<PointReference> children = new ArrayList<>(ctx.weights.length);
            for (final Weight w : ctx.weights) {
                final double pw = ref.getWeight() * w.probability;
//                if (pw <= ctx.probabilityThreshold) {
//                    continue;
//                }

                wind.scale(w.wind);
                current.scale(w.current);
                move.x = wind.x + current.x;
                move.y = wind.y + current.y;
                move.scale(ctx.timestep.getSeconds());

                final double[] movedLocation = location.getCoordinates();
                movedLocation[0] += move.x;
                movedLocation[1] += move.y;

                children.add(ctx.points.add(movedLocation, pw));
            }

            if (children.size() > 0) {
                movements.put(ref, children);
            }
        }

        if (movements.isEmpty())
            return null;

        try {
            ctx.points.refreshGrid();
        } catch (TransformException ex) {
            throw new ProcessException("Cannot project geo-points on output grid", this, ex);
        }

        final double[] probabilityChanges = new double[ctx.grid.width*ctx.grid.height];
        // Number of points evaluated at this step in the output grid.
        int numOnGrid = 0;

        for (Map.Entry<PointReference, List<PointReference>> entry : movements.entrySet()) {
            final PointReference origin = entry.getKey();
            origin.readInGrid(location);
            ctx.points.remove(origin);

            // TODO : check order of grid axes
            final double xStart = location.getCoordinate(xAxis);
            final double yStart = location.getCoordinate(yAxis);
            double Δxi = Double.NaN;
            double Δyi = Double.NaN;
            // TODO : this code has been copied without proper understanding. We should review it and make it more lisible.
            for (PointReference child : entry.getValue()) {
                child.readInGrid(location);
                final double x1 = location.getCoordinate(xAxis);
                final double y1 = location.getCoordinate(yAxis);
                double xi = xStart;
                double yi = yStart;
                boolean isValid;        // is (xi,yi) on (x₀,y₀)-(x₁,y₁) line and inside (x₀, y₀, x₀+1, y₀+1) cell?
                do {
                    int gx = (int) xi;
                    int gy = (int) yi;
                    final double x0 = xi;
                    final double y0 = yi;
                    final double Δx = x1 - x0;
                    final double Δy = y1 - y0;
                    isValid = (Δx > 0) ?  ((xi = Math.floor(x0) + 1) <  x1)
                            : (Δx < 0) && ((xi = Math.ceil (x0) - 1) >= x1);
                    if (isValid) {
                        Δxi = xi - x0;
                        Δyi = Δy * (Δxi / Δx);
                        yi  = Δyi + y0;
                        final double f = Math.floor(y0);
                        final double e = yi - f;
                        if (f != y0) {
                            isValid = (e >= 0 && e <= 1);
                        } else {
                            isValid = (e >= -1 && e <= 1);
                            if (isValid && e < 0) gy--;
                        }
                        if (isValid && Δxi == -1) gx--;
                    }
                    if (!isValid) {     // if we do not intersect vertical grid line, maybe we intersect horizontal one.
                        isValid = (Δy > 0) ?  ((yi = Math.floor(y0) + 1) <  y1)
                                : (Δy < 0) && ((yi = Math.ceil (y0) - 1) >= y1);
                        if (isValid) {
                            Δyi = yi - y0;
                            Δxi = Δx * (Δyi / Δy);
                            xi  = Δxi + x0;
                            final double f = Math.floor(x0);
                            final double e = xi - f;
                            if (f != x0) {
                                assert (e >= 0 && e <= 1) : e;
                            } else {
                                assert (e >= -1 && e <= 1) : e;
                                if (e < 0) gx--;
                            }
                            if (Δyi == -1) gy--;
                        }
                    }
                    if (!isValid) {     // if no intersection with horizontal or vertical line, line is fully inside cell.
                        Δxi = Δx;
                        Δyi = Δy;
                        gx = (int) x1;
                        gy = (int) y1;
                    }

                    final double xOrigin = Math.abs(x1 - xStart);
                    final double yOrigin = Math.abs(y1 - yStart);

                    // Note : we've removed norm computing, as compared vectors are colinear. We just need to make a
                    // ratio over any dimension.
                    final double ratio;
                    if (xOrigin == 0 && yOrigin == 0) {
                        ratio = 1;
                    } else {
                        ratio = (xOrigin < yOrigin)? Math.abs(Δyi/yOrigin) : Math.abs(Δxi/xOrigin);
                    }
                    final double p = ((ratio < 1e-12) ? 1 : ratio) * child.getWeight();
                    if (OUT != null) {
                        OUT.printf("x=%3d y=%3d  Δx=%7.3f  Δy=%7.3f  p=%4.3f%n", gx, gy, Δxi, Δyi, p);
                    }
                    if (gx >= 0 && gx < ctx.grid.width && gy >= 0 && gy < ctx.grid.height) {
                        probabilityChanges[((ctx.grid.height - 1) - gy) * ctx.grid.width + gx] += p;
                        numOnGrid++;
                    }
                } while (isValid);

                if (OUT != null) {
                    OUT.println();
                }
            }
        }

        if (numOnGrid < 1)
            return null;

        // Average probability by number of evaluated points
        for (int i = 0 ; i < probabilityChanges.length ; i++) probabilityChanges[i] /= numOnGrid;
        return probabilityChanges;
    }

    MeteoDataset.TimeSet init(MeteoDataset meteo, final DirectPosition origin) throws ProcessException {
        return meteo.setOrigin(origin)
                .orElseThrow(() -> new ProcessException("Cannot initialize datasource for origin "+origin, this));
    }

    private static class PredictionContext {

        final GridModel grid;
        final Duration timestep;

        final PointBucket points;

        final Weight[] weights;

        double probabilityThreshold;

        final double[] probabilityGrid;

        public PredictionContext(GridModel targetGrid, Weight[] weights, Duration timestep, int nbPts) throws NoninvertibleTransformException {
            this.probabilityThreshold = 0.2;
            this.grid = targetGrid;
            this.timestep = timestep;
            this.weights = weights;

            this.points = new PointBucket(targetGrid, nbPts);

            probabilityGrid = new double[targetGrid.width * targetGrid.height];
        }

        public double getProbabilityThreshold() {
            return probabilityThreshold;
        }

        public void setProbabilityThreshold(double probabilityThreshold) {
            this.probabilityThreshold = probabilityThreshold;
        }

        public Instant step(final Instant source) {
            return source.plus(timestep);
        }
    }
}
