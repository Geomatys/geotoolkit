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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;

/**
 * Equivalent class of {@link YamlBuilder} use during Yaml binding.
 *
 * @author Remi Marechal (Geomatys).
 * @since 4.0
 * //-- faire un lien vers les methods utilitaire
 */
@JsonInclude(Include.NON_NULL)
public final class YamlImageInfo {

    /**
     * Define version of Yaml Image information.
     */
    @JsonIgnore
    static final String VERSION = "1.0";

    /**
     * Define current version.
     */
    private String version;

    /**
     * Image {@link SampleDimension} which will be written.
     */
    private List<YamlSampleDimension> sampleDimension;

    public YamlImageInfo() {
    }

    /**
     * Build and prepare future written attributs.
     *
     * @param sampleDims
     */
    public YamlImageInfo(final List<SampleDimension> sampleDims) {
        version = VERSION;
        this.sampleDimension = new ArrayList<YamlSampleDimension>();
        for (final SampleDimension gsd : sampleDims) {
            this.sampleDimension.add(new YamlSampleDimension(gsd));
        }
    }

    /**
     * Returns current version of Yaml work.
     *
     * @return current version of Yaml work.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set current version of Yaml work.
     *
     * @param version current version of Yaml work.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Returns all {@link YamlSampleDimension} which just have been read from Yaml file.
     *
     * @return {@link YamlSampleDimension}
     */
    public List<YamlSampleDimension> getSampleDimension() {
        return sampleDimension;
    }

    /**
     * Set {@link YamlSampleDimension} which will be written into Yaml.
     *
     * @param sampleDimension
     */
    public void setSampleDimension(final List<YamlSampleDimension> sampleDimension) {
        this.sampleDimension = sampleDimension;
    }

    public List<SampleDimension> toSampleDimensions(Class dataType) {
        if (!VERSION.equals(version)) {
            throw new IllegalStateException("Current file version does not match expected : 1.0. Found : " + version);
        }

        final List<SampleDimension> dim = new ArrayList<>();
        for (YamlSampleDimension sd : sampleDimension) {
            dim.add(sd.toSampleDimension(dataType));
        }
        return dim;
    }
}
