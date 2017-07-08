/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.propertyedit.filterproperty;

import java.awt.Image;
import java.util.logging.Level;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.gui.swing.filter.JCQLEditor;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.map.FeatureMapLayer;
import org.apache.sis.util.logging.Logging;

/**
 * CQL property panel
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
public class JCQLPropertyPanel extends JCQLEditor implements PropertyPane{

    private FeatureMapLayer layer;

    private void parse(){
        setLayer(layer);
        setFilter(layer.getQuery().getFilter());
    }

    @Override
    public boolean canHandle(Object target) {
        return target instanceof FeatureMapLayer;
    }

    @Override
    public void setTarget(final Object target) {
        if (target instanceof FeatureMapLayer) {
            layer = (FeatureMapLayer) target;
            parse();
        }
    }

    @Override
    public void apply() {
        if(layer !=null){
            try {
                layer.setQuery(QueryBuilder.filtered(layer.getCollection().getType().getName().toString(), getFilter()));
            } catch (CQLException ex) {
                Logging.getLogger("org.geotoolkit.gui.swing.propertyedit.filterproperty").log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void reset() {
        parse();
    }

    @Override
    public Image getPreview() {
        return null;
    }

}
