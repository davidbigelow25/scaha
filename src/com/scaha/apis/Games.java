package com.scaha.apis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.apis.ApisAuthentication;
import org.primefaces.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Games extends HttpServlet {

    private static final JSONArray HTTP = null;
    transient private ResultSet rs = null;
    public static String c_sp_profile = "Call scaha.profile(?,?)";

    /*private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());*/

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        response.getOutputStream().println("do get worked");

    }



    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //lets pull values from the requesting objects clubid, optional teamid, optional year

        ObjectMapper readobjectMapper = new ObjectMapper();

        JsonNode jsonNode = readobjectMapper.readTree(request.getReader());
        Integer scheduleid = 0;
        if (jsonNode!=null){
            if (jsonNode.get("ScheduleID")!=null){
                scheduleid=jsonNode.get("ScheduleID").asInt();
            }
        }

        ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

        //first lets get the username password and then pass those in to the db for
        // authenticate the request.  if successfull continue on otherwise return response not authorized.
        ApisAuthentication apisa = new ApisAuthentication();
        Boolean authenticated = apisa.AuthenticateRequest(request,db,rs);

        //lets get the list of clubs for the current season.  The method doesn't need to know what the current season
        //is, the stored procedure will look it up and then retrieve the list.

        ObjectMapper objectMapper = new ObjectMapper();
        List<ObjectNode> templist = new ArrayList<ObjectNode>();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        try{
            if (!authenticated){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Credentials");
            };

            CallableStatement cs = db.prepareCall("CALL scaha.getSCAHAGamesBySchedule(?)");

            cs.setInt("scheduleid",scheduleid);
            rs = cs.executeQuery();

            if (rs != null){

                while (rs.next()) {
                    ObjectNode object = objectMapper.createObjectNode();
                    object.put ("GameID", rs.getInt("idlivegame"));
                    object.put( "HomeTeamID", rs.getInt("hometeam"));
                    object.put( "AwayTeamID", rs.getInt("awayteam"));
                    object.put( "ScheduleID", rs.getInt("idschedule"));
                    object.put( "GameType", rs.getString("typetag"));
                    object.put( "Status", rs.getString("status"));
                    object.put( "Venue", rs.getString("location"));
                    object.put( "StartDate", rs.getString("date"));
                    object.put( "StartTime", rs.getString("time"));
                    object.put( "HomeScore", rs.getString("homescore"));
                    object.put( "AwayScore", rs.getString("awayscore"));
                    object.put( "OTInfo", rs.getString("otinfo"));
                    object.put( "OTWinner", rs.getString("otwinner"));
                    object.put( "Sheetname", rs.getString("sheetname"));
                    templist.add(object);

                    object = null;

                }

            }


            rs.close();
            db.cleanup();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            /*LOGGER.info("ERROR IN getting suspension list");*/
            e.printStackTrace();
            db.rollback();
            db.free();
        } finally {
            //
            // always clean up after yourself..
            //
            db.free();
        }

        arrayNode.addAll(templist);
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);

        response.getOutputStream().println(json);


    }


}
