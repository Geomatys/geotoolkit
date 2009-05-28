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
package org.geotoolkit.report.graphic.scalebar;

import java.awt.Image;
import javax.swing.ImageIcon;
import net.sf.jasperreports.engine.JRRenderable;

import org.geotoolkit.map.MapContext;
import org.geotoolkit.report.JRMapperFactory;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.util.InternationalString;

/**
 * Factory to create java2d scale bar mappers.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ScaleBarMapperFactory implements JRMapperFactory<JRRenderable,MapContext>{

    private static final ImageIcon ICON = new ImageIcon(ScaleBarMapperFactory.class.getResource("/org/geotoolkit/report/scale.png"));
    private static final InternationalString TITLE = new SimpleInternationalString("Scale Bar");
    private static final String[] FAVORITES = new String[]{"GO2-Scale-Bar"};

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<JRRenderable> getValueClass() {
        return JRRenderable.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ScaleBarMapper createMapper() {
        return new ScaleBarMapper(this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Image getIcon(int type) {
        return ICON.getImage();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InternationalString getTitle() {
        return TITLE;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class getFieldClass() {
        return Object.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<MapContext> getRecordClass() {
        return MapContext.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getFavoritesFieldName() {
        return FAVORITES;
    }

}
