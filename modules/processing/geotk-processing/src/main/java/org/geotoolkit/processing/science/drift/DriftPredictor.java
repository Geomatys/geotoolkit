package org.geotoolkit.processing.science.drift;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Creates a coverage showing were an object may be after some delay if it drift in the direction of currents.
 * Notes:
 * <ul>
 *   <li>Calculation are performed in a UTM projection for the zone of the starting point.</li>
 * </ul>
 *
 * Other algorithms:
 * <ul>
 *   <li><a href="http://www.meteorologie.eu.org/mothy/rapports/rapport_guerin_coiffier.pdf">Optimisation
 *       du modèle MOTHY pour les opérations de recherche et sauvetage en mer</a></li>
 *   <li><a href="http://wwz.cedre.fr/en/content/download/1657/16493/file/1-meteofrance-mothy_EN.pdf">Mothy
 *       – Oil and object surface drift</a></li>
 * </ul>
 *
 * @author Alexis Manin (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 */
public class DriftPredictor extends AbstractProcess {
    /**
     * Logger where to duplicate messages sent to listeners.
     */
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.processing.science.drift");

    /**
     * {@code true} if the wind is stored as (magnitude, direction) vectors,
     * or {@code false} for the usual (u, v) vectors.
     */
    private static final boolean WIND_USE_MAGNITUDE = false;

    /**
     * Where to send debugging info. If non-null, this is usually {@link System#out}.
     * Tip: if non-null, better to have a {@link #weights} array of length one,
     * otherwise it become difficult to follow the trajectory.
     */
    private static final PrintStream OUT = null;

    /**
     * Data directory, vector weights and other information needed by the process.
     */
    private Configuration configuration;

    /**
     * East-West (u) and North-South (v) component of the velocity vectors, in metres per second.
     */
    private DataSource current;

    /**
     * Speed and direction component of the wind speed, in metres per second.
     */
    private DataSource wind;

    public static final class Weight implements Comparable<Weight> {
        /**
         * Multiplication factor to apply on oceanic current and to wind speed when computing the drift speed.
         * Note that the {@code current} + {@code wind} sum does not need to be 1.
         */
        final double current, wind;

        /**
         * The probability that the current and wind scales defined by this {@code Weight} instance happen.
         */
        final double probability;

        /**
         * Specifies weights to give to oceanic current and wind speed.
         * Note that the {@code current} + {@code wind} sum does not need to be 1.
         *
         * @param current      multiplication factor to apply on oceanic current when computing the drift speed.
         * @param wind         multiplication factor to apply on wind speed when computing the drift speed.
         * @param probability  the probability that the current and wind scales defined by this {@code Weight} instance happen.
         */
        public Weight(final double current, final double wind, final double probability) {
            this.current     = current;
            this.wind        = wind;
            this.probability = probability;
        }

        /**
         * Sorts largest weights first.
         */
        @Override
        public int compareTo(final Weight o) {
            if (probability < o.probability) return +1;
            if (probability > o.probability) return -1;
            return 0;
        }
    }

    /**
     * Stop tracking position having a probability equals or lower than this threshold.
     * This is initially zero, then incremented as the number of trajectories increase.
     */
    private double probabilityThreshold;

    /**
     * Length of each tuple in the {@link #positions} array.
     * Must be small than any {@code *_OFFSET} value.
     */
    private static final int TUPLE_LENGTH = 3;

    /**
     * Index of an element in a tuple of length {@value #TUPLE_LENGTH}.
     */
    private static final int WEIGHT_OFFSET = 2;

    /**
     * Instant when the simulation start.
     */
    private Instant startTime;

    /**
     * Current time.
     */
    private Instant currentTime;

    /**
     * When to stop the simulation (inclusive).
     */
    private Instant endTime;

    /**
     * List of possible locations as (x,y) tuples in metres followed by their weight.
     * The number of valid tuples is given by {@link #numOrdinates} / {@value #TUPLE_LENGTH}.
     * The array length is the capacity.
     */
    private float[] positions;

    /**
     * Number of valid ordinates in the {@link #positions} array.
     * This is {@value #TUPLE_LENGTH} time the number of valid tuples.
     */
    private int numOrdinates;

    /**
     * The coordinate reference system in which to perform the calculation. Axis units must be metres.
     */
    private ProjectedCRS modelCRS;

    /**
     * Transformation from {@link #modelCRS} to the CRS used by {@link #uComponents} and {@link #vComponents}.
     */
    private MathTransform2D modelToUV;

    /**
     * Conversion from the coordinate in metres to the grid index.
     */
    private AffineTransform coordToGrid;

    /**
     * The probability to find the drifting object at a location given by the grid index.
     */
    private double[] probabilities, probabilityChanges, trajectoryPropabilities;

    /**
     * Temporary object for coordinate operations.
     */
    private final Point2D.Double tmp;

    /**
     * Images to write, one per day. The last item in the list shall be the overall trajectory.
     */
    private final List<Output> outputs;


    public DriftPredictor(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
        tmp = new Point2D.Double();
        outputs = new ArrayList<>();
    }

    private DirectPosition geographic(DirectPosition pos) throws ProcessException {
        CoordinateReferenceSystem crs = pos.getCoordinateReferenceSystem();
        if (crs != null) try {
            pos = CRS.findOperation(crs, CommonCRS.defaultGeographic(), null).getMathTransform().transform(pos, null);
        } catch (FactoryException | TransformException ex) {
            throw new ProcessException(null, this, ex);
        }
        return pos;
    }

    /**
     * Returns the process configuration. The configuration contains (among other information)
     * the {@linkplain Configuration#directory root directory} of {@code "config.txt"} file
     * and {@code "cache"} sub-directory.
     */
    final Configuration configuration() {
        return configuration;
    }

    @Override
    protected void execute() throws ProcessException {
        final Parameters input = Parameters.castOrWrap(inputParameters);
        try {
            configuration = new Configuration(input.getValue(DriftPredictionDescriptor.DATA_DIRECTORY));
        } catch (IOException e) {
            throw new ProcessException(null, this, e);
        }
        final int gridWidth      = configuration.gridWidth;
        final int gridHeight     = configuration.gridHeight;
        final int gridResolution = configuration.gridResolution;
        final DirectPosition startPoint = geographic(input.getValue(DriftPredictionDescriptor.START_POINT));
        startTime    = Instant.ofEpochMilli(input.getValue(DriftPredictionDescriptor.START_TIMESTAMP));
        endTime      = Instant.ofEpochMilli(input.getValue(DriftPredictionDescriptor.END_TIMESTAMP));
        currentTime  = startTime;
        modelCRS     = CommonCRS.WGS84.universal(startPoint.getOrdinate(1), startPoint.getOrdinate(0));
        positions    = new float[configuration.maximumTrajectoryCount * TUPLE_LENGTH];
        positions[0] = (float) startPoint.getOrdinate(1);
        positions[1] = (float) startPoint.getOrdinate(0);
        positions[WEIGHT_OFFSET] = 1;
        numOrdinates  = TUPLE_LENGTH;
        probabilities = new double[gridWidth * gridHeight];
        probabilityChanges = new double[probabilities.length];
        trajectoryPropabilities = new double[probabilities.length];
        try {
            final MathTransform2D toModel = (MathTransform2D) modelCRS.getConversionFromBase().getMathTransform();
            toModel.transform(positions, 0, positions, 0, 1);
            modelToUV = toModel.inverse();
        } catch (TransformException ex) {
            throw new ProcessException(null, this, ex);
        }
        coordToGrid = AffineTransform.getTranslateInstance(gridWidth/2, gridHeight/2);
        coordToGrid.scale(1./gridResolution, 1./gridResolution);
        coordToGrid.translate(-positions[0], -positions[1]);

        final Path outputPath;
        try {
            current = new DataSource.HYCOM(this);
            wind = new DataSource.MeteoFrance(this);
            boolean newDay = false;
            boolean hasData = false;
            long day = currentTime.getEpochSecond() / (24*60*60);
            while (!currentTime.isAfter(endTime)) {
                if (!advance()) break;
                hasData = true;
                final long d = currentTime.getEpochSecond() / (24*60*60);
                newDay = (d != day);
                if (newDay) {
                    day = d;
                    snapshot();
                }
            }
            if (!hasData) {
                throw new UnavailableDataException("No data available at " + startTime, this, null);
            }
            if (!newDay) {
                snapshot();
            }
            trajectory();
            outputPath = writeNetcdf();
        } catch (ProcessException e) {
            throw e;
        } catch (Exception e) {
            throw new ProcessException("Can not compute drift at " + currentTime, this, e);
        }
        final Parameters p = Parameters.castOrWrap(outputParameters);
        p.getOrCreate(DriftPredictionDescriptor.OUTPUT_DATA).setValue(outputPath);
        p.getOrCreate(DriftPredictionDescriptor.ACTUAL_END_TIMESTAMP).setValue(currentTime.toEpochMilli());
    }

    /**
     * Moves all drift positions by one {@link #timeStep}.
     *
     * @return {@code true} on success, or {@code false} if there is no data for this step.
     */
    private boolean advance() throws Exception {
        final int  gridWidth  = configuration.gridWidth;
        final int  gridHeight = configuration.gridHeight;
        final long timeStep   = configuration.timeStep;
        final OffsetDateTime date = OffsetDateTime.ofInstant(currentTime, ZoneOffset.UTC);
        synchronized (DriftPredictor.class) {   // For making sure that only one process downloads data.
            try {
                wind.load(date);
                current.load(date);
            } catch (FileNotFoundException e) {
                progress(e.toString());                 // TODO: should actually be reported as a warning.
                return false;
            } catch (IOException e) {
                throw new CanNotDownloadException(e.getMessage(), this, e);
            }
            wind.deleteOldFiles();
            current.deleteOldFiles();
        }
        progress("Computing drift at " + currentTime);
        /*
         * At this point data have been loaded. Start computation.
         */
        double storageThreshold = probabilityThreshold;
        int numLastRun = numOrdinates;
        int newPosIndex = numLastRun;
        numOrdinates = 0;
        int numOnGrid = 0;
        Arrays.fill(probabilityChanges, 0);
        for (int posIndex = 0; posIndex < numLastRun;) {
            final double easting  = tmp.x = positions[posIndex++];
            final double northing = tmp.y = positions[posIndex++];
            final double weight           = positions[posIndex++];
            modelToUV.transform(tmp, tmp);
            double u = current.u.valueAt(tmp.y, tmp.x);
            double v = current.v.valueAt(tmp.y, tmp.x);
            if (Double.isNaN(u)) u = (Math.random() - 0.5) / 100;      // Small random noise (TODO: do something better).
            if (Double.isNaN(v)) v = (Math.random() - 0.5) / 100;
            double uWind, vWind;
            if (WIND_USE_MAGNITUDE) {
                final double windSpeed = wind.u.valueAt(tmp.y, tmp.x);
                final double windDir = -Math.toRadians(wind.v.valueAt(tmp.y, tmp.x));
                uWind = windSpeed * Math.cos(windDir);
                vWind = windSpeed * Math.sin(windDir);
            } else {
                uWind = wind.u.valueAt(tmp.y, tmp.x);
                vWind = wind.v.valueAt(tmp.y, tmp.x);
            }
            if (Double.isNaN(uWind)) uWind = Math.random() - 0.5;      // Small random noise (TODO: do something better).
            if (Double.isNaN(vWind)) vWind = Math.random() - 0.5;
            tmp.setLocation(easting, northing);
            coordToGrid.transform(tmp, tmp);
            final double xStart = tmp.x;
            final double yStart = tmp.y;
            double Δxi = Double.NaN;
            double Δyi = Double.NaN;
            /*
             * At this point (easting, northing) is the projected coordinates in metres and (xStart, yStart)
             * is the same position in grid coordinates. Now compute different possible drift speeds.
             */
            for (final Weight w : configuration.weights) {
                final double pw = weight * w.probability;
                if (pw <= probabilityThreshold) {
                    continue;
                }
                tmp.x = easting  + (u * w.current + uWind * w.wind) * timeStep;
                tmp.y = northing + (v * w.current + vWind * w.wind) * timeStep;
                if (pw > storageThreshold) {
                    if (numOrdinates + TUPLE_LENGTH <= posIndex) {
                        positions[numOrdinates++] = (float) tmp.x;
                        positions[numOrdinates++] = (float) tmp.y;
                        positions[numOrdinates++] = (float) pw;
                    } else {
                        /*
                         * New coordinates. If there is not enough room, forget all trajectories having the lowest
                         * probability and set the threshold to that probability, so we do not track them anymore.
                         * This operation is not executed very often, so its cost should not be very high.
                         */
                        if (newPosIndex >= positions.length) {
                            float threshold = Float.MAX_VALUE;
                            for (int j=WEIGHT_OFFSET; j < numOrdinates; j += TUPLE_LENGTH) {
                                final float p = positions[j];
                                if (p < threshold) threshold = p;
                            }
                            for (int j=numLastRun + WEIGHT_OFFSET; j < newPosIndex; j += TUPLE_LENGTH) {
                                final float p = positions[j];
                                if (p < threshold) threshold = p;
                            }
                            newPosIndex = discardLowProbability(numLastRun, newPosIndex, threshold);
                            final int n = discardLowProbability(0, numOrdinates, threshold);
                            final int d = numOrdinates - n;
                            if (d != 0) {
                                System.arraycopy(positions, numOrdinates, positions, n, newPosIndex - numOrdinates);
                                numOrdinates = n;
                                numLastRun  -= d;
                                newPosIndex -= d;
                            }
                            storageThreshold = threshold;
                        }
                        positions[newPosIndex++] = (float) tmp.x;
                        positions[newPosIndex++] = (float) tmp.y;
                        positions[newPosIndex++] = (float) pw;
                    }
                }
                coordToGrid.transform(tmp, tmp);
                final double x1 = tmp.x;
                final double y1 = tmp.y;
                final double length = Math.hypot(x1 - xStart, y1 - yStart);
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
                    final double p = ((length != 0) ? Math.hypot(Δxi, Δyi) / length : 1) * pw;
                    if (OUT != null) {
                        OUT.printf("x=%3d y=%3d  Δx=%7.3f  Δy=%7.3f  p=%4.3f%n", gx, gy, Δxi, Δyi, p);
                    }
                    if (gx >= 0 && gx < gridWidth && gy >= 0 && gy < gridHeight) {
                        probabilityChanges[((gridHeight - 1) - gy) * gridWidth + gx] += p;
                        numOnGrid++;
                    }
                } while (isValid);
                if (OUT != null) {
                    OUT.println();
                }
            }
        }
        probabilityThreshold = storageThreshold;
        for (int i=0; i < probabilities.length; i++) {
            final double change = probabilityChanges[i] / numOnGrid;
            probabilities[i] += change;
            trajectoryPropabilities[i] += change;
        }
        /*
         * Compact if needed (this is rarely needed), then take in account the new positions in the count
         * of number of positions.
         */
        if (numOrdinates < numLastRun) {
            System.arraycopy(positions, numLastRun, positions, numOrdinates, newPosIndex - numLastRun);
            newPosIndex -= (numLastRun - numOrdinates);
        }
        numOrdinates = newPosIndex;
        currentTime = currentTime.plusSeconds(timeStep);
        return true;
    }

    private int discardLowProbability(final int fromIndex, int toIndex, final float probability) {
        for (int j=toIndex + (WEIGHT_OFFSET - TUPLE_LENGTH); j >= fromIndex;) {
            if (positions[j] <= probability) {
                final int upper = j + (TUPLE_LENGTH - WEIGHT_OFFSET);
                while ((j -= TUPLE_LENGTH) >= fromIndex) {
                    if (positions[j] > probability) break;
                }
                final int lower = j + (TUPLE_LENGTH - WEIGHT_OFFSET);
                System.arraycopy(positions, upper, positions, lower, toIndex - upper);
                toIndex -= (upper - lower);
            } else {
                j -= TUPLE_LENGTH;
            }
        }
        return toIndex;
    }

    private void snapshot() {
        outputs.add(new Output(probabilities, configuration.gridWidth, configuration.gridHeight));
        Arrays.fill(probabilities, 0);
    }

    private void trajectory() {
        outputs.add(new Output(trajectoryPropabilities, configuration.gridWidth, configuration.gridHeight));
    }

    private Path writeNetcdf() throws Exception {
        final Path outputFile = Files.createTempFile("drift", ".nc");
        final AffineTransform gridToCoord = coordToGrid.createInverse();
        Output.write(outputs, modelCRS, startTime.toEpochMilli(),
                gridToCoord.getTranslateX(), gridToCoord.getTranslateY(),
                gridToCoord.getScaleX(),     gridToCoord.getScaleY(),
                outputFile.toString());
        return outputFile;
    }

    /**
     * Writes in PNG formats all snapshots created by the current process.
     * This method is used for debugging purpose only.
     *
     * @throws IOException if an error occurred while writing the snapshots.
     */
    public final void writeShapshots() throws IOException {
        for (int i=0; i<outputs.size(); i++) {
            String filename = "overall.png";
            if (i != outputs.size() - 1) {
                filename = "day-" + (i+1) + ".png";
            }
            outputs.get(i).writePNG(configuration().directory, filename);
        }
    }

    /**
     * Reports the name of task under progress.
     */
    final void progress(final String task) {
        LOGGER.info(task);
        fireProgressing(task, Float.NaN, false);
    }
}
