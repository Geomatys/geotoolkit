/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    This file is derived from NGA/NASA software available for unlimited distribution.
 *    See http://earth-info.nima.mil/GandG/wgs84/gravitymod/.
 */
package org.geotoolkit.referencing.operation.transform;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import net.jcip.annotations.Immutable;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.datum.GeodeticDatum;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.parameter.Parameter;
import org.geotoolkit.parameter.ParameterGroup;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.util.collection.WeakValueHashMap;
import org.apache.sis.util.ComparisonMode;

import static java.lang.Math.*;
import static org.geotoolkit.util.Utilities.hash;
import static org.apache.sis.util.ArgumentChecks.*;
import static org.geotoolkit.referencing.operation.provider.EllipsoidToGeoid.*;


/**
 * Transforms vertical coordinates using coefficients from the
 * <A HREF="http://earth-info.nima.mil/GandG/wgs84/gravitymod/wgs84_180/wgs84_180.html">Earth
 * Gravitational Model</A>. See any of the following providers for a list of programmatic
 * parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.EllipsoidToGeoid}</li>
 * </ul>
 *
 * {@note This class is an adaption of Fortran code
 * <code><a href="http://earth-info.nga.mil/GandG/wgs84/gravitymod/wgs84_180/clenqt.for">clenqt.for</a></code>
 * from the <cite>National Geospatial-Intelligence Agency</cite> and available in public domain. The
 * <cite>normalized geopotential coefficients</cite> file bundled in this module is an adaptation of
 * <code><a href="http://earth-info.nima.mil/GandG/wgs84/gravitymod/wgs84_180/egm180.nor">egm180.nor</a></code>
 * file, with some spaces trimmed.}
 *
 * @author Pierre Cardinal
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 2.3
 * @module
 */
@Immutable
public class EarthGravitationalModel extends VerticalTransform {
    /**
     * Pre-computed values of some square roots.
     */
    private static final double SQRT_03 = 1.7320508075688772935274463415059,
                                SQRT_05 = 2.2360679774997896964091736687313,
                                SQRT_13 = 3.6055512754639892931192212674705,
                                SQRT_17 = 4.1231056256176605498214098559741,
                                SQRT_21 = 4.5825756949558400065880471937280;

    /**
     * The default maximum degree and order, which is {@value}.
     */
    public static final int DEFAULT_ORDER = 180;

    /**
     * The pool of models created up to date. We recycle existing instance in order to reduce
     * memory usage (avoid duplicating arrays), not for saving the CPU time associated to the
     * creation of the model. So we really want weak references, not soft references.
     */
    private static final WeakValueHashMap<Integer,EarthGravitationalModel> POOL = new WeakValueHashMap<>();

    /** {@code true} for WGS84 model, or {@code false} for WGS72 model. */
    private final boolean isWGS84;

    /** Maximum degree and order attained. */
    private final int nmax;

    /** WGS 84 semi-major axis. */
    private final double semiMajor;

    /** The first Eccentricity Squared (e²) for WGS 84 ellipsoid. */
    private final double esq;

    /** Even zonal coefficient. */
    private final double c2;

    /** WGS 84 Earth's Gravitational Constant w/ atmosphere. */
    private final double rkm;

    /** Theoretical (Normal) Gravity at the Equator (on the Ellipsoid). */
    private final double grava;

    /** Theoretical (Normal) Gravity Formula Constant. */
    private final double star;

    /**
     * The geopotential coefficients read from the ASCII file.
     * Those arrays are filled by the {@link #load} method.
     */
    final double[] cnmGeopCoef, snmGeopCoef;

    /**
     * Cleanshaw coefficients needed for the selected gravimetric quantities that are computed.
     * Those arrays are computed by the {@link #initialize} method.
     */
    private final double[] aClenshaw, bClenshaw, as;

