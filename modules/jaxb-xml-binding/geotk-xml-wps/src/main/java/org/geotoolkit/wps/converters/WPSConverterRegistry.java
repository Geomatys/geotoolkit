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

import java.util.*;
import java.util.logging.Logger;
import javax.measure.Unit;

import org.geotoolkit.feature.util.converter.StringToDateConverter;
import org.geotoolkit.ows.xml.BoundingBox;
import org.geotoolkit.processing.util.converter.StringToCRSConverter;
import org.geotoolkit.processing.util.converter.StringToFilterConverter;
import org.geotoolkit.processing.util.converter.StringToNumberRangeConverter;
import org.geotoolkit.processing.util.converter.StringToSortByConverter;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.wps.converters.inputs.complex.*;
import org.geotoolkit.wps.converters.inputs.literal.*;
import org.geotoolkit.wps.converters.inputs.references.*;
import org.geotoolkit.wps.converters.outputs.complex.*;
import org.geotoolkit.wps.converters.outputs.literal.*;
import org.geotoolkit.wps.converters.outputs.references.*;
import org.geotoolkit.wps.xml.ComplexDataType;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.wps.xml.Reference;

/**
 * Registry that register all WPS converters used.
 *
 * @author Quentin Boileau (Geomatys).
 */
public class WPSConverterRegistry {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.wps.converters");
    private final List<WPSObjectConverter> converters;
    private static WPSConverterRegistry INSTANCE;


    private WPSConverterRegistry() {
        final List<WPSObjectConverter> list = new LinkedList<>();
        converters = Collections.synchronizedList(list);

        //ComplexDataType -> Object converters
        register(ComplexToAffineTransformConverter          .getInstance());
        register(ComplexToFeatureArrayConverter             .getInstance());
        register(ComplexToFeatureCollectionArrayConverter   .getInstance());
        register(ComplexToFeatureCollectionConverter        .getInstance());
        register(ComplexToFeatureConverter                  .getInstance());
        register(ComplexToFeatureTypeConverter              .getInstance());
        register(ComplexToGeometryArrayConverter            .getInstance());
        register(ComplexToGeometryConverter                 .getInstance());
        register(ComplexToRendredImageConverter             .getInstance());
        register(ComplexToCoverageConverter                 .getInstance());
        register(ComplexToFileConverter                     .getInstance());

        //ReferenceType -> Object Converters
        register(ReferenceToAffineTransformConverter        .getInstance());
        register(ReferenceToFeatureCollectionConverter      .getInstance());
        register(ReferenceToFeatureConverter                .getInstance());
        register(ReferenceToFeatureTypeConverter            .getInstance());
        register(ReferenceToFileConverter                   .getInstance());
        register(ReferenceToGeometryConverter               .getInstance());
        register(ReferenceToGridCoverage2DConverter         .getInstance());
        register(ReferenceToGridCoverageReaderConverter     .getInstance());
        register(ReferenceToRenderedImageConverter          .getInstance());
        register(ReferenceToStringConverter                 .getInstance());
        register(ReferenceToGeometryArrayConverter          .getInstance());
        register(ReferenceToUrlConverter                    .getInstance());
        register(ReferenceToUrlConnectionConverter          .getInstance());

        //Object -> ComplexDataType converters
        register(FeatureCollectionToComplexConverter        .getInstance());
        register(FeatureToComplexConverter                  .getInstance());
        register(FeatureTypeToComplexConverter              .getInstance());
        register(GeometryArrayToComplexConverter            .getInstance());
        register(GeometryToComplexConverter                 .getInstance());
        register(RenderedImageToComplexConverter            .getInstance());
        register(CoverageToComplexConverter                 .getInstance());
        register(FileToComplexConverter                     .getInstance());

        //Object -> ReferenceType converters
        register(CoverageToReferenceConverter               .getInstance());
        register(FeatureToReferenceConverter                .getInstance());
        register(FeatureCollectionToReferenceConverter      .getInstance());
        register(FeatureTypeToReferenceConverter            .getInstance());
        register(GeometryToReferenceConverter               .getInstance());
        register(RenderedImageToReferenceConverter          .getInstance());
        register(StringToReferenceConverter                 .getInstance());
        register(NumberToReferenceConverter                 .getInstance());
        register(BooleanToReferenceConverter                .getInstance());
        register(FileToReferenceConverter                   .getInstance());
        register(GeometryArrayToReferenceConverter          .getInstance());
        register(StyledLayerDescriptorToReferenceConverter  .getInstance());
        register(UrlToReferenceConverter                    .getInstance());
        register(UrlConnectionToReferenceConverter          .getInstance());

        //String -> Object converters
        register(new WPSObjectConverterAdapter(ObjectConverters.find(String.class, Unit.class)));
        register(new WPSObjectConverterAdapter(new StringToCRSConverter()));
        register(new WPSObjectConverterAdapter(new StringToFilterConverter()));
        register(new WPSObjectConverterAdapter(StringToSortByConverter.getInstance()));
        register(new WPSObjectConverterAdapter(StringToNumberRangeConverter.getInstance()));
        register(new WPSObjectConverterAdapter(new StringToDateConverter()));
        register(new WPSObjectConverterAdapter(new StringToDoubleArrayConverter()));
        register(new WPSObjectConverterAdapter(new StringToFloatArrayConverter()));
        register(new WPSObjectConverterAdapter(new StringToIntegerArrayConverter()));
        register(new WPSObjectConverterAdapter(new StringToDoubleWArrayConverter()));
        register(new WPSObjectConverterAdapter(new StringToFloatWArrayConverter()));
        register(new WPSObjectConverterAdapter(new StringToIntegerWArrayConverter()));
        register(new WPSObjectConverterAdapter(new StringToMapConverter()));

        // Object -> String converters
        register(new WPSObjectConverterAdapter(new DoubleArrayToStringConverter()));
        register(new WPSObjectConverterAdapter(new IntegerArrayToStringConverter()));
        register(new WPSObjectConverterAdapter(new FloatArrayToStringConverter()));
        register(new WPSObjectConverterAdapter(new DoubleWArrayToStringConverter()));
        register(new WPSObjectConverterAdapter(new IntegerWArrayToStringConverter()));
        register(new WPSObjectConverterAdapter(new FloatWArrayToStringConverter()));
        register(new WPSObjectConverterAdapter(new MapToStringConverter()));

    }

