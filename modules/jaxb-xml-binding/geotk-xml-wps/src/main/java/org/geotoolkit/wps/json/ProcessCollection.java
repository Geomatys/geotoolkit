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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ProcessCollection
 */
public class ProcessCollection implements WPSJSONResponse {

  private List<ProcessSummary> processes;

  public ProcessCollection() {
      this.processes = new ArrayList<>();
  }

  public ProcessCollection(Stream<org.geotoolkit.wps.xml.v200.ProcessSummary> offerings) {
      this.processes = offerings
              .map(ProcessSummary::new )
              .collect(Collectors.toList());
  }

  public ProcessCollection processes(List<ProcessSummary> processes) {
    this.processes = processes;
    return this;
  }

  public ProcessCollection addProcessesItem(ProcessSummary processesItem) {
    this.processes.add(processesItem);
    return this;
  }

  /**
  * Get processes
  * @return processes
  **/
  public List<ProcessSummary> getProcesses() {
    return processes;
  }
  public void setProcesses(List<ProcessSummary> processes) {
    this.processes = processes;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProcessCollection processCollection = (ProcessCollection) o;
    return Objects.equals(this.processes, processCollection.processes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(processes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProcessCollection {\n");

    sb.append("    processes: ").append(toIndentedString(processes)).append("\n");
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



