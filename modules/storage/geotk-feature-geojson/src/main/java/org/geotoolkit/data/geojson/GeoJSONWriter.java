package org.geotoolkit.data.geojson;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import org.locationtech.jts.geom.Geometry;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry;
import org.geotoolkit.data.geojson.utils.GeoJSONParser;
import org.geotoolkit.data.geojson.utils.GeoJSONUtils;
import org.geotoolkit.data.geojson.utils.GeometryUtils;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.util.Utilities;
import static org.geotoolkit.data.geojson.utils.GeoJSONMembres.*;
import static org.geotoolkit.data.geojson.utils.GeoJSONTypes.*;
import static org.geotoolkit.data.geojson.binding.GeoJSONGeometry.*;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;

/**
 * @author Quentin Boileau (Geomatys)
 */
class GeoJSONWriter implements Closeable, Flushable {

    private final static String SYS_LF;
    static {
        String lf = null;
        try {
            lf = System.getProperty("line.separator");
        } catch (Throwable t) { } // access exception?
        SYS_LF = (lf == null) ? "\n" : lf;
    }

    private final JsonGenerator writer;
    private final OutputStream outputStream;
    private boolean first = true;
    private boolean prettyPrint = true;

    // state boolean to ensure that we can't call writeStartFeatureCollection
    // if we first called writeSingleFeature
    private boolean isFeatureCollection = false;
    private boolean isSingleFeature = false;
    private boolean isSingleGeometry = false;

    private final NumberFormat numberFormat;

    @Deprecated
    GeoJSONWriter(File file, JsonEncoding encoding, int doubleAccuracy, boolean prettyPrint) throws IOException {
        this(file.toPath(), encoding, doubleAccuracy, prettyPrint);
    }

    GeoJSONWriter(Path file, JsonEncoding encoding, int doubleAccuracy, boolean prettyPrint) throws IOException {
        this(Files.newOutputStream(file, CREATE, WRITE, TRUNCATE_EXISTING), encoding, doubleAccuracy, prettyPrint);
    }

