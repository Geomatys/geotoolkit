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
package org.geotoolkit.image.io.plugin.yaml;

import org.geotoolkit.image.io.plugin.yaml.internal.YamlCategory;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlSampleDimension;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlImageInfo;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlWriterBuilder;
import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlBuilder;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlSampleCategory;
import org.opengis.coverage.SampleDimension;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Aggregate some needed method to read or write image informations into Yaml format.<br><br>
 *
 * Writing code example :<br><br>
 *
 * final List<SampleDimension> myListToWrite;<br>
 * final YamlWriterBuilder myYamlWriterBuilder = YamlFiles.getBuilder();<br>
 * myYamlWriterBuilder.setSampleDimensions(myListToWrite);<br><br>
 *
 * //-- to write in file path<br>
 * final File myPath = new File("myPath");<br>
 * YamlFiles.write(myPath, yamBuild);<br><br>
 *
 * //-- just to dump into String object<br>
 * YamlFiles.dump(yamBuild);<br>
 * <br><br>
 *
 * Reading code example : <br>
 *
 * final Class generatedCategoryDatatype = Byte.class; //-- for example, also could be Double, Float, Short, Integer<br><br>
 *
 * //-- to read from file<br>
 * final File myPath = new File("myPathToRead");<br>
 * final List<SampleDimension> myListToWrite = YamlFiles.read(myPath, generatedCategoryDatatype);<br><br>
 *
 * //-- to load from String object<br>
 * final List<SampleDimension> myListToWrite = YamlFiles.load(dumpResult, generatedCategoryDatatype);<br>
 *
 *
 * @author Remi Marechal (Geomatys).
 * @since 4.0
 */
public final class YamlFiles {

    /**
     * Logger to diffuse no blocking error message.
     */
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.image.io.plugin.yaml");

    /**
     * Define fields order for {@link YamlCategory} attributs.
     */
    private static final String[] YAML_CATEGORIES_FIELD_ORDER = new String[]{"name",
                                                                       "minSampleValue",
                                                                       "isMinInclusive",
                                                                       "maxSampleValue",
                                                                       "isMaxInclusive",
                                                                       "value",
                                                                       "scale",
                                                                       "offset"};

    /**
     * Define fields order for {@link YamlSampleDimension} attributs.
     */
    private static final String[] YAML_SAMPLEDIMS_FIELD_ORDER = new String[]{"description",
                                                                       "categories"};

    /**
     * Define fields order for {@link YamlImageInfo} attributs.
     */
    private static final String[] YAML_INFO_FIELD_ORDER = new String[]{"version",
                                                                 "sampleDimension"};

    /**
     * Multiple Yaml options for object dumping (writing).
     */
    private static final DumperOptions DUMPER_OPTION = new DumperOptions();

    /**
     * Define properties to how interpret class name.
     */
    private static final Representer DUMP_REPRESENTER = new NullRepresenter();

    /**
     * {@link Constructor} to define List and map attributs properties.
     */
    private static final Constructor DUMP_CONSTRUCTOR = new Constructor();

    /**
     * {@link Constructor} to define attribut load.
     */
    private static final Constructor LOAD_CONSTRUCTOR = new Constructor();

    static {
        //-- one attribut per line
        DUMPER_OPTION.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        //-- replace class name by choosen title
        DUMP_REPRESENTER.addClassTag(YamlImageInfo.class, Tag.MAP);
        DUMP_REPRESENTER.addClassTag(YamlSampleCategory.class, Tag.MAP);

        //-- to organize attribut writing order
        DUMP_REPRESENTER.setPropertyUtils(new writerPropertyUtils());

        //-- define internal list and map attributs
        final TypeDescription imageInfoTypeDescription = new TypeDescription(YamlImageInfo.class);
        imageInfoTypeDescription.putListPropertyType("sampleDimension", YamlSampleDimension.class);

        final TypeDescription sampleDimensionTypeDescription = new TypeDescription(YamlSampleDimension.class);
        sampleDimensionTypeDescription.putListPropertyType("categories", YamlCategory.class);

        DUMP_CONSTRUCTOR.addTypeDescription(imageInfoTypeDescription);
        DUMP_CONSTRUCTOR.addTypeDescription(sampleDimensionTypeDescription);


        //--------------------------- Read ---------------------------------
        LOAD_CONSTRUCTOR.addTypeDescription(imageInfoTypeDescription);
        LOAD_CONSTRUCTOR.addTypeDescription(sampleDimensionTypeDescription);
        LOAD_CONSTRUCTOR.addTypeDescription(new TypeDescription(YamlImageInfo.class, Tag.MAP));
        LOAD_CONSTRUCTOR.addTypeDescription(new TypeDescription(YamlSampleCategory.class, Tag.MAP));
    }

