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


/**
 * Gets or Sets jobControlOptions
 */

public enum JobControlOptions {
  
  SYNC_EXECUTE("sync-execute"),
  
  ASYNC_EXECUTE("async-execute");

  private String value;

  JobControlOptions(String value) {
    this.value = value;
  }


  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }


  public static JobControlOptions fromValue(String text) {
    for (JobControlOptions b : JobControlOptions.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }

}



