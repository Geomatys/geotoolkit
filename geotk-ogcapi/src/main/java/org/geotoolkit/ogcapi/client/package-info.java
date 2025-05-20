/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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

/**
 * Bindings for OGC-API services for java 11 HttpClient.
 *
 * <p>
 * Based bindings have been generated with https://github.com/OpenAPITools/openapi-generator
 * then cleaned and simplified.
 * <p>
 * Exemple :
 * <pre>
 * OpenApiConfiguration conf = new OpenApiConfiguration();
 * conf.setBasePath("https://server.com/service-openapi/");
 *
 * ConformanceApi api = new ConformanceApi(conf);
 * try {
 *     ConfClasses result = api.getConformance("json");
 *     System.out.println(result);
 * } catch (ApiException e) {
 *     System.err.println("Exception when calling ConformanceApi#getConformance");
 *     System.err.println("Status code: " + e.getCode());
 *     System.err.println("Reason: " + e.getResponseBody());
 *     System.err.println("Response headers: " + e.getResponseHeaders());
 *     e.printStackTrace();
 * }
 * </pre>
 *
 *
 */
package org.geotoolkit.ogcapi.client;
