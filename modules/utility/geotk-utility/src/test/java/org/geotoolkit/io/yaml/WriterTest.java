/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.io.yaml;

import java.util.Locale;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.opengis.metadata.citation.Role;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.citation.DefaultResponsibility;
import org.apache.sis.metadata.iso.distribution.DefaultDistribution;
import org.apache.sis.metadata.iso.distribution.DefaultDistributor;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.metadata.iso.identification.AbstractIdentification;
import org.junit.Test;

import static org.apache.sis.test.Assert.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;


/**
 * Tests {@link Writer}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @module
 */
public strictfp final class WriterTest {
    /**
     * The JSON string of the metadata object to be used in this test.
     */
    static final String JSON =
            "{\n" +
            "    \"fileIdentifier\": \"An archive\",\n" +
            "    \"language\": \"en\",\n" +
            "    \"characterSet\": \"UTF-8\",\n" +
            "    \"metadataStandardName\": \"ISO19115\",\n" +
            "    \"metadataStandardVersion\": \"2003/Cor.1:2006\",\n" +
            "    \"identificationInfo\": [{\n" +
            "        \"citation\": {\n" +
            "            \"title\": \"Data \\\"title\\\"\"\n" +
            "        },\n" +
            "        \"extent\": [{\n" +
            "            \"geographicElement\": [{\n" +
            "                \"westBoundLongitude\": -11.4865013,\n" +
            "                \"eastBoundLongitude\": -4.615912,\n" +
            "                \"southBoundLatitude\": 43.165467,\n" +
            "                \"northBoundLatitude\": 49.9990223,\n" +
            "                \"extentTypeCode\": true\n" +
            "            }]\n" +
            "        }]\n" +
            "    }],\n" +
            "    \"distributionInfo\": {\n" +
            "        \"distributor\": [{\n" +
            "            \"distributorContact\": {\n" +
            "                \"role\": \"author\"\n" +
            "            }\n" +
            "        },{\n" +
            "            \"distributorContact\": {\n" +
            "                \"role\": \"collaborator\"\n" +
            "            }\n" +
            "        }]\n" +
            "    }\n" +
            "}";

    /**
     * Creates the metadata object corresponding to the {@link #JSON} string.
     */
    static DefaultMetadata createMetadata() {
        final AbstractIdentification identification = new AbstractIdentification();
        identification.setCitation(new DefaultCitation("Data \"title\""));
        identification.setExtents(singleton(new DefaultExtent(null,
                new DefaultGeographicBoundingBox(-11.4865013, -4.615912, 43.165467, 49.9990223), null, null)));

        final DefaultDistribution distribution = new DefaultDistribution();
        distribution.setDistributors(asList(
                new DefaultDistributor(new DefaultResponsibility(Role.AUTHOR, null, null)),
                new DefaultDistributor(new DefaultResponsibility(Role.COLLABORATOR, null, null))));

        final DefaultMetadata metadata = new DefaultMetadata();
        metadata.setFileIdentifier("An archive");
        metadata.setLanguage(Locale.ENGLISH);
        metadata.setCharacterSet(StandardCharsets.UTF_8);
        metadata.setMetadataStandardName("ISO19115");
        metadata.setMetadataStandardVersion("2003/Cor.1:2006");
        metadata.setIdentificationInfo(singleton(identification));
        metadata.setDistributionInfo(distribution);
        return metadata;
    }

    /**
     * Tests formatting of a metadata object.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testFormat() throws IOException {

        final StringBuilder buffer = new StringBuilder();
        Writer writer = new Writer(buffer);
        writer.format(createMetadata());
        assertMultilinesEquals("JSON format", JSON, buffer.toString());
    }
}
