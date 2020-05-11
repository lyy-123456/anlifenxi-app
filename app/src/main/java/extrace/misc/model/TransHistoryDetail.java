package extrace.misc.model;

import java.io.Serializable;
import java.util.Comparator;

public class TransHistoryDetail implements Serializable,Comparable<TransHistoryDetail>{
    private ExpressSheet expressSheet;
    private TransHistory transHistory;
    private  UserInfo UIDFrom;
    private  UserInfo UIDTo;
    private TransNode fromNode;
    private TransNode toNode;

    public void setExpressSheet(ExpressSheet expressSheet) {
        this.expressSheet = expressSheet;
    }

    public ExpressSheet getExpressSheet() {
        return expressSheet;
    }

    public void setTransHistory(TransHistory transHistory) {
        this.transHistory = transHistory;
    }

    public void setUIDFrom(UserInfo UIDFrom) {
        this.UIDFrom = UIDFrom;
    }

    public void setUIDTo(UserInfo UIDTo) {
        this.UIDTo = UIDTo;
    }

    public TransHistory getTransHistory() {
        return transHistory;
    }

    public UserInfo getUIDFrom() {
        return UIDFrom;
    }

    public UserInfo getUIDTo() {
        return UIDTo;
    }

    public void setFromNode(TransNode fromNode) {
        this.fromNode = fromNode;
    }

    public void setToNode(TransNode toNode) {
        this.toNode = toNode;
    }

    public TransNode getFromNode() {
        return fromNode;
    }

    public TransNode getToNode() {
        return toNode;
    }

    @Override
    public int compareTo(TransHistoryDetail o) {
        if(this.getTransHistory().getActTime().before(o.getTransHistory().getActTime()) ){
            return -1;
        }
        return 1;
    }
}
