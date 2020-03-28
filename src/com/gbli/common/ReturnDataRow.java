/**
 * 
 */
package com.gbli.common;

import java.util.HashMap;
import java.util.Vector;


/**
 * @author dbigelow
 *
 */
@SuppressWarnings("rawtypes")
public class ReturnDataRow extends Vector {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String rowkey = null;
	String colm1 = null;
	String col2 = null;
	String col3 = null;
	String col4 = null;
	String col5 = null;
	String col6 = null;
	String col7 = null;
	String col8 = null;
	String col9 = null;
	String col10 = null;

	private ReturnDataResultSet rdrs = null;
	
	HashMap <String,Integer>hdrmap = new HashMap<String,Integer>();
	
	public ReturnDataRow (int _i, ReturnDataResultSet _rdrs) {
		super();
		rowkey = Integer.toString(_i);
		rdrs = _rdrs;
	}
	
	public ReturnDataRow (int _i) {
		this(_i, null);
	}


	@SuppressWarnings("unchecked")
	public boolean add(Object _obj, String _strRowKey) {
		this.hdrmap.put(_strRowKey, new Integer(this.size()));
		super.add(_obj);
		switch ( this.size()) {
		case 1:
			this.colm1 = _obj.toString();
			break;
		case 2:
			this.col2 = _obj.toString();
			break;
		case 3:
			this.col3 = _obj.toString();
			break;
		case 4:
			this.col4 = _obj.toString();
			break;
		case 5:
			this.col5 = _obj.toString();
			break;
		case 6:
			this.col6 = _obj.toString();
			break;
		case 7:
			this.col7 = _obj.toString();
			break;
		case 8:
			this.col8 = _obj.toString();
			break;
		case 9:
			this.col9 = _obj.toString();
			break;
		case 10:
			this.col10 = _obj.toString();
			break;
		}
		return true;
	}
	
	public Object getByKey(String _str) {
		Integer index = this.hdrmap.get(_str);
		if (index == null) return null;
		return get(index.intValue());
		
	}
	/**
	 * @return the rowkey
	 */
	public String getRowkey() {
		return rowkey;
	}

	/**
	 * @param rowkey the rowkey to set
	 */
	public void setRowkey(String rowkey) {
		this.rowkey = rowkey;
	}

	/**
	 * @return the col1
	 */
	public String getColm1() {
		return colm1;
	}

	/**
	 * @param col1 the col1 to set
	 */
	public void setColm1(String col1) {
		this.colm1 = col1;
	}

	/**
	 * @return the col2
	 */
	public String getCol2() {
		return col2;
	}

	/**
	 * @param col2 the col2 to set
	 */
	public void setCol2(String col2) {
		this.col2 = col2;
	}

	/**
	 * @return the col3
	 */
	public String getCol3() {
		return col3;
	}

	/**
	 * @param col3 the col3 to set
	 */
	public void setCol3(String col3) {
		this.col3 = col3;
	}

	/**
	 * @return the col4
	 */
	public String getCol4() {
		return col4;
	}

	/**
	 * @param col4 the col4 to set
	 */
	public void setCol4(String col4) {
		this.col4 = col4;
	}

	/**
	 * @return the col5
	 */
	public String getCol5() {
		return col5;
	}

	/**
	 * @param col5 the col5 to set
	 */
	public void setCol5(String col5) {
		this.col5 = col5;
	}

	/**
	 * @return the col6
	 */
	public String getCol6() {
		return col6;
	}

	/**
	 * @param col6 the col6 to set
	 */
	public void setCol6(String col6) {
		this.col6 = col6;
	}

	/**
	 * @return the col7
	 */
	public String getCol7() {
		return col7;
	}

	/**
	 * @param col7 the col7 to set
	 */
	public void setCol7(String col7) {
		this.col7 = col7;
	}

	/**
	 * @return the col8
	 */
	public String getCol8() {
		return col8;
	}

	/**
	 * @param col8 the col8 to set
	 */
	public void setCol8(String col8) {
		this.col8 = col8;
	}

	/**
	 * @return the col9
	 */
	public String getCol9() {
		return col9;
	}

	/**
	 * @param col9 the col9 to set
	 */
	public void setCol9(String col9) {
		this.col9 = col9;
	}

	/**
	 * @return the col10
	 */
	public String getCol10() {
		return col10;
	}

	/**
	 * @param col10 the col10 to set
	 */
	public void setCol10(String col10) {
		this.col10 = col10;
	}

	/**
	 * @return the rdrs
	 */
	public ReturnDataResultSet getRdrs() {
		return rdrs;
	}

	/**
	 * @param rdrs the rdrs to set
	 */
	public void setRdrs(ReturnDataResultSet rdrs) {
		this.rdrs = rdrs;
	}
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		for (Object obj : this) {
			sb.append(obj.toString() + ", ");
		}
		
		return sb.toString();

	}
	
	public String toClassString() {
		StringBuffer sb = new StringBuffer();
		
		for (Object obj : this) {
			sb.append(obj.getClass() + ", ");
		}
		
		return sb.toString();

	}

}
