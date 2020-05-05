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
    public String toString() {
        return toString(false);
    }
    private String toString(boolean b) {

        if(b) {
            return "null";
        }
        else {
            StringBuffer sb = new StringBuffer();
            if(transPackageList != null){
                for(TransPackage transPackage:transPackageList){
                    sb.append(transPackage.toString());
                }
            }
            return sb.toString();
        }

    }

}
