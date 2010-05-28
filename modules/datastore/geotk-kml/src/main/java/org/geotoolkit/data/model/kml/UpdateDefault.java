package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class UpdateDefault implements Update {

    private List<Create> creates;
    private List<Delete> deletes;
    private List<Change> changes;
    private List<Object> updateOpExtensions;
    private List<Object> updateExtensions;

    /**
     *
     * @param creates
     * @param deletes
     * @param changes
     * @param updateOpExtensions
     * @param updateExtensions
     */
    public UpdateDefault(List<Create> creates,
            List<Delete> deletes, List<Change> changes,
            List<Object> updateOpExtensions, List<Object> updateExtensions){
        this.creates = creates;
        this.deletes = deletes;
        this.changes = changes;
        this.updateOpExtensions = updateOpExtensions;
        this.updateExtensions = updateExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Create> getCreate() {return this.creates;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Delete> getDelete() {return this.deletes;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Change> getChange() {return this.changes;}

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
