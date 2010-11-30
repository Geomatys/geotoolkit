/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.util;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DomUtilitiesTest {

    @Test
    public void writeTest() throws ParserConfigurationException, IOException, TransformerException{
        
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document document = builder.newDocument();
        document.setXmlVersion("1.0");
        document.setXmlStandalone(true);

        final Element root = document.createElement("logs");

        final Element personne = document.createElement("user");
        personne.setAttribute("id","gfsd-589");
        root.appendChild(personne);

        final Element nom = document.createElement("name");
        nom.setTextContent("tom");
        personne.appendChild(nom);

        final Element age = document.createElement("age");
        age.setTextContent("45");
        personne.appendChild(age);

        document.appendChild(root);
        
        File f = File.createTempFile("test", ".xml");
        f.deleteOnExit();
        
        DomUtilities.write(document, f);
        
    }

}
