/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotools.test;

import java.io.File;
import java.io.FileReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import net.seagis.ws.rs.NamespacePrefixMapperImpl;
import org.geotools.util.Multiplicity;
import org.geotools.util.MultiplicityRange;
import org.opengis.util.UnlimitedInteger;

/**
 *
 * @author guilhem
 */
public class Test_Multiplicity {
    
    public static void main(String[] args) throws Exception {
        
    
     String fileName = "multiplicity.xml";
        // Unmarshalles the given XML file to objects
        JAXBContext context = JAXBContext.newInstance(Multiplicity.class);   
        
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Marshaller marshaller     = context.createMarshaller();
        try {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl("http://www.isotc211.org/2005/srv"));
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
