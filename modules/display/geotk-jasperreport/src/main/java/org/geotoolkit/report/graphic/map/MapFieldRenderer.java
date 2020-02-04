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
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.feature.SingleAttributeTypeBuilder;
import org.geotoolkit.report.JRFieldRenderer;
import org.geotoolkit.report.graphic.EmptyRenderable;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MapFieldRenderer implements JRFieldRenderer{

    @Override
    public boolean canHandle(final JRField field) {
        return field.getValueClass() == MapDef.class;
    }

    @Override
    public AttributeType createDescriptor(final JRField field) {
        final String name = field.getName();
        final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
        atb.setName(name);
        atb.setValueClass(MapDef.class);
        return atb.build();
    }

    @Override
    public Object createValue(final JRField field, final Feature feature) {
        final String name = field.getName();
        final MapDef map = (MapDef) feature.getPropertyValue(name);

        if(map != null && map.getDelegate() == null){
            //only create delegate if not yet assigned
            final CanvasDef canvasDef = map.getCanvasDef();
            final SceneDef sceneDef = map.getSceneDef();

            //create the canvas
            final CanvasRenderer renderable = new CanvasRenderer(sceneDef.getContext());
            try {
                DefaultPortrayalService.prepareCanvas(renderable, canvasDef, sceneDef);
            } catch (PortrayalException ex) {
                Logging.getLogger("org.geotoolkit.report.graphic.map").log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
            map.setDelegate(renderable);
        }

        if(map != null && map.getDelegate() == null){
            map.setDelegate(EmptyRenderable.INSTANCE);
        }

        return map;
    }

}
