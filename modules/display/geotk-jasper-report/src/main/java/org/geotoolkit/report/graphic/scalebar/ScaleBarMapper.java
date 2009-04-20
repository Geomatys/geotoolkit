/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.report.graphic.scalebar;

import java.awt.Component;
import java.util.Collection;

import net.sf.jasperreports.engine.JRRenderable;

import org.geotoolkit.map.MapContext;
import org.geotoolkit.report.graphic.JRRendererMapper;
import org.geotoolkit.report.JRMapperFactory;

import org.opengis.display.canvas.Canvas;

/**
 * Java2D scale bar mapper.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ScaleBarMapper extends JRRendererMapper{

    ScaleBarMapper(JRMapperFactory<JRRenderable,MapContext> factory){
        super(factory);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JRRenderable create(MapContext candidate, Collection<Object> renderedValues) {
        for(final Object renderedValue : renderedValues){
            if(renderedValue instanceof Canvas){
                Canvas canvas = (Canvas) renderedValue;
                ScaleBarRenderer renderer = new ScaleBarRenderer();
                renderer.setCanvas(canvas);
                return renderer;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Component getComponent() {
        return new JScaleBarEditor();
    }

}
