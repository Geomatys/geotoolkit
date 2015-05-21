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
import org.geotoolkit.coverage.GridSampleDimension;
import org.opengis.coverage.SampleDimension;

/**
 * Equivalent class of {@link YamlBuilder} use during Yaml binding.
 *
 * @author Remi Marechal (Geomatys).
 * @since 4.0
 * //-- faire un lien vers les methods utilitaire
 */
public class YamlImageInfo {

    /**
     * Define version of Yaml Image information.
     */
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
     * Build and prepare future written attributs from {@link YamlWriterBuilder}. 
     * 
     * @param yamlWB Builder which contains all image informations which will be written into Yaml format.
     */
    public YamlImageInfo(final YamlWriterBuilder yamlWB) {
        if (!(yamlWB instanceof YamlBuilder)) {
            throw new IllegalArgumentException("You can't write image informations "
                    + "with builder which not be instance of org.geotoolkit.image.io.plugin.yaml.YamlBuilder");
        }
        version = VERSION;
        final YamlBuilder yb = (YamlBuilder) yamlWB;
        this.sampleDimension = new ArrayList<YamlSampleDimension>();
        for (final SampleDimension gsd : yb.getSampleDimensions()) {
            this.sampleDimension.add(new YamlSampleDimension(GridSampleDimension.castOrCopy(gsd)));
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
     * Returns all {@link YamlSampleDimension} which just have been read from Yaml file.
     * 
     * @return {@link YamlSampleDimension} 
     */
    public List<YamlSampleDimension> getSampleDimension() {
        return sampleDimension;
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
     * Set {@link YamlSampleDimension} which will be written into Yaml.
     * 
     * @param sampleDimension 
     */
    public void setSampleDimension(final List<YamlSampleDimension> sampleDimension) {
        this.sampleDimension = sampleDimension;
    }
}
