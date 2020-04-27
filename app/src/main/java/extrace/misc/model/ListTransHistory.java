package extrace.misc.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

public class ListTransHistory implements Serializable {
    private static final long serialVersionUID = 4941503468985892397L;
    public ListTransHistory(){

    }
    @Expose  private List<TransHistory> transHistoryList;


    public List<TransHistory> getTransHistoryList() {
        return transHistoryList;
    }

    public void setTransHistoryList(List<TransHistory> transHistoryList) {
        this.transHistoryList = transHistoryList;
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
            if(transHistoryList != null){
                for(TransHistory transHistory:transHistoryList){
                    sb.append(transHistory.toString());
                }
            }
            return sb.toString();
        }

    }
}
