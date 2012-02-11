/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Johann Sorel
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

import java.awt.BorderLayout;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.geotoolkit.gui.swing.referencing.AuthorityCodesComboBox;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.opengis.feature.type.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class CRSEditor implements JFeatureOutLine.PropertyEditor {

    private final CRSRW r = new CRSRW();
    private final CRSRW w = new CRSRW();

    @Override
    public boolean canHandle(PropertyType candidate) {
        return CoordinateReferenceSystem.class.equals(candidate.getBinding());
    }

    @Override
    public TableCellEditor getEditor(PropertyType property) {
        w.property = property;
        return w;
    }

    @Override
    public TableCellRenderer getRenderer(PropertyType property) {
        r.property = property;
        return r.getRenderer();
    }

    private static class CRSRW extends TableCellEditorRenderer {

        private final AuthorityCodesComboBox component = new AuthorityCodesComboBox();

        private CRSRW() {
            panel.setLayout(new BorderLayout());
            panel.add(BorderLayout.CENTER, component);
        }

        @Override
        protected void prepare() {
            if (value instanceof CoordinateReferenceSystem) {
                String code = IdentifiedObjects.getIdentifier(((CoordinateReferenceSystem) value));
                component.setSelectedCode(code);
                try {
                    if (component.getSelectedItem() == null) {
                        //strip the 'EPSG:'
                        final int index = code.indexOf(':');
                        if (index >= 0) {
                            code = code.substring(index + 1);
                            component.setSelectedCode(code);
                        }
                    }
                } catch (FactoryException ex) {
                    //no need to log
                }
            }else{
                component.setSelectedCode(null);
            }
        }

        @Override
        public Object getCellEditorValue() {
            try {
                return component.getSelectedItem();
            } catch (FactoryException ex) {
                //no need to log
                return null;
            }
        }
    }
}
