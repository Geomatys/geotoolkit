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
package org.geotoolkit.process.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.GZIPOutputStream;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.Process;
import org.geotoolkit.util.FileUtilities;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import static org.junit.Assert.*;
import org.opengis.util.NoSuchIdentifierException;
import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarOutputStream;

/**
 * IO Process test.
 *
 * @author Johann Sorel (Geomatys)
 */
public class IOProcessTest {

    @Test
    public void createTempFileTest() throws NoSuchIdentifierException, ProcessException, URISyntaxException{

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("io", "createTempFile");
        assertNotNull(desc);

        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("prefix").setValue("myprefix");
        input.parameter("postfix").setValue(".post");

        final Process process = desc.createProcess(input);
        assertNotNull(process);
        final ParameterValueGroup result = process.call();
        assertNotNull(result);

        Object obj = result.parameter("file").getValue();
        assertNotNull(obj);
        assertTrue(obj instanceof URL);

        File f = new File( ((URL)obj).toURI() );
        assertTrue(f.exists());
        assertTrue(f.getName().startsWith("myprefix"));
        assertTrue(f.getName().endsWith(".post"));
        f.delete();
    }

    @Test
    public void createTempFolderTest() throws NoSuchIdentifierException, ProcessException, URISyntaxException{

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("io", "createTempFolder");
        assertNotNull(desc);

        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("prefix").setValue("myfolder");

        final Process process = desc.createProcess(input);
        assertNotNull(process);
        final ParameterValueGroup result = process.call();
        assertNotNull(result);

        Object obj = result.parameter("folder").getValue();
        assertNotNull(obj);
        assertTrue(obj instanceof URL);

        File f = new File( ((URL)obj).toURI() );
        assertTrue(f.exists());
        assertTrue(f.getName().startsWith("myfolder"));

    }

    @Test
    public void deleteTest() throws NoSuchIdentifierException, ProcessException, URISyntaxException, IOException{

        final File f = File.createTempFile("test", ".td");
        f.createNewFile();

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("io", "delete");
        assertNotNull(desc);

        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("path").setValue(f.toURI().toURL());

        final Process process = desc.createProcess(input);
        assertNotNull(process);
        final ParameterValueGroup result = process.call();
        assertNotNull(result);

        Object obj = result.parameter("result").getValue();
        assertNotNull(obj);
        assertTrue(obj instanceof Boolean);
        assertTrue((Boolean)obj);

        assertFalse(f.exists());
    }

    @Test
    public void unpackTest() throws NoSuchIdentifierException, ProcessException, URISyntaxException, IOException{

        //create two archives, zip and tar.gz
        final File f = File.createTempFile("test", "");
        f.delete();
        f.mkdirs();
        final File f1 = new File(f, "file1.png"); f1.createNewFile();
        final File f2 = new File(f, "file2.txt"); f2.createNewFile();

        final File archiveZip = File.createTempFile("archive", ".zip");
        archiveZip.deleteOnExit();
        final File archiveTar = File.createTempFile("archive", ".tar.gz");
        archiveTar.deleteOnExit();
        FileUtilities.zip(archiveZip.toPath(),true, f1.toPath(), f2.toPath());

        // Create a TarOutputStream
        final TarOutputStream out = new TarOutputStream(
                new BufferedOutputStream(
                new GZIPOutputStream(
                new FileOutputStream(archiveTar))));
        for (File c : new File[]{f1,f2}) {
            out.putNextEntry(new TarEntry(c, c.getName()));
            BufferedInputStream origin = new BufferedInputStream(new FileInputStream(c));
            IOUtilities.copy(origin, out);
            origin.close();
        }
        out.flush();
        out.close();



        //temporary unpacking directory
        final File target = File.createTempFile("target", "");
        target.delete();
        target.mkdirs();

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("io", "unpackFile");
        assertNotNull(desc);

        // TEST ZIP UNPACK -----------------------------------------------------
        ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("source").setValue(archiveZip.toURI());
        input.parameter("target").setValue(target.toURI());

        Process process = desc.createProcess(input);
        assertNotNull(process);
        ParameterValueGroup result = process.call();
        assertNotNull(result);

        Object obj = result.parameter("files").getValue();
        assertNotNull(obj);
        assertEquals(2, ((URL[])obj).length );

        boolean found1 = false;
        boolean found2 = false;
        for(URL url : ((URL[])obj)){
            if(url.toString().endsWith("file1.png")){
                found1 = true;
            }
            if(url.toString().endsWith("file2.txt")){
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);


        // TEST TAR.GZ UNPACK --------------------------------------------------
        FileUtilities.deleteDirectory(target.toPath());
        target.delete();
        target.mkdirs();

        input = desc.getInputDescriptor().createValue();
        input.parameter("source").setValue(archiveTar.toURI().toURL());
        input.parameter("target").setValue(target.toURI().toURL());

        process = desc.createProcess(input);
        assertNotNull(process);
        result = process.call();
        assertNotNull(result);

        obj = result.parameter("files").getValue();
        assertNotNull(obj);
        assertEquals(2, ((URL[])obj).length );

        assertTrue(((URL[])obj)[0].toString().endsWith("file1.png"));
        assertTrue(((URL[])obj)[1].toString().endsWith("file2.txt"));



    }


}
