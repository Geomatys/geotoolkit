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
package org.geotoolkit.observation;

import java.sql.SQLException;
import java.util.Objects;
import static org.geotoolkit.sos.xml.SOSXmlFactory.buildAnyScalar;
import static org.geotoolkit.sos.xml.SOSXmlFactory.buildQuantity;
import static org.geotoolkit.sos.xml.SOSXmlFactory.buildText;
import static org.geotoolkit.sos.xml.SOSXmlFactory.buildTime;
import static org.geotoolkit.sos.xml.SOSXmlFactory.buildUomProperty;
import org.geotoolkit.swe.xml.AbstractBoolean;
import org.geotoolkit.swe.xml.AbstractDataComponent;
import org.geotoolkit.swe.xml.AbstractText;
import org.geotoolkit.swe.xml.AbstractTime;
import org.geotoolkit.swe.xml.AnyScalar;
import org.geotoolkit.swe.xml.Quantity;
import org.geotoolkit.swe.xml.UomProperty;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class Field {

    public String fieldType;
    public String fieldName;
    public String fieldDesc;
    public String fieldUom;

    public Field(final String fieldType, final String fieldName, final String fieldDesc, final String fieldUom) {
        this.fieldDesc = fieldDesc;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldUom = fieldUom;
    }

    public Field(final String fieldName, final AbstractDataComponent value) throws SQLException {
        this.fieldName = fieldName;
        if (value instanceof Quantity) {
            final Quantity q = (Quantity) value;
            if (q.getUom() != null) {
                this.fieldUom = q.getUom().getCode();
            }
            this.fieldDesc = q.getDefinition();
            this.fieldType = "Quantity";
        } else if (value instanceof AbstractText) {
            final AbstractText q = (AbstractText) value;
            this.fieldDesc = q.getDefinition();
            this.fieldType = "Text";
        } else if (value instanceof AbstractBoolean) {
            final AbstractBoolean q = (AbstractBoolean) value;
            this.fieldDesc = q.getDefinition();
            this.fieldType = "Boolean";
        } else if (value instanceof AbstractTime) {
            final AbstractTime q = (AbstractTime) value;
            this.fieldDesc = q.getDefinition();
            this.fieldType = "Time";
        } else {
            throw new SQLException("Only Quantity, Text AND Time is supported for now");
        }

    }

    public AnyScalar getScalar(final String version) {
        final AbstractDataComponent compo;
        if ("Quantity".equals(fieldType)) {
            final UomProperty uomCode = buildUomProperty(version, fieldUom, null);
            compo = buildQuantity(version, fieldDesc, uomCode, null);
        } else if ("Text".equals(fieldType)) {
            compo = buildText(version, fieldDesc, null);
        } else if ("Time".equals(fieldType)) {
            compo = buildTime(version, fieldDesc, null);
        } else {
            throw new IllegalArgumentException("Unexpected field Type:" + fieldType);
        }
        return buildAnyScalar(version, null, fieldName, compo);
    }

    public String getSQLType(boolean isPostgres, boolean timescaledbMain) throws SQLException {
        if (fieldType.equals("Quantity")) {
            if (timescaledbMain) {
                return "integer";
            } else if (!isPostgres) {
                return "double";
            } else {
                return "double precision";
            }
        } else if (fieldType.equals("Text")) {
            return "character varying(1000)";
        } else if (fieldType.equals("Boolean")) {
            if (isPostgres) {
                return "boolean";
            } else {
                return "integer";
            }
        } else if (fieldType.equals("Time")) {
            return "timestamp";
        } else {
            throw new SQLException("Only Quantity, Text AND Time is supported for now");
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + java.util.Objects.hashCode(this.fieldType);
        hash = 89 * hash + java.util.Objects.hashCode(this.fieldName);
        hash = 89 * hash + java.util.Objects.hashCode(this.fieldDesc);
        hash = 89 * hash + java.util.Objects.hashCode(this.fieldUom);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Field) {
            final Field that = (Field) obj;
            return Objects.equals(this.fieldDesc, that.fieldDesc)
                    && Objects.equals(this.fieldName, that.fieldName)
                    && Objects.equals(this.fieldType, that.fieldType)
                    && Objects.equals(this.fieldUom, that.fieldUom);
        }
        return false;
    }

    @Override
    public String toString() {
        return fieldName + ": " + fieldType;
    }
}
