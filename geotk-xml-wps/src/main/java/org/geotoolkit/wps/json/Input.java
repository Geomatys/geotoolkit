/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.wps.json;

import java.util.Objects;

/**
 * Input
 */
public class Input extends InputBase {

    private String id = null;

    private InputBase input = null;

    public Input() {

    }

    public Input(Input that) {
        if (that != null) {
            this.id = that.id;
            if (that.input instanceof LiteralInput) {
                this.input = new LiteralInput((LiteralInput) that.input);
            } else if (that.input instanceof ComplexInput) {
                this.input = new ComplexInput((ComplexInput) that.input);
            } else if (that.input instanceof BoundingBoxInput) {
                this.input = new BoundingBoxInput((BoundingBoxInput) that.input);
            } else if (that instanceof Input) {
                this.input = new Input((Input)that.input);
            }
        }
    }

    public Input(String id, InputBase input) {
        this.id = id;
        this.input = input;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the input
     */
    public InputBase getInput() {
        return input;
    }

    /**
     * @param input the input to set
     */
    public void setInput(InputBase input) {
        this.input = input;
    }

    /**
     * Get value
     *
     * @return value
     *
     */
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Input input = (Input) o;
        return Objects.equals(this.id, input.id) &&
               Objects.equals(this.input, input.input);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, input);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Input {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    input: ").append(toIndentedString(input)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
