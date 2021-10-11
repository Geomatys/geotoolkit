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

/**
 * JobList
 */
public class JobCollection implements WPSJSONResponse {

  private List<String> jobs = null;

  public JobCollection() {

  }

  public JobCollection(List<String> jobs) {
    this.jobs = jobs;
  }

  public JobCollection jobs(List<String> jobs) {
    this.jobs = jobs;
    return this;
  }

  public JobCollection addJobsItem(String jobsItem) {

    if (this.jobs == null) {
      this.jobs = new ArrayList<>();
    }

    this.jobs.add(jobsItem);
    return this;
  }

  /**
  * Get jobs
  * @return jobs
  **/
  public List<String> getJobs() {
    return jobs;
  }
  public void setJobs(List<String> jobs) {
    this.jobs = jobs;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JobCollection jobList = (JobCollection) o;
    return Objects.equals(this.jobs, jobList.jobs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jobs);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class JobList {\n");

    sb.append("    jobs: ").append(toIndentedString(jobs)).append("\n");
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



