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

package org.geotoolkit.report.graphic.legend;



import net.sf.jasperreports.engine.JRField;
import org.apache.sis.feature.SingleAttributeTypeBuilder;

import org.geotoolkit.util.NamesExt;
import org.geotoolkit.report.JRFieldRenderer;
import org.geotoolkit.report.graphic.EmptyRenderable;
import org.geotoolkit.report.graphic.map.MapDef;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LegendFieldRenderer implements JRFieldRenderer{

    private static final String MAP_ATTRIBUTE = "map";

    @Override
    public boolean canHandle(final JRField field) {
        return field.getValueClass() == LegendDef.class;
    }

    @Override
    public AttributeType createDescriptor(final JRField field) throws IllegalArgumentException{
        final String name = field.getName();

        final String relatedMap = field.getPropertiesMap().getProperty(MAP_ATTRIBUTE);
        if(relatedMap == null){
            throw new IllegalArgumentException("Missing parameter 'map' in field properties.");
        }

        final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
        atb.setName(name);
        atb.setValueClass(LegendDef.class);
        atb.addCharacteristic(MAP_ATTRIBUTE, String.class, 1, 1, relatedMap);
        return atb.build();
    }

    @Override
    public Object createValue(final JRField field, final Feature feature) {
        final String name = field.getName();
        final Attribute prop = (Attribute) feature.getProperty(name);
        final LegendDef legend = (LegendDef) prop.getValue();

        if(legend != null && legend.getDelegate() == null){
            //only create delegate if not yet assigned
            final LegendRenderer renderable = new LegendRenderer();
            final Attribute mapChar = (Attribute) prop.characteristics().get(MAP_ATTRIBUTE);
            final Attribute mapProp = (Attribute) feature.getProperty((String)mapChar.getValue());
            if(mapProp != null && mapProp.getValue() instanceof MapDef){
                final MapDef md = (MapDef) mapProp.getValue();
                renderable.setContext(md.getSceneDef().getContext());
            }

            legend.setDelegate(renderable);
        }

        if(legend != null && legend.getDelegate() == null){
            legend.setDelegate(EmptyRenderable.INSTANCE);
        }

        return legend;
    }

}
