/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.coverage.xmlstore;

import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.referencing.CRS;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Test XML Binding for {@link XMLPyramid} from different versions.
 *
 * @author Remi Marechal (Geomatys).
 */
public class XMLPyramidTest {
    
    /**
     * Temporary file to test XML binding.
     */
    private static File TEMP_FILE;
    private static final File getMainfile() throws IOException {
        if (TEMP_FILE == null) TEMP_FILE = File.createTempFile("XMLPyramid", "");
        return TEMP_FILE;
    }
    
    /**
     * Test CRS.
     */
    private static CoordinateReferenceSystem CRS_TEST;
    private static CoordinateReferenceSystem getCRSTest() throws FactoryException {
        if (CRS_TEST == null) CRS_TEST = CRS.decode("EPSG:3395");
        return CRS_TEST;
    }
    
    /**
     * Marshall object to read and write into XML format.
     */
    @XmlTransient
    private static MarshallerPool POOL;
    private static synchronized MarshallerPool getPoolInstance() throws JAXBException {
        if (POOL == null) POOL = new MarshallerPool(JAXBContext.newInstance(XMLPyramid.class), null);
        return POOL;
    }
    
    /**
     * Save the coverage reference in the file.
     * 
     * @throws DataStoreException
     */
    synchronized void save(XMLOldPyramid pyram) throws JAXBException, IOException {
        final MarshallerPool pool   = getPoolInstance();
        final Marshaller marshaller = pool.acquireMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(pyram, getMainfile());
        pool.recycle(marshaller);
    }
    
    /**
     * Read the given file and return an XMLCoverageReference.
     *
     * @param file path where to read XML informations.
     * @return XMLPyramid
     * @throws JAXBException if an error occured while reading descriptor file.
     * @throws org.apache.sis.storage.DataStoreException if the file describe a pyramid, but it contains an invalid CRS.
     */
    public static XMLPyramid read(final File file) throws JAXBException, DataStoreException {
        final MarshallerPool pool       = getPoolInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final XMLPyramid pyramid;
        pyramid = (XMLPyramid) unmarshaller.unmarshal(file);
        pool.recycle(unmarshaller);
        return pyramid;
    }
    
    /**
     * Test XmlPyramid XML binding with internal serialized and Wkt1 formatting CRS.
     * 
     * @throws Exception 
     */
    @Test
    public void testOldVersion() throws Exception {
        XMLOldPyramid oldPyram = new XMLOldPyramid(getCRSTest());
        save(oldPyram);
        XMLPyramid newPyram = read(getMainfile());
        Assert.assertTrue(CRS.equalsIgnoreMetadata(getCRSTest(), newPyram.getCoordinateReferenceSystem()));
    }
    
    /**
     * Test XmlPyramid XML binding with only internal serialized CRS.
     * 
     * @throws Exception 
     */
    @Test
    public void testOldSerializedVersion() throws Exception {
        final XMLSerializePyramid oldSerializedPyram = new XMLSerializePyramid(getCRSTest());
        save(oldSerializedPyram);
        final XMLPyramid newPyram = read(getMainfile());
        Assert.assertTrue(CRS.equalsIgnoreMetadata(getCRSTest(), newPyram.getCoordinateReferenceSystem()));
    }
    
    /**
     * Test XmlPyramid XML binding, with only internal Wkt1 formatting CRS.
     * 
     * @throws Exception 
     */
    //TODO: delete ignore annotation
    @Ignore
    @Test
    public void testOldWkt1Version() throws Exception {
        final XMLWkt1Pyramid oldWkt1Pyram = new XMLWkt1Pyramid(getCRSTest());
        save(oldWkt1Pyram);
        final XMLPyramid newPyram = read(getMainfile());
        final CoordinateReferenceSystem expectedCrs = getCRSTest();
        final CoordinateReferenceSystem resultCrs = newPyram.getCoordinateReferenceSystem();
        Assert.assertTrue(CRS.equalsIgnoreMetadata(getCRSTest(), newPyram.getCoordinateReferenceSystem()));
    }
}
