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
package org.geotoolkit.data.dbf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.geotoolkit.data.dbf.DbaseFileReader.Row;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DBFEncodingTest {
    
    @Test
    public void testEncoding() throws Exception {
        
        final File file = new File("src/test/resources/org/geotoolkit/data/dbf/sample_UTF-8.dbf");
        
        DbaseFileReader reader = new DbaseFileReader(
                new RandomAccessFile(file,"r").getChannel(),
                false, 
                Charset.forName("UTF-8"));
        
        final List<Object[]> records = new ArrayList<Object[]>();
        while (reader.hasNext()) {
            final Row row = reader.next();
            records.add(row.readAll(null));
        }
        reader.close();
        
        assertEquals(6, records.size());
        final Properties props = new Properties();
        props.load(new FileInputStream(new File("src/test/resources/org/geotoolkit/data/dbf/sample_UTF-8.properties")));
        
        //check was we have read
        assertEquals(1d,                records.get(0)[0]);
        assertEquals(props.get("text1"),records.get(0)[1]);
        assertEquals("National",        records.get(0)[2]);
        
        assertEquals(2d,                records.get(1)[0]);
        assertEquals(props.get("text2"),records.get(1)[1]);
        assertEquals("National",        records.get(1)[2]);
        
        assertEquals(3d,                records.get(2)[0]);
        assertEquals(props.get("text3"),records.get(2)[1]);
        assertEquals("International",   records.get(2)[2]);
        
        assertEquals(4d,                records.get(3)[0]);
        assertEquals(props.get("text4"),records.get(3)[1]);
        assertEquals("National",        records.get(3)[2]);
        
        assertEquals(5d,                records.get(4)[0]);
        assertEquals(props.get("text5"),records.get(4)[1]);
        assertEquals("National",        records.get(4)[2]);
        
        assertEquals(6d,                records.get(5)[0]);
        assertEquals(props.get("text6"),records.get(5)[1]);
        assertEquals("National",        records.get(5)[2]);
                
    }
    
    @Test
    public void testGoto() throws Exception{
        final File file = new File("src/test/resources/org/geotoolkit/data/dbf/sample_UTF-8.dbf");
        final Properties props = new Properties();
        props.load(new FileInputStream(new File("src/test/resources/org/geotoolkit/data/dbf/sample_UTF-8.properties")));
        
        final DbaseFileReader reader = new DbaseFileReader(
                new RandomAccessFile(file,"r").getChannel(),
                false, 
                Charset.forName("UTF-8"));
        
        
        
        final Object[] buffer = new Object[3];
        Row row;
        
        //navigate randomly
        reader.goTo(6);
        row = reader.next();
        row.readAll(buffer);
        assertEquals(6d,                buffer[0]);
        assertEquals(props.get("text6"),buffer[1]);
        assertEquals("National",        buffer[2]);
        
        reader.goTo(3);
        row = reader.next();
        row.readAll(buffer);
        assertEquals(3d,                buffer[0]);
        assertEquals(props.get("text3"),buffer[1]);
        assertEquals("International",   buffer[2]);
        
        reader.goTo(1);
        row = reader.next();
        row.readAll(buffer);
        assertEquals(1d,                buffer[0]);
        assertEquals(props.get("text1"),buffer[1]);
        assertEquals("National",        buffer[2]);
        
        reader.goTo(5);
        row = reader.next();
        row.readAll(buffer);
        assertEquals(5d,                buffer[0]);
        assertEquals(props.get("text5"),buffer[1]);
        assertEquals("National",        buffer[2]);
        
        reader.goTo(2);
        row = reader.next();
        row.readAll(buffer);
        assertEquals(2d,                buffer[0]);
        assertEquals(props.get("text2"),buffer[1]);
        assertEquals("National",        buffer[2]);
        
        reader.close();
    }
    
    
}
