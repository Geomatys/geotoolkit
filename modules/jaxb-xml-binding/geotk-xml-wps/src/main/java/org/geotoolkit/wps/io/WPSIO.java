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
package org.geotoolkit.wps.io;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import javax.measure.unit.Unit;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.ows.xml.v110.BoundingBoxType;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.ows.xml.v110.DomainMetadataType;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.converters.WPSObjectConverterAdapter;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.geotoolkit.wps.xml.v100.DataType;
import org.geotoolkit.wps.xml.v100.ReferenceType;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.geometry.Envelope;

/**
 * 
 * @author Quentin Boileau (Geomatys).
 */
public final class WPSIO {

    private static final List<FormatSupport> FORMATSUPPORTS = Collections.synchronizedList(new ArrayList<FormatSupport>());

    static {
        FORMATSUPPORTS.add(new FormatSupport(Feature.class, IOType.BOTH, WPSMimeType.TEXT_XML.val(), Encoding.UTF8.getValue(), Schema.OGC_FEATURE_3_1_1.getValue(), false));
        FORMATSUPPORTS.add(new FormatSupport(Feature.class, IOType.BOTH, WPSMimeType.TEXT_GML.val(), Encoding.UTF8.getValue(), Schema.OGC_FEATURE_3_1_1.getValue(), false));
        FORMATSUPPORTS.add(new FormatSupport(Feature.class, IOType.BOTH, WPSMimeType.APP_GML.val(), Encoding.UTF8.getValue(), Schema.OGC_FEATURE_3_1_1.getValue(), true));

        FORMATSUPPORTS.add(new FormatSupport(Feature[].class, IOType.INPUT, WPSMimeType.TEXT_XML.val(), Encoding.UTF8.getValue(), Schema.OGC_FEATURE_3_1_1.getValue(), false));
        FORMATSUPPORTS.add(new FormatSupport(Feature[].class, IOType.INPUT, WPSMimeType.TEXT_GML.val(), Encoding.UTF8.getValue(), Schema.OGC_FEATURE_3_1_1.getValue(), false));
        FORMATSUPPORTS.add(new FormatSupport(Feature[].class, IOType.INPUT, WPSMimeType.APP_GML.val(), Encoding.UTF8.getValue(), Schema.OGC_FEATURE_3_1_1.getValue(), true));

        FORMATSUPPORTS.add(new FormatSupport(FeatureCollection.class, IOType.BOTH, WPSMimeType.TEXT_XML.val(), Encoding.UTF8.getValue(), Schema.OGC_FEATURE_3_1_1.getValue(), false));
        FORMATSUPPORTS.add(new FormatSupport(FeatureCollection.class, IOType.BOTH, WPSMimeType.TEXT_GML.val(), Encoding.UTF8.getValue(), Schema.OGC_FEATURE_3_1_1.getValue(), false));
        FORMATSUPPORTS.add(new FormatSupport(FeatureCollection.class, IOType.BOTH, WPSMimeType.APP_GML.val(), Encoding.UTF8.getValue(), Schema.OGC_FEATURE_3_1_1.getValue(), true));

        FORMATSUPPORTS.add(new FormatSupport(FeatureCollection[].class, IOType.INPUT, WPSMimeType.TEXT_XML.val(), Encoding.UTF8.getValue(), Schema.OGC_FEATURE_3_1_1.getValue(), false));
        FORMATSUPPORTS.add(new FormatSupport(FeatureCollection[].class, IOType.INPUT, WPSMimeType.TEXT_GML.val(), Encoding.UTF8.getValue(), Schema.OGC_FEATURE_3_1_1.getValue(), false));
        FORMATSUPPORTS.add(new FormatSupport(FeatureCollection[].class, IOType.INPUT, WPSMimeType.APP_GML.val(), Encoding.UTF8.getValue(), Schema.OGC_FEATURE_3_1_1.getValue(), true));

        FORMATSUPPORTS.add(new FormatSupport(Geometry.class, IOType.BOTH, WPSMimeType.TEXT_XML.val(), Encoding.UTF8.getValue(), Schema.ORC_GML_3_1_1.getValue(), false));
        FORMATSUPPORTS.add(new FormatSupport(Geometry.class, IOType.BOTH, WPSMimeType.TEXT_GML.val(), Encoding.UTF8.getValue(), Schema.ORC_GML_3_1_1.getValue(), false));
        FORMATSUPPORTS.add(new FormatSupport(Geometry.class, IOType.BOTH, WPSMimeType.APP_GML.val(), Encoding.UTF8.getValue(), Schema.ORC_GML_3_1_1.getValue(), true));

        FORMATSUPPORTS.add(new FormatSupport(Geometry[].class, IOType.BOTH, WPSMimeType.TEXT_XML.val(), Encoding.UTF8.getValue(), Schema.ORC_GML_3_1_1.getValue(), false));
        FORMATSUPPORTS.add(new FormatSupport(Geometry[].class, IOType.BOTH, WPSMimeType.TEXT_GML.val(), Encoding.UTF8.getValue(), Schema.ORC_GML_3_1_1.getValue(), false));
        FORMATSUPPORTS.add(new FormatSupport(Geometry[].class, IOType.BOTH, WPSMimeType.APP_GML.val(), Encoding.UTF8.getValue(), Schema.ORC_GML_3_1_1.getValue(), true));

        FORMATSUPPORTS.add(new FormatSupport(FeatureType.class, IOType.BOTH, WPSMimeType.TEXT_XML.val(), Encoding.UTF8.getValue(), Schema.OGC_FEATURE_3_1_1.getValue(), true));

        for (final String readerMime : ImageIO.getReaderMIMETypes()) {
            if (!readerMime.isEmpty()) {
                if (readerMime.equals("image/png")) {
                    FORMATSUPPORTS.add(new FormatSupport(RenderedImage.class, IOType.BOTH, readerMime, null, null, true));
                } else {
                    FORMATSUPPORTS.add(new FormatSupport(RenderedImage.class, IOType.BOTH, readerMime, null, null, false));
                }
            }
        }

        for (final String writerMime : ImageIO.getWriterMIMETypes()) {
            if (!writerMime.isEmpty()) {
                if (writerMime.equals("image/png")) {
                    FORMATSUPPORTS.add(new FormatSupport(RenderedImage.class, IOType.BOTH, writerMime, null, null, true));
                } else {
                    FORMATSUPPORTS.add(new FormatSupport(RenderedImage.class, IOType.BOTH, writerMime, null, null, false));
                }
            }
        }
        
        /*  Coverage
        for (final String readerMime : ImageIO.getReaderMIMETypes()) {
            if (!readerMime.isEmpty()) {
                if (readerMime.equals("image/png")) {
                    FORMATSUPPORTS.add(new FormatSupport(Coverage.class, IOType.BOTH, readerMime, null, null, true));
                } else {
                    FORMATSUPPORTS.add(new FormatSupport(Coverage.class, IOType.BOTH, readerMime, null, null, false));
                }
            }
        }

        for (final String writerMime : ImageIO.getWriterMIMETypes()) {
            if (!writerMime.isEmpty()) {
                if (writerMime.equals("image/png")) {
                    FORMATSUPPORTS.add(new FormatSupport(Coverage.class, IOType.BOTH, writerMime, null, null, true));
                } else {
                    FORMATSUPPORTS.add(new FormatSupport(Coverage.class, IOType.BOTH, writerMime, null, null, false));
                }
            }
        }
         */

        FORMATSUPPORTS.add(new FormatSupport(AffineTransform.class, IOType.INPUT, WPSMimeType.TEXT_XML.val(), Encoding.UTF8.getValue(), Schema.MATHML_3.getValue(), true));

        FORMATSUPPORTS.add(new FormatSupport(Number.class, IOType.BOTH, null, Encoding.UTF8.getValue(), null, true));
        FORMATSUPPORTS.add(new FormatSupport(Boolean.class, IOType.BOTH, null, Encoding.UTF8.getValue(), null, true));
        FORMATSUPPORTS.add(new FormatSupport(String.class, IOType.BOTH, null, Encoding.UTF8.getValue(), null, true));

        FORMATSUPPORTS.add(new FormatSupport(File.class, IOType.INPUT, null, null, null, true));

        FORMATSUPPORTS.add(new FormatSupport(Unit.class, IOType.INPUT, null, Encoding.UTF8.getValue(), null, true));

        FORMATSUPPORTS.add(new FormatSupport(SortBy[].class, IOType.INPUT, null, Encoding.UTF8.getValue(), null, true));
        FORMATSUPPORTS.add(new FormatSupport(NumberRange[].class, IOType.INPUT, null, Encoding.UTF8.getValue(), null, true));
        FORMATSUPPORTS.add(new FormatSupport(Filter.class, IOType.INPUT, null, Encoding.UTF8.getValue(), null, true));
        FORMATSUPPORTS.add(new FormatSupport(Date.class, IOType.INPUT, null, Encoding.UTF8.getValue(), null, true));

    }

