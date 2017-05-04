/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.gui.javafx.style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import org.geotoolkit.internal.Loggers;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStyleElementEditor {

    /**
     * The service loader. This loader and its iterator are not synchronized;
     * when doing an iteration, the iterator must be used inside synchronized blocks.
     */
    private static final ServiceLoader<FXStyleElementController> LOADER = ServiceLoader.load(FXStyleElementController.class);


    /**
     * Search the registered StyleElementEditor for one which support the given
     * object.
     *
     * @param candidate style element to edit
     * @return StyleElementEditor or null if no editor found
     */
    public static synchronized FXStyleElementController findEditor(Object candidate) {
        for(FXStyleElementController editor : LOADER){
            if(editor.getEditedClass().isInstance(candidate)){
                try {
                    return editor.getClass().newInstance();
                } catch (InstantiationException ex) {
                    Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
                } catch (IllegalAccessException ex) {
                    Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
        return null;
    }

    /**
     * Find all editors which handle a class child of the given one.
     *
     * @param candidate style element class to edit
     * @return Collection of StyleElementEditor , never null, but can be empty.
     *      List is sorted by edited class name.
     */
    public static synchronized List<FXStyleElementController> findEditorsForType(Class candidate){
        final List<FXStyleElementController> editors = new ArrayList<FXStyleElementController>();

        for(final FXStyleElementController editor : LOADER){
            if(candidate == null || candidate.isAssignableFrom(editor.getEditedClass())){
                editors.add(editor);
            }
        }

        Collections.sort(editors, new Comparator<FXStyleElementController>(){
            @Override
            public int compare(FXStyleElementController o1, FXStyleElementController o2) {
                return o1.getEditedClass().getSimpleName().compareTo(o2.getEditedClass().getSimpleName());
            }
        });
        return editors;
    }

}
