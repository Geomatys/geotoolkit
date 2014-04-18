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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class NCFieldAnalyze {
    
        public String title                     = null;
        public Field mainField                  = null;
        public Field separatorField             = null;
        public Field latField                   = null;
        public Field lonField                   = null;
        public NetcdfFile file                  = null;
        public final List<Field> phenfields     = new ArrayList<>();
        public boolean timeSeries               = true;
        public final List<Field> skippedFields  = new ArrayList<>();
        public final Map<String, Variable> vars = new HashMap<>();

        public String getYLabel() {
            final StringBuilder result = new StringBuilder();
            for (Field field : phenfields) {
                result.append(field.label).append(",");
            }
            if (result.length() != 0) {
                result.deleteCharAt(result.length() - 1);
            }
            return result.toString();
        }

        public String getXLabel() {
            if (mainField != null) {
                return mainField.label;
            }
            return null;
        }
        
        public boolean hasSpatial() {
            return latField != null && lonField != null;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof NCFieldAnalyze) {
                final NCFieldAnalyze that = (NCFieldAnalyze) obj;
                return Objects.equals(this.mainField,      that.mainField) &&
                       Objects.equals(this.phenfields,     that.phenfields) &&
                       Objects.equals(this.separatorField, that.separatorField) &&
                       Objects.equals(this.skippedFields,  that.skippedFields) &&
                       Objects.equals(this.timeSeries,     that.timeSeries);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 17 * hash + (this.mainField != null ? this.mainField.hashCode() : 0);
            hash = 17 * hash + (this.separatorField != null ? this.separatorField.hashCode() : 0);
            hash = 17 * hash + (this.phenfields != null ? this.phenfields.hashCode() : 0);
            hash = 17 * hash + (this.timeSeries ? 1 : 0);
            hash = 17 * hash + (this.skippedFields != null ? this.skippedFields.hashCode() : 0);
            return hash;
        }
    }