    /**
     * Creates a model for the specified datum and maximum degree and order.
     * In current version, only {@linkplain DefaultGeodeticDatum#WGS84 WGS84}
     * and {@linkplain DefaultGeodeticDatum#WGS72 WGS72} datum are supported.
     *
     * @param  datum The datum for which to create the model.
     * @param  nmax  The maximum degree and order.
     * @return The model.
     * @throws IllegalArgumentException If {@code nmax} is not greater than zero,
     *         or if the given datum is not a supported one.
     * @throws FactoryException If an error occurred while loading the data.
     */
    public static EarthGravitationalModel create(final GeodeticDatum datum, final int nmax)
            throws IllegalArgumentException, FactoryException
    {
        EarthGravitationalModel model;
        final Integer key = hashCode(CRS.equalsIgnoreMetadata(DefaultGeodeticDatum.WGS84, datum), nmax);
        synchronized (POOL) {
            model = POOL.get(key);
            if (model == null) {
                model = new EarthGravitationalModel(datum, nmax);
                POOL.put(key, model);
            }
        }
        return model;
    }

    /**
     * Creates a model for the WGS84 datum with the default maximum degree and order.
     *
     * @throws FactoryException If an error occurred while loading the data.
     */
    protected EarthGravitationalModel() throws FactoryException {
        this(DefaultGeodeticDatum.WGS84, DEFAULT_ORDER);
    }

    /**
     * Creates a model for the specified datum and maximum degree and order.
     * In current version, only {@linkplain DefaultGeodeticDatum#WGS84 WGS84}
     * and {@linkplain DefaultGeodeticDatum#WGS72 WGS72} datum are supported.
     *
     * @param  datum The datum for which to create the model.
     * @param  nmax  The maximum degree and order.
     * @throws IllegalArgumentException If {@code nmax} is not greater than zero,
     *         or if the given datum is not a supported one.
     * @throws FactoryException If an error occurred while loading the data.
     */
    protected EarthGravitationalModel(final GeodeticDatum datum, final int nmax)
            throws IllegalArgumentException, FactoryException
    {
        this(datum, nmax, true);
        /*
         * Loads the data. The filename is hardcoded for now. But in a future version, we
         * may load different data for different datum if we have more example of data files.
         */
        final String filename = "EGM180.bnor";
        try {
            load(filename);
        } catch (IOException e) {
            throw new FactoryException(Errors.format(Errors.Keys.CANT_READ_FILE_1, filename), e);
        }
        initialize();
    }

