package org.geotoolkit.wms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.storage.AbstractResource;
import org.geotoolkit.storage.Resource;
import org.geotoolkit.wms.xml.AbstractLayer;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class WMSAggregate extends AbstractResource implements Aggregate {

    private final List<Resource> children;

    public WMSAggregate(final WebMapClient client, final AbstractLayer layer) throws CoverageStoreException {
        super(Names.createLocalName(null, ":", layer.getName() == null? "anonymous" : layer.getName()));

        final ArrayList tmp = new ArrayList<>();
        for (final AbstractLayer child : layer.getLayer()) {
            client.asResource(child)
                    .ifPresent(tmp::add);
        }

        children = Collections.unmodifiableList(tmp);
    }

    @Override
    public Collection<org.apache.sis.storage.Resource> components() throws DataStoreException {
        return (Collection) children;
    }
}
