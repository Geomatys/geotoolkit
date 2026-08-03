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
package org.geotoolkit.openeo.process;

import org.apache.sis.coverage.grid.GridCoverage;
import org.geotoolkit.openeo.dto.process.DataTypeSchema;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.ProcessingRegistry;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Identifier;
import org.opengis.util.NoSuchIdentifierException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for OpenEO process ID conversions and data type schema building.
 * <p>
 * Process-specific facts (the openEO id of a descriptor, and any parameter schema override) are
 * not hardcoded here: they are discovered generically through {@link ProcessFinder} on descriptors
 * implementing {@link OpenEOExposedProcess}. Exposing a new Examind process through openEO only
 * requires implementing that interface on the descriptor itself.
 *
 * @author Quentin Bialota (Geomatys)
 */
public class OpenEOUtils {

    /**
     * Convert an Examind process ID to its openEO process ID.
     * Accepts both the full form ("&lt;registry&gt;.&lt;code&gt;", e.g. "examind.coverage.openeo.load")
     * and the short form ("&lt;code&gt;", e.g. "coverage.openeo.load").
     * If the resolved descriptor does not implement {@link OpenEOExposedProcess}, or no descriptor
     * is found, the input is returned unchanged.
     *
     * @param exaProcessId The Examind process ID.
     * @return The corresponding OpenEO process ID.
     */
    public static String examindProcessIdToOpenEOProcessId(String exaProcessId) {
        ProcessDescriptor descriptor = findDescriptor(exaProcessId);
        if (descriptor instanceof OpenEOExposedProcess exposed) {
            return exposed.getOpenEOProcessId();
        }
        return exaProcessId;
    }

    private static ProcessDescriptor findDescriptor(String exaProcessId) {
        // Short form: the whole string is the code, registry unknown -> let ProcessFinder scan all registries.
        try {
            return ProcessFinder.getProcessDescriptor(null, exaProcessId);
        } catch (NoSuchIdentifierException ex) {
            // Full form: "<registry>.<code>".
        }
        int dot = exaProcessId.indexOf('.');
        if (dot > 0) {
            String registryName = exaProcessId.substring(0, dot);
            String code = exaProcessId.substring(dot + 1);
            try {
                return ProcessFinder.getProcessDescriptor(registryName, code);
            } catch (NoSuchIdentifierException ex) {
                // fall through
            }
        }
        return null;
    }

    /**
     * Convert an openEO process ID to its Examind process ID, by scanning every registered process
     * descriptor for one implementing {@link OpenEOExposedProcess} whose openEO id matches.
     * If none is found, the input is returned unchanged.
     *
     * @param eoProcessId The OpenEO process ID.
     * @param fullDescriptor If true, returns the full Examind process ID ("&lt;registry&gt;.&lt;code&gt;");
     *                       otherwise, returns the short form ("&lt;code&gt;").
     * @return The corresponding Examind process ID.
     */
    public static String openEOProcessIdToExamindProcessId(String eoProcessId, boolean fullDescriptor) {
        for (Iterator<ProcessingRegistry> it = ProcessFinder.getProcessFactories(); it.hasNext(); ) {
            final ProcessingRegistry processingRegistry = it.next();
            for (ProcessDescriptor descriptor : processingRegistry.getDescriptors()) {
                if (descriptor instanceof OpenEOExposedProcess exposed
                        && Objects.equals(exposed.getOpenEOProcessId(), eoProcessId)) {
                    String code = descriptor.getIdentifier().getCode();
                    if (!fullDescriptor) {
                        return code;
                    }
                    Iterator<? extends Identifier> ids = processingRegistry.getIdentification()
                            .getCitation().getIdentifiers().iterator();
                    String registryName = ids.hasNext() ? ids.next().getCode() : processingRegistry.getIdentification().toString();
                    return registryName + "." + code;
                }
            }
        }
        return eoProcessId;
    }

