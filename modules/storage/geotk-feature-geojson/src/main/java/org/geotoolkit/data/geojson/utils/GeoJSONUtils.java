package org.geotoolkit.data.geojson.utils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonLocation;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.io.wkt.Convention;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.data.geojson.binding.GeoJSONObject;
import org.geotoolkit.lang.Static;
import org.geotoolkit.nio.IOUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import java.io.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Collection;
import java.util.logging.Level;

import org.apache.sis.util.Utilities;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Function;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.io.wkt.WKTFormat;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.util.Numbers;
import static org.geotoolkit.data.geojson.utils.GeoJSONMembres.*;
import static org.geotoolkit.data.geojson.utils.GeoJSONTypes.*;
import org.geotoolkit.feature.FeatureExt;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;

/**
 * @author Quentin Boileau (Geomatys)
 */
public final class GeoJSONUtils extends Static {

    /**
     * Fallback CRS
     */
    private static final CoordinateReferenceSystem DEFAULT_CRS = CommonCRS.WGS84.normalizedGeographic();

    /**
     * Parse LinkedCRS (href + type).
     * @param href
     * @param crsType
     * @return CoordinateReferenceSystem or null.
     */
    public static CoordinateReferenceSystem parseCRS(String href, String crsType) {
        String wkt = null;
        try(InputStream stream = new URL(href).openStream()) {
            wkt = IOUtilities.toString(stream);
        } catch (IOException e) {
            GeoJSONParser.LOGGER.log(Level.WARNING, "Can't access to linked CRS "+href, e);
        }

        if (wkt != null) {
            WKTFormat format = new WKTFormat(Locale.ENGLISH, TimeZone.getTimeZone("GMT"));
            if (crsType.equals(CRS_TYPE_OGCWKT)) {
                format.setConvention(Convention.WKT1);
            } else if (crsType.equals(CRS_TYPE_ESRIWKT)) {
                format.setConvention(Convention.WKT1_COMMON_UNITS);
            }
            try {
                Object obj = format.parseObject(wkt);
                if (obj instanceof CoordinateReferenceSystem) {
                    return (CoordinateReferenceSystem) obj;
                } else {
                    GeoJSONParser.LOGGER.log(Level.WARNING, "Parsed WKT is not a CRS "+wkt);
                }
            } catch (ParseException e) {
                GeoJSONParser.LOGGER.log(Level.WARNING, "Can't parse CRS WKT " + crsType+ " : "+wkt, e);
            }
        }

        return null;
    }

    /**
     * Convert a CoordinateReferenceSystem to a identifier string like
     * urn:ogc:def:crs:EPSG::4326
     * @param crs
     * @return
     */
    public static Optional<String> toURN(CoordinateReferenceSystem crs) {
        ArgumentChecks.ensureNonNull("crs", crs);

        String urn = null;
        try {
            if (Utilities.equalsIgnoreMetadata(crs, CommonCRS.WGS84.normalizedGeographic())) {
                crs = CommonCRS.WGS84.normalizedGeographic();
            }

            urn = IdentifiedObjects.lookupURN(crs, Citations.EPSG);
        } catch (FactoryException e) {
            GeoJSONParser.LOGGER.log(Level.WARNING, "Unable to extract epsg code from given CRS "+crs, e);
        }

        return Optional.ofNullable(urn);
    }

    /**
     * Try to extract/parse the CoordinateReferenceSystem from a GeoJSONObject.
     * Use WGS_84 as fallback CRS.
     * @param obj GeoJSONObject
     * @return GeoJSONObject CoordinateReferenceSystem or fallback CRS (WGS84).
     * @throws MalformedURLException
     * @throws DataStoreException
     */
    public static CoordinateReferenceSystem getCRS(GeoJSONObject obj) throws MalformedURLException, DataStoreException {
        CoordinateReferenceSystem crs = null;
        try {
            if (obj.getCrs() != null) {
                crs = obj.getCrs().getCRS();
            }
        } catch (FactoryException e) {
            throw new DataStoreException(e.getMessage(), e);
        }

        if (crs == null) {
            crs = DEFAULT_CRS;
        }
        return crs;
    }

