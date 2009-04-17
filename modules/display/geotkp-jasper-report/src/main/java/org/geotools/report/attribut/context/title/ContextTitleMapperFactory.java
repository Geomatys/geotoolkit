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

package org.geotools.report.attribut.context.title;

import java.awt.Image;
import javax.swing.ImageIcon;

import org.geotools.map.MapContext;
import org.geotools.report.JRMapper;
import org.geotools.report.JRMapperFactory;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.util.InternationalString;

/**
 * Factory to create context title mappers.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ContextTitleMapperFactory implements JRMapperFactory<String,MapContext>{

    private static final ImageIcon ICON = new ImageIcon(ContextTitleMapperFactory.class.getResource("/org/geotools/report/text.png"));
    private static final InternationalString TITLE = new SimpleInternationalString("Map title");
    private static final String[] FAVORITES = new String[]{"CONTEXT-Title"};

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<String> getValueClass() {
        return String.class;
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
        return String.class;
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

    /**
     * {@inheritDoc }
     */
    @Override
    public JRMapper<String, MapContext> createMapper() {
        return new ContextTitleMapper(this);
    }

}
