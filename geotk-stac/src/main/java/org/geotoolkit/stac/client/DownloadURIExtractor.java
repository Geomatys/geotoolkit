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

import org.geotoolkit.stac.dto.Item;

import java.net.URI;

/**
 * Interface for extracting download URIs from a STAC Item.
 * Implementations can provide specific logic, e.g., prioritizing alternate links.
 *
 * @author Quentin Bialota (Geomatys)
 */
public interface DownloadURIExtractor {

    /**
     * Extracts a download URI from the given STAC item.
     *
     * @param item the STAC item
     * @return the download URI, or null if none could be found
     */
    URI extract(Item item);
}
