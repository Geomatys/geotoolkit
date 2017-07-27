package org.geotoolkit.processing.science.drift;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.referencing.operation.builder.LocalizationGridBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.filestore.FileCoverageStore;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.storage.coverage.CoverageResource;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.util.GenericName;
import ucar.ma2.ArrayFloat;
import ucar.nc2.dataset.CoordinateSystem;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.VariableDS;


/**
 * U or V component of a speed vector read from NetCDF file.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
abstract class VelocityComponent {
    /**
     * For sub-class constructors.
     */
    private VelocityComponent() {
    }

    /**
     * Returns the value of this velocity component at the given geographic location.
     * The given coordinate is usually geographic, but could also be projected.
     */
    abstract double valueAt(final double x, final double y) throws TransformException;

    /**
     * HYCOM archive file from National Centers for Environmental Prediction.
     *
     * {@preformat text
     *     dimensions:
     *         MT = UNLIMITED ; // (1 currently)
     *         Y = 3298 ;
     *         X = 4500 ;
     *         Layer = 1 ;
     *     variables:
     *         double MT(MT) ;
     *             MT:long_name = "time" ;
     *             MT:units = "days since 1900-12-31 00:00:00" ;
     *         float Latitude(Y, X) ;
     *             Latitude:units = "degrees_north" ;
     *         float Longitude(Y, X) ;
     *             Longitude:units = "degrees_east" ;
     *         float u_velocity(MT, Layer, Y, X) ;
     *             u_velocity:units = "m/s" ;
     *             u_velocity:_FillValue = 1.267651e+30f ;
     *             u_velocity:valid_range = -1.90976f, 2.590575f ;
     *         float v_velocity(MT, Layer, Y, X) ;
     *             v_velocity:units = "m/s" ;
     *             v_velocity:_FillValue = 1.267651e+30f ;
     *             v_velocity:valid_range = -2.755858f, 2.058427f ;
     * }
     */
    static final class HYCOM extends VelocityComponent {
        /**
         * Temporary file where to cache the {@link #coordToGrid} transform for next execution.
         * Set to {@code null} for disabling the cache.
         */
        private static final String CACHE = "../HYCOM_grid.serialized";

        /**
         * U or V component as an array of dimension (MT, Layer, Y, X).
         * The length of MT and Layer dimensions are 1.
         */
        private final ArrayFloat.D4 values;

        /**
         * The length of X and Y dimensions.
         */
        private final int width, height;

        /**
         * The transform from longitudes and latitudes to grid coordinates.
         */
        private final MathTransform coordToGrid;

        /**
         * A temporary buffer used for transforming coordinates.
         */
        private final double[] position;

        /**
         * Creates a new speed component for the given variable.
         *
         * @param  ds            the dataset to read.
         * @param  variableName  name of the variable to read in the dataset.
         */
        HYCOM(final NetcdfDataset ds, final String variableName, final HYCOM share, final Path directory) throws Exception {
            final VariableDS v = (VariableDS) ds.findVariable(variableName);
            Path cache = null;
            if (share != null) {
                width  = share.width;
                height = share.height;
                coordToGrid = share.coordToGrid;
            } else if (CACHE == null || !Files.exists(cache = directory.resolve(CACHE))) {
                final CoordinateSystem cs = v.getCoordinateSystems().get(0);
                final ArrayFloat.D2 lonValues = (ArrayFloat.D2) cs.getLonAxis().read();
                final ArrayFloat.D2 latValues = (ArrayFloat.D2) cs.getLatAxis().read();
                final int[] shape = lonValues.getShape();
                if (!Arrays.equals(shape, latValues.getShape())) {
                    throw new DataStoreException("Inconsistent (longitude, latitude) grid size.");
                }
                width  = shape[1];
                height = shape[0];
                final LocalizationGridBuilder builder = new LocalizationGridBuilder(width, height);
                final double[] coord = new double[2];
                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        coord[0] = lonValues.get(y, x);
                        coord[1] = latValues.get(y, x);
                        builder.setControlPoint(x, y, coord);
                    }
                }
                builder.setDesiredPrecision(1E-3);
                coordToGrid = builder.create(null).inverse();
                if (cache != null) {
                    try (final ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(cache.toFile())))) {
                        out.writeInt(width);
                        out.writeInt(height);
                        out.writeObject(coordToGrid);
                    }
                }
            } else try (final ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(cache.toFile())))) {
                width  = in.readInt();
                height = in.readInt();
                coordToGrid = (MathTransform) in.readObject();
            }
            values = (ArrayFloat.D4) v.read(new int[4], new int[] {1, 1, height, width});
            position = new double[2];
        }

        /**
         * Returns the velocity component at the given geographic location.
         */
        @Override
        double valueAt(final double x, final double y) throws TransformException {
            position[0] = x;
            position[1] = y;
            coordToGrid.transform(position, 0, position, 0, 1);
            final long gx = Math.round(position[0]);
            final long gy = Math.round(position[1]);
            if (gx >= 0 && gx < width && gy >= 0 && gy < height) {
                return values.get(0, 0, (int) gy, (int) gx);
            }
            return Double.NaN;
        }
    }

    /**
     * Météo-France grid coverage as TIFF files.
     */
    static final class MeteoFrance extends VelocityComponent {
        /**
         * The data.
         */
        private final GridCoverage coverage;

        /**
         * Temporary object for evaluating velocity component.
         */
        private final DirectPosition2D position;

        /**
         * Temporary object for evaluating velocity component.
         */
        private final double[] samples;

        MeteoFrance(final URI file) throws DataStoreException, IOException, URISyntaxException {
            try (FileCoverageStore store = new FileCoverageStore(file, "geotiff")) {
                final CoverageResource ref = store.findResource(singleton(store.getNames()));
                final GridCoverageReader reader = ref.acquireReader();
                coverage = reader.read(0, null);
                ref.recycle(reader);
            }
            position = new DirectPosition2D();
            samples = new double[1];
        }

        /**
         * Returns the singleton element in the given collection.
         * If the iteration is not over exactly one element, throws an exception.
         */
        private static GenericName singleton(final Iterable<? extends GenericName> names) throws DataStoreException {
            final Iterator<? extends GenericName> it = names.iterator();
            if (it.hasNext()) {
                final GenericName name = it.next();
                if (!it.hasNext()) return name;
            }
            throw new DataStoreException("Unexpected amount of names.");
        }

        /**
         * Returns the velocity component at the given geographic location.
         */
        @Override
        double valueAt(final double x, final double y) {
            position.x = x;
            position.y = y;
            return coverage.evaluate(position, samples)[0];
        }
    }

    /**
     * WindSat.
     *
     * {@preformat text
     *     dimensions:
     *         time = 2 ;
     *         lat = 720 ;
     *         lon = 1440 ;
     *     variables:
     *         float lat(lat) ;
     *             lat:units = "degrees_north" ;
     *         float lon(lon) ;
     *             lon:units = "degrees_east" ;
     *         byte wind_speed_aw(time, lat, lon) ;
     *             wind_speed_aw:long_name = "wind speed all weather" ;
     *             wind_speed_aw:units = "m s-1" ;
     *             wind_speed_aw:_FillValue = -128b ;
     *             wind_speed_aw:add_offset = 25.4f ;
     *             wind_speed_aw:scale_factor = 0.2f ;
     *         byte wind_direction(time, lat, lon) ;
     *             wind_direction:long_name = "wind direction" ;
     *             wind_direction:standard_name = "wind_to_direction" ;
     *             wind_direction:units = "degrees" ;
     *             wind_direction:_FillValue = -128b ;
     *             wind_direction:add_offset = 190.5f ;
     *             wind_direction:scale_factor = 1.5f ;
     * }
     *
     * Extract from CF standard names convention:
     * "to_direction" is used in the construction X_to_direction and indicates the direction towards which
     * the velocity vector of X is headed. The direction is a bearing in the usual geographical sense,
     * measured positive clockwise from due north.
     */
    static final class WindSat extends VelocityComponent {
        /**
         * Speed or direction component as an array of dimension (time, lat, lon).
         */
        private final ArrayFloat.D3 values;

        /**
         * The length of X and Y dimensions.
         */
        private final int width, height;

        /**
         * Creates a new speed component for the given variable.
         *
         * @param  ds            the dataset to read.
         * @param  variableName  name of the variable to read in the dataset.
         */
        WindSat(final NetcdfDataset ds, final String variableName) throws Exception {
            final VariableDS v = (VariableDS) ds.findVariable(variableName);
            final int[] shape = v.getShape();
            width  = shape[2];
            height = shape[1];
            values = (ArrayFloat.D3) v.read();
        }

        /**
         * Returns the velocity component nearest to the given geographic location.
         *
         * @todo we currently search for nearest value twice: once for speed component, and once for direction component.
         *       We should search only once for both.
         */
        @Override
        double valueAt(final double x, final double y) throws TransformException {
            final int gx = (int) ((x + 180) * (width  / 360d));
            final int gy = (int) ((y +  90) * (height / 180d));
            double value = Double.NaN;
            int distance = Integer.MAX_VALUE;
            for (int delta = 0; delta < 100; delta++) {
                final int xMin = Math.max(0,          gx - delta);
                final int yMin = Math.max(0,          gy - delta);
                final int xMax = Math.min(width  - 1, gx + delta);
                final int yMax = Math.min(height - 1, gy + delta);
                for (int ty = yMin; ty <= yMax; ty++) {
                    for (int tx = xMin; tx <= xMax; tx++) {
                        double v = values.get(0, ty, tx);
                        if (Double.isNaN(v)) {
                            v = values.get(1, ty, tx);
                            if (Double.isNaN(v)) {
                                continue;
                            }
                        }
                        final int dx = tx - gx;
                        final int dy = ty - gy;
                        final int d2 = dx*dx + dy*dy;
                        if (d2 < distance) {
                            distance = d2;
                            value = v;
                        }
                    }
                }
                if (!Double.isNaN(value)) break;
            }
            return value;
        }
    }
}
