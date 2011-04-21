/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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


import java.util.logging.Level;

import net.sf.jasperreports.engine.JRField;

import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.report.JRFieldRenderer;
import org.geotoolkit.report.graphic.EmptyRenderable;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.PropertyDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MapFieldRenderer implements JRFieldRenderer{

    private static final AttributeType TYPE;
    static{
        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        atb.setName(MapDef.class.getSimpleName());
        atb.setAbstract(false);
        atb.setBinding(MapDef.class);
        atb.setIdentifiable(false);
        TYPE = atb.buildType();
    }

    @Override
    public boolean canHandle(final JRField field) {
        return field.getValueClass() == MapDef.class;
    }

    @Override
    public PropertyDescriptor createDescriptor(final JRField field) {
        final String name = field.getName();
        return new DefaultAttributeDescriptor(TYPE, DefaultName.valueOf(name), 1, 1, true, null);
    }

    @Override
    public Object createValue(final JRField field, final Feature feature) {
        final String name = field.getName();
        final Property prop = feature.getProperty(name);
        final MapDef map = (MapDef) prop.getValue();

        if(map != null && map.getDelegate() == null){
            //only create delegate if not yet assigned
            final CanvasDef canvasDef = map.getCanvasDef();
            final SceneDef sceneDef = map.getSceneDef();
            final ViewDef viewDef = map.getViewDef();

            //create the canvas
            final CanvasRenderer renderable = new CanvasRenderer(sceneDef.getContext());
            try {
                DefaultPortrayalService.prepareCanvas(renderable, canvasDef, sceneDef, viewDef);
            } catch (PortrayalException ex) {
                Logging.getLogger(MapFieldRenderer.class).log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
            map.setDelegate(renderable);
        }

        if(map != null && map.getDelegate() == null){
            map.setDelegate(EmptyRenderable.INSTANCE);
        }

        return map;
    }

}
