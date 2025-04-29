/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.observation.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A model object representing a field in an Observation result.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class Field {

    /**
     * the place of the field in a dataArray.
     */
    public final Integer index;
    /**
     * The data type of the field. Can be : Quantity, Text, Boolean, Time, ...
     */
    public final FieldDataType dataType;
    /**
     * Field name, used as an identifier for the field. The name often identify
     * a phenomenon with the same id.
     */
    public final String name;

    /**
     * Field label, used as an human description for the field.
     */
    public final String label;

    /**
     * An URN describing the field.
     */
    public final String description;
    /**
     * Unit of measure of the associated data. Filled only for certain field
     * type like 'Quantity' or 'Time'.
     */
    public final String uom;

    /**
     * The type of the field. Can be : MAIN, MEASURE, QUALITY, PARAMETER
     */
    public final FieldType type;

    protected Field parent;

    /**
     * Associated quality fields.
     */
    public final List<Field> qualityFields;

    /**
     * Associated parameter fields.
     */
    public final List<Field> parameterFields;

    // for JSON
    private Field() {
        this.index = null;
        this.type = null;
        this.dataType = null;
        this.name = null;
        this.label = null;
        this.description = null;
        this.uom = null;
        this.parent = null;
        this.qualityFields = new ArrayList<>();
        this.parameterFields = new ArrayList<>();
    }

    /**
     * Build a field.
     *
     * @param index The place of the field in a dataArray.
     * @param dataType The data type of the field.
     * @param name Field name, used as an identifier for the field.
     * @param label Field label, used as an human description for the field.
     * @param description An URN describing the field.
     * @param uom Unit of measure of the associated data.
     * @param type The type of the field.
     */
    public Field(final Integer index, final FieldDataType dataType, final String name, final String label, final String description, final String uom, final FieldType type) {
        this(index, dataType, name, label, description, uom, type, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Build a field.
     *
     * @param index The place of the field in a dataArray.
     * @param dataType The data type of the field.
     * @param name Field name, used as an identifier for the field.
     * @param label Field label, used as an human description for the field.
     * @param description An URN describing the field.
     * @param uom Unit of measure of the associated data.
     * @param type The type of the field.
     * @param qualityFields Associated quality fields.
     * @param parameterFields Associated arameter fields.
     */
    public Field(final Integer index, final FieldDataType dataType, final String name, final String label, final String description, final String uom, final FieldType type,
            List<Field> qualityFields, List<Field> parameterFields) {
        this.index = index;
        this.description = description;
        this.name = name;
        this.dataType = dataType;
        this.uom = uom;
        this.label = label;
        this.type = type;
        this.qualityFields = qualityFields;
        //update parent
        if (qualityFields != null) {
            for (Field qf : qualityFields) {
                qf.parent = this;
            }
        }
        this.parameterFields = parameterFields;
        //update parent
        if (parameterFields != null) {
            for (Field pf : parameterFields) {
                pf.parent = this;
            }
        }
    }

    /**
     * Duplicate  a field.
     *
     * @param that The field to duplicate
     */
    public Field(Field that) {
        if (that == null) throw new IllegalArgumentException("Null field param");
        this.index = that.index;
        this.description = that.description;
        this.name = that.name;
        this.dataType = that.dataType;
        this.uom = that.uom;
        this.label = that.label;
        this.type = that.type;
        this.qualityFields = new ArrayList<>();
        for (Field qualField : that.qualityFields) {
            this.qualityFields.add(new Field(qualField));
        }
        this.parameterFields = new ArrayList<>();
        for (Field parField : that.parameterFields) {
            this.parameterFields.add(new Field(parField));
        }
    }

    /**
     * Duplicate a field but change the index.
     *
     * @param index Overriding field index.
     * @param that The field to duplicate
     */
    public Field(int index, Field that) {
        if (that == null) throw new IllegalArgumentException("Null field param");
        this.index = index;
        this.description = that.description;
        this.name = that.name;
        this.dataType = that.dataType;
        this.uom = that.uom;
        this.label = that.label;
        this.type = that.type;
        this.qualityFields = new ArrayList<>();
        for (Field qualField : that.qualityFields) {
            this.qualityFields.add(new Field(qualField));
        }
        this.parameterFields = new ArrayList<>();
        for (Field parField : that.parameterFields) {
            this.parameterFields.add(new Field(parField));
        }
    }

    /**
     * Return the SQL type asociated with this field.
     *
     * @param isPostgres if not, return a derby SQL type.
     * @param timescaledbMain Because of a restriction on Timescale db for
     * making buckets this flag replace a double column by integer.
     * @return
     * @throws SQLException
     */
    public String getSQLType(boolean isPostgres, boolean timescaledbMain) throws SQLException {
        if (FieldDataType.QUANTITY.equals(dataType)) {
            if (timescaledbMain) {
                return "integer";
            } else if (!isPostgres) {
                return "double";
            } else {
                return "double precision";
            }
        } else if (FieldDataType.TEXT.equals(dataType)) {
            return "character varying(1000)";
        } else if (FieldDataType.BOOLEAN.equals(dataType)) {
            if (isPostgres) {
                return "boolean";
            } else {
                return "integer";
            }
        } else if (FieldDataType.TIME.equals(dataType)) {
            return "timestamp";
        } else {
            throw new SQLException("Only Quantity, Text AND Time is supported for now");
        }
    }

    public Field getParent() {
        return parent;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + java.util.Objects.hashCode(this.dataType);
        hash = 89 * hash + java.util.Objects.hashCode(this.name);
        hash = 89 * hash + java.util.Objects.hashCode(this.description);
        hash = 89 * hash + java.util.Objects.hashCode(this.uom);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Field that) {
            return Objects.equals(this.description, that.description)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.dataType, that.dataType)
                && Objects.equals(this.uom, that.uom);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name).append("(").append(dataType).append(") \n");
        sb.append("index:").append(index).append('\n');
        sb.append("description:").append(description).append('\n');
        sb.append("type:").append(type).append('\n');
        if (uom != null) {
            sb.append("uom:").append(uom).append('\n');
        }
        return sb.toString();
    }
}
