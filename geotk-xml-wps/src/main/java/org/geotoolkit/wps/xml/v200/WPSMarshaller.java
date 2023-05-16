package org.geotoolkit.wps.xml.v200;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.stream.XMLEventWriter;
import org.apache.sis.util.Version;
import org.geotoolkit.ows.xml.AbstractCapabilitiesBase;
import org.geotoolkit.util.Versioned;
import org.geotoolkit.wps.xml.MarshallerProxy;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class WPSMarshaller extends MarshallerProxy {

    public WPSMarshaller(Marshaller wrapped) {
        super(wrapped);
    }

    protected void checkElement(Object jaxbElement) {
        if (jaxbElement instanceof JAXBElement) {
            jaxbElement = ((JAXBElement)jaxbElement).getValue();
        }

        Version version = null;
        if (jaxbElement instanceof DocumentBase) {
            version = ((DocumentBase) jaxbElement).getVersion();
        } else if (jaxbElement instanceof AbstractCapabilitiesBase) {
            version = new Version(((AbstractCapabilitiesBase)jaxbElement).getVersion());
        } else if (jaxbElement instanceof Versioned) {
            version = ((Versioned)jaxbElement).getVersion();
        }

        if (version != null) {
            if (version.getMajor().equals(2)) {
                FilterByVersion.IS_LEGACY.set(Boolean.FALSE);
            } else if (version.getMajor().equals(1)) {
                FilterByVersion.IS_LEGACY.set(Boolean.TRUE);
            }
        }
    }

    @Override
    public void marshal(Object jaxbElement, XMLEventWriter writer) throws JAXBException {
        try {
            checkElement(jaxbElement);
            super.marshal(jaxbElement, new TransformingWriter(writer));
        } finally {
            FilterByVersion.IS_LEGACY.remove();
        }
    }
}
