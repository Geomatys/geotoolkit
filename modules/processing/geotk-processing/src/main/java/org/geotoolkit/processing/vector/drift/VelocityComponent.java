package org.geotoolkit.processing.vector.drift;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import org.apache.sis.referencing.operation.builder.LocalizationGridBuilder;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
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
    abstract float valueAt(final double x, final double y) throws TransformException;

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
        private static final File CACHE = new File("/Users/desruisseaux/Data/NetCDF/NOAA/coordToGrid.tmp");

        /**
         * The variables to read.
         */
        private static final String[] VARIABLES = {
            "u_velocity",
            "v_velocity"
        };

        /**
         * U or V component as an array of dimension (MT, Layer, Y, X).
         * The length of MT and Layer dimensions are 1.
         */
        private final ArrayFloat.D4 values;

        /**
         * The value used for missing data in the NetCDF file.
         */
        private final float fillValue;

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
         * Creates a new speed component for the given dimension.
         *
         * @param ds         the dataset to read.
         * @param dimension  0 for U or 1 for V.
         */
        HYCOM(final NetcdfDataset ds, final int dimension, final VelocityComponent.HYCOM share)
                throws IOException, FactoryException, TransformException, ClassNotFoundException
        {
            final VariableDS v = (VariableDS) ds.findVariable(VARIABLES[dimension]);
            if (share != null) {
                width  = share.width;
                height = share.height;
                coordToGrid = share.coordToGrid;
            } else if (CACHE == null || !CACHE.exists()) {
                final CoordinateSystem cs = v.getCoordinateSystems().get(0);
                final ArrayFloat.D2 lonValues = (ArrayFloat.D2) cs.getLonAxis().read();
                final ArrayFloat.D2 latValues = (ArrayFloat.D2) cs.getLatAxis().read();
                final int[] shape = lonValues.getShape();
                if (!Arrays.equals(shape, latValues.getShape())) {
                    throw new IOException("Inconsistent (longitude, latitude) grid size.");
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
                coordToGrid = builder.create(null).inverse();
                if (CACHE != null) {
                    try (final ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(CACHE)))) {
                        out.writeInt(width);
                        out.writeInt(height);
                        out.writeObject(coordToGrid);
                    }
                }
            } else try (final ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(CACHE)))) {
                width  = in.readInt();
                height = in.readInt();
                coordToGrid = (MathTransform) in.readObject();
            }
            values = (ArrayFloat.D4) v.read();
            fillValue = v.findAttribute("_FillValue").getNumericValue().floatValue();
            position = new double[2];
        }

        /**
         * Returns the velocity component at the given geographic location.
         */
        @Override
        float valueAt(final double x, final double y) throws TransformException {
            position[0] = x;
            position[1] = y;
            coordToGrid.transform(position, 0, position, 0, 1);
            final long gx = Math.round(position[0]);
            final long gy = Math.round(position[1]);
            if (gx >= 0 && gx < width && gy >= 0 && gy < height) {
                final float v = values.get(0, 0, (int) gy, (int) gx);
                if (v != fillValue) {
                    return v;
                }
            }
            return Float.NaN;
        }
    }
}
