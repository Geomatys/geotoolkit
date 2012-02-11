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
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.opengis.feature.type.PropertyType;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class UnitEditor implements JFeatureOutLine.PropertyEditor {

    private final EnumRW r = new EnumRW();
    private final EnumRW w = new EnumRW();

    @Override
    public boolean canHandle(PropertyType candidate) {
        return Unit.class.equals(candidate.getBinding());
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

    private static class EnumRW extends TableCellEditorRenderer {

        private final JComboBox component = new JComboBox();

        private EnumRW() {
            final List<Unit> units = new ArrayList<Unit>(SI.getInstance().getUnits());
            units.addAll(NonSI.getInstance().getUnits());
            
            component.setModel(new ListComboBoxModel(units));
            component.setRenderer(new DefaultListCellRenderer(){

                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
                    
                    if(value instanceof Unit){
                        final Unit unit = (Unit) value;
                        String str = "";
                        try{
                            str = unit.toString();
                        }catch(Exception ex){
                            str = "-";
                        }
                        this.setText(str);
                    }
                    
                    return this;
                }
                
            });
            
            panel.setLayout(new BorderLayout());
            panel.add(BorderLayout.CENTER, component);
        }

        @Override
        protected void prepare() {
            if (value instanceof Unit) {
                component.setSelectedItem(value);
            }else{
                component.setSelectedItem(null);
            }
        }

        @Override
        public Object getCellEditorValue() {
            return component.getSelectedItem();
        }
    }
}
