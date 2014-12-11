/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.data.wfs;


/**
 * Thrown when {@link WebFeatureClient} can not parse the server's answer.
 *
 * @todo Should be replaced or retrofitted in existing exceptions
 *       (e.g. {@link java.util.concurrent.TimeoutException}).
 *       Some of them are checked exception, which require revisiting the API.
 */
public class WebFeatureException extends RuntimeException {
    public WebFeatureException() {
        super();
    }

    public WebFeatureException(String message) {
        super(message);
    }

    public WebFeatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