    public static synchronized WPSConverterRegistry getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new WPSConverterRegistry();
        }
        return INSTANCE;
    }

    private void register(final WPSObjectConverter converter) {
        if (converter != null) {
            converters.add(converter);
        }
    }

    /**
     * Search the most suitable converter registered with a source and target class.
     *
     * @param source
     * @param target
     * @return
     * @throws UnconvertibleObjectException in case of no converter found.
     */
    public WPSObjectConverter getConverter(final Class source, final Class target) throws UnconvertibleObjectException {

        ArgumentChecks.ensureNonNull("source", source);
        ArgumentChecks.ensureNonNull("target", target);

        for (final WPSObjectConverter converter : converters) {
            if (converter.getSourceClass().isAssignableFrom(source) && converter.getTargetClass().isAssignableFrom(target)) {
                return converter;
            }
        }
        throw new UnconvertibleObjectException("No converter found.");
    }

    /**
     * Return all converter that match a possible target class used by WPS INPUT.
     * e.g. : {@link BoundingBox}, {@link ComplexDataType}, {@link ReferenceType}, {@link String}
     *
     * @param target
     * @return all input converter for the source class.
     */
    public List<WPSObjectConverter> getInputConvertersFoTargetClass (final Class target) {
        final List<WPSObjectConverter> inputConverters = new ArrayList<>();

        for (final WPSObjectConverter converter : converters) {
            if (converter.getTargetClass().isAssignableFrom(target)) {
                final Class sourceClass = converter.getSourceClass();
                if (BoundingBox.class.isAssignableFrom(sourceClass) || ComplexDataType.class.isAssignableFrom(sourceClass)
                        || Reference.class.isAssignableFrom(sourceClass) || String.class.isAssignableFrom(sourceClass)) {
                    inputConverters.add(converter);
                }
            }
        }
        return inputConverters;
    }

    /**
     * Return all converter that match a possible source class used by WPS OUPTUT.
     * e.g. : {@link BoundingBox}, {@link ComplexDataType}, {@link Reference}, {@link String}
     *
     * @param source
     * @return all input converter for the source class.
     */
    public List<WPSObjectConverter> getOutputConvertersForSourceClass (final Class source) {
        final List<WPSObjectConverter> inputConverters = new ArrayList<>();

        for (final WPSObjectConverter converter : converters) {
            if (converter.getSourceClass().isAssignableFrom(source)) {
                final Class targetClass = converter.getTargetClass();
                if (BoundingBox.class.isAssignableFrom(targetClass) || ComplexDataType.class.isAssignableFrom(targetClass)
                        || Reference.class.isAssignableFrom(targetClass) || String.class.isAssignableFrom(targetClass)) {
                    inputConverters.add(converter);
                }
            }
        }
        return inputConverters;
    }

}
