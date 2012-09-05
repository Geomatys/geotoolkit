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
package org.geotoolkit.gui.swing.style;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.visitor.IsStaticExpressionVisitor;
import org.geotoolkit.gui.swing.misc.LoadingLockableUI;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyleFactory;
import static org.geotoolkit.util.ArgumentChecks.*;
import org.geotoolkit.util.logging.Logging;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Description;
import org.opengis.util.InternationalString;

/**
 * Style element editor
 * 
 * @param T : style element class edited
 * @author Johann Sorel
 * @module pending
 */
public abstract class StyleElementEditor<T> extends JPanel {

    /**
     * The service loader. This loader and its iterator are not synchronized;
     * when doing an iteration, the iterator must be used inside synchronized blocks.
     */
    private static final ServiceLoader<StyleElementEditor> LOADER = ServiceLoader.load(StyleElementEditor.class);
    
    protected static final Logger LOGGER = Logging.getLogger(StyleElementEditor.class);
    
    private static MutableStyleFactory STYLE_FACTORY = null;
    private static FilterFactory2 FILTER_FACTORY = null;
    
    private final Class<T> targetClass;

    public StyleElementEditor(Class<T> targetClass){
        this.targetClass = targetClass;
    }
    
    /**
     * 
     * @param candidate
     * @return true if the given object can be edited by this editor.
     */
    public boolean canHandle(Object candidate){
        return targetClass.isInstance(candidate);
    }

    /**
     * @return supported edited class.
     */
    public Class<T> getEditedClass() {
        return targetClass;
    }
    
    /**
     * Style element nearly always have an Expression field
     * the layer is used to fill the possible attribut in the expression editor
     * @param layer
     */
    public void setLayer(final MapLayer layer){}
    
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
    
    protected static synchronized MutableStyleFactory getStyleFactory(){
        if(STYLE_FACTORY == null){
            final Hints hints = new Hints();
            hints.put(Hints.STYLE_FACTORY, MutableStyleFactory.class);
            STYLE_FACTORY = (MutableStyleFactory)FactoryFinder.getStyleFactory(hints);
        }
        return STYLE_FACTORY;
    }

    protected static synchronized FilterFactory2 getFilterFactory(){
        if(FILTER_FACTORY == null){
            final Hints hints = new Hints();
            hints.put(Hints.FILTER_FACTORY, FilterFactory2.class);
            FILTER_FACTORY = (FilterFactory2) FactoryFinder.getFilterFactory(hints);
        }
        return FILTER_FACTORY;
    }
    
    protected boolean isStatic(final Expression exp){
        ensureNonNull("expression", exp);
        return (Boolean) exp.accept(IsStaticExpressionVisitor.VISITOR, null);
    }
    
    /**
     * Will popup a small dialog with this style editor.
     */
    public T show(final MapLayer layer, final T target){
        setLayer(layer);
        parse(target);

        JDialog dialog = new JDialog();
        dialog.setContentPane(this);
        dialog.setModal(true);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        return create();
    }

    protected static String descriptionTitleText(final Description desc){
        if(desc == null){
            return "";
        }else{
            final InternationalString str = desc.getTitle();
            if(str != null){
                return str.toString();
            }else{
                return "";
            }
        }
    }
    
    protected static String descriptionAbstractText(final Description desc){
        if(desc == null){
            return "";
        }else{
            final InternationalString str = desc.getAbstract();
            if(str != null){
                return str.toString();
            }else{
                return "";
            }
        }
    }
    
    /**
     * Search the registered StyleElementEditor for one which support the given 
     * object.
     * 
     * @param candidate
     * @return StyleElementEditor or null if no editor found
     */
    public static synchronized StyleElementEditor findEditor(Object candidate) {
        for(StyleElementEditor editor : LOADER){
            if(editor.canHandle(candidate)){
                try {
                    return editor.getClass().newInstance();
                } catch (InstantiationException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                } catch (IllegalAccessException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
        return null;        
    }
    
    /**
     * Find all editors which handle a class child of the given one.
     * 
     * @param candidate
     * @return Collection<StyleElementEditor> , never null, but can be empty.
     *      List is sorted by edited class name.
     */
    public static synchronized List<StyleElementEditor> findEditorsForType(Class candidate){
        final List<StyleElementEditor> editors = new ArrayList<StyleElementEditor>();
        
        for(StyleElementEditor editor : LOADER){
            if(candidate == null || candidate.isAssignableFrom(editor.getEditedClass())){
                editors.add(editor);
            }
        }
        
        Collections.sort(editors, new Comparator<StyleElementEditor>(){
            @Override
            public int compare(StyleElementEditor o1, StyleElementEditor o2) {
                return o1.getEditedClass().getSimpleName().compareTo(o2.getEditedClass().getSimpleName());
            }
        });
        return editors;        
    }
    
}
