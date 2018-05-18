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
 * StatusInfo
 */
public class StatusInfo implements WPSJSONResponse {
    
    public StatusInfo() {
        
    }
    
    public StatusInfo(org.geotoolkit.wps.xml.StatusInfo status) {
        if (status != null) {
            this.progress = status.getPercentCompleted();
            this.message = status.getStatus();
            this.status = StatusEnum.fromValue(status.getStatus());
        }
    }

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    
    ACCEPTED("accepted"),
    RUNNING("running"),
    SUCCESSFUL("successful"),
    FAILED("failed");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }
    
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    
    public static StatusEnum fromValue(String text) {
      for (StatusEnum b : StatusEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
    
  }
  
  private StatusEnum status = null;
  
  private String message = null;
  
  private Integer progress = null;
  
  public StatusInfo status(StatusEnum status) {
    this.status = status;
    return this;
  }

  
  /**
  * Get status
  * @return status
  **/
  public StatusEnum getStatus() {
    return status;
  }
  public void setStatus(StatusEnum status) {
    this.status = status;
  }
  
  public StatusInfo message(String message) {
    this.message = message;
    return this;
  }

  
  /**
  * Get message
  * @return message
  **/
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }
  
  public StatusInfo progress(Integer progress) {
    this.progress = progress;
    return this;
  }

  
  /**
  * Get progress
  * minimum: 0
  * maximum: 100
  * @return progress
  **/
  public Integer getProgress() {
    return progress;
  }
  public void setProgress(Integer progress) {
    this.progress = progress;
  }
  
  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StatusInfo statusInfo = (StatusInfo) o;
    return Objects.equals(this.status, statusInfo.status) &&
        Objects.equals(this.message, statusInfo.message) &&
        Objects.equals(this.progress, statusInfo.progress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, message, progress);
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StatusInfo {\n");
    
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    progress: ").append(toIndentedString(progress)).append("\n");
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



