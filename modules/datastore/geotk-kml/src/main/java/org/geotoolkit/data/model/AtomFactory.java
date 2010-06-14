package org.geotoolkit.data.model;

import java.util.List;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;

/**
 *
 * @author Samuel Andrés
 */
public interface AtomFactory {

    /**
     *
     * @param href
     * @param rel
     * @param type
     * @param hreflang
     * @param title
     * @param length
     * @return
     */
    public AtomLink createAtomLink(String href, String rel, String type, String hreflang, String title, String length);

    /**
     * 
     * @param names
     * @param uris
     * @param emails
     * @return
     */
    public AtomPersonConstruct createAtomPersonConstruct(List<String> names, List<String> uris, List<String> emails);
}
