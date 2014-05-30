package org.geotoolkit.data.geojson;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry;
import org.geotoolkit.data.geojson.utils.GeoJSONParser;
import org.geotoolkit.data.geojson.utils.GeoJSONUtils;
import org.geotoolkit.data.geojson.utils.GeometryUtils;
import org.geotoolkit.feature.IllegalAttributeException;
import org.geotoolkit.feature.*;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static org.geotoolkit.data.geojson.utils.GeoJSONMembres.*;
import static org.geotoolkit.data.geojson.utils.GeoJSONTypes.*;
import static org.geotoolkit.data.geojson.binding.GeoJSONGeometry.*;

/**
 * @author Quentin Boileau (Geomatys)
 */
class GeoJSONWriter implements Closeable, Flushable {

    private static final NumberFormat COORD_FORMAT = NumberFormat.getInstance(Locale.US);

    private final static String SYS_LF;
    static {
        String lf = null;
        try {
            lf = System.getProperty("line.separator");
        } catch (Throwable t) { } // access exception?
        SYS_LF = (lf == null) ? "\n" : lf;
    }

    private final JsonGenerator writer;
    private boolean first = true;
    private boolean prettyPrint = true;

    // state boolean to ensure that we can't call writeStartFeatureCollection
    // if we first called writeSingleFeature
    private boolean isFeatureCollection = false;
    private boolean isSingleFeature = false;
    private boolean isSingleGeometry = false;

    GeoJSONWriter(File file, JsonEncoding encoding, int doubleAccuracy, boolean prettyPrint) throws IOException {
        this.prettyPrint = prettyPrint;
        if (prettyPrint) {
            this.writer = GeoJSONParser.FACTORY.createGenerator(file, encoding).useDefaultPrettyPrinter();
        } else {
            this.writer = GeoJSONParser.FACTORY.createGenerator(file, encoding);
        }

        COORD_FORMAT.setMaximumFractionDigits(doubleAccuracy);
    }

