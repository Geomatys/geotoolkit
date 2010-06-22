package org.geotoolkit.data.xal.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPostBoxNumberExtension implements PostBoxNumberExtension {

    private final String content;
    private final String numberExtensionSeparator;

    /**
     * 
     * @param numberExtensionSeparator
     * @param content
     */
    public DefaultPostBoxNumberExtension(String numberExtensionSeparator, String content){
        this.content = content;
        this.numberExtensionSeparator = numberExtensionSeparator;
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
    public String getNumberExtensionSeparator() {return this.numberExtensionSeparator;}

}
