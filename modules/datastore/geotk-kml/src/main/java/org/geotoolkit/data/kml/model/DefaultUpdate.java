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
    private List<Create> creates;
    private List<Delete> deletes;
    private List<Change> changes;
    @Deprecated
    private List<AbstractFeature> replaces;
    private List<Object> updateOpExtensions;
    private List<Object> updateExtensions;

    public DefaultUpdate(){
        this.creates = EMPTY_LIST;
        this.deletes = EMPTY_LIST;
        this.changes = EMPTY_LIST;
        this.replaces = EMPTY_LIST;
        this.updateOpExtensions = EMPTY_LIST;
        this.updateExtensions = EMPTY_LIST;
    }

    /**
     * 
     * @param targetHref
     * @param creates
     * @param deletes
     * @param changes
     * @param replaces
     * @param updateOpExtensions
     * @param updateExtensions
     */
    public DefaultUpdate(URI targetHref, List<Create> creates,
            List<Delete> deletes, List<Change> changes,
            List<AbstractFeature> replaces,
            List<Object> updateOpExtensions, List<Object> updateExtensions) {
        this.targetHref = targetHref;
        this.creates = (creates == null) ? EMPTY_LIST : creates;
        this.deletes = (deletes == null) ? EMPTY_LIST : deletes;
        this.changes = (changes == null) ? EMPTY_LIST : changes;
        this.replaces = (replaces == null) ? EMPTY_LIST : replaces;
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
    public List<Create> getCreates() {
        return this.creates;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Delete> getDeletes() {
        return this.deletes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Change> getChanges() {
        return this.changes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    @Deprecated
    public List<AbstractFeature> getReplaces() {
        return this.replaces;
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
    public void setCreates(List<Create> creates) {
        this.creates = creates;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDeletes(List<Delete> deletes) {
        this.deletes = deletes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }
    
    /**
     *
     * @{@inheritDoc }
     */
    @Override
    @Deprecated
    public void setReplaces(List<AbstractFeature> replaces) {
        this.replaces = replaces;
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
