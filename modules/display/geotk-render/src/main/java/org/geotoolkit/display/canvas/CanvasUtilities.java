/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.display.canvas;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import javax.measure.UnitConverter;
import javax.measure.Unit;
import org.apache.sis.measure.Units;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.referencing.GeodeticCalculator;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CanvasUtilities {

    /**
     * Used in SLD/SE to calculate scale for degree CRSs.
     */
    private static final double SE_DEGREE_TO_METERS = 6378137.0 * 2.0 * Math.PI / 360;
    private static final double DEFAULT_DPI = 90; // ~ 0.28 * 0.28mm
    private static final double PIXEL_SIZE = 0.0254;

    private CanvasUtilities(){}

    /**
     * Returns the geographic scale, in a ground unit manner, relation between map display size
     * and real ground unit meters.
     *
     * @param center
     * @param objToDisp
     * @param crs
     * @return
     * @throws org.opengis.referencing.operation.TransformException
     * @throws IllegalStateException If the affine transform used for conversion is in illegal state.
     */
    public static double getGeographicScale(Point2D center, AffineTransform2D objToDisp, CoordinateReferenceSystem crs) throws TransformException {
        final double[] P1 = new double[]{center.getX(), center.getY()};
        final double[] P2 = new double[]{P1[0], P1[1] + 1};

        final AffineTransform trs;
        try {
            trs = objToDisp.createInverse();
        } catch (NoninvertibleTransformException ex) {
            throw new TransformException(ex.getLocalizedMessage(), ex);
        }
        trs.transform(P1, 0, P1, 0, 1);
        trs.transform(P2, 0, P2, 0, 1);

        final Unit unit = crs.getCoordinateSystem().getAxis(0).getUnit();

        final double distance;
        if (unit.isCompatible(Units.METRE)) {
            final Point2D p1 = new Point2D.Double(P1[0], P1[1]);
            final Point2D p2 = new Point2D.Double(P2[0], P2[1]);
            final UnitConverter conv = unit.getConverterTo(Units.METRE);
            distance = conv.convert(p1.distance(p2));
        } else {
            /*
             * If the latitude ordinates (for example) are outside the +/-90°
             * range, translate the points in order to bring them back in the
             * domain of validity.
             */
            final CoordinateSystem cs = crs.getCoordinateSystem();
            for (int i = cs.getDimension(); --i >= 0;) {
                final CoordinateSystemAxis axis = cs.getAxis(i);
                double delta = P1[i] - axis.getMaximumValue();
                if (delta > 0) {
                    P1[i] -= delta;
                    P2[i] -= delta;
                }
                delta = P2[i] - axis.getMaximumValue();
                if (delta > 0) {
                    P1[i] -= delta;
                    P2[i] -= delta;
                }
                delta = axis.getMinimumValue() - P1[i];
                if (delta > 0) {
                    P1[i] += delta;
                    P2[i] += delta;
                }
                delta = axis.getMinimumValue() - P2[i];
                if (delta > 0) {
                    P1[i] += delta;
                    P2[i] += delta;
                }
            }
            final GeodeticCalculator gc = new GeodeticCalculator(crs);
            final GeneralDirectPosition pos1 = new GeneralDirectPosition(crs);
            pos1.setOrdinate(0, P1[0]);
            pos1.setOrdinate(1, P1[1]);
            final GeneralDirectPosition pos2 = new GeneralDirectPosition(crs);
            pos2.setOrdinate(0, P2[0]);
            pos2.setOrdinate(1, P2[1]);
            try {
                gc.setStartingPosition(pos1);
                gc.setDestinationPosition(pos2);
            } catch (TransformException ex) {
                throw new TransformException(ex.getLocalizedMessage(), ex);
            } catch (IllegalArgumentException ex) {
                //might happen when changing projection and moving the area.
                //the coordinate can be out of the crs area, which causes this exception
                throw new TransformException(ex.getLocalizedMessage(), ex);
            }
            distance = Math.abs(gc.getOrthodromicDistance());
        }

        final double displayToDevice = 1f / DEFAULT_DPI * 0.0254f;
        return distance / displayToDevice;
    }

    /**
     * @param envelope : canvas objective bounds 2D
     * @param objToDisp
     * @param dispBounds
     * @return
     */
    public static double computeSEScale(final Envelope envelope, AffineTransform objToDisp, Rectangle dispBounds) {
        final CoordinateReferenceSystem objCRS = envelope.getCoordinateReferenceSystem();
        final int width = dispBounds.width;

        if (AffineTransforms2D.getRotation(objToDisp) != 0.0) {
            final double scale = XAffineTransform.getScale(objToDisp);
            if(objCRS instanceof GeographicCRS) {
                return (SE_DEGREE_TO_METERS*DEFAULT_DPI) / (scale*PIXEL_SIZE);
            } else {
                return DEFAULT_DPI / (scale *PIXEL_SIZE);
            }
        }else{
            if(objCRS instanceof GeographicCRS) {
                return (envelope.getSpan(0) * SE_DEGREE_TO_METERS) / (width / DEFAULT_DPI*PIXEL_SIZE);
            } else {
                return envelope.getSpan(0) / (width / DEFAULT_DPI*PIXEL_SIZE);
            }
        }
    }

}
