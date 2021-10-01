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
package org.geotoolkit.storage.geojson;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.Utilities;
import org.geotoolkit.internal.geojson.GeoJSONParser;
import org.geotoolkit.internal.geojson.GeoJSONUtils;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONGeometryCollection;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONLineString;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONMultiLineString;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONMultiPoint;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONMultiPolygon;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONPoint;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONPolygon;
import static org.geotoolkit.storage.geojson.GeoJSONConstants.*;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.geotoolkit.feature.xml.Link;

/**
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
final class GeoJSONWriter implements Closeable, Flushable {

    private static final String SYS_LF;

    static {
        String lf = null;
        try {
            lf = System.getProperty("line.separator");
        } catch (Throwable t) {
            // access exception?
        }
        SYS_LF = (lf == null) ? "\n" : lf;
    }

    private final JsonGenerator writer;
    private final OutputStream outputStream;
    private boolean first = true;
    private boolean prettyPrint = true;
    private final boolean isBinaryFomat;

    // state boolean to ensure that we can't call writeStartFeatureCollection
    // if we first called writeSingleFeature
    private boolean isFeatureCollection;
    private boolean isSingleFeature;
    private boolean isSingleGeometry;

    private final NumberFormat numberFormat;

    GeoJSONWriter(Path file, JsonFactory factory, JsonEncoding encoding, int doubleAccuracy, boolean prettyPrint) throws IOException {
        this(new BufferedOutputStream(Files.newOutputStream(file, CREATE, WRITE, TRUNCATE_EXISTING)), factory, encoding, doubleAccuracy, prettyPrint);
    }

    GeoJSONWriter(OutputStream stream, JsonFactory factory, JsonEncoding encoding, int doubleAccuracy, boolean prettyPrint) throws IOException {
        this.prettyPrint = prettyPrint;
        this.outputStream = null;
        if (prettyPrint) {
            this.writer = factory.createGenerator(stream, encoding).useDefaultPrettyPrinter();
        } else {
            this.writer = factory.createGenerator(stream, encoding);
        }

        this.writer.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, true);
        numberFormat = NumberFormat.getInstance(Locale.US);
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumFractionDigits(doubleAccuracy);

        isBinaryFomat = factory != GeoJSONParser.JSON_FACTORY;
    }

    void writeStartFeatureCollection(CoordinateReferenceSystem crs, Envelope envelope) throws IOException {

        assert (!isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
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
                throw new IOException("Cannot determine a valid URN for " + crs.getName());
            }
        }

        if (envelope != null) {
            //TODO write bbox
            writeNewLine();
        }
    }

    void writeEndFeatureCollection() throws IOException {
        assert (isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "Can't write FeatureCollection end before writeStartFeatureCollection().";

        if (!first) {
            writer.writeEndArray(); //close feature collection array
        }
        writer.writeEndObject(); //close root object
    }

    /**
     * Write GeoJSON with a single feature
     *
     * @param feature
     * @throws IOException
     * @throws IllegalArgumentException
     */
    void writeSingleFeature(Feature feature) throws IOException, IllegalArgumentException {
        assert (!isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "writeSingleFeature can called only once per GeoJSONWriter.";

        isSingleFeature = true;
        writeFeature(feature, true);
    }

    void writeFeature(Feature feature) throws IOException, IllegalArgumentException {
        assert (isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "Can't write a Feature before writeStartFeatureCollection.";
        writeFeature(feature, false);
    }

    /**
     * Write a Feature.
     *
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
            final Object idValue = feature.getPropertyValue(AttributeConvention.IDENTIFIER);
            // TODO : search for a property named id or identifier ?
            if (idValue != null) {
                writeAttribute(ID, idValue, true);
            }
        } catch (PropertyNotFoundException e) {
            GeoJSONParser.LOGGER.log(Level.FINE, "Cannot write ID cause no matching property has been found.", e);
        }

        //write CRS
        if (single) {
            final CoordinateReferenceSystem crs = GeoJSONUtils.getCRS(feature.getType());
            if (crs != null && !Utilities.equalsApproximately(crs, CommonCRS.defaultGeographic())) {
                if (!writeCRS(crs)) {
                    throw new IOException("Cannot determine a valid URN for " + crs.getName());
                }
            }
        }

        //write geometry
        final Optional<Geometry> geom = GeoJSONUtils.getDefaultGeometryValue(feature)
                .filter(Geometry.class::isInstance)
                .map(Geometry.class::cast);

        if (geom.isPresent()) {
            writer.writeFieldName(GEOMETRY);
            writeFeatureGeometry(geom.get());
        }

        //write properties
        writeProperties(feature, PROPERTIES, true);
        writer.writeEndObject();

        if (!single && !prettyPrint && !isBinaryFomat) {
            writer.writeRaw(SYS_LF);
        }
    }

    private void writeNewLine() throws IOException {
        if (!prettyPrint && !isBinaryFomat) {
            writer.writeRaw(SYS_LF);
        }
    }

    /**
     * Write CoordinateReferenceSystem
     *
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
     *
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
                .filter(GeoJSONUtils.IS_NOT_CONVENTION)
                .collect(Collectors.toList());
        for (PropertyType propType : descriptors) {
            if (AttributeConvention.contains(propType.getName())) continue;
            if (AttributeConvention.isGeometryAttribute(propType)) continue;
            final String name = propType.getName().tip().toString();
            final Object value = edited.getPropertyValue(propType.getName().toString());

            if (propType instanceof AttributeType) {
                final AttributeType attType = (AttributeType) propType;
                if (attType.getMaximumOccurs() > 1) {
                    writer.writeArrayFieldStart(name);
                    for (Object v : (Collection) value) {
                        writeProperty(name, v, false);
                    }
                    writer.writeEndArray();
                } else {
                    writeProperty(name, value, true);
                }
            } else if (propType instanceof FeatureAssociationRole) {
                final FeatureAssociationRole asso = (FeatureAssociationRole) propType;
                if (asso.getMaximumOccurs() > 1) {
                    writer.writeArrayFieldStart(name);
                    for (Object v : (Collection) value) {
                        writeProperty(name, v, false);
                    }
                    writer.writeEndArray();
                } else {
                    writeProperty(name, value, true);
                }

            } else if (propType instanceof Operation) {
                writeProperty(name, value, true);
            }
        }

        writer.writeEndObject();
    }

    /**
     * Write a property (Complex or Simple)
     *
     * @param property
     * @param writeFieldName
     * @throws IOException
     */
    private void writeProperty(String name, Object value, boolean writeFieldName) throws IOException, IllegalArgumentException {
        if (value instanceof Feature) {
            writeFeature((Feature) value, true);
        } else {
            writeAttribute(name, value, writeFieldName);
        }
    }

    /**
     * Write an Attribute and check if attribute value is assignable to binding
     * class.
     *
     * @param property
     * @param writeFieldName
     * @throws IOException
     */
    private void writeAttribute(String name, Object value, boolean writeFieldName) throws IOException, IllegalArgumentException {
        if (writeFieldName) {
            writer.writeFieldName(name);
        }
        GeoJSONUtils.writeValue(value, writer);
    }

    /**
     * Write a GeometryAttribute
     *
     * @param geom
     * @throws IOException
     */
    void writeSingleGeometry(Attribute geom) throws IOException {
        assert (!isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "writeSingleGeometry can called only once per GeoJSONWriter.";
        isSingleGeometry = true;
        GeoJSONGeometry jsonGeometry = GeoJSONGeometry.toGeoJSONGeometry((Geometry) geom.getValue());
        writeGeoJSONGeometry(jsonGeometry);
    }

    /**
     * Write a JTS Geometry
     *
     * @param geom
     * @throws IOException
     */
    void writeSingleGeometry(Geometry geom) throws IOException {
        assert (!isFeatureCollection && !isSingleFeature && !isSingleGeometry) :
                "writeSingleGeometry can called only once per GeoJSONWriter.";
        isSingleGeometry = true;
        GeoJSONGeometry jsonGeometry = GeoJSONGeometry.toGeoJSONGeometry(geom);
        writeGeoJSONGeometry(jsonGeometry);
    }

    /**
     * Write a GeometryAttribute
     *
     * @param geom
     * @throws IOException
     */
    private void writeFeatureGeometry(Geometry geom) throws IOException {
        writeGeoJSONGeometry(GeoJSONGeometry.toGeoJSONGeometry(geom));
    }

    /**
     * Write a GeoJSONGeometry
     *
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
        if (isBinaryFomat) {
            for (double coordinate : coordinates) {
                writer.writeNumber(coordinate);
            }
        } else {
            for (double coordinate : coordinates) {
                writer.writeNumber(numberFormat.format(coordinate));
            }
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

    public void writeLinks(List<Link> links) throws IOException {
        writer.writeArrayFieldStart("links");
        for (Link link : links) {
            writer.writeStartObject();
            writer.writeStringField("href", link.getHref());
            if (link.getRel() != null) {
                writer.writeStringField("rel", link.getRel());
            }
            if (link.getType() != null) {
                writer.writeStringField("type", link.getType());
            }
            if (link.getHreflang() != null) {
                writer.writeStringField("hreflang", link.getHreflang());
            }
            if (link.getTitle() != null) {
                writer.writeStringField("title", link.getTitle());
            }
            if (link.getLength() != null) {
                writer.writeNumberField("length", link.getLength());
            }
            writer.writeEndObject();
        }
        writer.writeEndArray();
        writeNewLine();
    }

    public void writeNumber(Integer nbMatched, Integer nbReturned) throws IOException{
        if (nbMatched != null) {
            writer.writeNumberField("numberMatched", nbMatched);
            writeNewLine();
        }
        if (nbReturned != null) {
            writer.writeNumberField("numberReturned", nbReturned);
            writeNewLine();
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