    GeoJSONWriter(OutputStream stream, JsonEncoding encoding,  int doubleAccuracy, boolean prettyPrint) throws IOException {
        this.prettyPrint = prettyPrint;
        this.outputStream = null;
        if (prettyPrint) {
            this.writer = GeoJSONParser.FACTORY.createGenerator(stream, encoding).useDefaultPrettyPrinter();
        } else {
            this.writer = GeoJSONParser.FACTORY.createGenerator(stream, encoding);
        }

        this.writer.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, true);
        numberFormat = NumberFormat.getInstance(Locale.US);
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumFractionDigits(doubleAccuracy);
    }


    void writeStartFeatureCollection(CoordinateReferenceSystem crs, Envelope envelope) throws IOException {

        assert(!isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "Can't write FeatureCollection if we start a single feature or geometry GeoJSON.";
        isFeatureCollection = true;

        writer.writeStartObject();
        writeNewLine();

        writer.writeStringField(TYPE, FEATURE_COLLECTION);
        writeNewLine();

        if (crs != null && !Utilities.equalsApproximately(crs, CommonCRS.defaultGeographic())) {
            if (writeCRS(crs)) {
                writeNewLine();
            } else {
                throw new IOException("Cannot determine a valid URN for "+crs.getName());
            }
        }

        if (envelope != null) {
            //TODO write bbox
            writeNewLine();
        }
    }

    void writeEndFeatureCollection() throws IOException {
        assert(isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "Can't write FeatureCollection end before writeStartFeatureCollection().";

        if (!first) {
            writer.writeEndArray(); //close feature collection array
        }
        writer.writeEndObject(); //close root object
    }

    /**
     * Write GeoJSON with a single feature
     * @param feature
     * @throws IOException
     * @throws IllegalArgumentException
     */
    void writeSingleFeature(Feature feature) throws IOException, IllegalArgumentException {
        assert(!isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "writeSingleFeature can called only once per GeoJSONWriter.";

        isSingleFeature = true;
        writeFeature(feature, true);
    }

    void writeFeature(Feature feature) throws IOException, IllegalArgumentException {
        assert(isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "Can't write a Feature before writeStartFeatureCollection.";
        writeFeature(feature, false);
    }

    /**
     * Write a Feature.
     * @param feature
     * @param single
     * @throws IOException
     * @throws IllegalArgumentException
     */
    private void writeFeature(Feature feature, boolean single) throws IOException, IllegalArgumentException {
        if (!single) {
            if (first) {
                writer.writeArrayFieldStart(FEATURES);
                writeNewLine();
                first = false;
            }
        }

        writer.writeStartObject();
        writer.writeStringField(TYPE, FEATURE);
        /* As defined in GeoJSON spec, identifier is an optional attribute. For
         * more details, see https://tools.ietf.org/html/rfc7946#section-3.2
         */
        try {
            final Object idValue = feature.getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString());
            // TODO : search for a property named id or identifier ?
            if (idValue != null) {
                writeAttribute(ID, idValue, true);
            }
        } catch (PropertyNotFoundException e) {
            GeoJSONParser.LOGGER.log(Level.FINE, "Cannot write ID cause no matching property has been found.", e);
        }

        //write CRS
        if (single) {
            final CoordinateReferenceSystem crs = FeatureExt.getCRS(feature.getType());
            if (crs != null && !Utilities.equalsApproximately(crs, CommonCRS.defaultGeographic())) {
                if (!writeCRS(crs)) {
                    throw new IOException("Cannot determine a valid URN for "+crs.getName());
                }
            }
        }

        //write geometry
        final Optional<Geometry> geom = FeatureExt.getDefaultGeometryValue(feature)
                        .filter(Geometry.class::isInstance)
                        .map(Geometry.class::cast);

        if (geom.isPresent()) {
            writer.writeFieldName(GEOMETRY);
            writeFeatureGeometry(geom.get());
        }

        //write properties
        writeProperties(feature, PROPERTIES, true);
        writer.writeEndObject();

        if (!single && !prettyPrint) writer.writeRaw(SYS_LF);
    }

    private void writeNewLine() throws IOException {
        if (!prettyPrint) writer.writeRaw(SYS_LF);
    }

    /**
     * Write CoordinateReferenceSystem
     * @param crs
     * @throws IOException
     */
    private boolean writeCRS(CoordinateReferenceSystem crs) throws IOException {
        final Optional<String> urn = GeoJSONUtils.toURN(crs);
        if (urn.isPresent()) {
            writer.writeObjectFieldStart(CRS);
            writer.writeStringField(TYPE, CRS_NAME);
            writer.writeObjectFieldStart(PROPERTIES);
            writer.writeStringField(NAME, urn.get());
            writer.writeEndObject();//close properties
            writer.writeEndObject();//close crs
        }

        return urn.isPresent();
    }

    /**
     * Write ComplexAttribute.
     * @param edited
     * @param fieldName
     * @param writeFieldName
     * @throws IOException
     * @throws IllegalArgumentException
     */
    private void writeProperties(Feature edited, String fieldName, boolean writeFieldName)
            throws IOException, IllegalArgumentException {
        if (writeFieldName) {
            writer.writeObjectFieldStart(fieldName);
        } else {
            writer.writeStartObject();
        }

        FeatureType type = edited.getType();
        Collection<? extends PropertyType> descriptors = type.getProperties(true).stream()
                .filter(FeatureExt.IS_NOT_CONVENTION)
                .collect(Collectors.toList());
        for (PropertyType propType : descriptors) {
            if(AttributeConvention.contains(propType.getName())) continue;
            if(AttributeConvention.isGeometryAttribute(propType)) continue;
            final String name = propType.getName().tip().toString();
            final Object value = edited.getPropertyValue(propType.getName().toString());

            if(propType instanceof AttributeType){
                final AttributeType attType = (AttributeType) propType;
                if(attType.getMaximumOccurs()>1){
                    writer.writeArrayFieldStart(name);
                    for(Object v : (Collection)value){
                        writeProperty(name, v, false);
                    }
                    writer.writeEndArray();
                }else{
                    writeProperty(name, value, true);
                }
            }else if(propType instanceof FeatureAssociationRole){
                final FeatureAssociationRole asso = (FeatureAssociationRole) propType;
                if(asso.getMaximumOccurs()>1){
                    writer.writeArrayFieldStart(name);
                    for(Object v : (Collection)value){
                        writeProperty(name, v, false);
                    }
                    writer.writeEndArray();
                }else{
                    writeProperty(name, value, true);
                }

            }else if(propType instanceof Operation){
                writeProperty(name, value, true);
            }
        }

        writer.writeEndObject();
    }

    /**
     * Write a property (Complex or Simple)
     * @param property
     * @param writeFieldName
     * @throws IOException
     */
    private void writeProperty(String name, Object value, boolean writeFieldName) throws IOException, IllegalArgumentException {
        if (value instanceof Feature) {
            writeProperties((Feature) value, name, writeFieldName);
        } else {
            writeAttribute(name, value, writeFieldName);
        }
    }

    /**
     * Write an Attribute and check if attribute value is assignable to binding class.
     * @param property
     * @param writeFieldName
     * @throws IOException
     */
    private void writeAttribute(String name, Object value,  boolean writeFieldName) throws IOException, IllegalArgumentException {
        if (writeFieldName) {
            writer.writeFieldName(name);
        }
        GeoJSONUtils.writeValue(value, writer);
    }

    /**
     * Write a GeometryAttribute
     * @param geom
     * @throws IOException
     */
    void writeSingleGeometry(Attribute geom) throws IOException {
        assert(!isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "writeSingleGeometry can called only once per GeoJSONWriter.";
        isSingleGeometry = true;
        GeoJSONGeometry jsonGeometry = GeometryUtils.toGeoJSONGeometry((Geometry) geom.getValue());
        writeGeoJSONGeometry(jsonGeometry);
    }

    /**
     * Write a JTS Geometry
     * @param geom
     * @throws IOException
     */
    void writeSingleGeometry(Geometry geom) throws IOException {
        assert(!isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "writeSingleGeometry can called only once per GeoJSONWriter.";
        isSingleGeometry = true;
        GeoJSONGeometry jsonGeometry = GeometryUtils.toGeoJSONGeometry(geom);
        writeGeoJSONGeometry(jsonGeometry);
    }

    /**
     * Write a GeometryAttribute
     * @param geom
     * @throws IOException
     */
    private void writeFeatureGeometry(Geometry geom) throws IOException {
        writeGeoJSONGeometry(GeometryUtils.toGeoJSONGeometry(geom));
    }

    /**
     * Write a GeoJSONGeometry
     * @param jsonGeometry
     * @throws IOException
     */
    private void writeGeoJSONGeometry(GeoJSONGeometry jsonGeometry) throws IOException {
        writer.writeStartObject();
        writer.writeStringField(TYPE, jsonGeometry.getType());

        if (jsonGeometry instanceof GeoJSONGeometryCollection) {
            List<GeoJSONGeometry> geometries = ((GeoJSONGeometryCollection) jsonGeometry).getGeometries();
            writer.writeArrayFieldStart(GEOMETRIES); // "geometries" : [
            for (GeoJSONGeometry geometry : geometries) {
                writeGeoJSONGeometry(geometry);
            }
            writer.writeEndArray(); // "]"
        } else {
            writer.writeArrayFieldStart(COORDINATES); // "coordinates" : [
            if (jsonGeometry instanceof GeoJSONPoint) {
                writeArray(((GeoJSONPoint) jsonGeometry).getCoordinates());
            } else if (jsonGeometry instanceof GeoJSONLineString) {
                writeArray(((GeoJSONLineString) jsonGeometry).getCoordinates());
            } else if (jsonGeometry instanceof GeoJSONPolygon) {
                writeArray(((GeoJSONPolygon) jsonGeometry).getCoordinates());
            } else if (jsonGeometry instanceof GeoJSONMultiPoint) {
                writeArray(((GeoJSONMultiPoint) jsonGeometry).getCoordinates());
            } else if (jsonGeometry instanceof GeoJSONMultiLineString) {
                writeArray(((GeoJSONMultiLineString) jsonGeometry).getCoordinates());
            } else if (jsonGeometry instanceof GeoJSONMultiPolygon) {
                writeArray(((GeoJSONMultiPolygon) jsonGeometry).getCoordinates());
            } else {
                throw new IllegalArgumentException("Unsupported geometry type : " + jsonGeometry);
            }
            writer.writeEndArray(); // "]"
        }

        writer.writeEndObject();
    }

    private void writeArray(double[] coordinates) throws IOException {
        for (double coordinate : coordinates) {
            writer.writeNumber(numberFormat.format(coordinate));
        }
    }

    private void writeArray(double[][] coordinates) throws IOException {
        for (double[] coordinate : coordinates) {
            writer.writeStartArray(); // "["
            writeArray(coordinate);
            writer.writeEndArray(); // "]"
        }
    }

    private void writeArray(double[][][] coordinates) throws IOException {
        for (double[][] coordinate : coordinates) {
            writer.writeStartArray(); // "["
            writeArray(coordinate);
            writer.writeEndArray(); // "]"
        }
    }

    private void writeArray(double[][][][] coordinates) throws IOException {
        for (double[][][] coordinate : coordinates) {
            writer.writeStartArray(); // "["
            writeArray(coordinate);
            writer.writeEndArray(); // "]"
        }
    }

    @Override
    public void flush() throws IOException {
        if (writer != null) {
            writer.flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
    }
}