    /**
     * Private constructor.
     */
    private WPSIO() {
    }

    /**
     * Check if a class for one {@link IOType} and one {@link DataType} is supported by the service.
     *
     * @param clazz
     * @param ioType
     * @param dataType
     * @return true if supported else false.
     */
    private static boolean isSupportedClass(final Class clazz, final IOType ioType, final FormChoice dataType) {
        boolean isSupported = false;
        if (clazz != null) {
            if (dataType.equals(FormChoice.LITERAL) || dataType.equals(FormChoice.ALL)) {
                try {

                    ConverterRegistry.system().converter(String.class, clazz);
                    isSupported = true;

                } catch (NonconvertibleObjectException ex) {
                    //Do nothing. In this case no simple converter where found
                }
            }

            //search the IOType
            final IOType classIOType = findIOType(clazz);

            if (!isSupported && classIOType != null) {
                Class source = null;
                Class target = null;

                Class formClass = null;
                if (dataType.equals(FormChoice.LITERAL)) {
                    formClass = String.class;
                } else if (dataType.equals(FormChoice.BBOX)) {
                    formClass = BoundingBoxType.class;
                } else if (dataType.equals(FormChoice.COMPLEX)) {
                    formClass = ComplexDataType.class;
                } else if (dataType.equals(FormChoice.LITERAL)) {
                    formClass = ReferenceType.class;
                }

                if (ioType.equals(IOType.INPUT)) {
                    source = formClass;
                    target = clazz;

                } else {
                    source = clazz;
                    target = formClass;
                }

                boolean tryAllSources = (source == null);
                boolean tryAllTargets = (target == null);

                final WPSConverterRegistry registry = WPSConverterRegistry.getInstance();
                WPSObjectConverter converter = null;
                int loop = 0;
                final Class[] testClass = new Class[]{String.class, ReferenceType.class, BoundingBoxType.class, ComplexDataType.class};

                while (converter == null) {
                    try {

                        if (!tryAllSources && !tryAllTargets) {
                            converter = registry.getConverter(source, target);
                        } else if (tryAllSources) {
                            converter = registry.getConverter(testClass[loop], target);
                        } else if (tryAllTargets) {
                            converter = registry.getConverter(source, testClass[loop]);
                        } else {
                            break;
                        }

                    } catch (NonconvertibleObjectException ex) {

                        loop++;
                        if (loop == testClass.length) {
                            break;
                        }
                        continue;
                    }
                }
                if (converter != null) {
                    isSupported = true;
                }
            }
        }
        return isSupported;
    }

