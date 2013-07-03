/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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

import java.util.Date;
import java.util.Comparator;
import java.awt.Dimension;

import org.geotoolkit.util.DateRange;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.display.shape.XRectangle2D;

import static java.lang.Double.NaN;


/**
 * Compare deux entrées {@link CoverageReference} en fonction d'un critère arbitraire. Ce
 * comparateur sert à classer un tableau d'images en fonction de leur intérêt par rapport
 * à ce qui avait été demandé. L'implémentation par défaut favorise les images dont la plage
 * de temps couvre le mieux la plage demandée (les dates de début et de fin), et n'examinera
 * la couverture spatiale que si deux images ont une couverture temporelle équivalente. Cette
 * politique est appropriée lorsque les images couvrent à peu près la même région, et que les
 * dates de ces images est le principal facteur qui varie. Les critères de comparison utilisés
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
     * The minimal and maximal values along the <var>z</var> dimension.
     */
    private final double zmin, zmax;

    /**
     * The minimal and maximal values along the <var>t</var> dimension.
     * Those values shall not be modified anymore after construction.
     */
    private long tmin, tmax;

    /**
     * An estimation of the resolution. This is approximative (not really calculated from the
     * {@code gridToCRS} transform). This is left to NaN if not applicable.
     */
    private final double scaleX, scaleY;

    /**
     * Creates a new instance for the given region of interest.
     *
     * @param regionOfInterest The spatio-temporal coordinates of the requested region
     *        in units of the database CRS.
     */
    GridCoverageComparator(final CoverageEnvelope regionOfInterest) {
        scaleX = scaleY = NaN;
        setRect(regionOfInterest.getHorizontalRange());
        final NumberRange<?> zRange = regionOfInterest.getVerticalRange();
        zmin = zRange.getMinDouble();
        zmax = zRange.getMaxDouble();
        setTimeRange(regionOfInterest.getTimeRange());
        // scaleX and scaleY are not used by this instance.
    }

    /**
     * Creates a new instance for the given entry.
     *
     * @param entry The entry for which to create a comparator.
     */
    private GridCoverageComparator(final GridCoverageReference entry) {
        setRect(entry.getXYRange());
        final GridGeometryEntry geometry = ((GridCoverageEntry) entry).getIdentifier().geometry;
        final Dimension size = geometry.getImageSize();
        scaleX = getWidth()  / size.width;
        scaleY = getHeight() / size.height;
        zmin = geometry.standardMinZ;
        zmax = geometry.standardMaxZ;
        setTimeRange(entry.getTimeRange());
    }

    /**
     * Sets the date range of this entry. This is used by constructors only.
     */
    private void setTimeRange(final DateRange range) {
        Date t;
        tmin = ((t = range.getMinValue()) != null) ? t.getTime() : Long.MIN_VALUE;
        tmax = ((t = range.getMaxValue()) != null) ? t.getTime() : Long.MAX_VALUE;
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
        final GridCoverageComparator ev1 = new GridCoverageComparator(entry1);
        final GridCoverageComparator ev2 = new GridCoverageComparator(entry2);
        long t1, t2;

        t1 = ev1.uncoveredTime(this);
        t2 = ev2.uncoveredTime(this);
        if (t1 > t2) return +1;
        if (t1 < t2) return -1;

        t1 = ev1.timeOffset(this);
        t2 = ev2.timeOffset(this);
        if (t1 > t2) return +1;
        if (t1 < t2) return -1;

        ev1.intersect(this);
        ev2.intersect(this);
        double d1 = ev1.area();
        double d2 = ev2.area();
        if (d1 < d2) return +1;
        if (d1 > d2) return -1;

        d1 = ev1.resolution();
        d2 = ev2.resolution();
        if (d1 > d2) return +1;
        if (d1 < d2) return -1;

        return 0;
    }

    /**
     * Retourne une mesure de la correspondance entre la plage de temps couverte par l'image
     * et la plage de temps qui avait été demandée.  Une valeur de 0 indique que la plage de
     * l'image correspond exactement à la plage demandée.  Une valeur supérieure à 0 indique
     * que l'image ne couvre pas toute la plage demandée,   où qu'elle couvre aussi du temps
     * en dehors de la plage demandée.
     */
    private long uncoveredTime(final GridCoverageComparator regionOfInterest) {
        final long rmin = regionOfInterest.tmin;
        final long rmax = regionOfInterest.tmax;
        final long lower  = Math.max(tmin, rmin);
        final long upper  = Math.min(tmax, rmax);
        final long range  = Math.max(0, upper-lower); // Find intersection range.
        return ((rmax-rmin) - range) +  // > 0 if image do not cover all requested range.
               ((tmax-tmin) - range);   // > 0 if image cover some part outside requested range.
    }

    /**
     * Retourne une mesure de l'écart entre la date de l'image et la date demandée.
     * Une valeur de 0 indique que l'image est exactement centrée sur la plage de
     * dates demandée. Une valeur supérieure à 0 indique que le centre de l'image
     * est décalée.
     */
    private long timeOffset(final GridCoverageComparator regionOfInterest) {
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
