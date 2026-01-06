package org.geotoolkit.ogcapi.marshaller;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;

/**
 * @author Quentin Bialota (Geomatys)
 * @author Guilhem Legal (Geomatys)
 */
public class CoverageResponseMarshallerPool {

    private static final MarshallerPool INSTANCE;

    static {
        try {
            INSTANCE = new MarshallerPool(JAXBContext.newInstance(
                    "org.geotoolkit.ogcapi.model.coverage"
            ), null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private CoverageResponseMarshallerPool() {
    }

    public static MarshallerPool getInstance() {
        return INSTANCE;
    }
}
