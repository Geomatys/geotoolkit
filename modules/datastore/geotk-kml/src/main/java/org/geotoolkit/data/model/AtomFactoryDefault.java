package org.geotoolkit.data.model;

import java.util.List;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.atom.AtomLinkDefault;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.atom.AtomPersonConstructDefault;

/**
 *
 * @author Samuel Andrés
 */
public class AtomFactoryDefault implements AtomFactory{

    /**
     * 
     * @{@inheritDoc }
     */
    @Override
    public AtomLink createAtomLink(String href, String rel, String type, String hreflang, String title, String length) {
        return new AtomLinkDefault(href, rel, type, hreflang, title, length);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomPersonConstruct createAtomPersonConstruct(List<String> names, List<String> uris, List<String> emails) {
        return new AtomPersonConstructDefault(names, uris, emails);
    }

}
