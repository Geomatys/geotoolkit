/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.gui.swing.propertyedit;

import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.netbeans.swing.outline.RowModel;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class LayerGeneralTableRowModel implements RowModel {
    public static class CrsCookie{}
    public static class LowerCookie{}
    public static class UpperCookie{}
    public static class DeleteCookie{}

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueFor(Object o, int i) {
        return o;
    }

    @Override
    public Class getColumnClass(int i) {
        switch(i){
            case 0 : return CrsCookie.class;
            case 1 : return LowerCookie.class;
            case 2 : return UpperCookie.class;
            case 3 : return DeleteCookie.class;
            default: return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(Object o, int i) {
        return true;
    }

    @Override
    public void setValueFor(Object o, int i, Object o1) {
    }

    @Override
    public String getColumnName(int i) {
        switch(i){
            case 0 : return MessageBundle.getString("colCrs");
            case 1 : return MessageBundle.getString("colLower");
            case 2 : return MessageBundle.getString("colUpper");
            case 3 : return MessageBundle.getString("colDelete");
            default: return "";
        }
    }

}