    /**
     * Read Yaml informations from {@link File}.<br>
     * If there was any parsing problem during reading action, an {@link Collections#EMPTY_LIST} will be return.
     *
     * @param path path to Yaml file informations.
     * @param sampleType sample data type to interprete categories values. (double, short, ...)
     * @return read {@link SampleDimension} {@link List},
     * or {@linkplain Collections#EMPTY_LIST empty list} if none or any reading problem.
     * @throws IllegalStateException if version doesn't match.
     */
    public static List<SampleDimension> read(final File path, final Class sampleType) throws IOException {
        final Yaml yamyam           = new Yaml(LOAD_CONSTRUCTOR);

        final Object obj;
        try (FileReader fileReader = new FileReader(path)) {
            obj = yamyam.load(fileReader);
        }

        if (!(obj instanceof Map))
            throw new IllegalStateException("Expected result from Yaml file reading should be instance of Map.");

        final YamlBuilder yamBuild = new YamlBuilder((Map<String, Object>) obj, sampleType);
        final List<SampleDimension> result = yamBuild.getSampleDimensions();

        if (result.isEmpty())
            LOGGER.log(Level.FINE, "Yaml image informations could not be read.");

        return result;
    }

    /**
     * Read Yaml informations from {@link String}.<br>
     * If there was any parsing problem during reading action, an {@link Collections#EMPTY_LIST} will be return.
     *
     * @param yaml path to Yaml file informations.
     * @param sampleType sample data type to interprete categories values. (double, short, ...)
     * @return read {@link SampleDimension} {@link List},
     * or {@linkplain Collections#EMPTY_LIST empty list} if none or any reading problem.
     * @throws IllegalStateException if version doesn't match.
     */
    public static List<SampleDimension> load(final String yaml, final Class sampleType) throws IOException {
        final Yaml yamyam           = new Yaml(LOAD_CONSTRUCTOR);

        final Object obj = yamyam.load(yaml);

        if (!(obj instanceof Map))
            throw new IllegalStateException("Expected result from Yaml file reading should be instance of Map.");

        final YamlBuilder yamBuild = new YamlBuilder((Map<String, Object>) obj, sampleType);
        final List<SampleDimension> result = yamBuild.getSampleDimensions();

        if (result.isEmpty())
            LOGGER.log(Level.FINE, "Yaml image informations could not be read.");

        return result;
    }

    /**
     * Returns needed builder to write image informations into Yaml format.
     *
     * @return builder to write image informations into Yaml format.
     * @see YamlBuilder
     */
    public static YamlWriterBuilder getBuilder() {
        return new YamlBuilder();
    }

    /**
     * Returns {@String} object which will be the serialization of image informations
     * from {@link YamlWriterBuilder} into Yaml format.
     *
     * @param imageInfoBuilder regroup all image informations which will be shortly serialized.
     * @return serialization of image informations into Yaml format.
     */
    public static String dump(final YamlWriterBuilder imageInfoBuilder) {

        //-- create YamlImageInfo
        final YamlImageInfo yii = new YamlImageInfo(imageInfoBuilder);
        final Yaml yamyam = new Yaml(DUMP_CONSTRUCTOR, DUMP_REPRESENTER, DUMPER_OPTION);
        return yamyam.dump(yii);
    }

    /**
     * Write image informations from precedently filled {@link YamlWriterBuilder}
     * into Yaml format at specified emplacement given by path parameter.
     *
     * @param path emplacement where Yaml file is create.
     * @param imageInfoBuilder regroup all image informations which will be shortly written.
     * @throws IOException if problem during Yaml file writing.
     */
    public static void write(final File path, final YamlWriterBuilder imageInfoBuilder) throws IOException {
        final FileWriter fileW = new FileWriter(path);
        fileW.write(dump(imageInfoBuilder));
        fileW.flush();
        fileW.close();
    }

    /**
     * Override Yaml object to avoid Yaml {@code null} value dumping.
     */
    private static class NullRepresenter extends Representer {

        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
            if (propertyValue == null) {
                return null;
            }
            return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag); //To change body of generated methods, choose Tools | Templates.
        }
    }

    /**
     * Override Yaml object to choose writing order of internal class fields.
     */
    private static class writerPropertyUtils extends PropertyUtils {

        @Override
        protected Set<Property> createPropertySet(Class<? extends Object> type, BeanAccess bAccess)
                throws IntrospectionException {
            final Set<Property> result = new LinkedHashSet<Property>();
            final Map<String, Property> props = getPropertiesMap(type, BeanAccess.DEFAULT);
            final String[] orderArray;
            if (type.isAssignableFrom(org.geotoolkit.image.io.plugin.yaml.internal.YamlCategory.class)
             || type.isAssignableFrom(org.geotoolkit.image.io.plugin.yaml.internal.YamlSampleCategory.class)) {
                orderArray = YAML_CATEGORIES_FIELD_ORDER;
            } else if (type.isAssignableFrom(org.geotoolkit.image.io.plugin.yaml.internal.YamlSampleDimension.class)) {
                orderArray = YAML_SAMPLEDIMS_FIELD_ORDER;
            } else if (type.isAssignableFrom(org.geotoolkit.image.io.plugin.yaml.internal.YamlImageInfo.class)) {
                orderArray = YAML_INFO_FIELD_ORDER;
            } else {
                orderArray = null;
            }
            //-- add properties elements in choosen order.
            if (orderArray != null)
                for (final String strKey : orderArray) {
                    for(final String key : props.keySet()) {
                        if (strKey.equalsIgnoreCase(key)) {
                            result.add(props.remove(key));
                            break;
                        }
                    }
                }
            //-- add remaining properties or all properties for other classes.
            result.addAll(props.values());
            return result;
        }
    }
}

