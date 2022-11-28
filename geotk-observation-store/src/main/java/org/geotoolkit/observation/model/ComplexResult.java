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

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ComplexResult implements Result {

    private List<Field> fields;

    private Integer nbValues;

    private TextEncoderProperties textEncodingProperties;
    private String values;

    private List<Object> dataArray;

    private ComplexResult() {
    }

    public ComplexResult(List<Field> fields, TextEncoderProperties textEncodingProperties, String values, Integer nbValues) {
        this.fields = fields;
        this.textEncodingProperties = textEncodingProperties;
        this.values = values;
        this.nbValues = nbValues;
    }

    public ComplexResult(List<Field> fields, List<Object> dataArray, Integer nbValues) {
        this.fields = fields;
        this.dataArray = dataArray;
        this.nbValues = nbValues;
    }

    public ComplexResult(Integer nbValues) {
        this.nbValues = nbValues;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Integer getNbValues() {
        return nbValues;
    }

    public void setNbValues(Integer nbValues) {
        this.nbValues = nbValues;
    }

    public TextEncoderProperties getTextEncodingProperties() {
        return textEncodingProperties;
    }

    public void setTextEncodingProperties(TextEncoderProperties textEncodingProperties) {
        this.textEncodingProperties = textEncodingProperties;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public List<Object> getDataArray() {
        return dataArray;
    }

    public void setDataArray(List<Object> dataArray) {
        this.dataArray = dataArray;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        ComplexResult that = (ComplexResult) obj;
        return Objects.equals(this.dataArray, that.dataArray)
                && Objects.equals(this.fields, that.fields)
                && Objects.equals(this.nbValues, that.nbValues)
                && Objects.equals(this.values, that.values)
                && Objects.equals(this.textEncodingProperties, that.textEncodingProperties);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.fields);
        hash = 83 * hash + this.nbValues;
        hash = 83 * hash + Objects.hashCode(this.textEncodingProperties);
        hash = 83 * hash + Objects.hashCode(this.values);
        hash = 83 * hash + Objects.hashCode(this.dataArray);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (fields != null) {
            sb.append("fields:\n");
            for (Field f : fields) {
                sb.append(f).append('\n');
            }
        }
        if (textEncodingProperties != null) {
            sb.append("textEncodingProperties:").append(textEncodingProperties).append('\n');
        }
        if (values != null) {
            sb.append("values:").append(values).append('\n');
        }
        if (dataArray != null) {
            sb.append("dataArray:\n");
            for (Object da : dataArray) {
                sb.append(da).append('\n');
            }
        }

        return sb.toString();
    }
}
