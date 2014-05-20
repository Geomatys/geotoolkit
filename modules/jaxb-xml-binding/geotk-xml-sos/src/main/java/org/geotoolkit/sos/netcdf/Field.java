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

package org.geotoolkit.sos.netcdf;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class Field implements Serializable {
        public String label;
        public String unit;
        public Number fillValue;
        public Type type;
        public int dimension;
        public String dimensionLabel;
        public boolean mainVariableFirst = true;

        public Field(final String label, final Type type, final int dimension, final String dimensionLabel) {
            this.label          = label;
            this.type           = type;
            this.dimensionLabel = dimensionLabel;
        }

        public Field(final String label, int dimension, final String dimensionLabel) {
            this.label          = label;
            this.dimension      = dimension;
            this.dimensionLabel = dimensionLabel;
        }

        @Override
        public String toString() {
            String dimLabel = "";
            if (dimension > 0) {
                dimLabel = "=> " + dimensionLabel;
            }
            String typeName;
            if (type == null) {
                typeName = "NULL TYPE";
            } else {
                typeName = type.name();
            }
            return label + " : " + typeName + '(' + dimension + dimLabel + ')';
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Field) {
                final Field that = (Field) obj;
                return Objects.equals(this.label, that.label) &&
                       Objects.equals(this.type,  that.type);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 29 * hash + (this.label != null ? this.label.hashCode() : 0);
            hash = 29 * hash + (this.type != null ? this.type.hashCode() : 0);
            return hash;
        }
    }
