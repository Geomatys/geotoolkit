/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.filter;

import java.util.Optional;
import java.util.WeakHashMap;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import org.geotoolkit.gui.javafx.parameter.FXValueEditor;
import org.opengis.feature.AttributeType;
import org.opengis.feature.PropertyType;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public abstract class FXComparableOperator implements FXFilterOperator {

    private static final WeakHashMap<Node, FXValueEditor> EDITORS_IN_USE = new WeakHashMap<>();
    
    @Override
    public boolean canHandle(PropertyType target) {
        if (target instanceof AttributeType) {
            final Class valueClass = ((AttributeType) target).getValueClass();
            return valueClass.isPrimitive() || Comparable.class.isAssignableFrom(valueClass);
        } 
        return false;
    }

    @Override
    public Optional<Node> createFilterEditor(PropertyType target) {
        if (target instanceof AttributeType) {
            final AttributeType tmpType = (AttributeType) target;
            for (final FXValueEditor editor : FXValueEditor.getDefaultEditors()) {
                if (editor.canHandle(tmpType)) {
                    final FXValueEditor editorCopy = editor.copy();
                    editorCopy.setAttributeType(tmpType);
                    final Node node = editorCopy.getComponent();
                    EDITORS_IN_USE.put(node, editorCopy);
                    return Optional.of(node);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean canExtractSettings(PropertyType propertyType, Node settingsContainer) {
        if (propertyType instanceof AttributeType) {
            FXValueEditor editor = EDITORS_IN_USE.get(settingsContainer);
            return editor != null && editor.canHandle((AttributeType)propertyType);
        } else {
            return false;
        }
    }
    
    /**
     * Attempt to extract value to perform comparison against in filter. 
     * @param editor The editor which has been used for comparison parameterization.
     * @return the object found in editor for comparison. Can be null.
     * @throws IllegalArgumentException If input editor is null or has not been 
     * provided by current component.
     */
    protected Object getEditorValue(final Node editor) throws IllegalArgumentException {
        FXValueEditor valueEditor = EDITORS_IN_USE.get(editor);
        if (valueEditor == null) {
            throw new IllegalArgumentException("Filter("+getTitle()+") cannot be parameterized from given editor.");
        }
        return valueEditor.valueProperty().getValue();
    }
}
