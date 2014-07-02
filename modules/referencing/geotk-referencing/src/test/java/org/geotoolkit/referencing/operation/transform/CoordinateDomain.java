/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.referencing.operation.transform;

import java.util.Random;
import org.apache.sis.referencing.CommonCRS;
import static java.lang.StrictMath.*;


/**
 * The domain of input coordinates. This class can generate random number suitable
 * for their domain.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.00
 */
public strictfp enum CoordinateDomain {
    /**
     * Geocentric input coordinates. The input dimension must be 3.
     */
    GEOCENTRIC {
        @Override double[] generateRandomInput(final Random random, final int dimension, final int numPts) {
            if (dimension != 3) {
                throw new IllegalArgumentException();
            }
            final double axis = CommonCRS.SPHERE.ellipsoid().getSemiMajorAxis();
            final double[] ordinates = GEOGRAPHIC.generateRandomInput(random, dimension, numPts);
            for (int i=0; i<ordinates.length;) {
                final double phi    = toRadians(ordinates[i  ]);
                final double theta  = toRadians(ordinates[i+1]);
                final double radius = axis  +   ordinates[i+2];
                final double radXY  = radius * cos(theta);
                ordinates[i++] = radXY  * cos(phi);
                ordinates[i++] = radXY  * sin(phi);
                ordinates[i++] = radius * sin(theta);
            }
            return ordinates;
        }
    },

    /**
     * Geographic input coordinates with angles in decimal degrees.
     * Ordinates are in (<var>longitude</var>, <var>latitude</var>, <var>height</var>) order.
     */
    GEOGRAPHIC {
        @Override double generate(final Random random, final int dimension) {
            final double range;
            switch (dimension) {
                case 0:  range =   180; break; // Longitude.
                case 1:  range =    90; break; // Latitude.
                case 2:  range = 10000; break; // Ellipsoidal height.
                default: return super.generate(random, dimension);
            }
            return random.nextDouble() * (2*range) - range;
        }
    },

    /**
     * Geographic input coordinates avoiding poles and anti-meridian.
     * Ordinates are in (<var>longitude</var>, <var>latitude</var>, <var>height</var>) order.
     */
    GEOGRAPHIC_SAFE {
        @Override double generate(final Random random, final int dimension) {
            final double range;
            switch (dimension) {
                case 0:  range =  179; break; // Longitude, avoiding anti-meridian.
                case 1:  range =   80; break; // Latitude, avoiding pole.
                case 2:  range = 5000; break; // Ellipsoidal height.
                default: return super.generate(random, dimension);
            }
            return random.nextDouble() * (2*range) - range;
        }
    },

    /**
     * Geographic input coordinates close to the poles.
     * Ordinates are in (<var>longitude</var>, <var>latitude</var>, <var>height</var>) order.
     */
    GEOGRAPHIC_POLES {
        @Override double generate(final Random random, final int dimension) {
            final double range;
            switch (dimension) {
                case 0:  range =  180; break;
                case 1:  range =   20; break;
                case 2:  range = 5000; break;
                default: return super.generate(random, dimension);
            }
            double value = random.nextDouble() * (2*range) - range;
            if (dimension == 1) {
                if (value <= 0) {
                    value += 90;
                } else {
                    value = 90 - value;
                }
            }
            return value;
        }
    },

    /**
     * Geographic input coordinates with angles in radians.
     * Ordinates are in (<var>lambda</var>, <var>phi</var>, <var>height</var>) order.
     */
    GEOGRAPHIC_RADIANS {
        @Override double generate(final Random random, final int dimension) {
            final double range;
            switch (dimension) {
                case 0:  range = PI;    break; // Longitude.
                case 1:  range = PI/2;  break; // Latitude.
                case 2:  range = 10000; break; // Ellipsoidal height.
                default: return super.generate(random, dimension);
            }
            return random.nextDouble() * (2*range) - range;
        }
    },

    /**
     * Geographic input coordinates with angles in radians and only half of the longitude range.
     * Ordinates are in (<var>lambda</var>, <var>phi</var>, <var>height</var>) order.
     */
    GEOGRAPHIC_RADIANS_HALF {
        @Override double generate(final Random random, final int dimension) {
            final double range;
            switch (dimension) {
                case 0:  range = PI/2;  break; // Longitude.
                case 1:  range = PI/2;  break; // Latitude.
                case 2:  range = 10000; break; // Ellipsoidal height.
                default: return super.generate(random, dimension);
            }
            return random.nextDouble() * (2*range) - range;
        }
    },

    /**
     * Geographic input coordinates with angles in radians in the North hemisphere only.
     * Ordinates are in (<var>lambda</var>, <var>phi</var>, <var>height</var>) order.
     */
    GEOGRAPHIC_RADIANS_NORTH {
        @Override double generate(final Random random, final int dimension) {
            final double range;
            switch (dimension) {
                case 0:  range = PI; break;
                case 1:  return +PI/2*random.nextDouble();
                case 2:  range = 10000; break;
                default: return super.generate(random, dimension);
            }
            return random.nextDouble() * (2*range) - range;
        }
    },

    /**
     * Geographic input coordinates with angles in radians in the South hemisphere only.
     * Ordinates are in (<var>lambda</var>, <var>phi</var>, <var>height</var>) order.
     */
    GEOGRAPHIC_RADIANS_SOUTH {
        @Override double generate(final Random random, final int dimension) {
            final double range;
            switch (dimension) {
                case 0:  range = PI; break;
                case 1:  return -PI/2*random.nextDouble();
                case 2:  range = 10000; break;
                default: return super.generate(random, dimension);
            }
            return random.nextDouble() * (2*range) - range;
        }
    },

    /**
     * Geographic input coordinates with angles in radians in the East hemisphere only.
     * Ordinates are in (<var>lambda</var>, <var>phi</var>, <var>height</var>) order.
     */
    GEOGRAPHIC_RADIANS_EAST {
        @Override double generate(final Random random, final int dimension) {
            final double range;
            switch (dimension) {
                case 0:  return +PI*random.nextDouble();
                case 1:  range = PI/2;  break;
                case 2:  range = 10000; break;
                default: return super.generate(random, dimension);
            }
            return random.nextDouble() * (2*range) - range;
        }
    },

    /**
     * Geographic input coordinates with angles in radians in the West hemisphere only.
     * Ordinates are in (<var>lambda</var>, <var>phi</var>, <var>height</var>) order.
     */
    GEOGRAPHIC_RADIANS_WEST {
        @Override double generate(final Random random, final int dimension) {
            final double range;
            switch (dimension) {
                case 0:  return -PI*random.nextDouble();
                case 1:  range = PI/2;  break;
                case 2:  range = 10000; break;
                default: return super.generate(random, dimension);
            }
            return random.nextDouble() * (2*range) - range;
        }
    },

    /**
     * Projected input coordinates in a range suitable for UTM projections.
     * Ordinates are in (<var>easting</var>, <var>northing</var>, <var>height</var>) order.
     */
    PROJECTED {
        @Override double generate(final Random random, final int dimension) {
            final double range;
            switch (dimension) {
                case 0:  range =  350000; break; // Easting.
                case 1:  range = 8000000; break; // Northing.
                case 2:  range =   10000; break; // Ellipsoidal height.
                default: return super.generate(random, dimension);
            }
            return random.nextDouble() * (2*range) - range;
        }
    },

    /**
     * Gaussian numbers: can be positives or negatives, mostly close to zero but some
     * numbers can be arbitrarily large.
     *
     * @since 3.17
     */
    GAUSSIAN;

    /**
     * Generates random input coordinates.
     *
     * @param  random    The random number generator to use.
     * @param  dimension The number of dimension of the points to generate.
     * @param  numPts    The number of points to generate.
     * @return An array of length {@code numPts*dimension} filled with random input ordinate values.
     */
    double[] generateRandomInput(final Random random, final int dimension, final int numPts) {
        final double[] ordinates = new double[numPts * dimension];
        for (int i=0; i<ordinates.length; i++) {
            ordinates[i] = generate(random, i % dimension);
        }
        return ordinates;
    }

    /**
     * Generates a random number for the given dimension.
     *
     * @param  random    The random number generator to use.
     * @param  dimension The dimension for which to generate a random number.
     * @return A random number suitable for the given dimension.
     */
    double generate(final Random random, final int dimension) {
        return random.nextGaussian();
    }
}
