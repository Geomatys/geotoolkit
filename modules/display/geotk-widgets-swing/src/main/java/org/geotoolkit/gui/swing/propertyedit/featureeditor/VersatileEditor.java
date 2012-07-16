/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.gui.swing.propertyedit.featureeditor;

import javax.swing.JComponent;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.opengis.feature.Property;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class VersatileEditor implements JFeatureOutLine.PropertyEditor {

    public abstract TableCellEditorRenderer getReadingRenderer();
    public abstract TableCellEditorRenderer getWritingRenderer();

    @Override
    public JComponent getSimpleEditor(Property property) {
        final TableCellEditorRenderer editor = getWritingRenderer();
        editor.setCellEditorValue(property);
        return editor.getComponent(null, true, 0, 0);
    }

}
