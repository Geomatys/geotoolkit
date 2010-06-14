package org.geotoolkit.data.kml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.model.AtomFactory;
import org.geotoolkit.data.model.AtomFactoryDefault;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.xml.StaxStreamReader;
import static org.geotoolkit.data.model.AtomModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class AtomReader extends StaxStreamReader{

    private static final AtomFactory atomFactory = new AtomFactoryDefault();

    public AtomReader() {
        super();
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    public AtomPersonConstruct readAuthor() throws XMLStreamException {
        List<String> names = new ArrayList<String>();
        List<String> uris = new ArrayList<String>();
        List<String> emails = new ArrayList<String>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_ATOM.equals(eUri)) {
                        if (TAG_NAME.equals(eName)) {
                            names.add(reader.getElementText());
                        } else if (TAG_URI.equals(eName)) {
                            uris.add(reader.getElementText());
                        } else if (TAG_EMAIL.equals(eName)) {
                            emails.add(reader.getElementText());
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_AUTHOR.equals(reader.getLocalName()) && URI_ATOM.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return AtomReader.atomFactory.createAtomPersonConstruct(names, uris, emails);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    public AtomLink readLink() throws XMLStreamException {
        String href = reader.getAttributeValue(null, ATT_HREF);
        String rel = reader.getAttributeValue(null, ATT_REL);
        String type = reader.getAttributeValue(null, ATT_TYPE);
        String hreflang = reader.getAttributeValue(null, ATT_HREFLANG);
        String title = reader.getAttributeValue(null, ATT_TITLE);
        String length = reader.getAttributeValue(null, ATT_LENGTH);

        return AtomReader.atomFactory.createAtomLink(href, rel, type, hreflang, title, length);
    }

}
