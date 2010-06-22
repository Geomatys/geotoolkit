package org.geotoolkit.data.xal.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultSortingCode implements SortingCode {

    private final String type;
    private final GrPostal grPostal;

    /**
     * 
     * @param type
     * @param grPostal
     */
    public DefaultSortingCode(String type, GrPostal grPostal){
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
