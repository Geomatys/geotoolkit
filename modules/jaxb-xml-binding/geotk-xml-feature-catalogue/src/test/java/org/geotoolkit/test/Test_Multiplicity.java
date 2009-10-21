/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.test;

import java.io.File;
import java.io.FileReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.util.Multiplicity;
import org.geotoolkit.util.MultiplicityRange;
import org.junit.Ignore;
import org.opengis.util.UnlimitedInteger;

/**
 *
 * @author guilhem
 * @module pending
 */
@Ignore
public class Test_Multiplicity {
    
    public static void main(String[] args) throws Exception {
        
    
     String fileName = "multiplicity.xml";
        // Unmarshalles the given XML file to objects
        JAXBContext context = JAXBContext.newInstance(Multiplicity.class);   
        
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Marshaller marshaller     = context.createMarshaller();
        try {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (PropertyException e) {
            System.out.println("prefix non trouv");
        }
        
        MultiplicityRange range = new MultiplicityRange(1, new UnlimitedInteger(Integer.MAX_VALUE));
        Multiplicity mul = new Multiplicity(range);
        
        UnlimitedInteger i = new UnlimitedInteger(Integer.MAX_VALUE);
        
        Object request = unmarshaller.unmarshal(new FileReader(fileName));
        
        System.out.println("unmarshalled: " + request);
        
        String fileOutput = "output.xml";
        
        marshaller.marshal(mul, new File(fileOutput));
    }
}