    /**
     * Find if a class is supported in INPUT, OUTPUT or BOTH.
     *
     * @param clazz
     * @return IOType supported or null otherwise.
     */
    private static IOType findIOType(final Class clazz) {

        boolean hasInputConverter = false;
        boolean hasOutputConverter = false;
        if (!WPSConverterRegistry.getInstance().getInputConvertersFoTargetClass(clazz).isEmpty()) {
            hasInputConverter = true;
        }
        if (!WPSConverterRegistry.getInstance().getOutputConvertersForSourceClass(clazz).isEmpty()) {
            hasOutputConverter = true;
        }

        if (hasInputConverter && hasOutputConverter) {
            return IOType.BOTH;
        } else if (hasInputConverter) {
            return IOType.INPUT;
        } else if (hasOutputConverter) {
            return IOType.OUTPUT;
        }
        return null;
    }

    /**
     * Return the list of supported format for a class in an {@link IOType}.
     *
     * @param clazz
     * @param ioType
     * @return a list of {@link FormatSupport}
     */
    public static List<FormatSupport> getFormats(final Class clazz, final IOType ioType) {
        final List<FormatSupport> supports = new ArrayList<FormatSupport>();

        for (final FormatSupport formatSupport : FORMATSUPPORTS) {
            if (formatSupport.getClazz().equals(clazz) || formatSupport.getClazz().isAssignableFrom(clazz)) {
                if (formatSupport.getIOType().equals(IOType.BOTH) || formatSupport.getIOType().equals(formatSupport.getIOType())) {
                    supports.add(formatSupport);
                }
            }
        }
        return supports;
    }

