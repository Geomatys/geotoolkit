/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.data.om.netcdf;

import java.io.Serializable;
import java.util.Objects;
import org.geotoolkit.observation.model.Field;
import org.geotoolkit.observation.model.FieldDataType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class NCField extends Field implements Serializable {

    public final Number fillValue;
    public final Type ncDataType;
    public final int dimension;
    public final String dimensionLabel;
    public boolean mainVariableFirst = true;

    public NCField(final String id, final String label, final Type ncDataType, final int dimension, final String dimensionLabel, final Number fillValue, final String unit) {
        super(-1, getTypeFromDataType(ncDataType), id, label, null, unit);
        this.ncDataType = ncDataType;
        this.dimensionLabel = dimensionLabel;
        this.dimension = dimension;
        this.fillValue = fillValue;
    }

    private static FieldDataType getTypeFromDataType(Type ncDataType) {
        switch (ncDataType) {
            case BOOLEAN: return FieldDataType.BOOLEAN;
            case DATE : return FieldDataType.TIME;
            case DOUBLE:
            case INT: return FieldDataType.QUANTITY;
            case STRING: return FieldDataType.TEXT;
            case UNSUPPORTED:
            default: return null;
        }
    }


    @Override
    public String toString() {
        String dimLabel = "";
        if (dimension > 0) {
            dimLabel = "=> " + dimensionLabel;
        }
        String typeName;
        if (dataType == null) {
            typeName = "NULL TYPE";
        } else {
            typeName = dataType.name();
        }
        return label + " : " + typeName + '(' + dimension + dimLabel + ')';
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof NCField) {
            final NCField that = (NCField) obj;
            return Objects.equals(this.label, that.label)
                && Objects.equals(this.ncDataType,  that.ncDataType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 29 * hash + (this.ncDataType != null ? this.ncDataType.hashCode() : 0);
        return hash;
    }
}
