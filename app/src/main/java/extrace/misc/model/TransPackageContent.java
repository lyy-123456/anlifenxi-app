package extrace.misc.model;

import java.io.Serializable;

public class TransPackageContent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2696910600791838998L;

	public TransPackageContent() {
	}

	private int SN;
	private ExpressSheet express;
	private TransPackage pkg;
	private int status;

	public void setSN(int value) {
		this.SN = value;
	}

	public int getSN() {
		return SN;
	}

	public int getORMID() {
		return getSN();
	}

	public void setExpress(ExpressSheet value) {
		this.express = value;
	}

	public ExpressSheet getExpress() {
		return express;
	}

	public void setPkg(TransPackage value) {
		this.pkg = value;
	}

	public TransPackage getPkg() {
		return pkg;
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
			return String.valueOf(getSN());
		}
		else {
			StringBuffer sb = new StringBuffer();
			sb.append("TransPackageContent[ ");
			sb.append("SN=").append(getSN()).append(" ");
			if (getExpress() != null)
				sb.append("Express.Persist_ID=").append(getExpress().toString(true)).append(" ");
			else
				sb.append("Express=null ");
			if (getPkg() != null)
				sb.append("Pkg.Persist_ID=").append(getPkg().toString(true)).append(" ");
			else
				sb.append("Pkg=null ");
			sb.append("Status=").append(getStatus()).append(" ");
			sb.append("]");
			return sb.toString();
		}
	}

	public static final class STATUS{
		public static final int STATUS_ACTIVE = 0;
		public static final int STATUS_OUTOF_PACKAGE = 1;
		public static final int STATUS_LOST = 2;
		public static final int STATUS_DAMAGED = 3;
	}
}