    /**
     * Creates a model without loading the data. This constructor is for the test suite only.
     */
    EarthGravitationalModel(final GeodeticDatum datum, final int nmax, final boolean dummy)
            throws IllegalArgumentException
    {
        ensureNonNull("datum", datum);
        ensureBetween("nmax", 2, 9999, nmax); // Arbitrary upper limit.
        this.nmax = nmax;
        isWGS84 = CRS.equalsIgnoreMetadata(DefaultGeodeticDatum.WGS84, datum);
        if (isWGS84) {
            /*
             * WGS84 model values.
             * NOTE: The Fortran program gives 3.9860015e+14 for 'rkm' constant. This value has been
             * modified in later programs. From http://cddis.gsfc.nasa.gov/926/egm96/doc/S11.HTML :
             *
             *     "We next need to consider the determination of GM, GM0, W0, U0. The value of GM0
             *      will be that adopted for the updated GM of the WGS84 ellipsoid. This value is
             *      3.986004418e+14 m³/s², which is identical to that given in the IERS Numerical
             *      Standards [McCarthy, 1996, Table 4.1]. The best estimate of GM can be taken as
             *      the same value based on the recommendations of the IAG Special Commission SC3,
             *      Fundamental Constants [Bursa, 1995b, p. 381]."
             */
            semiMajor = 6378137.0;
            esq       = 0.00669437999013;
            c2        = 108262.9989050e-8;
            rkm       = 3.986004418e+14;
            grava     = 9.7803267714;
            star      = 0.001931851386;
        } else if (CRS.equalsIgnoreMetadata(DefaultGeodeticDatum.WGS72, datum)) {
            /*
             * WGS72 model values.
             */
            semiMajor = 6378135.0;
            esq       = 0.006694317778;
            c2        = 108263.0e-8;
            rkm       = 3.986005e+14;
            grava     = 9.7803327;
            star      = 0.005278994;
        } else {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.UNSUPPORTED_DATUM_1,
                    datum.getName().getCode()));
        }
        final int cleanshawLength = locatingArray(nmax + 3);
        final int  geopCoefLength = locatingArray(nmax + 1);
        aClenshaw   = new double[cleanshawLength];
        bClenshaw   = new double[cleanshawLength];
        cnmGeopCoef = new double[geopCoefLength];
        snmGeopCoef = new double[geopCoefLength];
        as          = new double[nmax + 1];
    }

    /**
     * Computes the index as it would be returned by the locating array {@code iv}
     * (from the Fortran code).
     * <p>
     * Tip (used in some place in this class):
     * {@code locatingArray(n+1)} == {@code locatingArray(n) + n + 1}.
     */
    static int locatingArray(final int n) {
        return ((n+1) * n) >> 1;
    }

    /**
     * Loads the coefficients from the specified binary file. Callers must invoke {@link #initialize()}
     * after this method in order to initialize the internal <cite>clenshaw arrays</cite>.
     *
     * @param  filename The filename (e.g. {@code "EGM180.nor"}, relative to this class directory.
     * @throws IOException if the file can't be read or has an invalid content.
     */
    final void load(final String filename) throws IOException {
        final InputStream stream = EarthGravitationalModel.class.getResourceAsStream(filename);
        if (stream == null) {
            throw new FileNotFoundException(filename);
        }
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(stream))) {
            for (int i=0; i<cnmGeopCoef.length; i++) {
                cnmGeopCoef[i] = in.readDouble();
                snmGeopCoef[i] = in.readDouble();
            }
        }
    }

    /**
     * Computes the <cite>clenshaw arrays</cite> after all coefficients have been read.
     * We performs this step in a separated method than {@link #from} in case we wish
     * to read the coefficient from an other source than an ASCII file in some future
     * version.
     */
    private void initialize() {
        /*
         * MODIFY CNM EVEN ZONAL COEFFICIENTS.
         */
        if (isWGS84) {
            final double[] c2n = new double[6];
            c2n[1] = c2;
            int sign = 1;
            double esqi = esq;
            for (int i=2; i<c2n.length; i++) {
                sign *= -1;
                esqi *= esq;
                c2n[i] = sign * (3*esqi) / ((2*i + 1) * (2*i + 3)) * (1-i + (5*i*c2 / esq));
            }
            /* all nmax */ cnmGeopCoef[ 3] += c2n[1] / SQRT_05;
            /* all nmax */ cnmGeopCoef[10] += c2n[2] / 3;
            /* all nmax */ cnmGeopCoef[21] += c2n[3] / SQRT_13;
            if (nmax > 6)  cnmGeopCoef[36] += c2n[4] / SQRT_17;
            if (nmax > 9)  cnmGeopCoef[55] += c2n[5] / SQRT_21;
        } else {
            /* all nmax */ cnmGeopCoef[ 3] += 4.841732e-04;
            /* all nmax */ cnmGeopCoef[10] += -7.8305e-07;
        }
        /*
         * BUILD ALL CLENSHAW COEFFICIENT ARRAYS.
         */
        for (int i=0; i<=nmax; i++) {
            as[i] = -sqrt(1.0 + 1.0/(2*(i+1)));
        }
        for (int i=0; i<=nmax; i++) {
            for (int j=i+1; j<=nmax; j++) {
                final int ll = locatingArray(j) + i;
                final int n  = 2*j + 1;
                final int ji = (j-i) * (j+i);
                aClenshaw[ll] = sqrt(n*(2*j - 1)           / (double) (ji));
                bClenshaw[ll] = sqrt(n*(j+i - 1)*(j-i - 1) / (double) (ji*(2*j - 3)));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double heightOffset(final double longitude, final double latitude, final double height) {
        /*
         * Note: no need to ensure that longitude is in [-180..+180°] range, because its value
         * is used only in trigonometric functions (sin / cos), which roll it as we would expect.
         * Latitude is used only in trigonometric functions as well.
         */
        final double φ     = toRadians(latitude);
        final double sinφ  = sin(φ);
        final double sin2φ = sinφ * sinφ;
        final double rni   = sqrt(1.0 - esq*sin2φ);
        final double rn    = semiMajor / rni;
        final double t22   = (rn + height) * cos(φ);
        final double z1    = ((rn * (1 - esq)) + height) * sinφ;
        final double th    = (PI/2) - atan(z1 / t22);
        final double y     = sin(th);
        final double t     = cos(th);
        final double f1    = semiMajor / hypot(t22, z1);
        final double f2    = f1*f1;
        final double λ     = toRadians(longitude);
        final double gravn;
        if (isWGS84) {
            gravn = grava * (1.0 + star * sin2φ) / rni;
        } else {
            gravn = grava * (1.0 + star * sin2φ) + 0.000023461 * (sin2φ * sin2φ);
        }
        final double[] cr  = new double[nmax + 1];
        final double[] sr  = new double[nmax + 1];
        final double[] s11 = new double[nmax + 3];
        final double[] s12 = new double[nmax + 3];
        sr[0]=0; sr[1]=sin(λ);
        cr[0]=1; cr[1]=cos(λ);
        for (int j=2; j<=nmax; j++) {
            sr[j] = (2.0 * cr[1] * sr[j-1]) - sr[j-2];
            cr[j] = (2.0 * cr[1] * cr[j-1]) - cr[j-2];
        }
        double sht=0, previousSht=0;
        for (int i=nmax; i>=0; i--) {
            for (int j=nmax; j>=i; j--) {
                final int    ll  = locatingArray(j) + i;
                final int    ll2 = ll  + j + 1;
                final int    ll3 = ll2 + j + 2;
                final double ta  = aClenshaw[ll2] * f1 * t;
                final double tb  = bClenshaw[ll3] * f2;
                s11[j] = (ta * s11[j + 1]) - (tb * s11[j + 2]) + cnmGeopCoef[ll];
                s12[j] = (ta * s12[j + 1]) - (tb * s12[j + 2]) + snmGeopCoef[ll];
            }
            previousSht = sht;
            sht = (-as[i] * y * f1 * sht) + (s11[i] * cr[i]) + (s12[i] * sr[i]);
        }
        return ((s11[0] + s12[0]) * f1 + (previousSht * SQRT_03 * y * f2)) * rkm /
               (semiMajor * (gravn - (height * 0.3086e-5)));
    }

    /**
     * Returns the parameter descriptors for this math transform.
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        return PARAMETERS;
    }

    /**
     * Returns the parameters for this math transform.
     */
    @Override
    public ParameterValueGroup getParameterValues() {
        return new ParameterGroup(getParameterDescriptors(),
                new Parameter<>(DATUM, isWGS84 ? "WGS84" : "WGS72"),
                new Parameter<>(ORDER, nmax));
    }

    /**
     * Returns a hash code for the given fields value. This value <strong>must</strong>
     * be different for every legal combination of the arguments. This is required for
     * proper working of the pool.
     *
     * @param  {@code true} if the model is for the WGS84 datum.
     * @param  nmax The order in the range [2 .. 180].
     * @return The hash code, or 0 if an argument is invalid.
     */
    private static int hashCode(final boolean isWGS84, int nmax) {
        if (!isWGS84) {
            nmax = ~nmax;
        }
        return nmax;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(hashCode(isWGS84, nmax), super.computeHashCode());
    }

    /**
     * Compares this transform with the given object for equality.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            // Slight optimization
            return true;
        }
        if (super.equals(object, mode)) {
            final EarthGravitationalModel that = (EarthGravitationalModel) object;
            return isWGS84 == that.isWGS84 && nmax == that.nmax;
        }
        return false;
    }
}
