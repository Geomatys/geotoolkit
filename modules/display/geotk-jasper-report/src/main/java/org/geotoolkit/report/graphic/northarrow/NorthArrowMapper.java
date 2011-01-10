/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.report.graphic.northarrow;

import java.awt.Component;
import java.util.Collection;

import net.sf.jasperreports.engine.JRRenderable;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.report.graphic.JRRendererMapper;
import org.geotoolkit.report.JRMapperFactory;

import org.opengis.display.canvas.Canvas;

/**
 * Java2D north arrow mapper.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 * @deprecated
 */
@Deprecated
public class NorthArrowMapper extends JRRendererMapper{

    NorthArrowMapper(final JRMapperFactory<JRRenderable,MapContext> factory){
        super(factory);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JRRenderable create(final MapContext candidate, final Collection<Object> renderedValues) {
        for(final Object renderedValue : renderedValues){
            if(renderedValue instanceof Canvas){
                Canvas canvas = (Canvas) renderedValue;
                NorthArrowRenderer renderer = new NorthArrowRenderer();
                renderer.setRotation( ((ReferencedCanvas2D)canvas).getController().getRotation());
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
        return new JNorthArrowEditor();
    }

}
