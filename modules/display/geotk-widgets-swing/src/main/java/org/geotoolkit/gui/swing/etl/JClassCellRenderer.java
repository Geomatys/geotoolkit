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

import java.awt.Component;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import org.apache.sis.util.Classes;
import org.geotoolkit.process.chain.model.ClassFull;
import org.opengis.filter.Filter;
import org.opengis.metadata.Metadata;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JClassCellRenderer extends DefaultListCellRenderer{

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        final JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(value instanceof Class){
            lbl.setText(Classes.getShortName((Class)value));
        }
        return lbl;
    }

    public static String getShortSymbol(Class candidate){

        if(candidate == null) return "";

        if(Boolean.class.equals(candidate)){
            return "(B)";
        }else if(Integer.class.equals(candidate)){
            return "(I)";
        }else if(Double.class.equals(candidate)){
            return "(D)";
        }else if(String.class.equals(candidate)){
            return "(S)";
        }else if(boolean[].class.equals(candidate)){
            return "(B[])";
        }else if(int[].class.equals(candidate)){
            return "(I[])";
        }else if(double[].class.equals(candidate)){
            return "(D[])";
        }else if(String[].class.equals(candidate)){
            return "(S[])";
        }else if(Map.class.equals(candidate)){
            return "(M<K,V>)";
        }else if(Metadata.class.equals(candidate)){
            return "(Meta)";
        }else if(Filter.class.equals(candidate)){
            return "(F)";
        }

        return Classes.getShortName(candidate);
    }

    public static String getShortSymbol(ClassFull candidate){

        if(candidate == null) return "";

        final String[] parts = candidate.getName().split("\\.");
        return parts[parts.length-1];
    }


}
