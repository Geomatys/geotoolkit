/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2004-2008, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.display2d.ext.scalebar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.NumberFormat;
import javax.measure.unit.Unit;

/**
 * Default scalebar template, immutable.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultScaleBarTemplate implements ScaleBarTemplate{

    private final float thickness;
    private final boolean geodesic;
    private final int divisions;
    private final float tikHeight;
    private final NumberFormat format;
    private final Paint foreground;
    private final Paint secondRectPaint;
    private final Paint firstRectPaint;
    private final boolean scaleVisible;
    private final boolean useAbreviation;
    private final boolean textAboveBar;
    private final Font font;
    private final Unit unit;


    public DefaultScaleBarTemplate(int thickness, boolean geodesic,
                        int subdivisions, NumberFormat format, Paint foreground, 
                        Paint firstRect, Paint secondRect, int tikHeight,
                        boolean scaleVisible, boolean useAbreviation,
                        Font font, boolean textAboveBar, Unit unit){

        if(format == null) format         = NumberFormat.getInstance();
        if(foreground == null) foreground = Color.BLACK;
        if(firstRect == null) firstRect   = Color.WHITE;
        if(secondRect == null) secondRect = Color.BLACK;

        this.thickness = thickness;
        this.geodesic = geodesic;
        this.divisions = subdivisions;
        this.format = format;
        this.foreground = foreground;
        this.firstRectPaint = firstRect;
        this.secondRectPaint = secondRect;
        this.tikHeight = tikHeight;
        this.scaleVisible = scaleVisible;
        this.useAbreviation = useAbreviation;
        this.font = font;
        this.textAboveBar = textAboveBar;
        this.unit = unit;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getThickness() {
        return thickness;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean calculateUsingGeodesic() {
        return geodesic;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getDivisionCount() {
        return divisions;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public NumberFormat getNumberFormat() {
        return format;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Paint getForeground() {
        return foreground;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Paint getFirstRectanglePaint() {
        return firstRectPaint;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Paint getSecondRectanglePaint() {
        return secondRectPaint;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getTickHeight() {
        return tikHeight;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isScaleVisible() {
        return scaleVisible;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean useUnitAbreviation() {
        return useAbreviation;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Font getFont() {
        return font;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isTextAboveBar() {
        return textAboveBar;
    }

    @Override
    public Unit getUnit() {
        return unit;
    }

}
