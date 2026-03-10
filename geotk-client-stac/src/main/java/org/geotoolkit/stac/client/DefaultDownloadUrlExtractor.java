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
import java.util.Iterator;
import java.util.Map;

/**
 * Default implementation of DownloadUrlExtractor that looks for an asset with the "data" role,
 * or falls back to any asset with an "href".
 *
 * @author Quentin Bialota (Geomatys)
 */
public class DefaultDownloadUrlExtractor implements DownloadUrlExtractor {

    @Override
    public String extract(JsonNode item) {
        JsonNode assets = item.get("assets");
        if (assets == null) return null;

        Iterator<Map.Entry<String, JsonNode>> fields = assets.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            JsonNode meta = entry.getValue();
            JsonNode roles = meta.get("roles");
            boolean isData = false;
            
            if (roles != null && roles.isArray()) {
                for (JsonNode r : roles) {
                    if ("data".equals(r.asText())) {
                        isData = true;
                        break;
                    }
                }
            }
            
            if (isData) {
                if (meta.has("href")) {
                    return meta.get("href").asText();
                }
            }
        }

        // Fallback: any asset with href
        fields = assets.fields();
        while (fields.hasNext()) {
            JsonNode meta = fields.next().getValue();
            if (meta.has("href")) {
                return meta.get("href").asText();
            }
        }
        
        return null;
    }
}
