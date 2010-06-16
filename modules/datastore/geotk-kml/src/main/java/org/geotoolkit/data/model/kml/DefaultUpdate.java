package org.geotoolkit.data.model.kml;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultUpdate implements Update {

    private final List<Create> creates;
    private final List<Delete> deletes;
    private final List<Change> changes;
    private final List<Object> updateOpExtensions;
    private final List<Object> updateExtensions;

    /**
     *
     * @param creates
     * @param deletes
     * @param changes
     * @param updateOpExtensions
     * @param updateExtensions
     */
    public DefaultUpdate(List<Create> creates,
            List<Delete> deletes, List<Change> changes,
            List<Object> updateOpExtensions, List<Object> updateExtensions){
        this.creates = (creates == null) ? EMPTY_LIST : creates;
        this.deletes = (deletes == null) ? EMPTY_LIST : deletes;
        this.changes = (changes == null) ? EMPTY_LIST : changes;
        this.updateOpExtensions = (updateOpExtensions == null) ? EMPTY_LIST : updateOpExtensions;
        this.updateExtensions = (updateExtensions == null) ? EMPTY_LIST : updateExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Create> getCreates() {return this.creates;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Delete> getDeletes() {return this.deletes;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Change> getChanges() {return this.changes;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getUpdateOpExtensions() {return this.updateOpExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getUpdateExtensions() {return this.updateExtensions;}

}
