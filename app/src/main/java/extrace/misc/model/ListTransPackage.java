package extrace.misc.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

public class ListTransPackage implements Serializable {
    private static final long serialVersionUID = -4253960204603624324L;

    @Expose List<TransPackage> transPackageList;

    public List<TransPackage> getTransPackageList() {
        return transPackageList;
    }

    public void setTransPackageList(List<TransPackage> transPackageList) {
        this.transPackageList = transPackageList;
    }
}
