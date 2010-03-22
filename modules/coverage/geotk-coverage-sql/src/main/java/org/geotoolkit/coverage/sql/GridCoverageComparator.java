/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.Comparator;
import java.awt.Dimension;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.util.DateRange;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;

import static java.lang.Double.NaN;


/**
 * Compare deux entrées {@link CoverageReference} en fonction d'un critère arbitraire. Ce
 * comparateur sert à classer un tableau d'images en fonction de leur intérêt par rapport
 * à ce qui avait été demandé. L'implémentation par défaut favorise les images dont la plage
 * de temps couvre le mieux la plage demandée (les dates de début et de fin), et n'examinera
 * la couverture spatiale que si deux images ont une couverture temporelle équivalente. Cette
 * politique est appropriée lorsque les images couvrent à peu près la même région, et que les
 * dates de ces images est le principal facteur qui varie. Les critères de comparaison utilisés
 * sont:
 * <p>
 * <ul>
 *  <li>Pour chaque image, la quantité [<i>temps à l'intérieur de la plage de temps
 *      demandée</i>]-[<i>temps à l'extérieur de la plage de temps demandé</i>] sera
 *      calculée. Si une des image à une quantité plus grande, elle sera choisie.</li>
 *  <li>Sinon, si une image se trouve mieux centrée sur la plage de temps demandée, cette
 *      image sera choisie.</li>
 *  <li>Sinon, pour chaque image, l'intersection entre la région de l'image et la région
 *      demandée sera obtenue, et la superficie de cette intersection calculée. Si une
 *      des images obtient une valeur plus grande, cette image sera choisie.</li>
 *  <li>Sinon, la superficie moyenne des pixels des images seront calculées. Si une image
 *      a des pixels d'une meilleure résolution (couvrant une surface plus petite), cette
 *      image sera choisie.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
@SuppressWarnings("serial")
final class GridCoverageComparator extends XRectangle2D implements Comparator<GridCoverageReference> {
    /**
     * The minimal and maximal values along the <var>z</var> and <var>t</var> dimensions.
     * This is left to NaN if there is none. Those values shall not be modified anymore
     * after construction.
     */
    private double zmin, zmax, tmin, tmax;

    /**
     * An estimation of the resolution. This is approximative (not really calculated from the
     * {@code gridToCRS} transform). This is left to NaN if not applicable. Those values shall
     * not be modified anymore after construction.
     */
    private double scaleX, scaleY;

    /**
     * Creates a new instance for the given region of interest. Exactly one of the {@code entry}
     * and {@code regionOfInterest} arguments shall be non-null.
     *
     * @param entry The entry for which to create a comparator, or {@code null}.
     * @param regionOfInterest The spatio-temporal coordinates of the requested region
     *        in units of the database CRS, or {@code null}.
     */
    GridCoverageComparator(final GridCoverageReference entry, final Envelope regionOfInterest) {
        xmin = xmax = ymin = ymax = zmin = zmax = tmin = tmax = scaleX = scaleY = NaN;
        int xDim = -1;
        int yDim = -1;
        int zDim = -1;
        int tDim = -1;
        final CoordinateReferenceSystem crs;
        if (entry != null) {
            crs = entry.getSpatioTemporalCRS(true);
        } else {
            crs = regionOfInterest.getCoordinateReferenceSystem();
        }
        final CoordinateSystem cs = crs.getCoordinateSystem();
        for (int i=cs.getDimension(); --i>=0;) {
            final AxisDirection orientation = cs.getAxis(i).getDirection().absolute();
            if (orientation.equals(AxisDirection.EAST  )) xDim = i;
            if (orientation.equals(AxisDirection.NORTH )) yDim = i;
            if (orientation.equals(AxisDirection.UP    )) zDim = i;
            if (orientation.equals(AxisDirection.FUTURE)) tDim = i;
        }
        if (entry != null) {
            setRect(entry.getXYRange());
            final GridGeometryEntry geometry = ((GridCoverageEntry) entry).getIdentifier().geometry;
            final Dimension size = geometry.getImageSize();
            if (xDim >= 0) scaleX = getWidth()  / size.width;
            if (yDim >= 0) scaleY = getHeight() / size.height;
            if (zDim >= 0) {
                zmin = geometry.standardMinZ;
                zmax = geometry.standardMaxZ;
            }
            if (tDim >= 0) {
                final DefaultTemporalCRS temporalCRS = geometry.getTemporalCRS();
                if (temporalCRS != null) {
                    final DateRange timeRange = entry.getTimeRange();
                    tmin = temporalCRS.toValue(timeRange.getMinValue());
                    tmax = temporalCRS.toValue(timeRange.getMaxValue());
                }
            }
        } else {
            if (xDim >= 0) {
                xmin = regionOfInterest.getMinimum(xDim);
                xmax = regionOfInterest.getMaximum(xDim);
            }
            if (yDim >= 0) {
                ymin = regionOfInterest.getMinimum(yDim);
                ymax = regionOfInterest.getMaximum(yDim);
            }
            if (zDim >= 0) {
                zmin = regionOfInterest.getMinimum(zDim);
                zmax = regionOfInterest.getMaximum(zDim);
            }
            if (tDim >= 0) {
                tmin = regionOfInterest.getMinimum(tDim);
                tmax = regionOfInterest.getMaximum(tDim);
            }
        }
    }

    /**
     * Compares two {@link GridCoverageReference} entries.
     *
     * @return +1 if {@code entry1} should be preferred to {@code entry2}.
     *         -1 if {@code entry2} should be preferred to {@code entry1}.
     *          0 if both entries seem of equal interest.
     */
    @Override
    public int compare(final GridCoverageReference entry1, final GridCoverageReference entry2) {
        final GridCoverageComparator ev1 = new GridCoverageComparator(entry1, null);
        final GridCoverageComparator ev2 = new GridCoverageComparator(entry2, null);
        double t1, t2;

        t1 = ev1.uncoveredTime(this);
        t2 = ev2.uncoveredTime(this);
        if (t1 > t2) return +1;
        if (t1 < t2) return -1;

        t1 = ev1.timeOffset(this);
        t2 = ev2.timeOffset(this);
        if (t1 > t2) return +1;
        if (t1 < t2) return -1;

        intersect(this, ev1, ev1);
        intersect(this, ev2, ev2);
        t1 = ev1.area();
        t2 = ev2.area();
        if (t1 < t2) return +1;
        if (t1 > t2) return -1;

        t1 = ev1.resolution();
        t2 = ev2.resolution();
        if (t1 > t2) return +1;
        if (t1 < t2) return -1;

        return 0;
    }

    /**
     * Retourne une mesure de la correspondance entre la plage de temps couverte par l'image
     * et la plage de temps qui avait été demandée.  Une valeur de 0 indique que la plage de
     * l'image correspond exactement à la plage demandée.  Une valeur supérieure à 0 indique
     * que l'image ne couvre pas toute la plage demandée,   où qu'elle couvre aussi du temps
     * en dehors de la plage demandée.
     */
    private double uncoveredTime(final GridCoverageComparator regionOfInterest) {
        final double rmin = regionOfInterest.tmin;
        final double rmax = regionOfInterest.tmax;
        final double lower  = Math.max(tmin, rmin);
        final double upper  = Math.min(tmax, rmax);
        final double range  = Math.max(0, upper-lower); // Find intersection range.
        return ((rmax-rmin) - range) +  // > 0 if image do not cover all requested range.
               ((tmax-tmin) - range);   // > 0 if image cover some part outside requested range.
    }

    /**
     * Retourne une mesure de l'écart entre la date de l'image et la date demandée.
     * Une valeur de 0 indique que l'image est exactement centrée sur la plage de
     * dates demandée. Une valeur supérieure à 0 indique que le centre de l'image
     * est décalée.
     */
    private double timeOffset(final GridCoverageComparator regionOfInterest) {
        return Math.abs((tmin - regionOfInterest.tmin) +
                        (tmax - regionOfInterest.tmax));
    }

    /**
     * Returns an estimation of the area, in units of the database CRS
     * (may be a product of angles). Greater is better.
     */
    private double area() {
        return getWidth() * getHeight();
    }

    /**
     * Returns an estimation of the resolution.
     * Smaller is better.
     */
    private double resolution() {
        return Math.hypot(scaleX, scaleY);
    }
}
