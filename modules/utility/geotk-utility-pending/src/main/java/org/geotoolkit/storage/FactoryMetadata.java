package org.geotoolkit.storage;

/**
 * Base class for {@link org.geotoolkit.storage.DataStoreFactory} metadata. It should be retrived via {@link DataStoreFactory#getMetadata()}.
 *
 * @author Alexis Manin (Geomatys)
 */
public interface FactoryMetadata {

    public DataType getDataType();
}
