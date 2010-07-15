/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.index;

import java.util.ArrayList;

/**
 * Holds values (with associated DataDefinition)
 * 
 * @author Tommaso Nolli
 * @module pending
 */
public class Data {
    private final DataDefinition def;
    private final ArrayList values;

    /**
     * DOCUMENT ME!
     * 
     * @param def
     */
    public Data(DataDefinition def) {
        this.def = def;
        this.values = new ArrayList();
    }

    /**
     * Check to see if a <code>Data</code> respects its
     * <code>DataDefinition</code>
     * 
     */
    public final boolean isValid() {
        if (this.getValuesCount() != this.def.getFieldsCount()) {
            return false;
        }

        for (int i=0,n=this.def.getFieldsCount(); i<n; i++) {
            if (!this.def.getField(i).getFieldClass().isInstance(this.getValue(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param val
     * 
     * @return - this Data object
     * 
     * @throws TreeException
     */
    public Data addValue(Object val) throws TreeException {
        final int pos = this.values.size();
        if (pos == def.getFieldsCount()) {
            throw new TreeException("Max number of values reached!");
        }

        if (!val.getClass().equals(def.getField(pos).getFieldClass())) {
            throw new TreeException("Wrong class type, was expecting "
                    + def.getField(pos).getFieldClass());
        }

        this.values.add(val);
        return this;
    }

    /**
     * Return the KeyDefinition
     * 
     */
    public DataDefinition getDefinition() {
        return this.def;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public int getValuesCount() {
        return this.values.size();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param i
     * 
     */
    public Object getValue(int i) {
        return this.values.get(i);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();

        for (int i=0,n=this.values.size(); i<n ; i++) {
            if (i > 0) {
                ret.append(" - ");
            }
            ret.append(this.def.getField(i).getFieldClass());
            ret.append(": ");
            ret.append(this.values.get(i));
        }

        return ret.toString();
    }
}