    /**
     * Return the default {@link FormatSupport} for the given class in an {@link IOType}.
     *
     * @param clazz
     * @param ioType
     * @return a {@link FormatSupport}
     */
    public static FormatSupport getDefaultFormats(final Class clazz, final IOType ioType) {
        final List<FormatSupport> supports = getFormats(clazz, ioType);
        if (!supports.isEmpty()) {
            for (FormatSupport formatSupport : supports) {
                if (formatSupport.isDefaultFormat()) {
                    return formatSupport;
                }
            }
        }
        return null;
    }

    /**
     * Check if a class is supported in INPUT.
     *
     * @param clazz
     * @return true if supported, false otherwise.
     */
    public static boolean isSupportedInputClass(final Class clazz) {
        return isSupportedClass(clazz, IOType.INPUT, FormChoice.ALL);
    }

    /**
     * Check if a class is supported in OUTPUT.
     *
     * @param clazz
     * @return true if supported, false otherwise.
     */
    public static boolean isSupportedOutputClass(final Class clazz) {
        return isSupportedClass(clazz, IOType.OUTPUT, FormChoice.ALL);
    }

    /**
     * Check if a class is supported for LITERAL INPUT.
     *
     * @param clazz
     * @return true if supported, false otherwise.
     */
    public static boolean isSupportedLiteralInputClass(final Class clazz) {
        return isSupportedClass(clazz, IOType.INPUT, FormChoice.LITERAL);
    }

    /**
     * Check if a class is supported for COMPLEX INPUT.
     *
     * @param clazz
     * @return true if supported, false otherwise.
     */
    public static boolean isSupportedComplexInputClass(final Class clazz) {
        return isSupportedClass(clazz, IOType.INPUT, FormChoice.COMPLEX);
    }

    /**
     * Check if a class is supported for REFERENCE INPUT.
     *
     * @param clazz
     * @return true if supported, false otherwise.
     */
    public static boolean isSupportedReferenceInputClass(final Class clazz) {
        return isSupportedClass(clazz, IOType.INPUT, FormChoice.REFERENCE);
    }

    /**
     * Check if a class is supported for BBOX INPUT.
     *
     * @param clazz
     * @return true if supported, false otherwise.
     */
    public static boolean isSupportedBBoxInputClass(final Class clazz) {
        return isSupportedClass(clazz, IOType.INPUT, FormChoice.BBOX);
    }

    /**
     * Check if a class is supported for LITERAL OUTPUT.
     *
     * @param clazz
     * @return true if supported, false otherwise.
     */
    public static boolean isSupportedLiteralOutputClass(final Class clazz) {
        return isSupportedClass(clazz, IOType.OUTPUT, FormChoice.LITERAL);
    }

    /**
     * Check if a class is supported for COMPLEX OUTPUT.
     *
     * @param clazz
     * @return true if supported, false otherwise.
     */
    public static boolean isSupportedComplexOutputClass(final Class clazz) {
        return isSupportedClass(clazz, IOType.OUTPUT, FormChoice.COMPLEX);
    }

    /**
     * Check if a class is supported for REFERENCE OUTPUT.
     *
     * @param clazz
     * @return true if supported, false otherwise.
     */
    public static boolean isSupportedReferenceOutputClass(final Class clazz) {
        return isSupportedClass(clazz, IOType.OUTPUT, FormChoice.REFERENCE);
    }

