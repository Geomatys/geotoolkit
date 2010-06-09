package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class BuildingNameDefault implements BuildingName {

    private final String type;
    private final AfterBeforeEnum typeOccurrence;
    private final GrPostal grPostal;
    private final String content;

    /**
     *
     * @param type
     * @param typeOccurrence
     * @param grPostal
     * @param content
     */
    public BuildingNameDefault(String type, AfterBeforeEnum typeOccurrence,
            GrPostal grPostal, String content){
        this.type = type;
        this.typeOccurrence = typeOccurrence;
        this.grPostal = grPostal;
        this.content = content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AfterBeforeEnum getTypeOccurrence() {return this.typeOccurrence;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getContent() {return this.content;}

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
