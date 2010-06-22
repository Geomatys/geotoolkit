package org.geotoolkit.data.xal.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLargeMailUserName implements LargeMailUserName{

    private final String content;
    private final String type;
    private final String code;

    /**
     * 
     * @param type
     * @param code
     * @param content
     */
    public DefaultLargeMailUserName(String type, String code, String content){
        this.content = content;
        this.type = type;
        this.code = code;
    }

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
    public String getCode() {return this.code;}

}
