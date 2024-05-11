/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.internal.geojson;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Static;
import org.apache.sis.util.iso.Names;
import org.apache.sis.util.SimpleInternationalString;
import org.apache.sis.util.iso.DefaultNameFactory;
import org.geotoolkit.feature.FeatureExt;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;

/**
 * An utility class to handle read/write of FeatureType into a JSON schema file.
 *
 * Theses schema are inspired from <a href="http://json-schema.org/">JSON-Schema</a> specification.
 * Changes are :
 *  - introducing of a {@code javatype} that define Java class used.
 *  - introducing of a {@code nillable} property for nullable attributes
 *  - introducing of a {@code userdata} Map property that contain previous user data information.
 *  - introducing of a {@code geometry} property to describe a geometry
 *  - introducing of a {@code crs} property to describe a geometry crs
 *
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
public final class FeatureTypeUtils extends Static {

    private static final String TITLE = "title";
    private static final String TYPE = "type";
    private static final String JAVA_TYPE = "javatype";
    private static final String DESCRIPTION = "description";
    private static final String PROPERTIES = "properties";
    private static final String PRIMARY_KEY = "primaryKey";
    private static final String RESTRICTION = "restriction";
    private static final String REQUIRED = "required";
    private static final String MIN_ITEMS = "minItems";
    private static final String MAX_ITEMS = "maxItems";
    private static final String USER_DATA = "userdata";
    private static final String GEOMETRY = "geometry";
    private static final String GEOMETRY_ATT_NAME = "geometryName";
    private static final String CRS = "crs";

    private static final String OBJECT = "object";
    private static final String ARRAY = "array";
    private static final String INTEGER = "integer";
    private static final String NUMBER = "number";
    private static final String STRING = "string";
    private static final String BOOLEAN = "boolean";

    /**
     * Write a FeatureType in output File.
     *
     * @param ft
     * @param output
     * @throws IOException
     */
    public static void writeFeatureType(FeatureType ft, Path output) throws IOException, DataStoreException {
        ArgumentChecks.ensureNonNull("FeatureType", ft);
        ArgumentChecks.ensureNonNull("outputFile", output);

        Optional<AttributeType<?>> geom = GeoJSONUtils.castOrUnwrap(FeatureExt.getDefaultGeometrySafe(ft));

        try (OutputStream outStream = Files.newOutputStream(output, CREATE, WRITE, TRUNCATE_EXISTING);
                JsonGenerator writer = GeoJSONParser.JSON_FACTORY.createGenerator(outStream, JsonEncoding.UTF8)) {

            writer.useDefaultPrettyPrinter();
            //start write feature collection.
            writer.writeStartObject();
            writer.writeStringField(TITLE, ft.getName().tip().toString());
            writer.writeStringField(TYPE, OBJECT);
            writer.writeStringField(JAVA_TYPE, "FeatureType");
            if (ft.getDescription().isPresent()) {
                writer.writeStringField(DESCRIPTION, ft.getDescription().get().toString());
            }

            if (geom.isPresent()) {
                writeGeometryType(geom.get(), writer);
            }
            writeProperties(ft, writer);

            writer.writeEndObject();
            writer.flush();
        }
    }

    public static void writeFeatureTypes(List<FeatureType> fts, OutputStream output) throws IOException, DataStoreException {
        ArgumentChecks.ensureNonNull("FeatureType", fts);
        ArgumentChecks.ensureNonNull("outputStream", output);

        if (fts.isEmpty()) {
            return;
        }

        if (fts.size() > 1) {
            try (JsonGenerator writer = GeoJSONParser.JSON_FACTORY.createGenerator(output, JsonEncoding.UTF8).useDefaultPrettyPrinter()) {
                writer.writeStartArray();
                for (FeatureType ft : fts) {
                    writeFeatureType(ft, output, writer);
                }
                writer.writeEndArray();
                writer.flush();
            }
        } else {
            writeFeatureType(fts.get(0), output);
        }
    }

    /**
     * Write a FeatureType in output File.
     *
     * @param ft
     * @param output
     * @throws IOException
     */
    public static void writeFeatureType(FeatureType ft, OutputStream output) throws IOException, DataStoreException {
        try (JsonGenerator writer = GeoJSONParser.JSON_FACTORY.createGenerator(output, JsonEncoding.UTF8).useDefaultPrettyPrinter()) {
            writeFeatureType(ft, output, writer);
            writer.flush();
        }
    }

    private static void writeFeatureType(FeatureType ft, OutputStream output, JsonGenerator writer) throws IOException, DataStoreException {
        ArgumentChecks.ensureNonNull("FeatureType", ft);
        ArgumentChecks.ensureNonNull("outputStream", output);

        //start write feature collection.
        writer.writeStartObject();
        writer.writeStringField(TITLE, ft.getName().tip().toString());
        writer.writeStringField(TYPE, OBJECT);
        writer.writeStringField(JAVA_TYPE, "FeatureType");
        if (ft.getDescription().isPresent()) {
            writer.writeStringField(DESCRIPTION, ft.getDescription().get().toString());
        }

        final Optional<AttributeType<?>> geom = GeoJSONUtils.castOrUnwrap(FeatureExt.getDefaultGeometrySafe(ft));
        if (geom.isPresent()) {
            writeGeometryType(geom.get(), writer);
        }

        writeProperties(ft, writer);

        writer.writeEndObject();
    }

    private static void writeProperties(FeatureType ft, JsonGenerator writer) throws IOException {
        writer.writeObjectFieldStart(PROPERTIES);

        Collection<? extends PropertyType> descriptors = ft.getProperties(true);
        List<String> required = new ArrayList<>();

        for (PropertyType type : descriptors) {
            boolean isRequired = false;

            if (type instanceof FeatureAssociationRole) {
                isRequired = writeComplexType((FeatureAssociationRole) type, ((FeatureAssociationRole) type).getValueType(), writer);
            } else if (type instanceof AttributeType) {
                if (Geometry.class.isAssignableFrom(((AttributeType) type).getValueClass())) {
//                    GeometryType geometryType = (GeometryType) type;
//                    isRequired = writeGeometryType(descriptor, geometryType, writer);
                } else {
                    isRequired = writeAttributeType(ft, (AttributeType) type, writer);
                }
            }
            if (isRequired) {
                required.add(type.getName().tip().toString());
            }
        }

        if (!required.isEmpty()) {
            writer.writeArrayFieldStart(REQUIRED);
            for (String req : required) {
                writer.writeString(req);
            }
            writer.writeEndArray();
        }
        writer.writeEndObject();
    }

    private static boolean writeComplexType(FeatureAssociationRole descriptor, FeatureType complex, JsonGenerator writer)
            throws IOException {

        writer.writeObjectFieldStart(descriptor.getName().tip().toString());
        writer.writeStringField(TYPE, OBJECT);
        writer.writeStringField(JAVA_TYPE, "ComplexType");
        if (complex.getDescription().isPresent()) {
            writer.writeStringField(DESCRIPTION, complex.getDescription().get().toString());
        }
        writer.writeNumberField(MIN_ITEMS, descriptor.getMinimumOccurs());
        writer.writeNumberField(MAX_ITEMS, descriptor.getMaximumOccurs());
        writeProperties(complex, writer);

        writer.writeEndObject();

        return descriptor.getMinimumOccurs() > 0;
    }

    private static boolean writeAttributeType(FeatureType featureType, AttributeType att, JsonGenerator writer)
            throws IOException {

        writer.writeObjectFieldStart(att.getName().tip().toString());
        Class binding = att.getValueClass();

        writer.writeStringField(TYPE, findType(binding));
        writer.writeStringField(JAVA_TYPE, binding.getName());
        if (att.getDescription().isPresent()) {
            writer.writeStringField(DESCRIPTION, att.getDescription().get().toString());
        }
        if (GeoJSONUtils.isPartOfPrimaryKey(featureType, att.getName().toString())) {
            writer.writeBooleanField(PRIMARY_KEY, true);
        }
        writer.writeNumberField(MIN_ITEMS, att.getMinimumOccurs());
        writer.writeNumberField(MAX_ITEMS, att.getMaximumOccurs());
//        List<Filter> restrictions = att.getRestrictions();
//        if (restrictions != null && !restrictions.isEmpty()) {
//            final Filter merged = FF.and(restrictions);
//            writer.writeStringField(RESTRICTION, CQL.write(merged));
//        }
        writer.writeEndObject();

        return att.getMinimumOccurs() > 0;
    }

    private static String findType(Class binding) {

        if (Integer.class.isAssignableFrom(binding)) {
            return INTEGER;
        } else if (Number.class.isAssignableFrom(binding)) {
            return NUMBER;
        } else if (Boolean.class.isAssignableFrom(binding)) {
            return BOOLEAN;
        } else if (binding.isArray()) {
            return ARRAY;
        } else {
            //fallback
            return STRING;
        }
    }

    private static boolean writeGeometryType(AttributeType geometryType, JsonGenerator writer)
            throws IOException {
        writer.writeObjectFieldStart(GEOMETRY);
        writer.writeStringField(TYPE, OBJECT);
        if (geometryType.getDescription().isPresent()) {
            writer.writeStringField(DESCRIPTION, geometryType.getDescription().get().toString());
        }
        writer.writeStringField(JAVA_TYPE, geometryType.getValueClass().getCanonicalName());
        CoordinateReferenceSystem crs = FeatureExt.getCRS(geometryType);
        if (crs != null) {
            final Optional<String> urn = GeoJSONUtils.toURN(crs);
            if (urn.isPresent()) {
                writer.writeStringField(CRS, urn.get());
            }
        }
        writer.writeStringField(GEOMETRY_ATT_NAME, geometryType.getName().tip().toString());
        writer.writeEndObject();
        return true;
    }

    /**
     * Read a FeatureType from an input File.
     *
     * @param input file to read
     * @return FeatureType
     * @throws IOException
     */
    public static FeatureType readFeatureType(Path input) throws IOException, DataStoreException {

        try (InputStream stream = Files.newInputStream(input);
                JsonParser parser = GeoJSONParser.JSON_FACTORY.createParser(stream)) {

            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            parser.nextToken();

            while (parser.nextToken() != JsonToken.END_OBJECT) {

                final String currName = parser.getCurrentName();
                switch (currName) {
                    case TITLE:
                        ftb.setName(parser.nextTextValue());
                        break;
                    case JAVA_TYPE:
                        String type = parser.nextTextValue();
                        if (!"FeatureType".equals(type)) {
                            throw new DataStoreException("Invalid JSON schema : " + input.getFileName().toString());
                        }
                        break;
                    case PROPERTIES:
                        readProperties(ftb, parser);
                        break;
                    case GEOMETRY:
                        readGeometry(ftb, parser);
                        break;
                    case DESCRIPTION:
                        ftb.setDescription(parser.nextTextValue());
                        break;
                }
            }

            try {
                return ftb.build();
            } catch (IllegalStateException ex) {
                throw new DataStoreException("FeatureType name or default geometry not found in JSON schema\n" + ex.getMessage(), ex);
            }
        }
    }

    private static void readGeometry(FeatureTypeBuilder ftb, JsonParser parser)
            throws IOException, DataStoreException {

        Class<?> binding = null;
        CoordinateReferenceSystem crs = null;
        InternationalString description = null;
        String geometryName = null;

        parser.nextToken(); // {
        while (parser.nextToken() != JsonToken.END_OBJECT) { // -> }
            final String currName = parser.getCurrentName();
            switch (currName) {
                case JAVA_TYPE:
                    String javaTypeValue = parser.nextTextValue();
                    if (!"ComplexType".equals(javaTypeValue)) {
                        try {
                            binding = Class.forName(javaTypeValue);
                        } catch (ClassNotFoundException e) {
                            throw new DataStoreException("Geometry javatype " + javaTypeValue + " invalid : " + e.getMessage(), e);
                        }
                    }
                    break;

                case CRS:
                    String crsCode = parser.nextTextValue();
                    try {
                        crs = org.apache.sis.referencing.CRS.forCode(crsCode);
                    } catch (FactoryException e) {
                        throw new DataStoreException("Geometry crs " + crsCode + " invalid : " + e.getMessage(), e);
                    }
                    break;

                case DESCRIPTION:
                    description = new SimpleInternationalString(parser.nextTextValue());
                    break;
                case GEOMETRY_ATT_NAME:
                    geometryName = parser.nextTextValue();
            }
        }

        if (binding == null) {
            throw new DataStoreException("Binding class not found.");
        }

        final GenericName name = geometryName != null ? Names.createLocalName(null, null, geometryName) : Names.createLocalName(null, null, "geometry");

        final AttributeTypeBuilder<?> atb = ftb.addAttribute(binding);
        atb.setName(name);
        atb.setDescription(description);
        if (crs != null) {
            atb.setCRS(crs);
        }
        atb.setMinimumOccurs(1);
        atb.setMaximumOccurs(1);
        atb.addRole(AttributeRole.DEFAULT_GEOMETRY);
    }

    private static void readProperties(FeatureTypeBuilder ftb, JsonParser parser)
            throws IOException, DataStoreException {
        parser.nextToken(); // {

        List<String> requiredList = null;
        while (parser.nextToken() != JsonToken.END_OBJECT) { // -> }
            final JsonToken currToken = parser.getCurrentToken();

            if (currToken == JsonToken.FIELD_NAME) {
                final String currName = parser.getCurrentName();

                if (REQUIRED.equals(currName)) {
                    requiredList = parseRequiredArray(parser);
                } else {
                    parseProperty(ftb, parser);
                }
            }
        }
    }

    private static void parseProperty(FeatureTypeBuilder ftb, JsonParser parser)
            throws IOException, DataStoreException {

        final String attributeName = parser.getCurrentName();
        Class<?> binding = String.class;
        boolean primaryKey = false;
        int minOccurs = 0;
        int maxOccurs = 1;
        CharSequence description = null;
        String restrictionCQL = null;
        Map<Object, Object> userData = null;
        FeatureTypeBuilder subftb = null;

        parser.nextToken();
        while (parser.nextToken() != JsonToken.END_OBJECT) {

            final String currName = parser.getCurrentName();
            switch (currName) {
                case JAVA_TYPE:
                    String javaTypeValue = parser.nextTextValue();
                    if (!"ComplexType".equals(javaTypeValue)) {
                        try {
                            binding = Class.forName(javaTypeValue);
                        } catch (ClassNotFoundException e) {
                            throw new DataStoreException("Attribute " + attributeName + " invalid : " + e.getMessage(), e);
                        }
                    }
                    break;
                case MIN_ITEMS:
                    minOccurs = parser.nextIntValue(0);
                    break;
                case MAX_ITEMS:
                    maxOccurs = parser.nextIntValue(1);
                    break;
                case PRIMARY_KEY:
                    primaryKey = parser.nextBooleanValue();
                    break;
                case RESTRICTION:
                    restrictionCQL = parser.nextTextValue();
                    break;
                case USER_DATA:
                    userData = parseUserDataMap(parser);
                    break;
                case PROPERTIES:
                    subftb = new FeatureTypeBuilder();
                    readProperties(subftb, parser);
                    break;
                case DESCRIPTION:
                    description = parser.nextTextValue();
                    break;
            }
        }

        GenericName name = nameValueOf(attributeName);
        if (subftb == null) {
            //build AttributeDescriptor
            if (binding == null) {
                throw new DataStoreException("Empty javatype for attribute " + attributeName);
            }

            AttributeTypeBuilder<?> atb = ftb.addAttribute(binding)
                    .setName(name)
                    .setDescription(description)
                    .setMinimumOccurs(minOccurs)
                    .setMaximumOccurs(maxOccurs);

            if (primaryKey) {
                atb.addRole(AttributeRole.IDENTIFIER_COMPONENT);
            }

        } else {
            //build ComplexType
            subftb.setName(name);
            subftb.setDescription(description);
            final FeatureType complexType = subftb.build();

            ftb.addAssociation(complexType)
                    .setName(name)
                    .setMinimumOccurs(minOccurs)
                    .setMaximumOccurs(maxOccurs);
        }
    }

    private static Map<Object, Object> parseUserDataMap(JsonParser parser) throws IOException {

        Map<Object, Object> map = new HashMap<>();
        parser.nextToken(); // {
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            Object key = parser.getCurrentName();
            JsonToken next = parser.nextToken();
            map.put(key, GeoJSONParser.getValue(next, parser));
        }
        return map;

    }

    private static List<String> parseRequiredArray(JsonParser parser) throws IOException {
        List<String> requiredList = new ArrayList<>();
        parser.nextToken(); // [

        while (parser.nextToken() != JsonToken.END_ARRAY) { // -> ]
            requiredList.add(parser.getValueAsString());
        }

        return requiredList;
    }

    /**
     * Parse a string value that can be expressed in 2 different forms :
     * JSR-283 extended form : {uri}localpart
     * Separator form : uri:localpart
     *
     * if the given string do not match any, then a Name with no namespace will
     * be created and the localpart will be the given string.
     */
    public static GenericName nameValueOf(final String candidate) {

        if (candidate.startsWith("{")) {
            //name is in extended form
            return toSessionNamespaceFromExtended(candidate);
        }

        int index = candidate.lastIndexOf(':');

        if (index <= 0) {
            return createName(null, candidate);
        } else {
            final String uri = candidate.substring(0, index);
            final String name = candidate.substring(index + 1, candidate.length());
            return createName(uri, name);
        }

    }

    private static GenericName toSessionNamespaceFromExtended(final String candidate) {
        final int index = candidate.indexOf('}');

        if (index == -1) {
            throw new IllegalArgumentException("Invalide extended form : " + candidate);
        }

        final String uri = candidate.substring(1, index);
        final String name = candidate.substring(index + 1, candidate.length());

        return createName(uri, name);
    }

    /**
     *
     * @param namespace if null or empty will not be used for the name
     * @param local mandatory
     */
    public static GenericName createName(final String namespace, final String local) {

        // WARNING: DefaultFactories.NAMES is not a public API and may change in any future SIS version.
        if (namespace == null || namespace.isEmpty()) {
            return DefaultNameFactory.provider().createGenericName(null, local);
        } else {
            return DefaultNameFactory.provider().createGenericName(null, namespace, local);
        }
    }
}
