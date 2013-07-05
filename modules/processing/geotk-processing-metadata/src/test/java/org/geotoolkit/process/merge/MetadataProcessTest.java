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
package org.geotoolkit.process.merge;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.Process;
import org.junit.Test;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.identification.CharacterSet;
import org.opengis.parameter.ParameterValueGroup;
import static org.junit.Assert.*;
import org.opengis.util.NoSuchIdentifierException;

/**
 * Metadata Process test.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class MetadataProcessTest {
    
    @Test
    public void mergeTest() throws NoSuchIdentifierException, ProcessException, URISyntaxException, IOException{
        
        final long now = System.currentTimeMillis();
        
        final DefaultMetadata metadata1 = new DefaultMetadata();
        metadata1.setCharacterSet(CharacterSet.US_ASCII);
        final DefaultMetadata metadata2 = new DefaultMetadata();
        metadata2.setDateStamp(new Date(now));
        
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("metadata", "merge");
        assertNotNull(desc);
                
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("first").setValue(metadata1);
        input.parameter("second").setValue(metadata2);
                
        final Process process = desc.createProcess(input);
        assertNotNull(process);
        final ParameterValueGroup result = process.call();
        assertNotNull(result);
        
        Object obj = result.parameter("result").getValue();
        assertNotNull(obj);
        assertTrue(obj instanceof Metadata);
        
        final Metadata merged = (Metadata) obj;
        
        assertEquals(CharacterSet.US_ASCII, merged.getCharacterSet());
        assertEquals(new Date(now), merged.getDateStamp());
    }
    
}
