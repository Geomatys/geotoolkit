/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

/**
 * Holds values (with associated DataDefinition)
 *
 * @author Tommaso Nolli
 * @author Johann Sorel (Geomatys)
 */
public class DefaultData implements Data {

    private final DataDefinition def;
    private final Object[] values;
    private int nbValues = 0;

    /**
     * DOCUMENT ME!
     *
     * @param def
     */
    public DefaultData(final DataDefinition def) {
        this.def = def;
        this.values = new Object[def.getFieldsCount()];
    }

    /**
     * Check to see if a <code>Data</code> respects its
     * <code>DataDefinition</code>
     */
    @Override
    public final boolean isValid() {
        if (this.getValuesCount() != this.def.getFieldsCount()) {
            return false;
        }

        for (int i=0, n=this.def.getFieldsCount(); i<n ; i++) {
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
     * @return - this Data object
     * @throws TreeException
     */
    @Override
    public Data addValue(final Object val) throws TreeException {

        if (nbValues == values.length) {
            throw new TreeException("Max number of values reached!");
        }

        if (!val.getClass().equals(def.getField(nbValues).getFieldClass())) {
            throw new TreeException("Wrong class type, was expecting "
                    + def.getField(nbValues).getFieldClass());
        }

        this.values[nbValues++] = val;
        return this;
    }

    /**
     * Return the KeyDefinition
     */
    @Override
    public DataDefinition getDefinition() {
        return this.def;
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public int getValuesCount() {
        return nbValues;
    }

    /**
     * DOCUMENT ME!
     * @param i
     */
    @Override
    public Object getValue(final int i) {
        return this.values[i];
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();

        for (int i = 0; i < this.values.length; i++) {
            if (i > 0) {
                ret.append(" - ");
            }
            ret.append(this.def.getField(i).getFieldClass());
            ret.append(": ");
            ret.append(this.values[i]);
        }

        return ret.toString();
    }

}
