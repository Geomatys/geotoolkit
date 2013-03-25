/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.gui.swing.etl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.geotoolkit.process.chain.model.Parameter;
import org.geotoolkit.process.chain.model.Parameterized;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class ParametersTableModel extends AbstractTableModel{

    private final Parameterized process;
    protected final boolean in;
    protected final boolean editable;

    public ParametersTableModel(final Parameterized process, final boolean in, final boolean editable) {
        super();
        this.process = process;
        this.in = in;
        this.editable = editable;
    }

    private void updateModel() {
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return in ? process.getInputs().size() : process.getOutputs().size();
    }

    @Override
    public int getColumnCount() {
        if (editable) {
            return 3;
        }
        return 2;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Parameter.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Parameter getValueAt(int rowIndex, int columnIndex) {

        //sort parameters.
        final List<Parameter> params = in ? process.getInputs() : process.getOutputs();
        Collections.sort(params, new Comparator<Parameter>() {
            @Override
            public int compare(Parameter o1, Parameter o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });


        final Parameter param = params.get(rowIndex);
        return param;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        Parameter param = getValueAt(rowIndex, columnIndex);
        return columnIndex > 0;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        //do nothing
    }
}
