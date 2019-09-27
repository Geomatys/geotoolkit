/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import net.sf.json.JSONObject;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.GridCoverageReader;
import static org.geotoolkit.data.AbstractFileFeatureStoreFactory.PATH;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.geojson.GeoJSONProvider;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry;
import org.geotoolkit.data.geojson.binding.GeoJSONObject;
import org.geotoolkit.data.geojson.utils.GeoJSONParser;
import org.geotoolkit.data.geojson.utils.GeometryUtils;
import org.geotoolkit.feature.FeatureTypeExt;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import org.geotoolkit.mathml.xml.*;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.nio.ZipUtilities;
import org.geotoolkit.ows.xml.v200.DomainMetadataType;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.NamesExt;
import static org.geotoolkit.wps.converters.WPSObjectConverter.ENCODING;
import static org.geotoolkit.wps.converters.WPSObjectConverter.MIME;
import static org.geotoolkit.wps.converters.WPSObjectConverter.TMP_DIR_PATH;
import static org.geotoolkit.wps.converters.WPSObjectConverter.TMP_DIR_URL;
import org.geotoolkit.wps.io.WPSIO;
import static org.geotoolkit.wps.xml.WPSUtilities.CDATA_END_TAG;
import static org.geotoolkit.wps.xml.WPSUtilities.CDATA_START_TAG;
import org.geotoolkit.wps.xml.v200.ComplexData;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.Format;
import org.geotoolkit.wps.xml.v200.LiteralValue;
import org.geotoolkit.wps.xml.v200.Reference;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Property;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Identifier;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.w3c.dom.Node;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public class WPSConvertersUtils {

    public static final String OUT_STORAGE_DIR      = "OUT_STORAGE_DIR";    //webdav storage directory path
    public static final String OUT_STORAGE_URL      = "OUT_STORAGE_URL";    //webdav url
    public static final String WMS_STORAGE_DIR      = "WMS_STORAGE_DIR";    // provider storage directory path
    public static final String WMS_STORAGE_ID       = "WMS_STORAGE_ID";     // provider name
    public static final String WMS_INSTANCE_NAME    = "WMS_INSTANCE_NAME";  //WMS instance name
    public static final String WMS_INSTANCE_URL     = "WMS_INSTANCE_URL";   //WMS instance url
    public static final String WMS_LAYER_NAME       = "WMS_LAYER_NAME";   //WMS instance url
    public static final String CURRENT_JOB_ID       = "CURRENT_JOB_ID";   //current WPS job ID
    public static final int    FRACTION_DIGITS      = 12;                 // Number of fractions digits to write for floating point numbers

    /**
     * GML VERSION.
     */
    private static final Map<String, String> GML_VERSION = new HashMap<>();

    static {
        GML_VERSION.put("1.0.0", "3.1.1");
        GML_VERSION.put("2.0.0", "3.2.1");
    }

    /**
     * Convert a string to a binding class. If the binding class isn't a primitive like Integer, Double, .. we search
     * into the converter list if found a match.
     *
     * @param data string to convert
     * @param binding wanted class
     * @return converted object
     * @throws UnconvertibleObjectException if there is no match found
     */
    public static <T> Object convertFromString(final String data, final Class binding) throws UnconvertibleObjectException {

        Object convertedData = null; //resulting Object

        WPSObjectConverter<? super String, ? extends T> converter;//converter
        try {
            //try to convert into a primitive type
            converter = new WPSObjectConverterAdapter(ObjectConverters.find(String.class, binding));
        } catch (UnconvertibleObjectException ex) {
            //try to convert with some specified converter
            converter = WPSIO.getConverter(binding, WPSIO.IOType.INPUT, WPSIO.FormChoice.LITERAL);

            if (converter == null) {
                throw new UnconvertibleObjectException("Converter can't be found.");
            }
        }
        convertedData = converter.convert(data, null);
        return convertedData;
    }

    /**
     * Convert an object into a String.
     *
     * @param data
     * @return toString object.
     */
    public static String convertToString(final Object data) {
        String out = null;

        if (data != null) {
            try {
                WPSObjectConverter converter = WPSConverterRegistry.getInstance().getConverter(data.getClass(), String.class);
                out = (String) converter.convert(data, null);
            } catch (UnconvertibleObjectException ex) {
                if (data instanceof CoordinateReferenceSystem) {
                    out = IdentifiedObjects.getIdentifierOrName((CoordinateReferenceSystem) data);
                } else {
                    out = String.valueOf(data);
                }
            }
        }
        return out;
    }

    /**
     * Check that the given parameters map defines an encoding and a mime type.
     *
     * If the either the encoding or the mime type is null then a default one is
     * set based on the default enconding/mime type
     *
     * @param clazz class needing default parameters
     * @param ioType ioType of the class
     * @param params parameters map to check
     */
    private static void ensureParametersDefined(final Class clazz, final WPSIO.IOType ioType, final Map<String, Object> params) {
        ArgumentChecks.ensureNonNull("class", clazz);
        ArgumentChecks.ensureNonNull("ioType", ioType);
        ArgumentChecks.ensureNonNull("params", params);

        if (params.get(MIME) == null)
            params.put(MIME, WPSIO.getDefaultMimeType(clazz, ioType));

        if (params.get(ENCODING) == null)
            params.put(ENCODING, WPSIO.getDefaultEncoding(clazz, ioType));
    }

    /**
     * Get an convert data from a reference for an expected binding
     *
     * @param wpsVersion
     * @param expectedClass
     * @param complex
     * @return
     * @throws UnconvertibleObjectException
     */
    public static Object convertFromComplex(final String wpsVersion, final Data data, final Class expectedClass) throws UnconvertibleObjectException {

        final String mime = data.getMimeType();
        final String encoding = data.getEncoding();
        final String schema = data.getSchema();

        WPSIO.checkSupportedFormat(expectedClass, WPSIO.IOType.INPUT, mime, encoding, schema);

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(WPSObjectConverter.ENCODING, encoding);
        parameters.put(WPSObjectConverter.MIME, mime);
        parameters.put(WPSObjectConverter.SCHEMA, schema);
        parameters.put(WPSObjectConverter.WPSVERSION, wpsVersion);
        parameters.put(WPSObjectConverter.TARGET_CLASS, expectedClass);

        ensureParametersDefined(expectedClass, WPSIO.IOType.INPUT, parameters);

        final List<Object> content = data.getContent();

        //remove white spaces
        removeWhiteSpaceFromList(content);

        final WPSObjectConverter converter = WPSIO.getConverter(expectedClass, WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX);

        if (converter == null) {
            throw new UnconvertibleObjectException("Input complex not supported, no converter found.");
        }

        return converter.convert(data, parameters);
    }

    /**
     * Get an convert an object int a {@link ComplexData complex}.
     *
     * @param wpsVersion
     * @param object
     * @param mime
     * @param encoding
     * @param schema
     * @param params
     * @return
     * @throws UnconvertibleObjectException
     */
    public static Object convertToComplex(final String wpsVersion, final Object object, final String mime, final String encoding, final String schema,
            final Map<String, Object> params) throws UnconvertibleObjectException {

        ArgumentChecks.ensureNonNull("Object", object);

        WPSIO.checkSupportedFormat(object.getClass(), WPSIO.IOType.INPUT, mime, encoding, schema);

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(WPSObjectConverter.TMP_DIR_PATH, params.get(OUT_STORAGE_DIR));
        parameters.put(WPSObjectConverter.TMP_DIR_URL, params.get(OUT_STORAGE_URL));
        parameters.put(WPSObjectConverter.ENCODING, encoding);
        parameters.put(WPSObjectConverter.JOB_ID, params.get(CURRENT_JOB_ID));
        parameters.put(WPSObjectConverter.MIME, mime);
        parameters.put(WPSObjectConverter.SCHEMA, schema);
        parameters.put(WPSObjectConverter.WPSVERSION, wpsVersion);
        parameters.put(WPSObjectConverter.GMLVERSION, GML_VERSION.get(wpsVersion));

        ensureParametersDefined(object.getClass(), WPSIO.IOType.OUTPUT, parameters);

        final WPSObjectConverter converter = WPSIO.getConverter(object.getClass(), WPSIO.IOType.OUTPUT, WPSIO.FormChoice.COMPLEX);
        if (converter == null) {
            throw new UnconvertibleObjectException("Output complex not supported, no converter found.");
        }

        return converter.convert(object, parameters);
    }


    public static ComplexData convertToWMSComplex(String wpsVersion, Object object, String mimeType, String encoding, String schema, Map<String, Object> params)
            throws UnconvertibleObjectException {

        ArgumentChecks.ensureNonNull("Object", object);

        WPSIO.checkSupportedFormat(object.getClass(), WPSIO.IOType.INPUT, mimeType, encoding, schema);

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(WPSObjectConverter.TMP_DIR_PATH, params.get(OUT_STORAGE_DIR));
        parameters.put(WPSObjectConverter.TMP_DIR_URL, params.get(OUT_STORAGE_URL));
        parameters.put(WPSObjectConverter.ENCODING, encoding);
        parameters.put(WPSObjectConverter.MIME, mimeType);
        parameters.put(WPSObjectConverter.SCHEMA, schema);
        parameters.put(WPSObjectConverter.WPSVERSION, wpsVersion);
        parameters.put(WPSObjectConverter.GMLVERSION, GML_VERSION.get(wpsVersion));
        parameters.put(WPSObjectConverter.JOB_ID, params.get(CURRENT_JOB_ID));

        final ComplexData complex = new ComplexData(Collections.singletonList(new Format(encoding, mimeType, schema, null)));

        final Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("url", (String)params.get(WMS_INSTANCE_URL));
        jsonMap.put("type", "WMS");
        jsonMap.put("version", "1.3.0");

        final String layerName = params.get(WMS_LAYER_NAME)+"_"+System.currentTimeMillis();
        jsonMap.put("title", layerName);
        jsonMap.put("layers", layerName);

        final Path coverageFile = Paths.get((String) params.get(WMS_STORAGE_DIR), layerName + ".tiff");

        try {
            // Set the envelope crs to 4326 because of client request :
            final CoordinateReferenceSystem outCRS = CommonCRS.WGS84.geographic();
            Envelope env =null;
            Integer crsCode = null;
            if (object instanceof GridCoverage) {
                final GridCoverage coverage = (GridCoverage) object;
                CoverageIO.write(coverage, "GEOTIFF", coverageFile);
                env = Envelopes.transform(coverage.getGridGeometry().getEnvelope(), outCRS);
                crsCode = IdentifiedObjects.lookupEPSG(coverage.getCoordinateReferenceSystem());

            } else if (object instanceof File || object instanceof Path) {
                final Path objPath = (object instanceof File) ? ((File) object).toPath() : (Path) object;
                final GridCoverageReader reader = CoverageIO.createSimpleReader(objPath);
                env = Envelopes.transform(reader.getGridGeometry().getEnvelope(), outCRS);
                crsCode = IdentifiedObjects.lookupEPSG(reader.getGridGeometry().getCoordinateReferenceSystem());
                IOUtilities.copy(objPath, coverageFile, StandardCopyOption.REPLACE_EXISTING);

            } else if (object instanceof GridCoverageReader) {
                final GridCoverageReader reader = (GridCoverageReader) object;
                env = Envelopes.transform(reader.getGridGeometry().getEnvelope(), outCRS);
                crsCode = IdentifiedObjects.lookupEPSG(reader.getGridGeometry().getCoordinateReferenceSystem());
                Object in = reader.getInput();
                if(in == null) {
                    throw new IOException("Input coverage is invalid.");
                } else if( in instanceof File || in instanceof Path) {
                    final Path inPath = (in instanceof File) ? ((File) in).toPath() : (Path) in;
                    IOUtilities.copy(inPath, coverageFile, StandardCopyOption.REPLACE_EXISTING);
                } else if(in instanceof InputStream) {
                    IOUtilities.writeStream((InputStream) in, coverageFile);
                } else {
                    throw new IOException("Input coverage is invalid.");
                }
            }

            if(crsCode == null) {
                crsCode = 3857;
            }

            final double xMin = env.getLowerCorner().getOrdinate(0);
            final double xMax = env.getUpperCorner().getOrdinate(0);
            final double yMin = env.getLowerCorner().getOrdinate(1);
            final double yMax = env.getUpperCorner().getOrdinate(1);
            final Map<String,String> bboxMap = new HashMap<>();
            bboxMap.put("bounds", xMin+","+yMin+","+xMax+","+yMax);
            bboxMap.put("crs", "EPSG:4326");
            jsonMap.put("bbox", bboxMap);
            jsonMap.put("srs", "EPSG:" + crsCode);

        } catch (TransformException e) {
            throw new UnconvertibleObjectException("The geographic envelope of the layer can't be retrieved", e);
        } catch (Exception e) {
            throw new UnconvertibleObjectException(e.getMessage(), e);
        }

        final String json = JSONObject.fromObject(jsonMap).toString();
        complex.getContent().add(json);
        return complex;

    }

    /**
     * Get an convert data from a reference for an expected binding
     *
     * @param reference
     * @param expectedClass
     * @return an object
     * @throws UnconvertibleObjectException if something went wrong
     */
    public static Object convertFromReference(final Reference reference, final Class expectedClass) throws UnconvertibleObjectException {

        final String mime = reference.getMimeType();
        final String encoding = reference.getEncoding();
        final String schema = reference.getSchema();
        WPSIO.checkSupportedFormat(expectedClass, WPSIO.IOType.INPUT, mime, encoding, schema);

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(WPSObjectConverter.ENCODING, encoding);
        parameters.put(WPSObjectConverter.MIME, mime);
        parameters.put(WPSObjectConverter.SCHEMA, schema);

        ensureParametersDefined(expectedClass, WPSIO.IOType.INPUT, parameters);

        final WPSObjectConverter converter = WPSIO.getConverter(expectedClass, WPSIO.IOType.INPUT, WPSIO.FormChoice.REFERENCE);

        if (converter == null) {
            throw new UnconvertibleObjectException("Input reference not supported, no converter found.");
        }

        return converter.convert(reference, parameters);
    }

    /**
     * Get an convert an object int a {@link Reference reference}.
     *
     * @param version WPS version
     * @param object
     * @param mime
     * @param encoding
     * @param schema
     * @param params
     * @param iotype the io type requested (INPUT/OUTPUT)
     * @return an {@link Reference input/output reference}.
     * @throws UnconvertibleObjectException
     */
    public static Reference convertToReference(final String version, final Object object, final String mime, final String encoding, final String schema,
            final Map<String, Object> params, final WPSIO.IOType iotype) throws UnconvertibleObjectException {

        ArgumentChecks.ensureNonNull("Object", object);

        WPSIO.checkSupportedFormat(object.getClass(), WPSIO.IOType.INPUT, mime, encoding, schema);

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(WPSObjectConverter.TMP_DIR_PATH, params.get(OUT_STORAGE_DIR));
        parameters.put(WPSObjectConverter.TMP_DIR_URL, params.get(OUT_STORAGE_URL));
        parameters.put(WPSObjectConverter.ENCODING, encoding);
        parameters.put(WPSObjectConverter.MIME, mime);
        parameters.put(WPSObjectConverter.SCHEMA, schema);
        parameters.put(WPSObjectConverter.WPSVERSION, version);
        parameters.put(WPSObjectConverter.GMLVERSION, GML_VERSION.get(version));
        parameters.put(WPSObjectConverter.IOTYPE, iotype.toString());
        parameters.put(WPSObjectConverter.JOB_ID, params.get(CURRENT_JOB_ID));

        ensureParametersDefined(object.getClass(), iotype, params);

        final WPSObjectConverter converter = WPSIO.getConverter(object.getClass(), WPSIO.IOType.OUTPUT, WPSIO.FormChoice.REFERENCE);
        if (converter == null) {
            throw new UnconvertibleObjectException("Output complex not supported, no converter found.");
        }

        return (Reference) converter.convert(object, parameters);
    }

    /**
     * Create the DomaineMetaData object for a literal
     *
     * @param version WPS version
     * @param clazz
     * @return
     * @throws UnconvertibleObjectException
     */
    public static DomainMetadataType createDataType(final Class clazz) {
        if (clazz.equals(Double.class)) {
            return new DomainMetadataType("Double", "http://www.w3.org/TR/xmlschema-2/#double");

        } else if (clazz.equals(Float.class)) {
            return new DomainMetadataType("Float", "http://www.w3.org/TR/xmlschema-2/#float");

        } else if (clazz.equals(Boolean.class)) {
            return new DomainMetadataType("Boolean", "http://www.w3.org/TR/xmlschema-2/#boolean");

        } else if (clazz.equals(Integer.class)) {
            return new DomainMetadataType("Integer", "http://www.w3.org/TR/xmlschema-2/#integer");

        } else if (clazz.equals(Long.class)) {
            return new DomainMetadataType("Long", "http://www.w3.org/TR/xmlschema-2/#long");

        } else if (clazz.equals(String.class) || WPSIO.isSupportedInputClass(clazz) || WPSIO.isSupportedOutputClass(clazz)) {
            return new DomainMetadataType("String", "http://www.w3.org/TR/xmlschema-2/#string");

        } else {
           return null;
        }
    }

    public static String getDataTypeString(final String version, final Class clazz) {
        String ref = createDataType(clazz).getReference();

        if (ref == null) {
            ref = "http://www.w3.org/TR/xmlschema-2/#string";
        }
        return ref;
    }

    /**
     * Format an INPUT/OUTPUT format for errors messages.
     * @param mime
     * @param encoding
     * @param schema
     * @return
     */
    public static String dataFormatToString(final String mime, final String encoding, final String schema) {
         final StringBuilder builder = new StringBuilder();
        final String begin = "[";
        final String end = "]";
        final String separator = ", ";

        builder.append(begin);

        builder.append("mimeType=");
        builder.append(mime);
        builder.append(separator);

        builder.append("encoding=");
        builder.append(encoding);
        builder.append(separator);

        builder.append("schema=");
        builder.append(schema);

        builder.append(end);
        return builder.toString();
    }

    /**
     * Extract the fist MathML MTable object.
     * @param mathExp
     * @return
     */
    public static Mtable findMtable(List<Object> mathExp){

        for (Object object : mathExp) {
            if (object instanceof JAXBElement) {
                final JAXBElement element = (JAXBElement) object;
                if (element.getValue() instanceof Mtable) {
                    return (Mtable) element.getValue();
                } else if(element.getValue() instanceof Mrow) {
                    final Mrow mrow = (Mrow) element.getValue();
                    return findMtable(mrow.getMathExpression());
                }
            }
        }
        return null;
    }

    /**
     * Extact rows of an {@link Mtable table}.
     * @param table
     * @return
     */
    public static List<Mtr> getRows(final Mtable table) {
        final List<Mtr> rows = new ArrayList<Mtr>();

        final List<JAXBElement<?>> jaxbRows = table.getTableRowExpression();

        for (JAXBElement<?> jaxbRow : jaxbRows) {
            if (jaxbRow.getValue() instanceof Mtr && jaxbRow.getValue() != null) {
                rows.add((Mtr) jaxbRow.getValue());
            }
        }
        return rows;
    }

    /**
     * Extact double value of cell of a {@link Mtr row}.
     * @param row
     * @return
     */
    public static double[] getCells (final Mtr row) {
        final List<Double> cells = new ArrayList<>();
        final List<JAXBElement<TableCellExpression>> tableCellExpressionList = row.getTableCellExpression();

        for (JAXBElement<TableCellExpression> jAXBElement : tableCellExpressionList) {
            final TableCellExpression tableCellExpression = jAXBElement.getValue();
            final List<Object> objects = tableCellExpression.getMathExpression();

            for (Object object : objects) {
                final JAXBElement element = (JAXBElement) object;
                if (element.getValue() instanceof Mn && element.getValue() != null) {
                    final Mn mn = (Mn) element.getValue();
                    final String value = (String) mn.getContent().get(0);
                    cells.add(Double.valueOf(value));
                }
            }
        }
        final double[] cellsArray = new double[cells.size()];
        for (int i = 0; i < cells.size(); i++) {
            cellsArray[i] = cells.get(i).doubleValue();
        }

        return cellsArray;
    }

    /**
     * A function to transform a {@link ParameterDescriptorGroup} into {@link FeatureType}.
     *
     * This function care about wps constraints, so if our feature contains
     * heavy object type as coverages, we replace them with reference type.
     *
     * @param toConvert The group to convert.
     * @return A complex feature type which is the equivalent of the descriptor input.
     */
    public static FeatureType descriptorGroupToFeatureType(ParameterDescriptorGroup toConvert) {
        final FeatureType ct = FeatureTypeExt.toFeatureType(toConvert);
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder(ct);
        if(NamesExt.getNamespace(ftb.getName()) == null) {
            ftb.setName("constellation-sdi/WS/wps", ftb.getName().tip().toString());
        }

        final List<PropertyTypeBuilder> properties = ftb.properties();

        for(int i = 0 ; i < properties.size(); i++) {
            final PropertyTypeBuilder desc = properties.get(i);
            if(desc instanceof AttributeTypeBuilder){
                final AttributeTypeBuilder atb = (AttributeTypeBuilder) desc;
                Class binded = atb.getValueClass();
                if(RenderedImage.class.isAssignableFrom(binded) ||
                        GridCoverage.class.isAssignableFrom(binded) ||
                        File.class.isAssignableFrom(binded)) {
                    atb.setValueClass(URL.class);
                }
            }
        }
        return ftb.build();
    }

    /**
     * Convert a feature into a ParameterValueGroup.
     *
     * This function will try to convert objects if their types doesn't match between feature and parameter.
     * Then fill a ParameterValueGroup which contains data of the feature in parameter.
     *
     * @param version WPS version
     * @param toConvert The feature to transform.
     * @param toFill The descriptor of the ParameterValueGroup which will be created.
     */
    public static void featureToParameterGroup(String version, Feature toConvert, ParameterValueGroup toFill) throws UnconvertibleObjectException {
        ArgumentChecks.ensureNonNull("feature", toConvert);
        ArgumentChecks.ensureNonNull("ParameterGroup", toFill);

        final WPSConverterRegistry registry = WPSConverterRegistry.getInstance();
        final Parameters toFill2 = Parameters.castOrWrap(toFill);
        for (final GeneralParameterDescriptor gpd : toFill.getDescriptor().descriptors()) {

            if (gpd instanceof ParameterDescriptor) {
                final Property prop = toConvert.getProperty(gpd.getName().getCode());
                if (prop == null && gpd.getMinimumOccurs() > 0) {
                    throw new UnconvertibleObjectException("A mandatory attribute can't be found");
                }
                final ParameterDescriptor desc = (ParameterDescriptor) gpd;
                if (prop.getValue().getClass().isAssignableFrom(desc.getValueClass()) || desc.getValueClass().isAssignableFrom(prop.getValue().getClass())) {
                    toFill2.getOrCreate(desc).setValue(prop.getValue());
                } else {
                    if (prop.getValue().getClass().isAssignableFrom(URI.class)) {
                        Reference type = UriToReference(version, (URI) prop.getValue(), WPSIO.IOType.INPUT, null);
                        WPSObjectConverter converter = registry.getConverter(type.getClass(), desc.getValueClass());
                        toFill2.getOrCreate(desc).setValue(converter.convert(type, null));
                    }
                }
            } else if (gpd instanceof ParameterDescriptorGroup) {
                final Collection<Feature> propCollection = (Collection<Feature>) toConvert.getPropertyValue(gpd.getName().getCode());
                int filledGroups = 0;
                for (Feature complex : propCollection) {
                    ParameterValueGroup childGroup = toFill.addGroup(gpd.getName().getCode());
                    featureToParameterGroup(version, complex, childGroup);
                    filledGroups++;
                }
                if(filledGroups < gpd.getMinimumOccurs()) {
                    throw new UnconvertibleObjectException("Not enough attributes have been found.");
                }
            } else {
                throw new UnconvertibleObjectException("Parameter type is not managed.");
            }
        }
    }


    /**
     * Convert an URI into a wps reference.
     *
     * @param version WPS version
     * @param toConvert The source URI.
     * @param type The type of reference (input or output), can be null.
     * @param mimeType Mime type of the data pointed by the URI, can be null.
     *
     * @return A reference equivalent to the input URI.
     */
    public static Reference UriToReference(String version, URI toConvert, WPSIO.IOType type, String mimeType) {
        Reference ref = new Reference("UTF-8", mimeType, toConvert.toString());
        if (WPSIO.IOType.INPUT.equals(type)) {
            ref.setIsParentInput(true);
        }
        return ref;
    }


    /**
     * Check if a CRS has longitude first or not.
     * @param crs
     * @return
     */
    public static boolean isLongFirst(CoordinateReferenceSystem crs) {

        final CoordinateReferenceSystem hcrs = CRS.getHorizontalComponent(crs);
        final CoordinateSystem cs = hcrs.getCoordinateSystem();
        final CoordinateSystemAxis csa = cs.getAxis(0);
        if (csa.getDirection().equals(AxisDirection.NORTH) || csa.getDirection().equals(AxisDirection.SOUTH)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Extract CRS code like "EPSG:4326".
     * @param crs
     * @return
     */
    public static String encodeCRS(CoordinateReferenceSystem crs) {
        final Set<Identifier> identifiers = crs.getIdentifiers();
        final Identifier id = !identifiers.isEmpty() ? identifiers.iterator().next() : null;
        if (id != null) {
            final Collection<Identifier> authIdentifier = (Collection<Identifier>) id.getAuthority().getIdentifiers();
            final Identifier authId = !authIdentifier.isEmpty() ? authIdentifier.iterator().next() : null;
            return authId.getCode() + ":" + id.getCode();
        }

        return null;
    }

    /**
     * Unzip an archive which must contain a coverage (1 image + 0-->n metadata files). The decompression is made in a
     * temporary directory.
     * @param archive The archive to unzip.
     * @return The image file of the archive.
     * @throws IOException If there's a problem while decompression, or if we can't create a new temporary folder.
     */
    public static Path unzipCoverage(Path archive) throws IOException {
        Path inputFile = null;
        // User should have sent data as a zip archive. What we need to do is unzip it in a folder and identify the image.
        if (archive == null || !Files.exists(archive)) {
            throw new IOException("No input data have been found.");
        }
        final Path tmpDir = Files.createTempDirectory(null);

        List<Path> inputFiles = ZipUtilities.unzip(archive, tmpDir, null);

        // Try to find the image file to treat
        // TODO : Change for a better verification. Here we should get a specific parameter with the image file name.
        for (Path f : inputFiles) {
            String path = f.toAbsolutePath().toString();
            if (path.endsWith(".tif") || path.endsWith(".TIF") || path.endsWith(".tiff") || path.endsWith(".TIFF")
                    || path.endsWith("jp2") || path.endsWith("JP2")
                    || path.endsWith(".png") || path.endsWith(".PNG")
                    || path.endsWith(".jpg") || path.endsWith(".JPG")) {
                inputFile = f;
                break;
            }
        }
        //If list is empty, the source file is not an archive, so we put it as only file for input.
        if(inputFile == null && inputFiles.isEmpty()) {
            inputFile = archive;
        }
        return inputFile;
    }

    /**
     * Ensure that an URL points to a file on the filesystem.
     *
     * If it is a remote file, it will be copied and then an URL to this local
     * file will be returned
     *
     * @param uri uri of a remote or a file on the filesystem
     * @return an uri of file on the filesystem
     */
    private static final URI makeLocalURL(final URI uri) throws URISyntaxException, IOException {
        ArgumentChecks.ensureNonNull("uri", uri);

        // This condition detects that the file is not on the filesystem
        // based on the java.io.File(URI uri) constructor
        String scheme = uri.getScheme();
        if ((scheme == null) || !scheme.equalsIgnoreCase("file")) {

            final String toStringUrl = uri.toString();
            final String extension = toStringUrl.substring(toStringUrl.lastIndexOf("."), toStringUrl.length());

            // Create a temporary file
            final Path tmpFilePath = Files.createTempFile(UUID.randomUUID().toString(), extension);
            // Copy the content of the remote file into the file on the local filesystem
            try (InputStream remoteStream = IOUtilities.open(uri)) {
                IOUtilities.writeStream(remoteStream, tmpFilePath);
            }


            return tmpFilePath.toUri();
        }
        return uri;
    }

    /**
     * Read one feature from a GeoJSON file containing exactly one feature
     * @param uri location of the file to read
     * @return the read feature
     * @throws DataStoreException when there are more than one feature in the file
     * or when errors occurs while reading
     */
    public static final Feature readFeatureFromJson(final URI uri) throws DataStoreException, URISyntaxException, IOException {
        ParameterValueGroup param = GeoJSONProvider.PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(makeLocalURL(uri));
        DataStore store = DataStores.open(param);

        if (store == null)
            throw new DataStoreException("No available factory found");

        final List<FeatureSet> featureSets = new ArrayList<>(DataStores.flatten(store, true, FeatureSet.class));

        if (featureSets.size() != 1)
            throw new UnconvertibleObjectException("Expected one Geometry. Found " + featureSets.size());

        final FeatureSet featureSet = featureSets.get(0);

        long collectionSize = FeatureStoreUtilities.getCount(featureSet);
        if (collectionSize != 1)
            throw new UnconvertibleObjectException("Expected one geometry. Found " + collectionSize);

        try (Stream<Feature> st = featureSet.features(false)) {
            return st.findFirst().get();
        }
    }

    /**
     * TODO : completely change the content. The algorithm is not adapted to
     * WPS references. But to do so, we'll need a proper GeoJSON streaming reader.
     *
     * Read one feature collection from a GeoJSON file containing one feature collection
     * @param url location of the file to read
     * @return the read feature collection
     * @throws DataStoreException when no feature collection has been found,
     * when more than one feature collection has been found or when an error
     * occurs while reading the json file
     */
    public static final FeatureSet readFeatureCollectionFromJson(final URI url) throws DataStoreException, URISyntaxException, IOException {
        final ParameterValueGroup param = GeoJSONProvider.PARAMETERS_DESCRIPTOR.createValue();
        param.parameter(PATH.getName().getCode()).setValue(makeLocalURL(url));
        final DataStore store = DataStores.open(param);

        if (store == null)
            throw new DataStoreException("No available factory found");


        List<FeatureSet> featureSets = new ArrayList<>(DataStores.flatten(store, true, FeatureSet.class));

        if (featureSets.size() != 1)
            throw new UnconvertibleObjectException("Expected one Geometry. Found " + featureSets.size());

        final FeatureSet featureSet = featureSets.get(0);

        return featureSet;
    }

    /**
     * Helper method which extracts the GeoJSONObject from a String.
     *
     * @param content content string containing a GeoJSONObject
     * @return a GeoJSONObject
     * @throws java.io.IOException on reading errors
     */
    public static GeoJSONObject readGeoJSONObjectsFromString(String content) throws IOException {
        ArgumentChecks.ensureNonEmpty("content", content);

        // Parse GeoJSON
        try (InputStream inputStream = new ByteArrayInputStream(content.getBytes())) {
            return GeoJSONParser.parse(inputStream);
        }
    }

    /**
     * Convert a GeoJSONGeometry to a Geometry object and take count of the CRS.
     *
     * If no crs can be found in the json a default crs will be applied.
     * The default crs is WGS84
     *
     * @param jsonGeometry the json geometry to convert
     * @return a geometry converted from the provided json
     *
     * @throws FactoryException when an error occur while decoding a CRS
     */
    public static Geometry convertGeoJSONGeometryToGeometry(GeoJSONGeometry jsonGeometry) throws FactoryException, MalformedURLException {
        // Check that the json defines a crs, otherwise set a
        // default one as indicated by the GeoJSON specification
        CoordinateReferenceSystem crs = null;
        if (jsonGeometry.getCrs() != null)
            crs = jsonGeometry.getCrs().getCRS();
        else
            crs = CommonCRS.WGS84.normalizedGeographic();

        return GeometryUtils.toJTS(jsonGeometry, crs);
    }

    /**
     * Helper method that encapsulates a String into an XML CDATA section before
     * adding it to the content of a ComplexData instance.
     *
     * @param content content to put in a CDATA section
     * @param complex complex in which to add the CDATA section
     */
    public static void addCDATAToComplex(String content, final Data complex) {
        if (content != null && !content.startsWith(CDATA_START_TAG)) {
            content = CDATA_START_TAG+content+CDATA_END_TAG;
        }
        complex.getContent().add(content);
    }

    /**
     * Helper method that clean a list from all Strings containing only whitespace
     *
     * @param contentList the list to clean
     *
     */
    public static void removeWhiteSpaceFromList(final List<Object> contentList) {
        if (contentList != null) {
            final Iterator<Object> ite = contentList.iterator();
            while (ite.hasNext()) {
                final Object obj = ite.next();
                if (obj == null || (obj instanceof String && ((String) obj).trim().isEmpty())) {
                    ite.remove();
                }
            }
        }
    }

    /**
     * Extract the GeoJSON content of a complex and return it as a String.
     *
     * Pre-condition : the complex must have exactly one content element
     * Pre-condition : the content must be either of the type GeoJSONType, String
     * or Node
     *
     * @param objContent the complex to read
     * @return the complex content as a String
     */
    public static String geojsonContentAsString(final Object objContent) {
        ArgumentChecks.ensureNonNull("Data", objContent);
        Object content = objContent;
        if (content instanceof Data) {
            List<Object> contents = ((Data)content).getContent();
            if (contents == null || contents.isEmpty()) {
                content = "";
            } else if (contents.size() > 1) {
                throw new UnconvertibleObjectException("We search for a single text content, but given data contains "+contents.size());
            } else {
                content = contents.get(0);
            }
        }

        // Data can contain a literal value, so we test it after
        if (content instanceof LiteralValue) {
            content = ((LiteralValue)content).getValue();
        } else if (content instanceof Node) {
            // Otherwise, data could contain a Dom node (rarely), so we also test it
            content = ((Node) content).getTextContent();
        }

        if (content instanceof String)
            return (String) content; // TODO: remove CDATA ?

        throw new UnconvertibleObjectException(
                "Cannot extract text content from source " + objContent.getClass().getName()
        );
    }

    /**
     * Write a temporary file with the .json extension
     * @param fileContent the content to write in the file
     * @return a Path to the temporary file created
     * @throws IOException when errors occur while writing the file
     */
    public static Path writeTempJsonFile(String fileContent) throws IOException {
        final Path tmpFilePath = Files.createTempFile(UUID.randomUUID().toString(), ".json");
        IOUtilities.writeString(fileContent, tmpFilePath);
        return tmpFilePath;
    }

    /**
     * Encode given feature type as a gml schema.
     *
     * @param schema The feature type to create a schema for.
     * @param convertParams Parameters giving output directory, and URL for
     * accessing it. They must be named according to {@link #TMP_DIR_PATH} and {@link #TMP_DIR_URL}.
     *
     * @return The public URL to use to access written gml schema.
     * @throws IOException If we cannot create nor write into given path.
     * @throws JAXBException If we cannot transform given type to gml.
     */
    public static String writeSchema(final FeatureType schema, final Map convertParams) throws IOException, JAXBException {
        Object tmpDir = convertParams.get(TMP_DIR_PATH);
        Object tmpUrl = convertParams.get(TMP_DIR_URL);
        if (!(tmpDir instanceof String)) {
            throw new UnconvertibleObjectException("Missing parameter : output directory");
        } else if (!(tmpUrl instanceof String)) {
            throw new UnconvertibleObjectException("Missing parameter : output URL");
        }

        final String schemaFileName = "schema_" + UUID.randomUUID().toString() + ".xsd";
        final Path output = Paths.get((String) tmpDir, schemaFileName);
        try (final OutputStream stream = Files.newOutputStream(output, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)) {
            //write featureType xsd on file
            final JAXBFeatureTypeWriter xmlFTWriter = new JAXBFeatureTypeWriter();
            xmlFTWriter.write(schema, stream);
        }
        return tmpUrl + "/" + schemaFileName;
    }
}
