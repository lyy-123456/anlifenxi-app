/**
 * "Visual Paradigm: DO NOT MODIFY THIS FILE!"
 * 
 * This is an automatic generated file. It will be regenerated every time 
 * you generate persistence class.
 * 
 * Modifying its content may cause the program not work, or your work may lost.
 */

/**
 * Licensee: 
 * License Type: Evaluation
 */
package extrace.misc.model;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.Expose;

public class TransPackage  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3050390478904210174L;
	public static final int PKG_NEW = 0;  //新建
	public static final int PKG_PACKED = 1; //已打包
	public static final int PKG_TRSNSIT = 2; //运输中
	//public static final int PKG_ACCED = 3; //转运中心（已扫描）
	//public static final int PKG_ACHIEVED = 3; //以达到achieved
	public static final int PKG_UNPACKED = 4;  //已拆包

	public TransPackage() {
	}
	
	@Expose private String ID;
	@Expose private String sourceNode;
	@Expose private String targetNode;
	@Expose private Date createTime;
	@Expose private int status;
	
	public void setID(String value) {
		this.ID = value;
	}
	
	public String getID() {
		return ID;
	}
	
	public String getORMID() {
		return getID();
	}
	
	public void setSourceNode(String value) {
		this.sourceNode = value;
	}
	
	public String getSourceNode() {
		return sourceNode;
	}
	
	public void setTargetNode(String value) {
		this.targetNode = value;
	}
	
	public String getTargetNode() {
		return targetNode;
	}
	
	public void setCreateTime(Date value) {
		this.createTime = value;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setStatus(int value) {
		this.status = value;
	}
	
	public int getStatus() {
		return status;
	}
		
	public String toString() {
		return toString(false);
	}
	
	public String toString(boolean idOnly) {
		if (idOnly) {
			return String.valueOf(getID());
		}
		else {
			StringBuffer sb = new StringBuffer();
			sb.append("TransPackage[ ");
			sb.append("ID=").append(getID()).append(" ");
			sb.append("SourceNode=").append(getSourceNode()).append(" ");
			sb.append("TargetNode=").append(getTargetNode()).append(" ");
			sb.append("CreateTime=").append(getCreateTime()).append(" ");
			sb.append("Status=").append(getStatus()).append(" ");
			sb.append("]");
			return sb.toString();
		}
	}
	
	private boolean _saved = false;
	
	public void onSave() {
		_saved=true;
	}
	
	
	public void onLoad() {
		_saved=true;
	}
	
	
	public boolean isSaved() {
		return _saved;
	}


	public static  String getPackageStatus(int status){
		StringBuffer sb = new StringBuffer();
		switch (status){
			case TransPackage.PKG_NEW:
				sb.append("新建");
				break;
			case TransPackage.PKG_PACKED:
				sb.append("已打包");
				break;
			case TransPackage.PKG_TRSNSIT:
				sb.append("运输中");
				break;
			case TransPackage.PKG_UNPACKED:
				sb.append("已拆包");
				break;
		}
		return  sb.toString();
	}
	
}
