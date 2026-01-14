/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.openeo.dto;

/**
 * Check message DTO for OpenEO API.
 * This class is used to represent the result of a validation check,
 * indicating whether the check passed and providing an associated message.
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class CheckMessage {

    public CheckMessage(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }

    private boolean isValid;

    private String message;

    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }
}
