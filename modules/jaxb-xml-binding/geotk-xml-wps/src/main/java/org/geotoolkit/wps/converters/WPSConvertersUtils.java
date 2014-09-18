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

import com.vividsolutions.jts.geom.Geometry;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.Set;
import javax.xml.bind.JAXBElement;

import net.sf.json.JSONObject;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.type.DefaultFeatureType;
import org.geotoolkit.feature.type.DefaultGeometryType;
import org.geotoolkit.feature.type.DefaultPropertyDescriptor;
import org.geotoolkit.feature.type.DefaultPropertyType;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.mathml.xml.*;
import org.geotoolkit.ows.xml.v110.DomainMetadataType;
import org.geotoolkit.parameter.Parameters;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.FileUtilities;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;
import org.geotoolkit.wps.xml.v100.ReferenceType;
import org.opengis.coverage.Coverage;
import org.geotoolkit.feature.ComplexAttribute;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.feature.type.PropertyType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Identifier;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

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

    /**
     * Fix the CRS problem for a Feature or a FeatureCollection
     *
     * @param dataValue a Feature or a FeatureCollection
     * @return the sale Feature/FeatureCollection fixed
     * @throws UnconvertibleObjectException
     */
    public static Object fixFeature(final Object dataValue) throws FactoryException {

        if (dataValue instanceof Feature) {

            final Feature featureIN = (Feature) dataValue;
            DefaultFeatureType ft = (DefaultFeatureType) featureIN.getType();
            fixFeatureType(featureIN, ft);

            return featureIN;
        }

        if (dataValue instanceof FeatureCollection) {
            final FeatureCollection featureColl = (FeatureCollection) dataValue;

            DefaultFeatureType ft = (DefaultFeatureType) featureColl.getFeatureType();
            final FeatureIterator featureIter = featureColl.iterator();
            if (featureIter.hasNext()) {
                final Feature feature = featureIter.next();
                fixFeatureType(feature, ft);
            }
            featureIter.close();
            return featureColl;
        }

        throw new IllegalArgumentException("Invalid Feature");
    }

    /**
     * Fix a FeatureType in spread the geometry CRS from a feature to the geometry descriptor CRS
     *
     * @param featureIN feature with geometry used to fix the geometry descriptor
     * @param type the featureType to fix
     * @throws UnconvertibleObjectException
     */
    private static void fixFeatureType(final Feature featureIN, DefaultFeatureType type) throws FactoryException {

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.copy(type);

        //Fetch each geometry, get his CRS and
        for (Property property : featureIN.getProperties()) {
            if (property.getDescriptor() instanceof GeometryDescriptor) {
                final String propertyName = property.getName().getLocalPart();
                final Geometry propertyGeom = (Geometry) property.getValue();
                final CoordinateReferenceSystem extractCRS = JTS.findCoordinateReferenceSystem(propertyGeom);

                final Iterator<PropertyDescriptor> ite = type.getDescriptors().iterator();

                while (ite.hasNext()) {
                    final DefaultPropertyDescriptor propertyDesc = (DefaultPropertyDescriptor) ite.next();

                    if (propertyDesc.getName().getLocalPart().equals(propertyName)) {
                        final DefaultGeometryType geomType = (DefaultGeometryType) propertyDesc.getType();
                        geomType.setCoordinateReferenceSystem(extractCRS);
                        break;
                    }
                }
            }
        }
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
     * Get an convert data from a reference for an expected binding
     *
     * @param expectedClass
     * @param complex
     * @return
     * @throws UnconvertibleObjectException
     */
    public static Object convertFromComplex(final ComplexDataType complex, final Class expectedClass) throws UnconvertibleObjectException {

        final String mime = complex.getMimeType();
        final String encoding = complex.getEncoding();
        final String schema = complex.getSchema();

        WPSIO.checkSupportedFormat(expectedClass, WPSIO.IOType.INPUT, mime, encoding, schema);

        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(WPSObjectConverter.ENCODING, encoding);
        parameters.put(WPSObjectConverter.MIME, mime);
        parameters.put(WPSObjectConverter.SCHEMA, schema);

        final List<Object> content = complex.getContent();

        //remove white spaces
        if (content != null) {
            final Iterator<Object> ite = content.iterator();
            while (ite.hasNext()) {
                final Object obj = ite.next();
                if (obj == null || (obj instanceof String && ((String) obj).trim().isEmpty())) {
                    ite.remove();
                }
            }
        }

        final WPSObjectConverter converter = WPSIO.getConverter(expectedClass, WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX);

        if (converter == null) {
            throw new UnconvertibleObjectException("Input complex not supported, no converter found.");
        }

        return converter.convert(complex, parameters);
    }

    /**
     * Get an convert an object int a {@link ComplexDataType complex}.
     *
     * @param object
     * @param mime
     * @param encoding
     * @param schema
     * @param params
     * @return
     * @throws UnconvertibleObjectException
     */
    public static ComplexDataType convertToComplex(final Object object, final String mime, final String encoding, final String schema,
            final Map<String, Object> params) throws UnconvertibleObjectException {

        ArgumentChecks.ensureNonNull("Object", object);

        WPSIO.checkSupportedFormat(object.getClass(), WPSIO.IOType.INPUT, mime, encoding, schema);

        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(WPSObjectConverter.TMP_DIR_PATH, params.get(OUT_STORAGE_DIR));
        parameters.put(WPSObjectConverter.TMP_DIR_URL, params.get(OUT_STORAGE_URL));
        parameters.put(WPSObjectConverter.ENCODING, encoding);
        parameters.put(WPSObjectConverter.MIME, mime);
        parameters.put(WPSObjectConverter.SCHEMA, schema);


        final WPSObjectConverter converter = WPSIO.getConverter(object.getClass(), WPSIO.IOType.OUTPUT, WPSIO.FormChoice.COMPLEX);
        if (converter == null) {
            throw new UnconvertibleObjectException("Output complex not supported, no converter found.");
        }

        return (ComplexDataType) converter.convert(object, parameters);
    }


    public static ComplexDataType convertToWMSComplex(Object object, String mimeType, String encoding, String schema, Map<String, Object> params)
            throws UnconvertibleObjectException {

        ArgumentChecks.ensureNonNull("Object", object);

        WPSIO.checkSupportedFormat(object.getClass(), WPSIO.IOType.INPUT, mimeType, encoding, schema);

        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(WPSObjectConverter.TMP_DIR_PATH, params.get(OUT_STORAGE_DIR));
        parameters.put(WPSObjectConverter.TMP_DIR_URL, params.get(OUT_STORAGE_URL));
        parameters.put(WPSObjectConverter.ENCODING, encoding);
        parameters.put(WPSObjectConverter.MIME, mimeType);
        parameters.put(WPSObjectConverter.SCHEMA, schema);

        final ComplexDataType complex = new ComplexDataType();
        complex.setEncoding(encoding);
        complex.setMimeType(mimeType);
        complex.setSchema(schema);

        final Map<String,Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("url", (String)params.get(WMS_INSTANCE_URL));
        jsonMap.put("type", "WMS");
        jsonMap.put("version", "1.3.0");

        final String layerName = params.get(WMS_LAYER_NAME)+"_"+System.currentTimeMillis();
        jsonMap.put("title", layerName);
        jsonMap.put("layers", layerName);

        final File coverageFile = new File((String)params.get(WMS_STORAGE_DIR), layerName+".tiff");

        try {
            // Set the envelope crs to 4326 because of client request :
            final CoordinateReferenceSystem outCRS = org.geotoolkit.referencing.CRS.decode("EPSG:4326");
            Envelope env =null;
            Integer crsCode = null;
            if (object instanceof GridCoverage2D) {
                final GridCoverage2D coverage = (GridCoverage2D) object;
                CoverageIO.write(coverage, "GEOTIFF", coverageFile);
                env = Envelopes.transform(coverage.getEnvelope2D(), outCRS);
                crsCode = org.geotoolkit.referencing.IdentifiedObjects.lookupEpsgCode(
                        coverage.getCoordinateReferenceSystem(), false);

            } else if (object instanceof File) {
                final GridCoverageReader reader = CoverageIO.createSimpleReader(object);
                env = Envelopes.transform(reader.getGridGeometry(0).getEnvelope(), outCRS);
                crsCode = org.geotoolkit.referencing.IdentifiedObjects.lookupEpsgCode(
                        reader.getGridGeometry(0).getCoordinateReferenceSystem(), false);
                FileUtilities.copy((File) object, coverageFile);

            } else if (object instanceof GridCoverageReader) {
                final GridCoverageReader reader = (GridCoverageReader) object;
                env = Envelopes.transform(reader.getGridGeometry(0).getEnvelope(), outCRS);
                crsCode = org.geotoolkit.referencing.IdentifiedObjects.lookupEpsgCode(
                        reader.getGridGeometry(0).getCoordinateReferenceSystem(), false);
                Object in = reader.getInput();
                if(in == null) {
                    throw new IOException("Input coverage is invalid.");
                } else if( in instanceof File) {
                    FileUtilities.copy((File) in, coverageFile);
                } else if(in instanceof InputStream) {
                    FileUtilities.buildFileFromStream((InputStream)in, coverageFile);
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
            final Map<String,String> bboxMap = new HashMap<String, String>();
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
    public static Object convertFromReference(final ReferenceType reference, final Class expectedClass) throws UnconvertibleObjectException {

        final String mime = reference.getMimeType();
        final String encoding = reference.getEncoding();
        final String schema = reference.getSchema();
        WPSIO.checkSupportedFormat(expectedClass, WPSIO.IOType.INPUT, mime, encoding, schema);

        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(WPSObjectConverter.ENCODING, encoding);
        parameters.put(WPSObjectConverter.MIME, mime);
        parameters.put(WPSObjectConverter.SCHEMA, schema);

        final WPSObjectConverter converter = WPSIO.getConverter(expectedClass, WPSIO.IOType.INPUT, WPSIO.FormChoice.REFERENCE);

        if (converter == null) {
            throw new UnconvertibleObjectException("Input reference not supported, no converter found.");
        }

        return converter.convert(reference, parameters);
    }

    /**
     * Get an convert an object int a {@link ReferenceType reference}.
     *
     * @param object
     * @param mime
     * @param encoding
     * @param schema
     * @param params
     * @param iotype the io type requested (INPUT/OUTPUT)
     * @return an {@link InputReferenceType input reference} if ioType is set to INPUT, or an {@link OutputReferenceType output reference} otherwise.
     * @throws UnconvertibleObjectException
     */
    public static ReferenceType convertToReference(final Object object, final String mime, final String encoding, final String schema,
            final Map<String, Object> params, final WPSIO.IOType iotype) throws UnconvertibleObjectException {

        ArgumentChecks.ensureNonNull("Object", object);

        WPSIO.checkSupportedFormat(object.getClass(), WPSIO.IOType.INPUT, mime, encoding, schema);

        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(WPSObjectConverter.TMP_DIR_PATH, params.get(OUT_STORAGE_DIR));
        parameters.put(WPSObjectConverter.TMP_DIR_URL, params.get(OUT_STORAGE_URL));
        parameters.put(WPSObjectConverter.ENCODING, encoding);
        parameters.put(WPSObjectConverter.MIME, mime);
        parameters.put(WPSObjectConverter.SCHEMA, schema);
        parameters.put(WPSObjectConverter.IOTYPE, iotype.toString());

        final WPSObjectConverter converter = WPSIO.getConverter(object.getClass(), WPSIO.IOType.OUTPUT, WPSIO.FormChoice.REFERENCE);
        if (converter == null) {
            throw new UnconvertibleObjectException("Output complex not supported, no converter found.");
        }

        return (ReferenceType) converter.convert(object, parameters);
    }

    /**
     * Create the DomaineMetaData object for a literal
     *
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

    public static String getDataTypeString(final Class clazz) {
        String ref = createDataType(clazz).getReference();;

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
        final List<Double> cells = new ArrayList<Double>();
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
        final ComplexType ct = FeatureTypeUtilities.toPropertyType(toConvert);
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.copy(ct);
        if(ftb.getName().getNamespaceURI() == null) {
            ftb.setName("constellation-sdi/WS/wps", ftb.getName().getLocalPart());
        }
        List<PropertyDescriptor> properties = ftb.getProperties();

        for(int i = 0 ; i < properties.size(); i++) {
            final PropertyDescriptor desc = properties.get(i);
            final PropertyType type = desc.getType();
            Class binded = type.getBinding();
            if(RenderedImage.class.isAssignableFrom(binded) ||
                    Coverage.class.isAssignableFrom(binded) ||
                    File.class.isAssignableFrom(binded)) {
                final DefaultPropertyType newType = new DefaultPropertyType(type.getName(),
                        URL.class,
                        false,
                        type.getRestrictions(),
                        type.getSuper(),
                        type.getDescription());
                final PropertyDescriptor newDesc = new DefaultPropertyDescriptor(newType,
                        desc.getName(),
                        desc.getMinOccurs(),
                        desc.getMaxOccurs(),
                        desc.isNillable());
                properties.remove(i);
                properties.add(i, newDesc);

            }
        }
        return ftb.buildFeatureType();
    }

    /**
     * Convert a feature into a ParameterValueGroup.
     *
     * This function will try to convert objects if their types doesn't match between feature and parameter.
     *
     * @param toConvert The feature to transform.
     * @param toFill The descriptor of the ParameterValueGroup which will be created.
     * @return A ParameterValueGroup which contains data of the feature in parameter.
     */
    public static void featureToParameterGroup(ComplexAttribute toConvert, ParameterValueGroup toFill) throws UnconvertibleObjectException {
        ArgumentChecks.ensureNonNull("feature", toConvert);
        ArgumentChecks.ensureNonNull("ParameterGroup", toFill);

        final WPSConverterRegistry registry = WPSConverterRegistry.getInstance();
        for (final GeneralParameterDescriptor gpd : toFill.getDescriptor().descriptors()) {

            if (gpd instanceof ParameterDescriptor) {
                final Property prop = toConvert.getProperty(gpd.getName().getCode());
                if (prop == null && gpd.getMinimumOccurs() > 0) {
                    throw new UnconvertibleObjectException("A mandatory attribute can't be found");
                }
                final ParameterDescriptor desc = (ParameterDescriptor) gpd;
                if (prop.getValue().getClass().isAssignableFrom(desc.getValueClass()) || desc.getValueClass().isAssignableFrom(prop.getValue().getClass())) {
                    Parameters.getOrCreate(desc, toFill).setValue(prop.getValue());
                } else {
                    if (prop.getValue().getClass().isAssignableFrom(URI.class)) {
                        ReferenceType type = UriToReference((URI) prop.getValue(), WPSIO.IOType.INPUT, null);
                        WPSObjectConverter converter = registry.getConverter(type.getClass(), desc.getValueClass());
                        Parameters.getOrCreate(desc, toFill).setValue(converter.convert(type, null));
                    }
                }
            } else if (gpd instanceof ParameterDescriptorGroup) {
                final Collection<Property> propCollection = toConvert.getProperties(gpd.getName().getCode());
                int filledGroups = 0;
                for (Property complex : propCollection) {
                    if (complex instanceof ComplexAttribute) {
                        ParameterValueGroup childGroup = toFill.addGroup(gpd.getName().getCode());
                        featureToParameterGroup((ComplexAttribute) complex, childGroup);
                        filledGroups++;
                    }
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
     * @param toConvert The source URI.
     * @param type The type of reference (input or output), can be null.
     * @param mimeType Mime type of the data pointed by the URI, can be null.
     * @return A reference equivalent to the input URI.
     */
    public static ReferenceType UriToReference(URI toConvert, WPSIO.IOType type, String mimeType) {
        ReferenceType ref;
        if(WPSIO.IOType.INPUT.equals(type)) {
            ref = new InputReferenceType();
        } else {
            ref = new OutputReferenceType();
        }
        ref.setHref(toConvert.toString());
        ref.setMimeType(mimeType);
        ref.setEncoding("UTF-8");

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
        final Set<ReferenceIdentifier> identifiers = crs.getIdentifiers();
        final ReferenceIdentifier id = !identifiers.isEmpty() ? identifiers.iterator().next() : null;
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
    public static File unzipCoverage(File archive) throws IOException {
        File inputFile = null;
        // User should have sent data as a zip archive. What we need to do is unzip it in a folder and identify the image.
        if (archive == null || !archive.exists()) {
            throw new IOException("No input data have been found.");
        }
        String tmpDirPath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + UUID.randomUUID();
        File tmpDir = new File(tmpDirPath);
        tmpDir.mkdir();

        List<File> inputFiles = null;

        inputFiles = FileUtilities.unzip(archive.toPath(), tmpDir.toPath());

        // Try to find the image file to treat
        // TODO : Change for a better verification. Here we should get a specific parameter with the image file name.
        for (File f : inputFiles) {
            String path = f.getAbsolutePath();
            if (path.endsWith(".tif") || path.endsWith(".TIF") || path.endsWith(".tiff") || path.endsWith(".TIFF")
                    || path.endsWith("jp2") || path.endsWith("JP2")
                    || path.endsWith(".png") || path.endsWith(".PNG")
                    || path.endsWith(".jpg") || path.endsWith(".JPG")) {
                inputFile = f;
                break;
            }
        }
        //If list is empty, the source file is not an archive, so we put it as only file for input.
        if(inputFile == null && (inputFiles == null || inputFiles.isEmpty())) {
            inputFile = archive;
        }
        return inputFile;
    }

}