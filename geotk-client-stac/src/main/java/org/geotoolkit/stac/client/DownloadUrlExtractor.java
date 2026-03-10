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

package org.geotoolkit.stac.client;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface for extracting download URLs from a STAC Item.
 * Implementations can provide specific logic, e.g., prioritizing alternate links.
 *
 * @author Quentin Bialota (Geomatys)
 */
public interface DownloadUrlExtractor {

    /**
     * Extracts a download URL from the given STAC item JSON node.
     *
     * @param item the STAC item node
     * @return the download URL, or null if none could be found
     */
    String extract(JsonNode item);
}
