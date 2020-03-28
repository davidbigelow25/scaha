package com.scaha.objects;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
;

public class MemberList extends ListDataModel<Member> implements Serializable, SelectableDataModel<Member> {
	
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	

	public MemberList() {  
    }  
	
	/**
	 * 
	 * @param data
	 */
    public MemberList(List<Member> data) {  
        super(data);  
    }  

	
	public static MemberList NewBoardmemberListFactory(ScahaDatabase _db,String sqlparam) throws SQLException {
		
		//LOGGER.info ("Getting list of board members");

		List<Member> data = new ArrayList<Member>();
	
		PreparedStatement ps = _db.prepareStatement(sqlparam);
		
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
				Member mem = new Member();
				mem.setMembername(rs.getString("membername"));
				mem.setMemberemail(rs.getString("memberemail"));
				mem.setMemberrole(rs.getString("memberrole"));
				mem.setMemberrolecode(rs.getString("memberrolecode"));
				mem.setMemberphone(rs.getString("phonenumber"));
				mem.setMemberaddress(rs.getString("memberaddress"));
				mem.setRenderaddress(rs.getBoolean("renderaddress"));
				mem.setRenderphone(rs.getBoolean("renderphone"));
				mem.setProfileid(rs.getString("idbod"));
				if (rs.getString("renderaddress").equals("1")){
					mem.setActionrenderaddress("0");
					mem.setActionrenderaddressmessage("Hide Address");
				} else {
					mem.setActionrenderaddress("1");
					mem.setActionrenderaddressmessage("Show Address");
				}
				if (rs.getString("renderphone").equals("1")){
					mem.setActionrenderphoine("0");
					mem.setActionrenderphonemessage("Hide Phone");
				}else{
					mem.setActionrenderphoine("1");
					mem.setActionrenderphonemessage("Show Phone");
				}
				
				data.add(mem);
		}
		
		rs.close();
		ps.close();

		return new MemberList(data);
	}

	
	
    @Override  
    public Member getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        @SuppressWarnings("unchecked")
		List<Member> results = (List<Member>) getWrappedData();  
          
        for(Member result : results) {  
            if(Integer.toString(result.ID).equals(rowKey))  
                return result;  
        }  
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Member result) {  
        return Integer.toString(result.ID);
    }

}