    /**
     * Check if a class is supported for BBOX OUTPUT.
     *
     * @param clazz
     * @return true if supported, false otherwise.
     */
    public static boolean isSupportedBBoxOutputClass(final Class clazz) {
        return isSupportedClass(clazz, IOType.OUTPUT, FormChoice.BBOX);
    }

    /**
     * Check if given mimetype, encoding, schema are supported by the given input class in an {@link IOType}.
     *
     * @param clazz
     * @param ioType
     * @param mimeType
     * @param encoding
     * @param schema
     * @throws NonconvertibleObjectException if not supported.
     */
    public static void checkSupportedFormat(final Class clazz, final IOType ioType, String mimeType, String encoding, String schema)
            throws NonconvertibleObjectException {

        boolean formatOK = false;
        final List<FormatSupport> candidates = getFormats(clazz, ioType);

        int nbSearch = 0;
        if (mimeType != null) {
            mimeType.trim();
            if (mimeType.isEmpty()) {
                mimeType = null;
            } else {
                nbSearch++;
            }
        }

        if (encoding != null) {
            encoding.trim();
            if (encoding.isEmpty()) {
                encoding = null;
            } else {
                nbSearch++;
            }
        }

        if (schema != null) {
            schema.trim();
            if (schema.isEmpty()) {
                schema = null;
            } else {
                nbSearch++;
            }
        }

        if (mimeType == null && encoding == null && schema == null) {

            formatOK = true;

        } else {
            FormatSupport bestMatch = null;
            int maxMatch = 0;
            int match;
            for (int i = 0; i < candidates.size(); i++) {
                final FormatSupport formatSupport = candidates.get(i);
                final String wpsMime = formatSupport.getMimeType();
                final String wpsSchema = formatSupport.getSchema() != null ? formatSupport.getSchema() : null;
                final String wpsEncoding = formatSupport.getEncoding() != null ? formatSupport.getEncoding() : null;

                match = 0;

                if (mimeType != null && mimeType.equalsIgnoreCase(wpsMime)) {
                    match++;
                }
                if (schema != null && schema.equalsIgnoreCase(wpsSchema)) {
                    match++;
                }
                if (encoding != null && encoding.equalsIgnoreCase(wpsEncoding)) {
                    match++;
                }

                if (match > maxMatch) {
                    maxMatch = match;
                    bestMatch = formatSupport;
                }
                if (maxMatch == nbSearch) {
                    formatOK = true;
                }
            }
            if (!formatOK && bestMatch != null) {
                final String bestMime = bestMatch.getMimeType();
                final String bestSchema = bestMatch.getSchema() != null ? bestMatch.getSchema() : null;
                final String bestEncoding = bestMatch.getEncoding() != null ? bestMatch.getEncoding() : null;

                throw new NonconvertibleObjectException("Can't find a converter for these format : " + WPSConvertersUtils.dataFormatToString(mimeType, encoding, schema)
                        + ". You can try with this tuple : " + WPSConvertersUtils.dataFormatToString(bestMime, bestEncoding, bestSchema));
            }
        }
    }

