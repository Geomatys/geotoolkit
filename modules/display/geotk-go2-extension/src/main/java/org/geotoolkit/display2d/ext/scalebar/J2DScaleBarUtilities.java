/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.ext.scalebar;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.FieldPosition;
import java.util.Arrays;
import javax.measure.converter.ConversionException;
import javax.measure.converter.UnitConverter;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.ext.BackgroundTemplate;
import org.geotoolkit.display2d.ext.BackgroundUtilities;
import org.geotoolkit.math.XMath;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.internal.referencing.CRSUtilities;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

/**
 * Utility class to render scalebars using a provided template and geographic information.
 *
 * @author Martin Desruisseaux
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class J2DScaleBarUtilities {

    /**
     * Round numbers for map scale, between 1 and 10. The map scale length in "real world"
     * units will be rounded to one of those numbers at rendering time.
     */
    private static final double[] SNAP = {1.0, 1.5, 2.0, 2.5, 3.0, 4.0, 5.0, 7.5, 10.0};


    private J2DScaleBarUtilities(){}

    /**
     * Paint a scalebar using Java2D.
     *
     * @param objectiveCRS : objective CRS of the map
     * @param displayCRS : display CRS of the map
     * @param geoPosition : Geographic position in objective CRS where to calculate the scale
     * @param scaleUnit : scalebar unit
     * @param g2d : Graphics2D used to render
     * @param bounds : Rectangle where the scale must be painted
     * @param template : scalebar template
     * @throws org.geotoolkit.display.exception.PortrayalException
     */
    public static void paint(final CoordinateReferenceSystem objectiveCRS,
                              final CoordinateReferenceSystem displayCRS,
                              final Point2D geoPosition,
                              final Graphics2D g2d,
                              final int x, int y,
                              final ScaleBarTemplate template) throws PortrayalException{

        final Dimension estimation = estimate(g2d, template, false);
        final Dimension bounds = template.getSize();
        int X = x;
        int Y = y;

        final BackgroundTemplate background = template.getBackground();
        if(background != null){
            final Rectangle area = new Rectangle(estimation);
            area.x = x;
            area.y = y;

            Insets insets = background.getBackgroundInsets();
            area.width += insets.left + insets.right;
            area.height += insets.top + insets.bottom;
            X += insets.left;
            Y += insets.top;

            BackgroundUtilities.paint(g2d, area, background);
        }




        final Unit scaleUnit = template.getUnit();

        ////////////////////////////////////////////////////////////////////////////
        //////                                                                  ////
        //////    BLOCK 1 - Compute the map scale length. No painting occurs    ////
        //////              here. No Graphics2D modification for now.           ////
        //////                                                                  ////
        ////////////////////////////////////////////////////////////////////////////
        /*
         * Gets an estimation of the map scale in linear units (usually kilometers). First,
         * we get an estimation of the map scale position in screen coordinates. We use a
         * coordinate system local to the legend in which the upper-left corner of the map
         * scale is located at (0,0). Ticks labels and scale title locations will be relative
         * to the map scale.
         */

        Point2D P1, P2;

        CoordinateReferenceSystem mapCRS = objectiveCRS;
        if (template.calculateUsingGeodesic() && (mapCRS instanceof ProjectedCRS)) {
            mapCRS = ((ProjectedCRS) mapCRS).getBaseCRS();
        }

        final MathTransform2D displayToObjective;
        final MathTransform2D objectiveToDisplay;
        try{
            final MathTransform transform = CRS.findMathTransform(displayCRS, objectiveCRS,true);
            final MathTransform inverse   = transform.inverse();
            if(!(transform instanceof MathTransform2D) || !(inverse instanceof MathTransform2D)){
                throw new PortrayalException("MathTransform is not 2D.");
            }

            displayToObjective = (MathTransform2D) transform;
            objectiveToDisplay = (MathTransform2D) inverse;

            //move points to the provided objective position
            final Point2D displayGeo = objectiveToDisplay.transform(geoPosition, null);
            P1 = new Point2D.Double(displayGeo.getX(), displayGeo.getY());
            P2 = new Point2D.Double(displayGeo.getX()+bounds.getWidth(), displayGeo.getY());

            P1 = displayToObjective.transform(P1, P1);
            P2 = displayToObjective.transform(P2, P2);
        }catch(FactoryException ex){
            throw new PortrayalException(ex);
        }catch(TransformException ex){
            throw new PortrayalException(ex);
        }

        /*
         * Convert the position from pixels to "real world" coordinates. Then, measures its length
         * using orthodromic distance computation if the rendering units were angular units. Then,
         * "snap" the length to some number easier to read. For example the length 2371 will be
         * snapped to 2500. Finally, the new "snapped" length will be converted bach to pixel units.
         */
        final Unit<?> mapUnitX = mapCRS.getCoordinateSystem().getAxis(0).getUnit();
        final Unit<?> mapUnitY = mapCRS.getCoordinateSystem().getAxis(1).getUnit();
        if (mapUnitX == null || mapUnitY == null) {
            throw new NullPointerException("no unit for one axi.");
        }
        double logicalLength;
        final Ellipsoid ellipsoid = CRSUtilities.getHeadGeoEllipsoid(mapCRS);
        try {

            if (ellipsoid != null && ellipsoid instanceof DefaultEllipsoid) {
                final UnitConverter xConverter = mapUnitX.getConverterToAny(NonSI.DEGREE_ANGLE);
                final UnitConverter yConverter = mapUnitY.getConverterToAny(NonSI.DEGREE_ANGLE);
                P1.setLocation(xConverter.convert(P1.getX()), yConverter.convert(P1.getY()));
                P2.setLocation(xConverter.convert(P2.getX()), yConverter.convert(P2.getY()));
                logicalLength = ((DefaultEllipsoid)ellipsoid).orthodromicDistance(P1, P2);
                logicalLength = ellipsoid.getAxisUnit().getConverterToAny(scaleUnit).convert(logicalLength);
            } else {
                final UnitConverter xConverter = mapUnitX.getConverterToAny(scaleUnit);
                final UnitConverter yConverter = mapUnitY.getConverterToAny(scaleUnit);
                P1.setLocation(xConverter.convert(P1.getX()), yConverter.convert(P1.getY()));
                P2.setLocation(xConverter.convert(P2.getX()), yConverter.convert(P2.getY()));
                logicalLength = P1.distance(P2);
            }
        } catch (ConversionException exception) {
            // Should not occurs, unless the user is using a very particular coordinate system.
            throw new PortrayalException(exception);
        }

        final double maximumLength = bounds.getWidth();
        final double scaleFactor   = logicalLength / maximumLength;
        logicalLength /= template.getDivisionCount()+0.5f;

        // If the current logical length is between two values in the SNAP array, then select
        // the lowest value. It produces a more compact scale than selecting the highest value.
        final double factor = XMath.pow10((int) Math.floor(Math.log10(logicalLength)));
        logicalLength /= factor;
        int index = Arrays.binarySearch(SNAP, logicalLength);
        if (index < 0) {
            index = ~index;  // Highest value (really ~, not -)
            if (index > 0) {
                index--; // Choose lowest value instead, if such a value exists.
            }
        }
        logicalLength = SNAP[index];
        logicalLength *= factor;

        final int visualLength = (int) Math.ceil(logicalLength / scaleFactor);


        ////////////////////////////////////////////////////////////////////////////
        //////                                                                  ////
        //////    BLOCK 2 - Compute the content. No painting occurs here.       ////
        //////              No Graphics2D modification, except through          ////
        //////              RenderingContext.setCoordinateSystem(...).          ////
        //////                                                                  ////
        ////////////////////////////////////////////////////////////////////////////
        final float thickness               = template.getThickness();
        final GlyphVector[] tickGlyphs      = new GlyphVector[template.getDivisionCount() + 2];
        final Rectangle2D[] tickBounds      = new Rectangle2D[template.getDivisionCount() + 2];
        final FontRenderContext fontContext = g2d.getFontRenderContext();
        final Font font                     = template.getFont();
        final StringBuffer buffer           = new StringBuffer(16);
        final FieldPosition pos             = new FieldPosition(0);
        
        for (int i=0,n=template.getDivisionCount(); i<=n; i++) {
            String text = template.getNumberFormat().format(logicalLength * i, buffer, pos).toString();
            GlyphVector glyphs = font.createGlyphVector(fontContext, text);
            Rectangle2D rect = glyphs.getVisualBounds();
            rect.setRect(visualLength * i, thickness + 3, rect.getWidth(), rect.getHeight());
            if (i == n) {
                buffer.append(' ');
                buffer.append(scaleUnit);
                final double anchorX = rect.getMinX();
                final double anchorY = rect.getMinY();
                text = buffer.toString();
                glyphs = font.createGlyphVector(fontContext, text);
                rect = glyphs.getVisualBounds();
                rect.setRect(anchorX, anchorY, rect.getWidth(), rect.getHeight());
            }
            tickBounds[i] = rect;
            tickGlyphs[i] = glyphs;
            buffer.setLength(0);
        }

        ////////////////////////////////////////////////////////////////////////////
        //////                                                                  ////
        //////    BLOCK 3 - Paint the content.                                  ////
        //////                                                                  ////
        ////////////////////////////////////////////////////////////////////////////
        g2d.translate(X, Y);
        g2d.setStroke(new BasicStroke(1));

        final Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, visualLength, thickness);
        for (int i=0,n=template.getDivisionCount(); i<n; i++) {

            final Paint fill = ((i & 1) != 0) ? template.getFirstRectanglePaint() :
                                                template.getSecondRectanglePaint() ;
            g2d.setPaint(fill);
            g2d.fill(rect);
            g2d.setPaint(template.getForeground());
            g2d.draw(rect);
            rect.x += visualLength;
        }

        /*
         * Writes tick labels, units and map scale legend.
         */
        g2d.setPaint(template.getForeground());
        for (int i = 0; i < tickGlyphs.length; i++) {
            if (tickGlyphs[i] != null) {
                final Rectangle2D tick = tickBounds[i];
                g2d.drawGlyphVector(tickGlyphs[i], (float) tick.getMinX(),(float) tick.getMaxY());
            }
        }

    }

    public static Dimension estimate(Graphics2D g, ScaleBarTemplate template, boolean considerBackground){
        final Dimension dim = new Dimension(0, 0);

        dim.width = template.getSize().width;
        dim.height = template.getSize().height;

        if(considerBackground && template.getBackground() != null){
            final Insets insets = template.getBackground().getBackgroundInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.bottom + insets.top;
        }

        return dim;
    }

}
