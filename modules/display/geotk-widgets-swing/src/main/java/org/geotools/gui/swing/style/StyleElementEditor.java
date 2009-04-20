/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gui.swing.style;

import javax.swing.JPanel;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotools.filter.visitor.IsStaticExpressionVisitor;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;

/**
 * Style element editor
 * 
 * @param T : style element class edited
 * @author Johann Sorel
 */
public abstract class StyleElementEditor<T> extends JPanel {

    private static MutableStyleFactory STYLE_FACTORY = null;
    private static FilterFactory2 FILTER_FACTORY = null;

    public StyleElementEditor(){}
    
    /**
     * Style element nearly always have an Expression field
     * the layer is used to fill the possible attribut in the expression editor
     * @param layer
     */
    public void setLayer(MapLayer layer){}
    
    /**
     * Layer used for expression edition in the style element
     * @return MapLayer
     */
    public MapLayer getLayer(){
        return null;
    }
    
    /**
     * the the edited object
     * @param target : object to edit
     */
    public abstract void parse(T target);
    
    /**
     * return the edited object if there is one.
     * Id no edited object has been set this will create a new one.
     * @return T object
     */
    public abstract T create();
    
    public void apply(){
    }
    
    protected synchronized static final MutableStyleFactory getStyleFactory(){
        if(STYLE_FACTORY == null){
            final Hints hints = new Hints();
            hints.put(Hints.STYLE_FACTORY, MutableStyleFactory.class);
            STYLE_FACTORY = (MutableStyleFactory)FactoryFinder.getStyleFactory(hints);
        }
        return STYLE_FACTORY;
    }

    protected synchronized static final FilterFactory2 getFilterFactory(){
        if(FILTER_FACTORY == null){
            final Hints hints = new Hints();
            hints.put(Hints.FILTER_FACTORY, FilterFactory2.class);
            FILTER_FACTORY = (FilterFactory2) FactoryFinder.getFilterFactory(hints);
        }
        return FILTER_FACTORY;
    }
    
    protected boolean isStatic(final Expression exp){
        if(exp == null) throw new NullPointerException("Expression is null");
        return (Boolean) exp.accept(IsStaticExpressionVisitor.VISITOR, null);
    }
    
}