    /**
     * Utility method Create geotk Envelope if bbox array is filled.
     * @return Envelope or null.
     */
    public static Envelope getEnvelope(GeoJSONObject obj, CoordinateReferenceSystem crs) {

        double[] bbox = obj.getBbox();
        if (bbox != null) {
            GeneralEnvelope env = new GeneralEnvelope(crs);
            int dim = bbox.length/2;
            if (dim == 2) {
                env.setRange(0, bbox[0], bbox[2]);
                env.setRange(1, bbox[1], bbox[3]);
            } else if (dim == 3) {
                env.setRange(0, bbox[0], bbox[3]);
                env.setRange(1, bbox[1], bbox[4]);
            }
            return env;
        }
        return null;
    }

    /**
     * Return file name without extension
     * @param file candidate
     * @return String
     */
    public static String getNameWithoutExt(Path file) {
        return IOUtilities.filenameWithoutExtension(file);
    }

    /**
     * Returns the filename extension from a {@link String}, {@link File}, {@link URL} or
     * {@link URI}. If no extension is found, returns an empty string.
     *
     * @param  path The path as a {@link String}, {@link File}, {@link URL} or {@link URI}.
     * @return The filename extension in the given path, or an empty string if none.
     */
    public static String extension(final Object path) {
        return IOUtilities.extension(path);
    }

    /**
     * Write an empty FeatureCollection in a file
     * @param f output file
     * @throws IOException
     */
    @Deprecated
    public static void writeEmptyFeatureCollection(File f) throws IOException {
        writeEmptyFeatureCollection(f.toPath());
    }

    /**
     * Write an empty FeatureCollection in a file
     * @param f output file
     * @throws IOException
     */
    public static void writeEmptyFeatureCollection(Path f) throws IOException {

        try (OutputStream outStream = Files.newOutputStream(f, CREATE, WRITE, TRUNCATE_EXISTING);
             JsonGenerator writer = GeoJSONParser.FACTORY.createGenerator(outStream, JsonEncoding.UTF8)) {

            //start write feature collection.
            writer.writeStartObject();
            writer.writeStringField(TYPE, FEATURE_COLLECTION);
            writer.writeArrayFieldStart(FEATURES);
            writer.writeEndArray();
            writer.writeEndObject();
            writer.flush();
        }
    }

    /**
     * Useful method to help write an object into a JsonGenerator.
     * This method can handle :
     * <ul>
     *     <li>Arrays</li>
     *     <li>Collection</li>
     *     <li>Numbers (Double, Float, Short, BigInteger, BigDecimal, integer, Long, Byte)</li>
     *     <li>Boolean</li>
     *     <li>String</li>
     * </ul>
     * @param value
     * @param writer
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public static void writeValue(Object value, JsonGenerator writer) throws IOException, IllegalArgumentException {

        if (value == null) {
            writer.writeNull();
            return;
        }

        Class binding = value.getClass();

        if (binding.isArray()) {
            if (byte.class.isAssignableFrom(binding.getComponentType())) {
                writer.writeBinary((byte[])value);
            } else {
                writer.writeStartArray();
                final int size = Array.getLength(value);
                for (int i = 0; i < size; i++) {
                    writeValue(Array.get(value, i), writer);
                }
                writer.writeEndArray();
            }

        } else if (Collection.class.isAssignableFrom(binding)) {
            writer.writeStartArray();
            Collection coll = (Collection) value;
            for (Object obj : coll) {
                writeValue(obj, writer);
            }
            writer.writeEndArray();

        } else if (Double.class.isAssignableFrom(binding)) {
            writer.writeNumber((Double) value);
        } else if (Float.class.isAssignableFrom(binding)) {
            writer.writeNumber((Float) value);
        } else if (Short.class.isAssignableFrom(binding)) {
            writer.writeNumber((Short) value);
        } else if (Byte.class.isAssignableFrom(binding)) {
            writer.writeNumber((Byte) value);
        } else if (BigInteger.class.isAssignableFrom(binding)) {
            writer.writeNumber((BigInteger) value);
        } else if (BigDecimal.class.isAssignableFrom(binding)) {
            writer.writeNumber((BigDecimal) value);
        } else if (Integer.class.isAssignableFrom(binding)) {
            writer.writeNumber((Integer) value);
        } else if (Long.class.isAssignableFrom(binding)) {
            writer.writeNumber((Long) value);

        } else if (Boolean.class.isAssignableFrom(binding)) {
            writer.writeBoolean((Boolean) value);
        } else if (String.class.isAssignableFrom(binding)) {
            writer.writeString(String.valueOf(value));
        } else {
            //fallback
            writer.writeString(String.valueOf(value));
        }
    }

    /**
     * Compare {@link JsonLocation} equality without sourceRef test.
     * @param loc1
     * @param loc2
     * @return
     */
    public static boolean equals(JsonLocation loc1, JsonLocation loc2) {
        if (loc1 == null) {
            return (loc2 == null);
        }

        return loc2 != null && (loc1.getLineNr() == loc2.getLineNr() &&
                loc1.getColumnNr() == loc2.getColumnNr() &&
                loc1.getByteOffset() == loc2.getByteOffset() &&
                loc1.getCharOffset() == loc2.getCharOffset());
    }

