

package org.geotoolkit.pending.demo.filter.customaccessor;

import java.util.Date;

public class Pojo {

    private String family;
    private int depth;
    private Date birth;

    public Pojo(String family, int depth, Date birth) {
        this.family = family;
        this.depth = depth;
        this.birth = birth;
    }

    public Date getBirth() {
        return birth;
    }

    public int getDepth() {
        return depth;
    }

    public String getFamily() {
        return family;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setFamily(String family) {
        this.family = family;
    }

}
