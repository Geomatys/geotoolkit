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
package org.geotoolkit.data.s57.annexe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.gui.swing.tree.Trees;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class S57PropertyType implements Serializable {
    String acronym;
    int code;
    String fullName;
    String type;
    List<String> expecteds = new ArrayList<String>();
    String definition;
    String references;
    Double minimum;
    Double maximum;
    String remarks;
    String indication;
    String format;
    String exemple;

    @Override
    public String toString() {
        final List lst = new ArrayList();
        lst.add("CODE=" + code);
        lst.add("NAME=" + fullName);
        lst.add("TYPE=" + type);
        lst.add("definition=" + definition);
        lst.add("references=" + references);
        lst.add("minimum=" + minimum);
        lst.add("maximum=" + maximum);
        lst.add("remarks=" + remarks);
        lst.add("indication=" + indication);
        lst.add("format=" + format);
        lst.add("exemple=" + exemple);
        return Trees.toString(acronym, lst);
    }

    public String toFormattedString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(code);
        sb.append('.');
        sb.append(acronym);
        sb.append('=');
        sb.append(toString(fullName)).append(';');
        sb.append(toString(type)).append(';');
        sb.append(toString(definition)).append(';');
        sb.append(toString(references)).append(';');
        sb.append(toString(minimum)).append(';');
        sb.append(toString(maximum)).append(';');
        sb.append(toString(remarks)).append(';');
        sb.append(toString(indication)).append(';');
        sb.append(toString(format)).append(';');
        sb.append(toString(exemple));
        return sb.toString();
    }

    public void fromFormattedString(String str) {
        final String[] parts = str.split(";", -1);
        if (parts.length != 10) {
            throw new IllegalArgumentException("more divisions then expected :" + parts.length);
        }
        int i = 0;
        this.fullName = parts[i++];
        this.type = parts[i++];
        this.definition = parts[i++];
        this.references = parts[i++];
        this.minimum = tryDouble(parts[i++]);
        this.maximum = tryDouble(parts[i++]);
        this.remarks = parts[i++];
        this.indication = parts[i++];
        this.format = parts[i++];
        this.exemple = parts[i++];
    }

    private static String toString(Object obj) {
        if (obj == null) {
            return "";
        }
        String str = obj.toString();
        str = str.replaceAll(";", " ");
        return str;
    }

    private static Double tryDouble(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return Double.valueOf(str);
    }
    
}
