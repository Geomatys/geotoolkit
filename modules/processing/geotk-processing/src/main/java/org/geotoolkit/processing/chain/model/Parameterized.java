
package org.geotoolkit.processing.chain.model;

import java.util.List;

/**
 *
 * @author Johann Sorel
 */
public interface Parameterized {

    public List<Parameter> getInputs();
    public void setInputs(List<Parameter> outputs);

    public List<Parameter> getOutputs();
    public void setOutputs(List<Parameter> outputs);

}
