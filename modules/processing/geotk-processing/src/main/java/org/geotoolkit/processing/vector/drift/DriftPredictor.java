package org.geotoolkit.processing.vector.drift;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.image.palette.PaletteFactory;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.DefaultCoverageReference;
import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class DriftPredictor extends AbstractProcess {
    /**
     * Where to send debugging info. If non-null, this is usually {@link System#out}.
     * Tip: if non-null, better to have a {@link #weights} array of length one,
     * otherwise it become difficult to follow the trajectory.
     */
    private static final PrintStream OUT = null;

    /**
     * Whether the output probability should use a logarithmic scale.
     */
    private static final boolean LOG_SCALE = false;

    /**
     * Data directory, vector weights and other information needed by the process.
     */
    private Configuration configuration;

    /**
     * Output grid size. Hard-coded for now but could become a parameter in a future version
     * (we do not use the upper-case convention for that reason).
     */
    private static final int numColumns = 1000, numRows = 1000;

    /**
     * Size of a grid cell, in metres. Hard-coded for now but could become a parameter in a future version
     * (we do not use the upper-case convention for that reason).
     */
    private static final int gridResolution = 1000;

    /**
     * Time elapsed between two steps, in seconds. Hard-coded for now but could become a parameter
     * in a future version (we do not use the upper-case convention for that reason).
     */
    private static final long timeStep = 6*60*60;

    /**
     * East-West (u) and North-South (v) component of the velocity vectors, in metres per second.
     */
    private DataSource current;

    public static final class Weight {
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


    public DriftPredictor(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
        tmp = new Point2D.Double();
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

    @Override
    protected void execute() throws ProcessException {
        final Parameters input = Parameters.castOrWrap(inputParameters);
        final DirectPosition startPoint = geographic(input.getValue(DriftPredictionDescriptor.START_POINT));
        currentTime  = Instant.ofEpochMilli(input.getValue(DriftPredictionDescriptor.START_TIMESTAMP));
        endTime      = Instant.ofEpochMilli(input.getValue(DriftPredictionDescriptor.END_TIMESTAMP));
        modelCRS     = CommonCRS.WGS84.universal(startPoint.getOrdinate(1), startPoint.getOrdinate(0));
        positions    = new float[configuration.maximumTrajectoryCount * TUPLE_LENGTH];
        positions[0] = (float) startPoint.getOrdinate(1);
        positions[1] = (float) startPoint.getOrdinate(0);
        positions[WEIGHT_OFFSET] = 1;
        numOrdinates  = TUPLE_LENGTH;
        probabilities = new double[numColumns * numRows];
        probabilityChanges = new double[probabilities.length];
        trajectoryPropabilities = new double[probabilities.length];
        try {
            final MathTransform2D toModel = (MathTransform2D) modelCRS.getConversionFromBase().getMathTransform();
            toModel.transform(positions, 0, positions, 0, 1);
            modelToUV = toModel.inverse();
        } catch (TransformException ex) {
            throw new ProcessException(null, this, ex);
        }
        coordToGrid = AffineTransform.getTranslateInstance(numColumns/2, numRows/2);
        coordToGrid.scale(1./gridResolution, 1./gridResolution);
        coordToGrid.translate(-positions[0], -positions[1]);

        current = new DataSource.HYCOM(configuration.directory.resolve("HYCOM"));
        try {
            while (!currentTime.isAfter(endTime)) {
                if (!advance()) {
                    throw new ProcessException("No data at " + currentTime, this);
                }
            }
        } catch (Exception e) {
            throw new ProcessException(null, this, e);
        }
        // TODO : Put the processing

        final Path outputPath = null; // TODO : replace with real result
        final CoverageReference result = new DefaultCoverageReference(outputPath, Names.createLocalName(null, ":", "drift"));
        Parameters.castOrWrap(outputParameters).getOrCreate(DriftPredictionDescriptor.OUTPUT_DATA).setValue(result);
    }

    /**
     * Moves all drift positions by one {@link #timeStep}.
     *
     * @return {@code true} on success, or {@code false} if there is no data for this step.
     */
    private boolean advance() throws Exception {
        if (!current.load(currentTime)) {
            return false;
        }
        /*
         * At this point data have been loaded. Start computation.
         */
        final int numLastRun = numOrdinates;
        int newPosIndex = numLastRun;
        numOrdinates = 0;
        int numOnGrid = 0;
        Arrays.fill(probabilityChanges, 0);
        for (int posIndex = 0; posIndex < numLastRun;) {
            final double easting  = tmp.x = positions[posIndex++];
            final double northing = tmp.y = positions[posIndex++];
            final double weight           = positions[posIndex++];
            modelToUV.transform(tmp, tmp);
            final double u = current.u.valueAt(tmp.y, tmp.x);
            final double v = current.v.valueAt(tmp.y, tmp.x);
            final double uWind = u * (1 + Math.random());           // TODO
            final double vWind = v * (1 + Math.random());
            if (!Double.isNaN(u) && !Double.isNaN(v)) {
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
                    if (numOrdinates + TUPLE_LENGTH <= posIndex) {
                        positions[numOrdinates++] = (float) tmp.x;
                        positions[numOrdinates++] = (float) tmp.y;
                        positions[numOrdinates++] = (float) pw;
                    } else {
                        if (newPosIndex >= positions.length) {
                            double tr = Double.MAX_VALUE;
                            for (int j=numLastRun + WEIGHT_OFFSET; j < newPosIndex; j += TUPLE_LENGTH) {
                                final double p = positions[j];
                                if (p < tr) tr = p;
                            }
                            for (int j=newPosIndex + (WEIGHT_OFFSET - TUPLE_LENGTH); j >= numLastRun;) {
                                if (positions[j] == tr) {
                                    final int upper = j + (TUPLE_LENGTH - WEIGHT_OFFSET);
                                    while ((j -= TUPLE_LENGTH) >= numLastRun) {
                                        if (positions[j] != tr) break;
                                    }
                                    final int lower = j + (TUPLE_LENGTH - WEIGHT_OFFSET);
                                    final int length = upper - lower;
                                    newPosIndex -= length;
                                    System.arraycopy(positions, lower, positions, upper, length);
                                } else {
                                    j -= TUPLE_LENGTH;
                                }
                            }
                            probabilityThreshold = tr;
                        }
                        positions[newPosIndex++] = (float) tmp.x;
                        positions[newPosIndex++] = (float) tmp.y;
                        positions[newPosIndex++] = (float) pw;
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
                        if (gx >= 0 && gx < numColumns && gy >= 0 && gy < numRows) {
                            probabilityChanges[((numRows - 1) - gy) * numColumns + gx] += p;
                            numOnGrid++;
                        }
                    } while (isValid);
                    if (OUT != null) {
                        OUT.println();
                    }
                }
            }
        }
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

    private void snapshot(final String filename) throws IOException {
        save(filename, probabilities);
        Arrays.fill(probabilities, 0);
    }

    private void trajectory(final String filename) throws IOException {
        save(filename, trajectoryPropabilities);
    }

    private void save(final String filename, final double[] data) throws IOException {
        double scale  = 0;
        double offset = Double.POSITIVE_INFINITY;
        for (final double v : data) {
            if (v != 0) {
                if (v > scale)  scale = v;
                if (v < offset) offset = v;
            }
        }
        if (LOG_SCALE) {
            offset = Math.log(offset);
            scale  = Math.log(scale);
        }
        scale = 255 / (scale - offset);
        final BufferedImage img = new BufferedImage(numColumns, numRows, BufferedImage.TYPE_BYTE_INDEXED, (IndexColorModel)
                PaletteFactory.getDefault().getPalettePadValueFirst("yellow-green-blue", 256).getColorModel());
        final WritableRaster raster = img.getRaster();
        for (int i=0,y=0; y<numRows; y++) {
            for (int x=0; x<numColumns; x++) {
                double v = data[i++];
                if (v != 0) {
                    if (LOG_SCALE) v = Math.log(v);
                    raster.setSample(x, y, 0, 1 + (int) Math.round((v - offset) * scale));
                }
            }
        }
        ImageIO.write(img, "png", configuration.directory.resolve(filename).toFile());
    }
}
