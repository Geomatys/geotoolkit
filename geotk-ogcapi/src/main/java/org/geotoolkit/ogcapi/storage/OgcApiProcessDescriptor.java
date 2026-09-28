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
package org.geotoolkit.ogcapi.storage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.SimpleInternationalString;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.ogcapi.dto.jsonschema.JSONSchemaProperty;
import org.geotoolkit.ogcapi.dto.jsonschema.JSONType;
import org.geotoolkit.ogcapi.dto.processes.InputDescription;
import org.geotoolkit.ogcapi.dto.processes.OutputDescription;
import org.geotoolkit.ogcapi.dto.processes.ProcessDescription;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.utility.parameter.ExtendedParameterDescriptor;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 * Process descriptor built from an OGC API - Processes process description.
 *
 * @author Johann Sorel (Geomatys)
 */
final class OgcApiProcessDescriptor extends AbstractProcessDescriptor {

    /**
     * User object key storing the original JSON schema of a parameter.
     */
    static final String KEY_SCHEMA = "ogcapi.schema";
    /**
     * JSON schema format of bounding box parameters.
     */
    static final String FORMAT_BBOX = "ogc-bbox";

    private final OgcApiProcessingRegistry registry;
    private final ProcessDescription description;

    private OgcApiProcessDescriptor(OgcApiProcessingRegistry registry, ProcessDescription description,
            ParameterDescriptorGroup inputs, ParameterDescriptorGroup outputs) {
        super(new DerivateIdentifier(description.getId(), registry.getIdentification()),
              text(description.getDescription()),
              text(description.getTitle() != null ? description.getTitle() : description.getId()),
              inputs, outputs, description.getVersion());
        this.registry = registry;
        this.description = description;
    }

    /**
     * @return original process description, never null
     */
    ProcessDescription getDescription() {
        return description;
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new OgcApiProcess(registry, this, input);
    }

    static OgcApiProcessDescriptor create(OgcApiProcessingRegistry registry, ProcessDescription description) {
        final List<GeneralParameterDescriptor> inputs = new ArrayList<>();
        if (description.getInputs() != null) {
            for (Map.Entry<String, InputDescription> entry : description.getInputs().entrySet()) {
                final InputDescription input = entry.getValue();
                final int min = input.getMinOccurs() == null ? 1 : input.getMinOccurs();
                final int max = Math.max(min, maxOccurs(input.getMaxOccurs()));
                inputs.add(toDescriptor(entry.getKey(), input.getTitle(), input.getDescription(), input.getSchema(), min, max));
            }
        }

        final List<GeneralParameterDescriptor> outputs = new ArrayList<>();
        if (description.getOutputs() != null) {
            for (Map.Entry<String, OutputDescription> entry : description.getOutputs().entrySet()) {
                final OutputDescription output = entry.getValue();
                outputs.add(toDescriptor(entry.getKey(), output.getTitle(), output.getDescription(), output.getSchema(), 0, 1));
            }
        }

        final ParameterDescriptorGroup inputGroup = new ParameterBuilder().addName("inputs")
                .createGroup(inputs.toArray(GeneralParameterDescriptor[]::new));
        final ParameterDescriptorGroup outputGroup = new ParameterBuilder().addName("outputs")
                .createGroup(outputs.toArray(GeneralParameterDescriptor[]::new));
        return new OgcApiProcessDescriptor(registry, description, inputGroup, outputGroup);
    }

    private static <T> ExtendedParameterDescriptor<T> toDescriptor(String name, String title, String remarks,
            JSONSchemaProperty schema, int min, int max) {
        @SuppressWarnings("unchecked")
        final Class<T> valueClass = (Class<T>) valueClass(schema);

        T[] validValues = null;
        final Set<Object> enums = schema == null ? null : schema.getEnum();
        if (enums != null && !enums.isEmpty()) {
            try {
                @SuppressWarnings("unchecked")
                final T[] values = (T[]) Array.newInstance(valueClass, enums.size());
                int i = 0;
                for (Object value : enums) {
                    values[i++] = ObjectConverters.convert(value, valueClass);
                }
                validValues = values;
            } catch (UnconvertibleObjectException ex) {
                OgcApiProcessingRegistry.LOGGER.log(Level.FINE, "Ignoring enumeration of parameter " + name, ex);
            }
        }

        final Map<String, Object> userObject = new HashMap<>();
        if (schema != null) {
            userObject.put(KEY_SCHEMA, schema);
        }
        return new ExtendedParameterDescriptor<>(name, title, remarks, min, max, valueClass, null, validValues, userObject);
    }

    /**
     * Java class used to store values of the given JSON schema.
     */
    static Class<?> valueClass(JSONSchemaProperty schema) {
        if (schema == null) return Object.class;
        if (FORMAT_BBOX.equals(schema.getFormat())) return Envelope.class;
        final JSONType type = schema.getType();
        if (type == null) return Object.class;
        return switch (type) {
            case STRING  -> String.class;
            case NUMBER  -> Double.class;
            case INTEGER -> Integer.class;
            case BOOLEAN -> Boolean.class;
            case ARRAY   -> List.class;
            case OBJECT  -> Map.class;
            default      -> Object.class;
        };
    }

    /**
     * Parse maxOccurs, which is either an integer or "unbounded".
     */
    private static int maxOccurs(Object value) {
        if (value instanceof Number n) {
            return n.intValue();
        } else if (value instanceof String s) {
            s = s.trim();
            if ("unbounded".equalsIgnoreCase(s)) return Integer.MAX_VALUE;
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                return 1;
            }
        }
        return 1;
    }

    private static InternationalString text(String value) {
        return new SimpleInternationalString(value == null ? "" : value);
    }
}
