package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class SortingCodeDefault implements SortingCode {

    private final String type;
    private final GrPostal grPostal;

    /**
     * 
     * @param type
     * @param grPostal
     */
    public SortingCodeDefault(String type, GrPostal grPostal){
        this.type = type;
        this.grPostal = grPostal;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {return this.type;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}

}
