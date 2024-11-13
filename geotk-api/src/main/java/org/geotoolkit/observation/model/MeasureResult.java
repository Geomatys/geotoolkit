/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.observation.model;

import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class MeasureResult implements Result {

    private Field field;
    private Object value;

    private MeasureResult() {
    }

    public MeasureResult(Field field, Object value) {
        this.field = field;
        this.value = value;
    }

    public Field getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        MeasureResult that = (MeasureResult) obj;
        return Objects.equals(this.field, that.field)
                && Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.field);
        hash = 31 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        sb.append(this.getClass().getSimpleName());
        sb.append("]\n");
        sb.append("field:").append(field).append('\n');
        sb.append("value:").append(value).append('\n');
        return sb.toString();
    }
}
