/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.image.io.plugin.yaml.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.opengis.coverage.SampleDimension;

/**
 * Builder which aggregate all image information which will be write/read into/from Yaml format.
 *
 * @author Remi Marechal (Geomatys).
 * @since 4.0
 */
public class YamlBuilder implements YamlReaderBuilder, YamlWriterBuilder {

    /**
     * Image {@link SampleDimension} which will be written or re-built from Yaml file reading.
     */
    private List<SampleDimension> sampleDimensions;

    /**
     * Create a default builder to store shortly written image information.<br>
     * Also use during Yaml binding.
     */
    public YamlBuilder() {
    }

//    /**
//     * Create a Yaml builder from its equivalent Yaml class, use during unmarshalling.
//     *
//     * @param imageInfo needed Yaml class to re-build this builder during reading Yaml file.
//     * @param dataType
//     * @see //--- faire un lien vers method read static de YamlFiles
//     */
//    YamlBuilder(final YamlImageInfo imageInfo, final Class dataType) {
//        ArgumentChecks.ensureNonNull("imageInfo", imageInfo);
//        ArgumentChecks.ensureNonNull("dataType", dataType);
//        assert sampleDimensions == null;
//
//        sampleDimensions = new ArrayList<SampleDimension>();
//
//        convertSampleDimensions(imageInfo.getSampleDimension(), dataType, sampleDimensions);
//    }

    /**
     * Create a Yaml builder from its equivalent Yaml class, use during unmarshalling.
     *
     * @param imageInfoMapped needed Yaml class to re-build this builder during reading Yaml file.
     * @param dataType
     * @see //--- faire un lien vers method read static de YamlFiles
     */
    public YamlBuilder(final Map<String, Object> imageInfoMapped, final Class dataType) {
        ArgumentChecks.ensureNonNull("imageInfo", imageInfoMapped);
        ArgumentChecks.ensureNonNull("dataType", dataType);


        assert imageInfoMapped.size() == 2; //-- (at the instant) only two attributs
                                            //-- (version, sampleDimension) from YamlImageInfo.Class

        final String objVersion = (String) imageInfoMapped.get("version");
        if (!YamlImageInfo.VERSION.equalsIgnoreCase(objVersion))
            throw new IllegalStateException("Current file version does not match expected : "+YamlImageInfo.VERSION+". Found : "+objVersion);

        final Object objList = imageInfoMapped.get("sampleDimension");
        sampleDimensions = new ArrayList<SampleDimension>();

        if (!(objList instanceof List)) return;
        convertSampleDimensionsMap((List) objList, dataType, sampleDimensions);
    }

    /**
     * Returns precedently setted {@link SampleDimension}.
     *
     * @return {@link SampleDimension}.
     */
    @Override
    public List<SampleDimension> getSampleDimensions() {
        return sampleDimensions;
    }

    /**
     * Set {@link SampleDimension} which will be
     *
     * @param sampleDimensions
     */
    @Override
    public void setSampleDimensions(final List<SampleDimension> sampleDimensions) {
        ArgumentChecks.ensureNonNull("sampleDimensions", sampleDimensions);
//        if (sampleDimensions.isEmpty())
//            throw new IllegalArgumentException("Impossible to write empty SampleDimension list.");

        this.sampleDimensions = sampleDimensions;
    }

    /**
     * Convert {@link YamlSampleDimension} from yaml file reading to {@link SampleDimension}.
     *
     * @param yamlSD {@link YamlSampleDimension} source list.
     * @param dataType internal datatype from {@link SampleDimension} {@linkplain Category categories}.
     * @param destinationList the dsetination {@link list} to store converted {@link SampleDimension}
     */
    private static void convertSampleDimensionsMap(final List<Map> yamlSD, final Class dataType,
                                                final List<SampleDimension> destinationList) {
        ArgumentChecks.ensureNonNull("yamlSD", yamlSD);
        ArgumentChecks.ensureNonNull("dataType", dataType);
        ArgumentChecks.ensureNonNull("destinationList", destinationList);
        for (Map ysd : yamlSD) {
            final String sdDescription = (String) ysd.get("description");
            final List<Map> ylCats = (List<Map>) ysd.get("categories");
            int c = 0;
            final Category[] cats = new Category[ylCats.size()];
            for (Map yCat : ylCats) {
                final String catName = (String) yCat.get("name");
                final Double value   = (Double) yCat.get("value");

                final double  minSampleValue, maxSampleValue;
                final boolean isMinInclusive, isMaxInclusive;
                if (value != null) {
                    minSampleValue = maxSampleValue = value;
                    isMinInclusive = isMaxInclusive = true;
                } else {
                    assert yCat.size() >= 5;//-- at least 5 attributs
                    minSampleValue = (double)  yCat.get("minSampleValue");
                    isMinInclusive = (boolean) yCat.get("isMinInclusive");
                    maxSampleValue = (double)  yCat.get("maxSampleValue");
                    isMaxInclusive = (boolean) yCat.get("isMaxInclusive");
                }

                if (catName.equalsIgnoreCase(SampleDimensionUtils.NODATA_CATEGORY_NAME.toString(Locale.ENGLISH))) {
                    cats[c++] = SampleDimensionUtils.buildNoDataCategory(dataType,
                                                                         minSampleValue, isMinInclusive,
                                                                         maxSampleValue, isMaxInclusive);
                } else {
                    final double scale  = (double) yCat.get("scale");
                    final double offset = (double) yCat.get("offset");
                    cats[c++] = SampleDimensionUtils.buildCategory(catName, dataType, null,
                            minSampleValue, isMinInclusive,
                            maxSampleValue, isMaxInclusive,
                            scale, offset);
                }
            }
            destinationList.add(new GridSampleDimension(sdDescription, cats, null));
        }
    }
}