    /**
     * Return the converter used to parse the data, using his class, his IOType, his DataType and his mimeType.
     *
     * @param clazz the binding Class.
     * @param ioType the {@link IOType}
     * @param dataType the {@link FormChoice}
     * @return a WPSObjectConverter
     * @throws NonconvertibleObjectException if no converter found
     */
    public static WPSObjectConverter getConverter(final Class clazz, final IOType ioType, final FormChoice dataType) throws NonconvertibleObjectException {

        WPSObjectConverter converter = null;
        if (dataType.equals(FormChoice.LITERAL)) {
            try {
                converter = new WPSObjectConverterAdapter(ConverterRegistry.system().converter(String.class, clazz));
            } catch (NonconvertibleObjectException ex) {
                //Do nothing. In this case no simple converter where found
            }
        }

        if (converter == null) {
            List<WPSObjectConverter> candidates = null;
            if (ioType.equals(IOType.INPUT)) {

                //get candidates
                candidates = WPSConverterRegistry.getInstance().getInputConvertersFoTargetClass(clazz);

                for (WPSObjectConverter conv : candidates) {
                    final Class sourceClass = conv.getSourceClass();
                    if ((dataType.equals(FormChoice.COMPLEX) && ComplexDataType.class.isAssignableFrom(sourceClass))
                            || (dataType.equals(FormChoice.REFERENCE) && ReferenceType.class.isAssignableFrom(sourceClass))
                            || (dataType.equals(FormChoice.BBOX) && BoundingBoxType.class.isAssignableFrom(sourceClass))
                            || (dataType.equals(FormChoice.LITERAL) && String.class.isAssignableFrom(sourceClass))) {
                        converter = conv;
                        break;
                    }
                }

                if (candidates.isEmpty() || converter == null) {
                    throw new NonconvertibleObjectException("No converter found.");
                }

            } else if (ioType.equals(IOType.OUTPUT)) {

                candidates = WPSConverterRegistry.getInstance().getOutputConvertersForSourceClass(clazz);

                for (WPSObjectConverter conv : candidates) {
                    final Class targetClass = conv.getTargetClass();
                    if ((dataType.equals(FormChoice.COMPLEX) && ComplexDataType.class.isAssignableFrom(targetClass))
                            || (dataType.equals(FormChoice.REFERENCE) && ReferenceType.class.isAssignableFrom(targetClass))
                            || (dataType.equals(FormChoice.BBOX) && BoundingBoxType.class.isAssignableFrom(targetClass))
                            || (dataType.equals(FormChoice.LITERAL) && String.class.isAssignableFrom(targetClass))) {
                        converter = conv;
                        break;
                    }
                }

                if (candidates.isEmpty() || converter == null) {
                    throw new NonconvertibleObjectException("No converter found.");
                }
            }
        }
        return converter;
    }

    /**
     * Find the most appropriate Java class for an INPUT/OUTPUT using mime/encoding/schema. If the FormChoice is LITERAL
     * and a DomainMetadataType not null, the method use the DomaineMetadataType value to find an appropriate class.
     *
     * @param ioType
     * @param dataType
     * @param mimeType
     * @param encoding
     * @param schema
     * @param literalDataType
     *
     * @return a java class or null if nothing found.
     */
    public static Class findClass(final IOType ioType, final FormChoice dataType, final String mimeType, final String encoding,
            final String schema, final DomainMetadataType literalDataType) {

        ArgumentChecks.ensureNonNull("dataType", dataType);
        Class clazz = null;

        if (dataType.equals(FormChoice.LITERAL) && literalDataType != null) {
            String value = literalDataType.getValue();
            clazz = String.class;
            if (value != null && !value.isEmpty()) {
                try {
                    clazz = Class.forName(value);
                } catch (ClassNotFoundException ex) {
                    value = value.toLowerCase();
                    if (value.contains("double")) {
                        clazz = Double.class;
                    } else if (value.contains("boolean")) {
                        clazz = Boolean.class;
                    } else if (value.contains("float")) {
                        clazz = Float.class;
                    } else if (value.contains("integer")) {
                        clazz = Integer.class;
                    } else if (value.contains("long")) {
                        clazz = Long.class;
                    }
                }
            }
            return clazz;

        } else if (dataType.equals(FormChoice.BBOX)) {
            clazz = Envelope.class;
        } else {

            final List<FormatSupport> candidates = new ArrayList<FormatSupport>();

            for (final FormatSupport format : FORMATSUPPORTS) {
                if (format.getIOType().equals(ioType) && format.getMimeType().equalsIgnoreCase(mimeType)) {
                    candidates.add(format);
                    if (schema != null && format.getSchema() != null && format.getSchema().equals(schema)) {
                        clazz = format.getClazz();
                        break;
                    }
                }
            }
        }
        return clazz;
    }

    /**
     * Supported encoding.
     */
    public static enum Encoding {

        UTF8("utf-8"),
        BASE64("base64");
        public final String encoding;

        private Encoding(final String encoding) {
            this.encoding = encoding;
        }

        public String getValue() {
            return encoding;
        }

