package org.geotoolkit.ogcapi.model.coverage;

import jakarta.xml.bind.annotation.XmlRegistry;

/**
 * Object Factory for Coverage Response (OGC API Coverage)
 *
 * @author Quentin BIALOTA
 */
@XmlRegistry
public class ObjectFactory {

    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package: net.opengis.wfs
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DomainSet}
     *
     * @return
     */
    public DomainSet createDomainSet() {
        return new DomainSet();
    }

    /**
     * Create an instance of {@link DataRecord}
     *
     * @return
     */
    public DataRecord createDataRecord() {
        return new DataRecord();
    }
}