    GeoJSONWriter(OutputStream stream, JsonEncoding encoding,  int doubleAccuracy, boolean prettyPrint) throws IOException {
        this.prettyPrint = prettyPrint;
        if (prettyPrint) {
            this.writer = GeoJSONParser.FACTORY.createGenerator(stream, encoding).useDefaultPrettyPrinter();
        } else {
            this.writer = GeoJSONParser.FACTORY.createGenerator(stream, encoding);
        }

        this.writer.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, true);
        COORD_FORMAT.setMaximumFractionDigits(doubleAccuracy);
    }


    void writeStartFeatureCollection(CoordinateReferenceSystem crs, Envelope envelope) throws IOException {

        assert(!isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "Can't write FeatureCollection if we start a single feature or geometry GeoJSON.";
        isFeatureCollection = true;

        writer.writeStartObject();
        writeNewLine();

        writer.writeStringField(TYPE, FEATURE_COLLECTION);
        writeNewLine();

        writeCRS(crs);
        writeNewLine();

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
     * @throws IllegalAttributeException
     */
    void writeSingleFeature(Feature feature) throws IOException, IllegalAttributeException {
        assert(!isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "writeSingleFeature can called only once per GeoJSONWriter.";

        isSingleFeature = true;
        writeFeature(feature, true);
    }

    void writeFeature(Feature feature) throws IOException, IllegalAttributeException {
        assert(isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "Can't write a Feature before writeStartFeatureCollection.";
        writeFeature(feature, false);
    }

    /**
     * Write a Feature.
     * @param feature
     * @param single
     * @throws IOException
     * @throws IllegalAttributeException
     */
    private void writeFeature(Feature feature, boolean single) throws IOException, IllegalAttributeException {
        if (!single) {
            if (first) {
                writer.writeArrayFieldStart(FEATURES);
                writeNewLine();
                first = false;
            }
        }

        writer.writeStartObject();
        writer.writeStringField(TYPE, FEATURE);
        writer.writeStringField(ID, feature.getIdentifier().getID());

        //write CRS
        if (single) {
            writeCRS(feature.getDefaultGeometryProperty().getType().getCoordinateReferenceSystem());
        }

        //write geometry
        writer.writeFieldName(GEOMETRY);
        writeFeatureGeometry(feature.getDefaultGeometryProperty());

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
    private void writeCRS(CoordinateReferenceSystem crs) throws IOException {
        writer.writeObjectFieldStart(CRS);
        writer.writeStringField(TYPE, CRS_NAME);
        writer.writeObjectFieldStart(PROPERTIES);
        writer.writeStringField(NAME, GeoJSONUtils.toURN(crs));
        writer.writeEndObject();//close properties
        writer.writeEndObject();//close crs
    }

    /**
     * Write ComplexAttribute.
     * @param edited
     * @param fieldName
     * @param writeFieldName
     * @throws IOException
     * @throws IllegalAttributeException
     */
    private void writeProperties(ComplexAttribute edited, String fieldName, boolean writeFieldName)
            throws IOException, IllegalAttributeException {
        if (writeFieldName) {
            writer.writeObjectFieldStart(fieldName);
        } else {
            writer.writeStartObject();
        }

        ComplexType type = edited.getType();
        Collection<PropertyDescriptor> descriptors = type.getDescriptors();
        for (PropertyDescriptor propDesc : descriptors) {
            List<Property> properties = new ArrayList<>(edited.getProperties(propDesc.getName()));

            if (!properties.isEmpty()) {
                if (properties.size() > 1) { //array of properties objects
                    writer.writeArrayFieldStart(propDesc.getName().getLocalPart());
                    for (Property property : properties) {
                        writeProperty(property, false);
                    }
                    writer.writeEndArray();
                } else {
                    writeProperty(properties.get(0), true);
                }
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
    private void writeProperty(Property property, boolean writeFieldName) throws IOException, IllegalAttributeException {
        if (property instanceof ComplexAttribute) {
            writeProperties((ComplexAttribute) property, property.getName().getLocalPart(), writeFieldName);
        } else {
            if (property instanceof GeometryAttribute) {
                //do nothing
            } else {
                writeAttribute((Attribute) property, writeFieldName);
            }
        }
    }

    /**
     * Write an Attribute and check if attribute value is assignable to binding class.
     * @param property
     * @param writeFieldName
     * @throws IOException
     */
    private void writeAttribute(Attribute property, boolean writeFieldName) throws IOException, IllegalAttributeException {

        if (writeFieldName) {
            String fieldName = property.getName().getLocalPart();
            writer.writeFieldName(fieldName);
        }

        Object value = property.getValue();

        if (value == null) {
            writer.writeNull();
            return;
        }

        Class binding = property.getDescriptor().getType().getBinding();

        if (Double.class.isAssignableFrom(binding)) {
            writer.writeNumber((Double)value);
        } else if (Integer.class.isAssignableFrom(binding)) {
            writer.writeNumber((Integer)value);
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
     * Write a GeometryAttribute
     * @param geom
     * @throws IOException
     */
    void writeSingleGeometry(GeometryAttribute geom) throws IOException {
        assert(!isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "writeSingleGeometry can called only once per GeoJSONWriter.";
        isSingleGeometry = true;
        GeoJSONGeometry jsonGeometry = GeometryUtils.toGeoJSONGeometry((Geometry) geom.getValue());
        writeGeoJSONGeometry(jsonGeometry);
    }

    /**
     * Write a GeometryAttribute
     * @param geom
     * @throws IOException
     */
    private void writeFeatureGeometry(GeometryAttribute geom) throws IOException {
        GeoJSONGeometry jsonGeometry = GeometryUtils.toGeoJSONGeometry((Geometry) geom.getValue());
        writeGeoJSONGeometry(jsonGeometry);
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
            writer.writeNumber(COORD_FORMAT.format(coordinate));
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
    }
}
