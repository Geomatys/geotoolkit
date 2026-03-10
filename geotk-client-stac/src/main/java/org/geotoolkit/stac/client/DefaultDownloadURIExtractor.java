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

import org.geotoolkit.stac.dto.Asset;
import org.geotoolkit.stac.dto.Item;
import java.net.URI;
import java.util.Map;
import java.util.List;

/**
 * Default implementation of DownloadUrlExtractor that looks for an asset with the "data" role,
 * or falls back to any asset with an "href".
 *
 * @author Quentin Bialota (Geomatys)
 */
public class DefaultDownloadURIExtractor implements DownloadURIExtractor {

    @Override
    public URI extract(Item item) {
        Map<String, Asset> assets = item.getAssets();
        if (assets == null || assets.isEmpty()) return null;

        for (Map.Entry<String, Asset> entry : assets.entrySet()) {
            Asset asset = entry.getValue();
            List<String> roles = asset.getRoles();
            boolean isData = false;

            if (roles != null) {
                for (String r : roles) {
                    if ("data".equals(r)) {
                        isData = true;
                        break;
                    }
                }
            }

            if (isData) {
                if (asset.getHref() != null) {
                    return URI.create(asset.getHref());
                }
            }
        }

        // Fallback: any asset with href
        for (Map.Entry<String, Asset> entry : assets.entrySet()) {
            Asset asset = entry.getValue();
            if (asset.getHref() != null) {
                return URI.create(asset.getHref());
            }
        }

        return null;
    }
}
