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

package org.geotoolkit.report.graphic.scalebar;



import net.sf.jasperreports.engine.JRField;

import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.report.JRFieldRenderer;
import org.geotoolkit.report.graphic.EmptyRenderable;
import org.geotoolkit.report.graphic.map.MapDef;
import org.opengis.display.canvas.Canvas;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.PropertyDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ScaleBarFieldRenderer implements JRFieldRenderer{

    private static final AttributeType TYPE;
    static{
        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        atb.setName(ScaleBarDef.class.getSimpleName());
        atb.setAbstract(false);
        atb.setBinding(ScaleBarDef.class);
        atb.setIdentifiable(false);
        TYPE = atb.buildType();
    }

    private static final String MAP_ATTRIBUTE = "map";

    @Override
    public boolean canHandle(JRField field) {
        return field.getValueClass() == ScaleBarDef.class;
    }

    @Override
    public PropertyDescriptor createDescriptor(JRField field) throws IllegalArgumentException{
        final String name = field.getName();

        final String relatedMap = field.getPropertiesMap().getProperty(MAP_ATTRIBUTE);
        if(relatedMap == null){
            throw new IllegalArgumentException("Missing parameter 'map' in field properties.");
        }

        final DefaultAttributeDescriptor attDesc = new DefaultAttributeDescriptor(
                  TYPE, DefaultName.valueOf(name), 1, 1, true, null);
        attDesc.getUserData().put(MAP_ATTRIBUTE, relatedMap);
        return attDesc;
    }

    @Override
    public Object createValue(JRField field, Feature feature) {
        final String name = field.getName();
        final Property prop = feature.getProperty(name);
        final ScaleBarDef sb = (ScaleBarDef) prop.getValue();

        if(sb != null && sb.getDelegate() == null){
            //only create delegate if not yet assigned
            final ScaleBarRenderer renderable = new ScaleBarRenderer();
            final Property mapProp = feature.getProperty(prop.getDescriptor().getUserData().get(MAP_ATTRIBUTE).toString());
            if(mapProp != null && mapProp.getValue() instanceof MapDef){
                final MapDef md = (MapDef) mapProp.getValue();
                renderable.setCanvas((Canvas)md.getDelegate());
            }

            sb.setDelegate(renderable);
        }

        if(sb != null && sb.getDelegate() == null){
            sb.setDelegate(EmptyRenderable.INSTANCE);
        }

        return sb;
    }

}
