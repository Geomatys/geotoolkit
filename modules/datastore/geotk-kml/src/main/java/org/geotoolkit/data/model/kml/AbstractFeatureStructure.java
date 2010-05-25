package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class AbstractFeatureStructure {

    public String name;
    public boolean visibility;
    public boolean open;
    public AtomPersonConstruct author;
    public AtomLink link;
    public String address;
    public AddressDetails addressDetails;
    public String phoneNumber;
    public String snippet;
    public String description;
    public AbstractView view;
    public AbstractTimePrimitive timePrimitive;
    public String styleUrl;
    public List<AbstractStyleSelector> styleSelector;
    public Region region;
    public ExtendedData extendedData;
    public List<SimpleType> featureSimpleExtensions;
    public List<AbstractObject> featureObjectExtensions;
}
