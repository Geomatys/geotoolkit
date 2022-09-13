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
import static org.geotoolkit.sos.xml.SOSXmlFactory.*;
import org.geotoolkit.swe.xml.AbstractBoolean;
import org.geotoolkit.swe.xml.AbstractDataComponent;
import org.geotoolkit.swe.xml.AbstractQualityProperty;
import org.geotoolkit.swe.xml.AbstractText;
import org.geotoolkit.swe.xml.AbstractTime;
import org.geotoolkit.swe.xml.AnyScalar;
import org.geotoolkit.swe.xml.Quantity;
import org.geotoolkit.swe.xml.UomProperty;

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
     * The data type of the field. Can be : - Quantity - Text - Boolean - Time
     */
    public final FieldType type;
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
     * Associated quality fields.
     */
    public final List<Field> qualityFields;

    /**
     * Build a field.
     *
     * @param index The place of the field in a dataArray.
     * @param type The data type of the field.
     * @param name Field name, used as an identifier for the field.
     * @param label Field label, used as an human description for the field.
     * @param description An URN describing the field.
     * @param uom Unit of measure of the associated data.
     */
    public Field(final Integer index, final FieldType type, final String name, final String label, final String description, final String uom) {
        this(index, type, name, label, description, uom, new ArrayList<>());
    }

    /**
     * Build a field.
     *
     * @param index The place of the field in a dataArray.
     * @param type The data type of the field.
     * @param name Field name, used as an identifier for the field.
     * @param label Field label, used as an human description for the field.
     * @param description An URN describing the field.
     * @param uom Unit of measure of the associated data.
     * @param qualityFields Associated quality fields.
     */
    public Field(final Integer index, final FieldType type, final String name, final String label, final String description, final String uom, List<Field> qualityFields) {
        this.index = index;
        this.description = description;
        this.name = name;
        this.type = type;
        this.uom = uom;
        this.label = label;
        this.qualityFields = qualityFields;
    }

    /**
     * Build a field from a SWE object.
     *
     * @param index The place of the field in a dataArray.
     * @param name Field name, used as an identifier for the field.
     * @param label Field label, used as an human description for the field.
     * @param value A SWE datacomopnent extracted form a data array.
     * @throws SQLException
     */
    public Field(final Integer index, final String name, final String label, final AbstractDataComponent value) throws SQLException {
        this.name = name;
        this.index = index;
        this.label = label;
        this.qualityFields = new ArrayList<>();
        if (value instanceof Quantity q) {
            this.description = q.getDefinition();
            this.type = FieldType.QUANTITY;
            if (q.getUom() != null) {
                this.uom = q.getUom().getCode();
            } else {
                this.uom = null;
            }
            if (q.getQuality() != null) {
                for (AbstractQualityProperty aqp : q.getQuality()) {
                    AbstractDataComponent dc = aqp.getDataComponent();
                    if (dc != null) {
                        this.qualityFields.add(new Field(null, dc.getId(), aqp.getTitle(), dc));
                    }
                }
            }
        } else if (value instanceof AbstractText q) {
            this.description = q.getDefinition();
            this.type = FieldType.TEXT;
            this.uom = null;
        } else if (value instanceof AbstractBoolean q) {
            this.description = q.getDefinition();
            this.type = FieldType.BOOLEAN;
            this.uom = null;
        } else if (value instanceof AbstractTime q) {
            this.description = q.getDefinition();
            this.type = FieldType.TIME;
            if (q.getUom() != null) {
                this.uom = q.getUom().getCode();
            } else {
                this.uom = null;
            }
        } else {
            throw new SQLException("Only Quantity, Text AND Time is supported for now");
        }
    }

    /**
     * Return an SWE object.
     *
     * @param version The SOS version of the object (and so the SWE version).
     * @return
     */
    public AnyScalar getScalar(final String version) {
        final AbstractDataComponent compo = getComponent(version, false);
        return buildAnyScalar(version, null, name, compo);
    }

    private AbstractDataComponent getComponent(final String version, boolean nameAsId) {
        final List<AbstractQualityProperty> quality = new ArrayList<>();
        if (qualityFields != null) {
            for (Field qField : qualityFields) {
                quality.add(buildQualityProperty(version, qField.getComponent(version, true)));
            }
        }
        final AbstractDataComponent compo;
        if (FieldType.QUANTITY.equals(type)) {
            final UomProperty uomCode = buildUomProperty(version, uom, null);
            compo = buildQuantity(version, nameAsId ? name : null, description, uomCode, null, quality);
        } else if (FieldType.TEXT.equals(type)) {
            compo = buildText(version, nameAsId ? name : null, description, null, quality);
        } else if (FieldType.TIME.equals(type)) {
            compo = buildTime(version, nameAsId ? name : null, description, null, quality);
        } else if (FieldType.BOOLEAN.equals(type)) {
            compo = buildBoolean(version, nameAsId ? name : null, description, null, quality);
        } else {
            throw new IllegalArgumentException("Unexpected field Type:" + type);
        }
        return compo;
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
        if (FieldType.QUANTITY.equals(type)) {
            if (timescaledbMain) {
                return "integer";
            } else if (!isPostgres) {
                return "double";
            } else {
                return "double precision";
            }
        } else if (FieldType.TEXT.equals(type)) {
            return "character varying(1000)";
        } else if (FieldType.BOOLEAN.equals(type)) {
            if (isPostgres) {
                return "boolean";
            } else {
                return "integer";
            }
        } else if (FieldType.TIME.equals(type)) {
            return "timestamp";
        } else {
            throw new SQLException("Only Quantity, Text AND Time is supported for now");
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + java.util.Objects.hashCode(this.type);
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
                && Objects.equals(this.type, that.type)
                && Objects.equals(this.uom, that.uom);
        }
        return false;
    }

    @Override
    public String toString() {
        return name + ": " + type;
    }
}
