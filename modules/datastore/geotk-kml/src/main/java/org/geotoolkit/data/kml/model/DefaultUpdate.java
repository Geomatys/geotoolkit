package org.geotoolkit.data.kml.model;

import java.net.URI;
import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultUpdate implements Update {

    private URI targetHref;
    private List<Object> updates;
    private List<Object> updateOpExtensions;
    private List<Object> updateExtensions;

    public DefaultUpdate(){
        this.updates = EMPTY_LIST;
        this.updateOpExtensions = EMPTY_LIST;
        this.updateExtensions = EMPTY_LIST;
    }

    /**
     * 
     * @param targetHref
     * @param updates
     * @param updateOpExtensions
     * @param updateExtensions
     */
    public DefaultUpdate(URI targetHref, List<Object> updates,
            List<Object> updateOpExtensions, List<Object> updateExtensions) {
        this.targetHref = targetHref;
        this.updates = (updates == null) ? EMPTY_LIST : updates;
        this.updateOpExtensions = (updateOpExtensions == null) ? EMPTY_LIST : updateOpExtensions;
        this.updateExtensions = (updateExtensions == null) ? EMPTY_LIST : updateExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public URI getTargetHref() {
        return this.targetHref;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getUpdates() {
        return this.updates;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getUpdateOpExtensions() {
        return this.updateOpExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getUpdateExtensions() {
        return this.updateExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTargetHref(URI targetHref) {
        this.targetHref = targetHref;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setUpdates(List<Object> updates) {
        this.updates = updates;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setUpdateOpExtensions(List<Object> updateOpEXtensions) {
        this.updateOpExtensions = updateOpEXtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setUpdateExtensions(List<Object> updateExtensions) {
        this.updateExtensions = updateExtensions;
    }

}
