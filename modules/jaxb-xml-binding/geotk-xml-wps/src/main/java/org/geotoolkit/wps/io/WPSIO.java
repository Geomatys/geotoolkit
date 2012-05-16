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
import org.geotoolkit.process.converters.*;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.converter.SimpleConverter;
import org.geotoolkit.ows.xml.v110.DomainMetadataType;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.converters.inputs.complex.*;
import org.geotoolkit.wps.converters.inputs.references.*;
import org.geotoolkit.wps.converters.outputs.complex.*;
import org.geotoolkit.wps.converters.outputs.references.FeatureToReferenceConverter;
import org.geotoolkit.wps.converters.outputs.references.FeatureTypeToReferenceConverter;
import org.geotoolkit.wps.converters.outputs.references.GeometryToReferenceConverter;
import org.geotoolkit.wps.converters.outputs.references.LiteralsToReferenceConverter;
import org.geotoolkit.wps.converters.outputs.references.RenderedImageToReferenceConverter;
import org.geotoolkit.wps.xml.v100.DataType;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * 
 * @author Quentin Boileau
 */
public final class WPSIO {

    private static final List<WPSSupport> SUPPORT = Collections.synchronizedList(new ArrayList<WPSSupport>());

    static {
        /**
         * Feature.
         */
        SUPPORT.add(new WPSSupport(Feature.class, IOType.INPUT, FormChoice.COMPLEX, ComplexToFeatureConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(Feature.class, IOType.INPUT, FormChoice.COMPLEX, ComplexToFeatureConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(Feature.class, IOType.INPUT, FormChoice.COMPLEX, ComplexToFeatureConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, true));

        SUPPORT.add(new WPSSupport(Feature.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToFeatureConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(Feature.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToFeatureConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(Feature.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToFeatureConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, true));
        
        SUPPORT.add(new WPSSupport(Feature.class, IOType.OUTPUT, FormChoice.COMPLEX, FeatureToComplexConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(Feature.class, IOType.OUTPUT, FormChoice.COMPLEX, FeatureToComplexConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(Feature.class, IOType.OUTPUT, FormChoice.COMPLEX, FeatureToComplexConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, true));

        SUPPORT.add(new WPSSupport(Feature.class, IOType.OUTPUT, FormChoice.REFERENCE, FeatureToReferenceConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(Feature.class, IOType.OUTPUT, FormChoice.REFERENCE, FeatureToReferenceConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(Feature.class, IOType.OUTPUT, FormChoice.REFERENCE, FeatureToReferenceConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, true));

        /**
         * FeatureCollection.
         */
        SUPPORT.add(new WPSSupport(FeatureCollection.class, IOType.INPUT, FormChoice.COMPLEX, ComplexToFeatureCollectionConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(FeatureCollection.class, IOType.INPUT, FormChoice.COMPLEX, ComplexToFeatureCollectionConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(FeatureCollection.class, IOType.INPUT, FormChoice.COMPLEX, ComplexToFeatureCollectionConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, true));

        SUPPORT.add(new WPSSupport(FeatureCollection.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToFeatureCollectionConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(FeatureCollection.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToFeatureCollectionConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(FeatureCollection.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToFeatureCollectionConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, true));
        SUPPORT.add(new WPSSupport(FeatureCollection.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToFeatureCollectionConverter.getInstance(), WPSMimeType.APP_SHP.val(), false));
        SUPPORT.add(new WPSSupport(FeatureCollection.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToFeatureCollectionConverter.getInstance(), WPSMimeType.APP_OCTET.val(), false));

        SUPPORT.add(new WPSSupport(FeatureCollection.class, IOType.OUTPUT, FormChoice.COMPLEX, FeatureCollectionToComplexConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(FeatureCollection.class, IOType.OUTPUT, FormChoice.COMPLEX, FeatureCollectionToComplexConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(FeatureCollection.class, IOType.OUTPUT, FormChoice.COMPLEX, FeatureCollectionToComplexConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, true));

        SUPPORT.add(new WPSSupport(FeatureCollection.class, IOType.OUTPUT, FormChoice.REFERENCE, FeatureToReferenceConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(FeatureCollection.class, IOType.OUTPUT, FormChoice.REFERENCE, FeatureToReferenceConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(FeatureCollection.class, IOType.OUTPUT, FormChoice.REFERENCE, FeatureToReferenceConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, true));

        /**
         * Feature[].
         */
        SUPPORT.add(new WPSSupport(Feature[].class, IOType.INPUT, FormChoice.COMPLEX, ComplexToFeatureArrayConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(Feature[].class, IOType.INPUT, FormChoice.COMPLEX, ComplexToFeatureArrayConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(Feature[].class, IOType.INPUT, FormChoice.COMPLEX, ComplexToFeatureArrayConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, true));
        
        /**
         * FeatureCollection[].
         */
        SUPPORT.add(new WPSSupport(FeatureCollection[].class, IOType.INPUT, FormChoice.COMPLEX, ComplexToFeatureCollectionArrayConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(FeatureCollection[].class, IOType.INPUT, FormChoice.COMPLEX, ComplexToFeatureCollectionArrayConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, false));
        SUPPORT.add(new WPSSupport(FeatureCollection[].class, IOType.INPUT, FormChoice.COMPLEX, ComplexToFeatureCollectionArrayConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, true));
        
        /**
         * Geometry.
         */
        SUPPORT.add(new WPSSupport(Geometry.class, IOType.INPUT, FormChoice.COMPLEX, ComplexToGeometryConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, false));
        SUPPORT.add(new WPSSupport(Geometry.class, IOType.INPUT, FormChoice.COMPLEX, ComplexToGeometryConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, false));
        SUPPORT.add(new WPSSupport(Geometry.class, IOType.INPUT, FormChoice.COMPLEX, ComplexToGeometryConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, true));

        SUPPORT.add(new WPSSupport(Geometry.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToGeometryConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, false));
        SUPPORT.add(new WPSSupport(Geometry.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToGeometryConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, false));
        SUPPORT.add(new WPSSupport(Geometry.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToGeometryConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, true));

        SUPPORT.add(new WPSSupport(Geometry.class, IOType.OUTPUT, FormChoice.COMPLEX, GeometryToComplexConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, false));
        SUPPORT.add(new WPSSupport(Geometry.class, IOType.OUTPUT, FormChoice.COMPLEX, GeometryToComplexConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, false));
        SUPPORT.add(new WPSSupport(Geometry.class, IOType.OUTPUT, FormChoice.COMPLEX, GeometryToComplexConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, true));

        SUPPORT.add(new WPSSupport(Geometry.class, IOType.OUTPUT, FormChoice.REFERENCE, GeometryToReferenceConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, false));
        SUPPORT.add(new WPSSupport(Geometry.class, IOType.OUTPUT, FormChoice.REFERENCE, GeometryToReferenceConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, false));
        SUPPORT.add(new WPSSupport(Geometry.class, IOType.OUTPUT, FormChoice.REFERENCE, GeometryToReferenceConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, true));
        
        /**
         * Geometry[].
         */
        SUPPORT.add(new WPSSupport(Geometry[].class, IOType.INPUT, FormChoice.COMPLEX, ComplexToGeometryArrayConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, false));
        SUPPORT.add(new WPSSupport(Geometry[].class, IOType.INPUT, FormChoice.COMPLEX, ComplexToGeometryArrayConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, false));
        SUPPORT.add(new WPSSupport(Geometry[].class, IOType.INPUT, FormChoice.COMPLEX, ComplexToGeometryArrayConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, true));

        SUPPORT.add(new WPSSupport(Geometry[].class, IOType.OUTPUT, FormChoice.COMPLEX, GeometryArrayToComplexConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, false));
        SUPPORT.add(new WPSSupport(Geometry[].class, IOType.OUTPUT, FormChoice.COMPLEX, GeometryArrayToComplexConverter.getInstance(), WPSMimeType.TEXT_GML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, false));
        SUPPORT.add(new WPSSupport(Geometry[].class, IOType.OUTPUT, FormChoice.COMPLEX, GeometryArrayToComplexConverter.getInstance(), WPSMimeType.APP_GML.val(), Encoding.UTF8, Schema.ORC_GML_3_1_1, true));
        
        /**
         * FeatureType.
         */
        SUPPORT.add(new WPSSupport(FeatureType.class, IOType.INPUT, FormChoice.COMPLEX, ComplexToFeatureTypeConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, true));
        SUPPORT.add(new WPSSupport(FeatureType.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToFeatureTypeConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, true));
        SUPPORT.add(new WPSSupport(FeatureType.class, IOType.OUTPUT, FormChoice.COMPLEX, FeatureTypeToComplexConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, true));
        SUPPORT.add(new WPSSupport(FeatureType.class, IOType.OUTPUT, FormChoice.REFERENCE, FeatureTypeToReferenceConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.OGC_FEATURE_3_1_1, true));
             
        /**
         * RenderedImage.
         */
        for (final String readerMime : ImageIO.getReaderMIMETypes()) {
            if (!readerMime.isEmpty()) {
                if (readerMime.equals("image/png")) {
                    SUPPORT.add(new WPSSupport(RenderedImage.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToRenderedImageConverter.getInstance(), readerMime, null, null, true));
                } else {
                    SUPPORT.add(new WPSSupport(RenderedImage.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToRenderedImageConverter.getInstance(), readerMime, null, null, false));
                }
            }
        }

        for (final String writerMime : ImageIO.getWriterMIMETypes()) {
            if (!writerMime.isEmpty()) {
                if (writerMime.equals("image/png")) {
                    SUPPORT.add(new WPSSupport(RenderedImage.class, IOType.OUTPUT, FormChoice.REFERENCE, RenderedImageToReferenceConverter.getInstance(), writerMime, null, null, true));
                } else {
                    SUPPORT.add(new WPSSupport(RenderedImage.class, IOType.OUTPUT, FormChoice.REFERENCE, RenderedImageToReferenceConverter.getInstance(), writerMime, null, null, false));
                }
            }
        }
        
        /**
         * Coverage. @TODO wait to URL support in GridCoverageReader
         */
        /*for (final String readerMime : ImageIO.getReaderMIMETypes()) {
            if (!readerMime.isEmpty()) {
                if (readerMime.equals("image/png")) {
                    SUPPORT.add(new WPSSupport(Coverage.class, IOType.INPUT, DataType.REFERENCE, ReferenceToGridCoverage2DConverter.getInstance(), readerMime, null, null, true));
                } else {
                    SUPPORT.add(new WPSSupport(Coverage.class, IOType.INPUT, DataType.REFERENCE, ReferenceToGridCoverage2DConverter.getInstance(), readerMime, null, null, false));
                }
            }
        }

        for (final String writerMime : ImageIO.getWriterMIMETypes()) {
            if (!writerMime.isEmpty()) {
                if (writerMime.equals("image/png")) {
                    SUPPORT.add(new WPSSupport(Coverage.class, IOType.OUTPUT, DataType.REFERENCE, CoverageToReferenceConverter.getInstance(), writerMime, null, null, true));
                } else {
                    SUPPORT.add(new WPSSupport(Coverage.class, IOType.OUTPUT, DataType.REFERENCE, CoverageToReferenceConverter.getInstance(), writerMime, null, null, false));
                }
            }
        }*/
        
         /**
         * AffineTransform.
         */
        SUPPORT.add(new WPSSupport(AffineTransform.class, IOType.INPUT, FormChoice.COMPLEX, ComplexToAffineTransformConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.MATHML_3, true));
        SUPPORT.add(new WPSSupport(AffineTransform.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToAffineTransformConverter.getInstance(), WPSMimeType.TEXT_XML.val(), Encoding.UTF8, Schema.MATHML_3, true));
        //SUPPORT.add(new WPSSupport(AffineTransform.class, IOType.OUTPUT, FormChoice.REFERENCE, LiteralsToReferenceConverter.getInstance(), WPSMimeType.TEXT_PLAIN.val(), Encoding.UTF8, null, true));
        
        
        SUPPORT.add(new WPSSupport(Number.class, IOType.OUTPUT, FormChoice.REFERENCE, LiteralsToReferenceConverter.getInstance(), WPSMimeType.TEXT_PLAIN.val(), Encoding.UTF8, null, true));
        SUPPORT.add(new WPSSupport(String.class, IOType.OUTPUT, FormChoice.REFERENCE, LiteralsToReferenceConverter.getInstance(), WPSMimeType.TEXT_PLAIN.val(), Encoding.UTF8, null, true));
        SUPPORT.add(new WPSSupport(Double.class, IOType.OUTPUT, FormChoice.REFERENCE, LiteralsToReferenceConverter.getInstance(), WPSMimeType.TEXT_PLAIN.val(), Encoding.UTF8, null, true));
        
        /**
         * File.
         */
        SUPPORT.add(new WPSSupport(File.class, IOType.INPUT, FormChoice.REFERENCE, ReferenceToFileConverter.getInstance(), null, null, null, true));

        /**
         * Unit.
         */
        SUPPORT.add(new WPSSupport(Unit.class, IOType.INPUT, FormChoice.LITERAL, StringToUnitConverter.getInstance(), true));
        SUPPORT.add(new WPSSupport(Unit.class, IOType.OUTPUT, FormChoice.LITERAL, null, true));
        SUPPORT.add(new WPSSupport(Unit.class, IOType.OUTPUT, FormChoice.REFERENCE, LiteralsToReferenceConverter.getInstance(), WPSMimeType.TEXT_PLAIN.val(), Encoding.UTF8, null, true));

        /**
         * CoordinateReferenceSystem.
         */
        SUPPORT.add(new WPSSupport(CoordinateReferenceSystem.class, IOType.INPUT, FormChoice.LITERAL, StringToCRSConverter.getInstance(), true));
        SUPPORT.add(new WPSSupport(CoordinateReferenceSystem.class, IOType.OUTPUT, FormChoice.LITERAL, null, true));
        SUPPORT.add(new WPSSupport(AffineTransform.class, IOType.OUTPUT, FormChoice.REFERENCE, LiteralsToReferenceConverter.getInstance(), WPSMimeType.TEXT_PLAIN.val(), Encoding.UTF8, null, true));
        
        /**
         * SortBy[].
         */
        SUPPORT.add(new WPSSupport(SortBy[].class, IOType.INPUT, FormChoice.LITERAL, StringToSortByConverter.getInstance(), true));

        /**
         * NumberRange[].
         */
        SUPPORT.add(new WPSSupport(NumberRange[].class, IOType.INPUT, FormChoice.LITERAL, StringToNumberRangeConverter.getInstance(), true));

        /**
         * Filter.
         */
        SUPPORT.add(new WPSSupport(Filter.class, IOType.INPUT, FormChoice.LITERAL, StringToFilterConverter.getInstance(), true));
        
        /**
         * BBOX Envelop opengis.
         */
        SUPPORT.add(new WPSSupport(Envelope.class, IOType.INPUT, FormChoice.BBOX, StringToFilterConverter.getInstance(), true));
        SUPPORT.add(new WPSSupport(Envelope.class, IOType.OUTPUT, FormChoice.BBOX, StringToFilterConverter.getInstance(), true));
        
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
            
            if (!isSupported) {
                for (final WPSSupport wPSSupport : SUPPORT) {
                    if ((wPSSupport.getClazz().equals(clazz) || wPSSupport.getClazz().isAssignableFrom(clazz)) && wPSSupport.getType().equals(ioType)) {
                        if (dataType.equals(FormChoice.ALL)) {
                            isSupported = true;
                            break;
                        } else {
                            if (wPSSupport.getFrom().equals(dataType)) {
                                isSupported = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return isSupported;
    }

    /**
     * Return the list of {@link WPSSupport } that match the given {@link Class binding}, {@link IOType io type} and {@link DataType type}.
     * 
     * @param clazz
     * @param ioType
     * @param dataType
     * @return list of {@link WPSSupport }
     */
    public static List<WPSSupport> getSupports(final Class clazz, final IOType ioType, final FormChoice dataType) {
        final List<WPSSupport> supports = new ArrayList<WPSSupport>();
        for (final WPSSupport wpsSupport : SUPPORT) {
            if ((wpsSupport.getClazz().equals(clazz) || wpsSupport.getClazz().isAssignableFrom(clazz))
                    && wpsSupport.getType().equals(ioType)
                    && wpsSupport.getFrom().equals(dataType)) {
                supports.add(wpsSupport);
            }
        }
        return supports;
    }

    public static WPSSupport getDefaultSupport(final Class clazz, final IOType ioType, final FormChoice dataType) {
        final List<WPSSupport> supports = getSupports(clazz, ioType, dataType);
        if (!supports.isEmpty()) {
            for (WPSSupport wPSSupport : supports) {
                if (wPSSupport.isDefaultIO()) {
                    return wPSSupport;
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
     * Return the converter used to parse the data, using his class, his IOType, hist DataType and his mimeType.
     *
     * @param clazz
     * @param ioType
     * @param dataType
     * @param mimeType
     * @return converter
     * @throws NonconvertibleObjectException no converter found
     */
    public static SimpleConverter getConverter(final Class clazz, final IOType ioType, final FormChoice dataType, String mimeType,
            String encoding, String schema) throws NonconvertibleObjectException {
        
        SimpleConverter converter = null;
        if (clazz != null) {

            final List<WPSSupport> candidates = getSupports(clazz, ioType, dataType);
            
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
                
                if (!candidates.isEmpty()) {
                    converter= candidates.get(0).getConverter();
                } else {
                    throw new NonconvertibleObjectException("Can't find a converter for these format : " + WPSConvertersUtils.dataFormatToString(mimeType, encoding, schema));
                }
                
            } else {
            
                WPSSupport bestMatch = null;
                int maxMatch = 0;
                int match;
                for (int i = 0; i < candidates.size(); i++) {
                    final WPSSupport wpsSupport = candidates.get(i);
                    final String wpsMime = wpsSupport.getMime();
                    final String wpsSchema = wpsSupport.getSchema() != null ? wpsSupport.getSchema().getValue() : null;
                    final String wpsEncoding = wpsSupport.getEncoding() != null ? wpsSupport.getEncoding().getValue() : null;
                    
                    match = 0;
                   
                    if (mimeType != null  && mimeType.equalsIgnoreCase(wpsMime)) {
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
                        bestMatch = wpsSupport;
                    }
                    if (maxMatch == nbSearch) {
                        converter = wpsSupport.getConverter();
                        break;
                    }
                }
                if (converter == null) {
                    final String bestMime = bestMatch.getMime();
                    final String bestSchema = bestMatch.getSchema() != null ? bestMatch.getSchema().getValue() : null;
                    final String bestEncoding = bestMatch.getEncoding() != null ? bestMatch.getEncoding().getValue() : null;
                    
                    throw new NonconvertibleObjectException("Can't find a converter for these format : " + WPSConvertersUtils.dataFormatToString(mimeType, encoding, schema) + 
                            ". You can try with this tuple : "+WPSConvertersUtils.dataFormatToString(bestMime, bestEncoding, bestSchema));
                }
            }
        }
        return converter;
    }

    /**
     * Found the most appropriate Java class for on INPUT/OUTPUT.
     * 
     * @param ioType
     * @param dataType
     * @param mimeType
     * @param encoding
     * @param schema
     * @param literalDataType
     * @return a class or null if not found.
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
            
        } else {
            ArgumentChecks.ensureNonNull("ioType", ioType);
            ArgumentChecks.ensureNonNull("mimeType", mimeType);
            
            final List<WPSSupport> candidates = new ArrayList<WPSSupport>();
            
            for (final WPSSupport wpsSupport : SUPPORT) {
                if (wpsSupport.getType().equals(ioType) && wpsSupport.getFrom().equals(dataType) && wpsSupport.getMime().equalsIgnoreCase(mimeType)) {
                    candidates.add(wpsSupport);
                    if (schema != null && wpsSupport.getSchema() != null && wpsSupport.getSchema().getValue().equals(schema)) {
                        clazz = wpsSupport.getClazz();
                        break;
                    }
                }
            }
            
            if (clazz == null && !candidates.isEmpty()) {
                clazz = candidates.get(0).getClazz();
            }
            //use FeatureCollection for more genericity
            if ((Feature.class).equals(clazz)) {
                clazz = FeatureCollection.class;
            }
        
            return clazz;
        }
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

        INPUT, OUTPUT;
    }

    /**
     * Enumeration of WPS data type.
     */
    public static enum FormChoice {

        LITERAL, COMPLEX, BBOX, REFERENCE, ALL;
    }

    /**
     * POJO that contain for one supported INPUT or OUTPUT. 
     * That pojo define an {@link Class binding}, {@link IOType io type} (INPUT/OUTPUT), {@link DataType data type} define by the WPS standard 
     * (LITERAL,COMPLEX,BBOX,REFERENCE). He also define a {@link SimpleConverter converter}, a mimeType, an encoding and a schema.
     */
    public static class WPSSupport {

        private Class clazz;
        private IOType type;
        private FormChoice from;
        private SimpleConverter converter;
        private String mime;
        private Encoding encoding;
        private Schema schema;
        private boolean defaultIO;

        public WPSSupport(final Class clazz, final IOType type, final FormChoice from, final SimpleConverter converter,
                final boolean defaultIO) {
            this(clazz, type, from, converter, null, null, null, defaultIO);
        }

        public WPSSupport(final Class clazz, final IOType type, final FormChoice from, final SimpleConverter converter,
                final String mime, final boolean defaultIO) {
            this(clazz, type, from, converter, mime, null, null, defaultIO);
        }

        public WPSSupport(final Class clazz, final IOType type, final FormChoice from, final SimpleConverter converter,
                final String mime, final Encoding encoding, final Schema schema, final boolean defaultIO) {
            this.clazz = clazz;
            this.type = type;
            this.from = from;
            this.converter = converter;
            this.mime = mime;
            this.encoding = encoding;
            this.schema = schema;
            this.defaultIO = defaultIO;
        }

        public Class getClazz() {
            return clazz;
        }

        public SimpleConverter getConverter() {
            return converter;
        }

        public boolean isDefaultIO() {
            return defaultIO;
        }

        public Encoding getEncoding() {
            return encoding;
        }

        public FormChoice getFrom() {
            return from;
        }

        public String getMime() {
            return mime;
        }

        public Schema getSchema() {
            return schema;
        }

        public IOType getType() {
            return type;
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
            if (this.type != other.type) {
                return false;
            }
            if (this.from != other.from) {
                return false;
            }
            if ((this.mime == null) ? (other.mime != null) : !this.mime.equals(other.mime)) {
                return false;
            }
            if (this.encoding != other.encoding) {
                return false;
            }
            if (this.schema != other.schema) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 37 * hash + (this.clazz != null ? this.clazz.hashCode() : 0);
            hash = 37 * hash + (this.type != null ? this.type.hashCode() : 0);
            hash = 37 * hash + (this.from != null ? this.from.hashCode() : 0);
            return hash;
        }
    }
}
