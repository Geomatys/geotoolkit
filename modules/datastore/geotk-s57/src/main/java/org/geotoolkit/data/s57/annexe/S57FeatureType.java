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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.geotoolkit.gui.swing.tree.Trees;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class S57FeatureType implements Serializable {
    public String acronym;
    public int code;
    public String fullName;
    public String description;
    public String remarks;
    public String reference;
    /**
     * indicates the allowable geometric forms. Point, line or area.
     */
    public String geometricPrimitive;
    public List<String> attA = new ArrayList<>();
    public List<String> attB = new ArrayList<>();
    public List<String> attC = new ArrayList<>();

    @Override
    public String toString() {
        final List lst = new ArrayList();
        lst.add("CODE=" + code);
        lst.add("NAME=" + fullName);
        lst.add("DESC=" + description);
        lst.add("REMARK=" + remarks);
        lst.add("REFS=" + reference);
        lst.add(Trees.toString("AttA", attA));
        lst.add(Trees.toString("AttB", attB));
        lst.add(Trees.toString("AttC", attC));
        return Trees.toString(acronym, lst);
    }

    public String toFormattedString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(code);
        sb.append('.');
        sb.append(acronym);
        sb.append('=');
        sb.append(toString(fullName)).append(';');
        sb.append(toString(description)).append(';');
        sb.append(toString(remarks)).append(';');
        sb.append(toString(reference)).append(';');
        sb.append(toFormattedString(attA)).append(';');
        sb.append(toFormattedString(attB)).append(';');
        sb.append(toFormattedString(attC));
        if(geometricPrimitive != null){
            sb.append(';');
            sb.append(geometricPrimitive);
        }
        return sb.toString();
    }

    private static String toString(Object obj) {
        if (obj == null) {
            return "";
        }
        String str = obj.toString();
        str = str.replaceAll(";", " ");
        return str;
    }

    private static String toFormattedString(List lst) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0, n = lst.size(); i < n; i++) {
            if (i != 0) {
                sb.append('/');
            }
            sb.append(lst.get(i));
        }
        return sb.toString();
    }

    public void fromFormattedString(String str) {
        final String[] parts = str.split(";", -1);
        if (parts.length != 7 && parts.length != 8) {
            throw new IllegalArgumentException("more divisions then expected :" + parts.length);
        }
        int i = 0;
        this.fullName = parts[i++];
        this.description = parts[i++];
        this.remarks = parts[i++];
        this.reference = parts[i++];
        attA.addAll(toList(parts[i++]));
        attB.addAll(toList(parts[i++]));
        attC.addAll(toList(parts[i++]));
        if(parts.length == 8){
            this.geometricPrimitive = parts[i++];
        }
    }

    private static List toList(String str) {
        final String[] parts = str.split("/");
        if(parts.length==1 && parts[0].isEmpty()) return Collections.EMPTY_LIST;
        return Arrays.asList(parts);
    }

}
