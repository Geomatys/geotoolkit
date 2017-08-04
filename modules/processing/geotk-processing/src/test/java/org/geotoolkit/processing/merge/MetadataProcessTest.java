/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.processing.merge;

import org.apache.sis.metadata.iso.DefaultMetadata;
import org.geotoolkit.csw.xml.CSWMarshallerPool;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.metadata.merge.MergeDescriptor;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.junit.Assert;

/**
 * Metadata merge process test.
 *
 * @author Johann Sorel (Geomatys)
 * @author Benjamin Garcia (Geomatys)
 */
public class MetadataProcessTest extends org.geotoolkit.test.TestBase {

    @Test
    public void mergeTest() throws NoSuchIdentifierException, ProcessException, URISyntaxException, IOException, JAXBException {
        final Unmarshaller xmlReader = CSWMarshallerPool.getInstance().acquireUnmarshaller();
        final InputStream templateFile = MetadataProcessTest.class.getResourceAsStream("/template.xml");
        final DefaultMetadata templateMetadata = (DefaultMetadata) xmlReader.unmarshal(templateFile);
        final InputStream dataFile = MetadataProcessTest.class.getResourceAsStream("/data.xml");
        final DefaultMetadata dataMetadata = (DefaultMetadata) xmlReader.unmarshal(dataFile);
        final InputStream finalFile = MetadataProcessTest.class.getResourceAsStream("/final.xml");
        final DefaultMetadata finalMetadata = (DefaultMetadata) xmlReader.unmarshal(finalFile);

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, MergeDescriptor.NAME);
        final ParameterValueGroup inputs = desc.getInputDescriptor().createValue();
        inputs.parameter(MergeDescriptor.FIRST_IN_NAME).setValue(dataMetadata);
        inputs.parameter(MergeDescriptor.SECOND_IN_NAME).setValue(templateMetadata);
        final org.geotoolkit.process.Process mergeProcess = desc.createProcess(inputs);
        ParameterValueGroup pvg = mergeProcess.call();
        final DefaultMetadata resultMetadata = (DefaultMetadata) pvg.parameter(MergeDescriptor.RESULT_OUT_NAME).getValue();

        Assert.assertNotNull(resultMetadata);
        Assert.assertEquals(resultMetadata, finalMetadata);
    }

}
