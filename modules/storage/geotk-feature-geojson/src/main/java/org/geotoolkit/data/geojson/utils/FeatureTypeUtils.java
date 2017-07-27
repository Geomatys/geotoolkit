package org.geotoolkit.data.geojson.utils;

import org.opengis.util.GenericName;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.vividsolutions.jts.geom.Geometry;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.cql.CQL;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.opengis.feature.AttributeType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.iso.SimpleInternationalString;

import org.geotoolkit.util.NamesExt;
import org.geotoolkit.lang.Static;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardOpenOption.*;
import org.geotoolkit.feature.SingleAttributeTypeBuilder;
import org.apache.sis.feature.DefaultAssociationRole;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.FeatureTypeExt;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;

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
 */
public final class FeatureTypeUtils extends Static {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.geojson.utils");
    private static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(
            new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));

    private static final String TITLE = "title";
    private static final String TYPE = "type";
    private static final String JAVA_TYPE = "javatype";
    private static final String DESCRIPTION = "description";
    private static final String PROPERTIES = "properties";
    private static final String PRIMARY_KEY = "primaryKey";
    private static final String RESTRICTION = "restriction";
    private static final String REQUIRED = "required";
    private static final String NILLABLE = "nillable";
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
     * @param ft
     * @param output
     * @throws IOException
     */
    @Deprecated
    public static void writeFeatureType(FeatureType ft, File output) throws IOException, DataStoreException {
        writeFeatureType(ft, output.toPath());
    }
    /**
     * Write a FeatureType in output File.
     * @param ft
     * @param output
     * @throws IOException
     */
    public static void writeFeatureType(FeatureType ft, Path output) throws IOException, DataStoreException {
        ArgumentChecks.ensureNonNull("FeatureType", ft);
        ArgumentChecks.ensureNonNull("outputFile", output);

        final AttributeType<?> geom = FeatureExt
                .castOrUnwrap(FeatureExt.getDefaultGeometry(ft))
                .orElseThrow(() -> new DataStoreException("No default Geometry in given FeatureType : " + ft));

        try (OutputStream outStream = Files.newOutputStream(output, CREATE, WRITE, TRUNCATE_EXISTING);
             JsonGenerator writer = GeoJSONParser.FACTORY.createGenerator(outStream, JsonEncoding.UTF8)) {

            writer.useDefaultPrettyPrinter();
            //start write feature collection.
            writer.writeStartObject();
            writer.writeStringField(TITLE, ft.getName().tip().toString());
            writer.writeStringField(TYPE, OBJECT);
            writer.writeStringField(JAVA_TYPE, "FeatureType");
            if (ft.getDescription() != null) {
                writer.writeStringField(DESCRIPTION, ft.getDescription().toString());
            }

            writeGeometryType(geom, writer);
            writeProperties(ft, writer);

            writer.writeEndObject();
            writer.flush();
        }
    }

    public static void writeFeatureTypes(List<FeatureType> fts, OutputStream output) throws IOException, DataStoreException {
        ArgumentChecks.ensureNonNull("FeatureType", fts);
        ArgumentChecks.ensureNonNull("outputStream", output);

        if (fts.isEmpty()) return;

        if (fts.size() > 1) {
            JsonGenerator writer = GeoJSONParser.FACTORY.createGenerator(output, JsonEncoding.UTF8).useDefaultPrettyPrinter();
            writer.writeStartArray();
            for (FeatureType ft : fts) {
                writeFeatureType(ft, output, writer);
            }
            writer.writeEndArray();
            writer.flush();
            writer.close();
        } else {
            writeFeatureType(fts.get(0), output);
        }
    }
    /**
     * Write a FeatureType in output File.
     * @param ft
     * @param output
     * @throws IOException
     */
    public static void writeFeatureType(FeatureType ft, OutputStream output) throws IOException, DataStoreException {
        JsonGenerator writer = GeoJSONParser.FACTORY.createGenerator(output,JsonEncoding.UTF8).useDefaultPrettyPrinter();
        writeFeatureType(ft, output, writer);
        writer.flush();
        writer.close();
    }


    private static void writeFeatureType(FeatureType ft, OutputStream output, JsonGenerator writer) throws IOException, DataStoreException {
        ArgumentChecks.ensureNonNull("FeatureType", ft);
        ArgumentChecks.ensureNonNull("outputStream", output);

        if (FeatureExt.getDefaultGeometry(ft) == null) {
            throw new DataStoreException("No default Geometry in given FeatureType : "+ft);
        }

        //start write feature collection.
        writer.writeStartObject();
        writer.writeStringField(TITLE, ft.getName().tip().toString());
        writer.writeStringField(TYPE, OBJECT);
        writer.writeStringField(JAVA_TYPE, "FeatureType");
        if (ft.getDescription() != null) {
            writer.writeStringField(DESCRIPTION, ft.getDescription().toString());
        }

        final Optional<AttributeType<?>> geom = FeatureExt.castOrUnwrap(
                FeatureExt.getDefaultGeometry(ft)
        );
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
                isRequired = writeComplexType((FeatureAssociationRole) type, ((FeatureAssociationRole)type).getValueType(), writer);
            } else if (type instanceof AttributeType) {
                if(Geometry.class.isAssignableFrom( ((AttributeType) type).getValueClass())){
//                    GeometryType geometryType = (GeometryType) type;
//                    isRequired = writeGeometryType(descriptor, geometryType, writer);
                } else {
                    isRequired = writeAttributeType(ft, (AttributeType)type, writer);
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
        if (complex.getDescription() != null) {
            writer.writeStringField(DESCRIPTION, complex.getDescription().toString());
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
        if (att.getDescription() != null) {
            writer.writeStringField(DESCRIPTION, att.getDescription().toString());
        }
        if (FeatureTypeExt.isPartOfPrimaryKey(featureType,att.getName().toString())) {
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
        } else if(Boolean.class.isAssignableFrom(binding)) {
            return BOOLEAN;
        } else if (binding.isArray()) {
            return ARRAY;
        } else  {
            //fallback
            return STRING;
        }
    }

    private static boolean writeGeometryType(AttributeType geometryType, JsonGenerator writer)
            throws IOException {
        writer.writeObjectFieldStart(GEOMETRY);
        writer.writeStringField(TYPE, OBJECT);
        if (geometryType.getDescription() != null) {
            writer.writeStringField(DESCRIPTION, geometryType.getDescription().toString());
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
     * @param input file to read
     * @return FeatureType
     * @throws IOException
     */
    @Deprecated
    public static FeatureType readFeatureType(File input) throws IOException, DataStoreException {
        return readFeatureType(input.toPath());
    }

    /**
     * Read a FeatureType from an input File.
     * @param input file to read
     * @return FeatureType
     * @throws IOException
     */
    public static FeatureType readFeatureType(Path input) throws IOException, DataStoreException {

        try (InputStream stream = Files.newInputStream(input);
             JsonParser parser = GeoJSONParser.FACTORY.createParser(stream)) {

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
                        for(PropertyType pt : readProperties(parser)){
                            if(pt instanceof AttributeType){
                                ftb.addAttribute((AttributeType) pt);
                            }else if(pt instanceof FeatureAssociationRole){
                                ftb.addAssociation((FeatureAssociationRole) pt);
                            }
                        }
                        break;
                    case GEOMETRY:
                        AttributeType geomAtt = readGeometry(parser);
                        ftb.addAttribute(geomAtt).addRole(AttributeRole.DEFAULT_GEOMETRY);
                        break;
                    case DESCRIPTION:
                        ftb.setDescription(parser.nextTextValue());
                        break;
                }
            }

            try{
                return ftb.build();
            }catch(IllegalStateException ex){
                throw new DataStoreException("FeatureType name or default geometry not found in JSON schema\n"+ex.getMessage(),ex);
            }
        }
    }

    private static AttributeType readGeometry(JsonParser parser)
            throws IOException, DataStoreException {

        Class binding = null;
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

        final GenericName name = geometryName != null ? NamesExt.create(geometryName) : NamesExt.create("geometry");
        final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
        atb.setName(name);
        if (crs != null) {
            atb.setCRS(crs);
        }
        atb.setValueClass(binding);
        atb.setMinimumOccurs(1);
        atb.setMaximumOccurs(1);
        return atb.build();
    }

    private static List<PropertyType> readProperties(JsonParser parser)
            throws IOException, DataStoreException {
        List<PropertyType> propertyDescriptors = new ArrayList<>();
        parser.nextToken(); // {

        List<String> requiredList = null;
        while (parser.nextToken() != JsonToken.END_OBJECT) { // -> }
            final JsonToken currToken = parser.getCurrentToken();

            if (currToken == JsonToken.FIELD_NAME) {
                final String currName = parser.getCurrentName();

                if (REQUIRED.equals(currName)) {
                    requiredList = parseRequiredArray(parser);
                } else {
                    propertyDescriptors.add(parseProperty(parser));
                }
            }
        }
        return propertyDescriptors;
    }

    private static PropertyType parseProperty(JsonParser parser)
            throws IOException, DataStoreException {

        final String attributeName = parser.getCurrentName();
        Class binding = String.class;
        boolean primaryKey = false;
        int minOccurs = 0;
        int maxOccurs = 1;
        String description = null;
        String restrictionCQL = null;
        Map<Object, Object> userData = null;
        List<PropertyType> descs = new ArrayList<>();

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
                    descs = readProperties(parser);
                    break;
                case DESCRIPTION:
                    description = parser.nextTextValue();
                    break;
            }
        }

        if (primaryKey) {
            if (userData == null) userData = new HashMap<>();
            userData.put(HintsPending.PROPERTY_IS_IDENTIFIER, Boolean.TRUE);
        }

        GenericName name = NamesExt.valueOf(attributeName);
        InternationalString desc = description != null ? new SimpleInternationalString(description) : null;
        if (descs.isEmpty()) {
            //build AttributeDescriptor
            if (binding == null) {
                throw new DataStoreException("Empty javatype for attribute "+attributeName);
            }

            List<Filter> restrictions = null;
            if (restrictionCQL != null) {
                try {
                   restrictions = new ArrayList<>();
                   restrictions.add(CQL.parseFilter(restrictionCQL, FF));
                } catch (CQLException e) {
                    LOGGER.log(Level.WARNING, "Can't parse restriction filter : "+restrictionCQL, e);
                }
            }

            final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
            atb.setName(name);
            atb.setDescription(desc);
            atb.setValueClass(binding);
            atb.setMinimumOccurs(minOccurs);
            atb.setMaximumOccurs(maxOccurs);
            return atb.build();

        } else {
            //build ComplexType
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(name);
            for (PropertyType pt : descs) {
                if(pt instanceof AttributeType){
                    ftb.addAttribute((AttributeType) pt);
                }else if(pt instanceof FeatureAssociationRole){
                    ftb.addAssociation((FeatureType) pt);
                }
            }
            ftb.setDescription(desc);
            final FeatureType complexType = ftb.build();

            return new DefaultAssociationRole(Collections.singletonMap("name", name), complexType, minOccurs, maxOccurs);
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
}