    /**
     * Check wether the given data type contains an identifier property according
     * to SIS convention (see {@link AttributeConvention#IDENTIFIER_PROPERTY}).
     *
     * @param toSearchIn The data type to scan for an identifier.
     * @return True if an sis:identifier property is available. False otherwise.
     */
    public static boolean hasIdentifier(final FeatureType toSearchIn) {
        try {
            toSearchIn.getProperty(AttributeConvention.IDENTIFIER_PROPERTY.toString());
            return true;
        } catch (PropertyNotFoundException ex) {
            return false;
        }
    }

    /**
     * If an sis:identifier property is available (see
     * {@link AttributeConvention#IDENTIFIER_PROPERTY}), we try to acquire it
     * value type (see {@link AttributeType#getValueClass() }. If we cannot
     * determine the value type for this property, we simply return an empty
     * optional. Note that an error is thrown if the given feature type does not
     * contain any identifier property.
     *
     * @param toSearchIn The property to extract identifier from.
     * @return The value class of found property if we can determine it (i.e:
     * it's an attribute or an operation from which we can unravel an
     * attribute), or an empty object if the property cannot provide value
     * class.
     * @throws PropertyNotFoundException If no
     * {@link AttributeConvention#IDENTIFIER_PROPERTY} is present in the input.
     */
    public static Optional<Class> getIdentifierType(final FeatureType toSearchIn) throws PropertyNotFoundException {
        final PropertyType idProperty = toSearchIn.getProperty(AttributeConvention.IDENTIFIER_PROPERTY.toString());
        return FeatureExt.castOrUnwrap(idProperty)
                .map(AttributeType::getValueClass);
    }

    /**
     * Create a converter to set values of arbitrary type into the sis:identifier
     * property of a given feature type.
     * Note: RFC7946 specifies that identifier must be either numeric or string.
     *
     * @param target The feature type which specifies the sis:identifier, and by
     * extension the output value class for the converter to create.
     * @return A function capable of converting arbitrary objects into required
     * type for sis:identifier property.
     * @throws IllegalArgumentException If the given data type provides a bad value
     * class for identifier property.
     */
    public static Function getIdentifierConverter(final FeatureType target) throws IllegalArgumentException {
        final Class identifierType = GeoJSONUtils.getIdentifierType(target)
                .orElseThrow(() -> new IllegalArgumentException("Cannot determine the value type for identifier property. Should either be a string or a number."));
        final Function converter;
        if (Numbers.isFloat(identifierType)) {
            converter = input -> Double.parseDouble(input.toString());
        } else if (Long.class.isAssignableFrom(identifierType)) {
            converter = input -> Long.parseLong(input.toString());
        } else if (Numbers.isInteger(identifierType)) {
            converter = input -> Integer.parseInt(input.toString());
        } else if (String.class.isAssignableFrom(identifierType)) {
            converter = Object::toString;
        } else {
            throw new IllegalArgumentException("Unsupported type for identifier property. RFC 7946 asks for a string or number data.");
        }

        return input -> {
            if (input == null || identifierType.getClass().isAssignableFrom(input.getClass())) {
                return input;
            }

            return converter.apply(input);
        };
    }
}
