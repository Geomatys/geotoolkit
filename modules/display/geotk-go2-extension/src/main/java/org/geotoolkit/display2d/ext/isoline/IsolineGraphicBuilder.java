/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.ext.isoline;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyle;

import org.opengis.display.canvas.Canvas;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class IsolineGraphicBuilder implements GraphicBuilder<GraphicJ2D>{

    private final ValueExtractor extractor;
    private MutableStyle isoLineStyle = null;
    private MutableStyle coverageStyle = null;
    private boolean interpolateCoverageColor = true;
    private int step = 10;

    public IsolineGraphicBuilder(ValueExtractor extractor){
        this.extractor = extractor;
    }

    public void setCoverageStyle(MutableStyle coverageStyle) {
        this.coverageStyle = coverageStyle;
    }

    public void setIsoLineStyle(MutableStyle isoLineStyle) {
        this.isoLineStyle = isoLineStyle;
    }

    public MutableStyle getCoverageStyle() {
        return coverageStyle;
    }

    public MutableStyle getIsoLineStyle() {
        return isoLineStyle;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    public void setInterpolateCoverageColor(boolean interpolateCoverageColor) {
        this.interpolateCoverageColor = interpolateCoverageColor;
    }

    public boolean isInterpolateCoverageColor() {
        return interpolateCoverageColor;
    }

    @Override
    public Collection<GraphicJ2D> createGraphics(MapLayer layer, Canvas canvas) {

        if(canvas instanceof ReferencedCanvas2D && layer instanceof FeatureMapLayer){
            final ReferencedCanvas2D refCanvas = (ReferencedCanvas2D) canvas;
            final Collection<GraphicJ2D> graphics = new ArrayList<GraphicJ2D>();
            
            final IsolineGraphicJ2D iso = new IsolineGraphicJ2D(refCanvas,(FeatureMapLayer)layer, extractor);
            iso.setInterpolateCoverageColor(interpolateCoverageColor);
            iso.setCoverageStyle(coverageStyle);
            iso.setIsoLineStyle(isoLineStyle);
            iso.setStep(step);

            graphics.add(iso);
            return graphics;
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    public Class<GraphicJ2D> getGraphicType() {
        return GraphicJ2D.class;
    }

    @Override
    public Image getLegend(MapLayer layer) throws PortrayalException {
        return null;
    }

}
