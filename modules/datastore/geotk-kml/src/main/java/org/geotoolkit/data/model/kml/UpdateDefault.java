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

    public UpdateDefault(List<Create> creates,
            List<Delete> deletes, List<Change> changes,
            List<Object> updateOpExtensions, List<Object> updateExtensions){
        this.creates = creates;
        this.deletes = deletes;
        this.changes = changes;
        this.updateOpExtensions = updateOpExtensions;
        this.updateExtensions = updateExtensions;
    }

    @Override
    public List<Create> getCreate() {return this.creates;}

    @Override
    public List<Delete> getDelete() {return this.deletes;}

    @Override
    public List<Change> getChange() {return this.changes;}

    @Override
    public List<Object> getUpdateOpExtensions() {return this.updateOpExtensions;}

    @Override
    public List<Object> getUpdateExtensions() {return this.updateExtensions;}

}
