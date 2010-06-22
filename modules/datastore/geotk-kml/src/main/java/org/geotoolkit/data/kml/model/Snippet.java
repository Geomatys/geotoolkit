package org.geotoolkit.data.kml.model;

/**
 * <p>This interface maps SnippetType type.</p>
 *
 * <pre>
 * &lt;complexType name="SnippetType" final="#all">
 *  &lt;simpleContent>
 *      &lt;extension base="string">
 *          &lt;attribute name="maxLines" type="int" use="optional" default="2"/>
 *      &lt;/extension>
 *  &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Snippet {

    /**
     *
     * @return
     */
    public int getMaxLines();

    /**
     * 
     * @return
     */
    public String getContent();

}
