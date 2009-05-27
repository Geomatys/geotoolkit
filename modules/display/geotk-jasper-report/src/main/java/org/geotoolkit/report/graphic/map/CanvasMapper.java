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
package org.geotoolkit.report.graphic.map;

import java.awt.Component;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;
import java.util.Collection;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRRenderable;

import org.geotoolkit.map.MapContext;
import org.geotoolkit.report.graphic.JRRendererMapper;
import org.geotoolkit.report.JRMapperFactory;
import org.opengis.referencing.operation.TransformException;


/**
 * Java2D canvas mapper.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CanvasMapper extends JRRendererMapper{

    
    CanvasMapper(JRMapperFactory<JRRenderable,MapContext> factory) {
        super(factory);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JRRenderable create(MapContext candidate, Collection<Object> renderedValues) {

        if(!(candidate instanceof MapContext)) return null;

        final MapContext context    = (MapContext) candidate;
        final CanvasRenderer canvas = new CanvasRenderer(context);
        try {
            canvas.setObjectiveCRS(context.getCoordinateReferenceSystem());
            canvas.getController().setVisibleArea(context.getBounds());

            Date date = (Date) candidate.getUserPropertie(MAP_START_DATE);
            if(date != null){
                canvas.getController().setTemporalRange(date, date);
            }

        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(CanvasMapper.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            Logger.getLogger(CanvasMapper.class.getName()).log(Level.SEVERE, null, ex);
        }catch (TransformException ex) {
            Logger.getLogger(CanvasMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return canvas;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Component getComponent() {
        return new JCanvasEditor();
    }

}
