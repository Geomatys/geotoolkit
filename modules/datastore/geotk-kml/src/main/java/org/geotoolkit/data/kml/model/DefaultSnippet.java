package org.geotoolkit.data.kml.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultSnippet implements Snippet {

    private final int maxLines;
    private final Object content;

    /**
     *
     * @param maxLines
     * @param content
     */
    public DefaultSnippet(int maxLines, Object content) {
        this.maxLines = maxLines;
        this.content = content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public int getMaxLines() {
        return this.maxLines;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Object getContent() {
        return this.content;
    }
}
