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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.wps.xml.v200.DataTransmissionMode;
import org.geotoolkit.wps.xml.v200.JobControlOptions;

/**
 * ProcessDescriptionChoiceType
 */
public class ProcessDescriptionChoiceType {

  private String href = null;

  private String mimeType = null;

  private String schema = null;

  private String encoding = null;

  private Process process = null;

  private String processVersion = null;

  private List<JobControlOptions> jobControlOptions = null;

  private List<DataTransmissionMode> outputTransmission = null;

  public ProcessDescriptionChoiceType() {

  }

  public ProcessDescriptionChoiceType(ProcessDescriptionChoiceType that) {
      if (that != null) {
          if (that.process != null) {
              this.process = new Process(that.process);
          }
          if (that.jobControlOptions != null) {
              this.jobControlOptions = new ArrayList<>(that.jobControlOptions);
          }
          if (that.outputTransmission != null) {
              this.outputTransmission = new ArrayList<>(that.outputTransmission);
          }
          this.encoding = that.encoding;
          this.href = that.href;
          this.mimeType = that.mimeType;
          this.processVersion = that.processVersion;
          this.schema = that.schema;
      }
  }

  public ProcessDescriptionChoiceType(Process process, String processVersion, List<JobControlOptions> jobControlOptions, List<DataTransmissionMode> outputTransmission) {
      this.process = process;
      this.jobControlOptions = jobControlOptions;
      this.outputTransmission = outputTransmission;
      this.processVersion = processVersion;
  }

  public ProcessDescriptionChoiceType href(String href) {
    this.href = href;
    return this;
  }


  /**
  * Get href
  * @return href
  **/
  public String getHref() {
    return href;
  }
  public void setHref(String href) {
    this.href = href;
  }

  public ProcessDescriptionChoiceType mimeType(String mimeType) {
    this.mimeType = mimeType;
    return this;
  }


  /**
  * Get mimeType
  * @return mimeType
  **/
  public String getMimeType() {
    return mimeType;
  }
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public ProcessDescriptionChoiceType schema(String schema) {
    this.schema = schema;
    return this;
  }


  /**
  * Get schema
  * @return schema
  **/
  public String getSchema() {
    return schema;
  }
  public void setSchema(String schema) {
    this.schema = schema;
  }

  public ProcessDescriptionChoiceType encoding(String encoding) {
    this.encoding = encoding;
    return this;
  }


  /**
  * Get encoding
  * @return encoding
  **/
  public String getEncoding() {
    return encoding;
  }
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public ProcessDescriptionChoiceType process(Process process) {
    this.process = process;
    return this;
  }


  /**
  * Get process
  * @return process
  **/
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
    ProcessDescriptionChoiceType processDescriptionChoiceType = (ProcessDescriptionChoiceType) o;
    return Objects.equals(this.href, processDescriptionChoiceType.href) &&
        Objects.equals(this.mimeType, processDescriptionChoiceType.mimeType) &&
        Objects.equals(this.schema, processDescriptionChoiceType.schema) &&
        Objects.equals(this.encoding, processDescriptionChoiceType.encoding) &&
        Objects.equals(this.process, processDescriptionChoiceType.process);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(href, mimeType, schema, encoding, process);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProcessDescriptionChoiceType {\n");

    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    mimeType: ").append(toIndentedString(mimeType)).append("\n");
    sb.append("    schema: ").append(toIndentedString(schema)).append("\n");
    sb.append("    encoding: ").append(toIndentedString(encoding)).append("\n");
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

    /**
     * @return the processVersion
     */
    public String getProcessVersion() {
        return processVersion;
    }

    /**
     * @param processVersion the processVersion to set
     */
    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    /**
     * @return the jobControlOptions
     */
    public List<JobControlOptions> getJobControlOptions() {
        return jobControlOptions;
    }

    /**
     * @param jobControlOptions the jobControlOptions to set
     */
    public void setJobControlOptions(List<JobControlOptions> jobControlOptions) {
        this.jobControlOptions = jobControlOptions;
    }

    /**
     * @return the outputTransmission
     */
    public List<DataTransmissionMode> getOutputTransmission() {
        return outputTransmission;
    }

    /**
     * @param outputTransmission the outputTransmission to set
     */
    public void setOutputTransmission(List<DataTransmissionMode> outputTransmission) {
        this.outputTransmission = outputTransmission;
    }
}
