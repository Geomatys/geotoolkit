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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.coverage.SampleDimension;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlImageInfo;

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
    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.image.io.plugin.yaml");

    /**
     * Read Yaml informations from String.<br>
     * If there was any parsing problem during reading action, an {@link Collections#EMPTY_LIST} will be return.
     *
     * @param yaml yaml content.
     * @param dataType sample data type to interprete categories values. (double, short, ...)
     * @return read {@link SampleDimension} {@link List},
     * or {@linkplain Collections#EMPTY_LIST empty list} if none or any reading problem.
     * @throws IllegalStateException if version doesn't match.
     */
    public static List<SampleDimension> read(final String yaml, Class dataType) throws IOException {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        final YamlImageInfo obj = mapper.readValue(yaml, YamlImageInfo.class);
        final List<SampleDimension> result = obj.toSampleDimensions(dataType);
        if (result.isEmpty()) {
            LOGGER.log(Level.FINE, "Yaml image informations could not be read.");
        }
        return result;
    }

    /**
     * Read Yaml informations from {@link File}.<br>
     * If there was any parsing problem during reading action, an {@link Collections#EMPTY_LIST} will be return.
     *
     * @param path path to Yaml file informations.
     * @param dataType sample data type to interprete categories values. (double, short, ...)
     * @return read {@link SampleDimension} {@link List},
     * or {@linkplain Collections#EMPTY_LIST empty list} if none or any reading problem.
     * @throws IllegalStateException if version doesn't match.
     */
    public static List<SampleDimension> read(final File path, Class dataType) throws IOException {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        final YamlImageInfo obj = mapper.readValue(path, YamlImageInfo.class);
        final List<SampleDimension> result = obj.toSampleDimensions(dataType);
        if (result.isEmpty()) {
            LOGGER.log(Level.FINE, "Yaml image informations could not be read.");
        }
        return result;
    }

    /**
     * Returns {@String} object which will be the serialization of image informations
     * from sample dimensions into Yaml format.
     *
     * @param sd sample dimensions to write.
     * @return serialization of image informations into Yaml format.
     */
    public static String write(final List<SampleDimension> sd) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new YamlImageInfo(sd));
    }

    /**
     * Write sample dimensions into Yaml format.
     *
     * @param sd sample dimensions to write.
     * @param path File to write to.
     */
    public static void write(final List<SampleDimension> sd, final Path path) throws JsonProcessingException, IOException {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), new YamlImageInfo(sd));
    }

}