    /**
     * Build DataTypeSchema based on the provided parameters.
     * Handles special cases for certain descriptor names and types.
     * Example : If the input is an Envelope, will return a DataTypeSchema representing a bounding box.
     *
     * @param descriptor process descriptor
     * @param descriptorName process descriptor name
     * @param type type of the descriptor (as a string)
     * @param clazz Java class of the descriptor
     * @param isArray indicates if the descriptor is an array
     * @param mandatory indicates if the descriptor is mandatory
     * @return an array of DataTypeSchema objects
     */
    public static DataTypeSchema[] buildDataTypeSchema(ProcessDescriptor descriptor, String descriptorName,
                                                       String type, Class<?> clazz, boolean isArray, boolean mandatory) {
        if (descriptor instanceof OpenEOExposedProcess exposed) {
            DataTypeSchema[] override = exposed.getOpenEOSchemaOverride(descriptorName, type, clazz, isArray, mandatory);
            if (override != null) {
                return override;
            }
        }

        List<DataTypeSchema> dataTypeSchemas = new ArrayList<>();

        if (clazz == Envelope.class) {
            String title = "Bounding Box";
            String description = "A bounding box is a list of 4 numbers (west, south, east, north) and an associated CRS.";
            String subtype = "bounding-box";
            List<String> required = new ArrayList<>(List.of("west", "south", "east", "north"));

            Map<String, Object> properties = new HashMap<>();
            properties.put("north", Map.of("description", "North (upper right corner)", "type", "number"));
            properties.put("south", Map.of("description", "South (lower left corner)", "type", "number"));
            properties.put("west", Map.of("description", "West (lower left corner)", "type", "number"));
            properties.put("east", Map.of("description", "East (upper right corner)", "type", "number"));
            properties.put("crs", Map.of("description", "The coordinate reference system in which the coordinates are given.", "type", "string", "default", "EPSG:4326"));

            dataTypeSchemas.add(new DataTypeSchema(title, description, properties, null, required, List.of(DataTypeSchema.Type.fromValue(type, isArray)), subtype));

            if (!mandatory) {
                dataTypeSchemas.add(new DataTypeSchema("No filter", "Don't filter spatially", null, null, null, List.of(DataTypeSchema.Type.NULL), null));
            }
            return dataTypeSchemas.toArray(new DataTypeSchema[0]);
        }

        if (clazz == String[].class && isArray && descriptorName.equalsIgnoreCase("temporal_extent")) {
            String title = "Temporal extent";
            String description = "Temporal extent to load.";
            String subtype = "temporal-interval";

            Map<String, Object> items = new HashMap<>();
            items.put("anyOf", List.of(
                    Map.of("format", "date-time", "subtype", "date-time", "type", "string"),
                    Map.of("format", "date", "subtype", "date", "type", "string"),
                    Map.of("type", "null")
                    )
            );

            dataTypeSchemas.add(new DataTypeSchema(title, description, null, items, null, List.of(DataTypeSchema.Type.fromValue(type, isArray)), subtype));

            if (!mandatory) {
                dataTypeSchemas.add(new DataTypeSchema("No filter", "Don't filter temporally.", null, null, null, List.of(DataTypeSchema.Type.NULL), null));
            }
            return dataTypeSchemas.toArray(new DataTypeSchema[0]);
        }

        if ((clazz == Integer[].class || clazz == String[].class) && isArray && descriptorName.equalsIgnoreCase("bands")) {
            String title = "Bands identifier to select";
            String description = "Bands to select using their index (you can use information in the collection to match this index to the content of the band)";

            Map<String, Object> items = Map.of("subtype", "band", "type", "number");

            dataTypeSchemas.add(new DataTypeSchema(title, description, null, items, null, List.of(DataTypeSchema.Type.fromValue(type, isArray)), null));

            if (!mandatory) {
                dataTypeSchemas.add(new DataTypeSchema("No filter", "Don't filter bands. All bands are included in the data cube.", null, null, null, List.of(DataTypeSchema.Type.NULL), null));
            }
            return dataTypeSchemas.toArray(new DataTypeSchema[0]);
        }

        // Array of GridCoverage → openEO "raster-cube" (array form)
        if (isArray && clazz != null && clazz.isArray()
                && GridCoverage.class.isAssignableFrom(clazz.getComponentType())) {
            return new DataTypeSchema[]{
                    new DataTypeSchema(List.of(DataTypeSchema.Type.ARRAY), "raster-cube")
            };
        }

        // Single GridCoverage → openEO "raster-cube" (object form)
        if (!isArray && clazz != null && GridCoverage.class.isAssignableFrom(clazz)) {
            return new DataTypeSchema[]{
                    new DataTypeSchema(List.of(DataTypeSchema.Type.OBJECT), "raster-cube")
            };
        }

        return new DataTypeSchema[]{new DataTypeSchema(type == null ? List.of() : List.of(DataTypeSchema.Type.fromValue(type, isArray)), null)};
    }
}