        public static Encoding customValueOf(String candidate) {
            for (final Encoding encoding : values()) {
                if (encoding.getValue() != null) {
                    if (encoding.getValue().equalsIgnoreCase(candidate)) {
                        return encoding;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Supported schema.
     */
    public static enum Schema {

        OGC_FEATURE_3_1_1("http://schemas.opengis.net/gml/3.1.1/base/feature.xsd"),
        ORC_GML_3_1_1("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"),
        MATHML_3("http://www.w3.org/Math/XMLSchema/mathml3/mathml3.xsd");
        public final String schema;

        private Schema(final String schema) {
            this.schema = schema;
        }

        public String getValue() {
            return schema;
        }

        public static Schema customValueOf(String candidate) {
            for (final Schema schema : values()) {
                if (schema.getValue() != null) {
                    if (schema.getValue().equalsIgnoreCase(candidate)) {
                        return schema;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Enumeration for INPUT/OUTPUT.
     */
    public static enum IOType {

        INPUT, OUTPUT, BOTH;
    }

    /**
     * Enumeration of WPS data type.
     */
    public static enum FormChoice {

        LITERAL, COMPLEX, BBOX, REFERENCE, ALL;
    }

    /**
     * That pojo define if an {@link Class binding} is supported by the WPS for the{@link FormChoice} defined by the WPS
     * standard (LITERAL,COMPLEX,BBOX,REFERENCE).
     */
    public static class WPSSupport {

        private Class clazz;
        private FormChoice from;

        public WPSSupport(final Class clazz, final FormChoice from) {
            this.clazz = clazz;
            this.from = from;
        }

        public Class getClazz() {
            return clazz;
        }

        public FormChoice getFrom() {
            return from;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final WPSSupport other = (WPSSupport) obj;
            if (this.clazz != other.clazz && (this.clazz == null || (!this.clazz.equals(other.clazz) && !other.clazz.isAssignableFrom(this.clazz)))) {
                return false;
            }
            if (this.from != other.from) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 37 * hash + (this.clazz != null ? this.clazz.hashCode() : 0);
            hash = 37 * hash + (this.from != null ? this.from.hashCode() : 0);
            return hash;
        }
    }

    /**
     * That pojo define the mime/encoding/schema supported by a binding class for one {@link IOType} INPUT/OUPTUT.
     */
    public static class FormatSupport {

        private Class clazz;
        private IOType ioType;
        private String mimeType;
        private String encoding;
        private String schema;
        private boolean defaultFormat;

        public FormatSupport(Class clazz, IOType ioType, String mimeType, String encoding, String schema, boolean defaultFormat) {
            this.clazz = clazz;
            this.ioType = ioType;
            this.mimeType = mimeType;
            this.encoding = encoding;
            this.schema = schema;
            this.defaultFormat = defaultFormat;
        }

        public Class getClazz() {
            return clazz;
        }

        public void setClazz(Class clazz) {
            this.clazz = clazz;
        }

        public IOType getIOType() {
            return ioType;
        }

        public void setIOType(IOType ioType) {
            this.ioType = ioType;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        public boolean isDefaultFormat() {
            return defaultFormat;
        }

        public void setDefaultFormat(boolean defaultFormat) {
            this.defaultFormat = defaultFormat;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final FormatSupport other = (FormatSupport) obj;
            if (this.clazz != other.clazz && (this.clazz == null || (!this.clazz.equals(other.clazz) && !other.clazz.isAssignableFrom(this.clazz)))) {
                return false;
            }
            if ((this.mimeType == null) ? (other.mimeType != null) : !this.mimeType.equals(other.mimeType)) {
                return false;
            }
            if ((this.encoding == null) ? (other.encoding != null) : !this.encoding.equals(other.encoding)) {
                return false;
            }
            if ((this.schema == null) ? (other.schema != null) : !this.schema.equals(other.schema)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 83 * hash + (this.mimeType != null ? this.mimeType.hashCode() : 0);
            hash = 83 * hash + (this.encoding != null ? this.encoding.hashCode() : 0);
            hash = 83 * hash + (this.schema != null ? this.schema.hashCode() : 0);
            return hash;
        }
    }
}
