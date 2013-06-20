/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 */
package org.geotoolkit.demo.metadata;

import javax.xml.bind.JAXBException;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.PresentationForm;

import org.geotoolkit.xml.XML;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.citation.DefaultResponsibleParty;


/**
 * A simple demo creating a {@link Citation} metadata with the "<cite>Hello World</cite>"
 * title and some other attributes, and marshalling it to XML.
 */
public class HelloWorld {
    /**
     * Creates a "<cite>Hello World</cite>" citation. At the time of writing, there is
     * not yet GeoAPI factory interface for metadata. So we have to instantiate directly
     * the implementation class. However after our metadata has been created, we return
     * the implementation-neutral interface.
     */
    private static Citation createHelloWorld() {
        final DefaultCitation citation = new DefaultCitation();
        citation.setTitle(new SimpleInternationalString("Hello world"));
        citation.getPresentationForms().add(PresentationForm.DOCUMENT_DIGITAL);

        // Create a child metadata.
        final DefaultResponsibleParty cited = new DefaultResponsibleParty();
        cited.setIndividualName("Galileo");
        cited.setRole(Role.ORIGINATOR);
        citation.getCitedResponsibleParties().add(cited);
        return citation;
    }

    /**
     * Runs the demo from the command line.
     *
     * @param args Command-line arguments (ignored).
     * @throws JAXBException If an error occurred while marshalling the metadata to XML.
     */
    public static void main(String[] args) throws JAXBException {
        final Citation citation = createHelloWorld();
        System.out.println("First, let see the metadata in a tabular format.");
        System.out.println("If you see strange characters, make sure that the System.out");
        System.out.println("encoding matches the console encoding of your operating system.");
        System.out.println(citation);
        System.out.println();
        System.out.println("Now, marshal to XML:");
        XML.marshal(citation, System.out);
    }
}
