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
 * ProcessOffering
 */
public class ProcessOffering implements WPSJSONResponse {

    private Process process = null;

    public ProcessOffering() {

    }

    public ProcessOffering(org.geotoolkit.wps.xml.v200.ProcessOffering offering) {
        if (offering != null) {
            this.process = new Process(offering);
        }
    }

    public ProcessOffering process(Process process) {
        this.process = process;
        return this;
    }

    /**
     * Get process
     *
     * @return process
  *
     */
    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProcessOffering processOffering = (ProcessOffering) o;
        return Objects.equals(this.process, processOffering.process);
    }

    @Override
    public int hashCode() {
        return Objects.hash(process);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ProcessOffering {\n");

        sb.append("    process: ").append(toIndentedString(process)).append("\n");
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
