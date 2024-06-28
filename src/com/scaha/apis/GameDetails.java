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


public class GameDetails extends HttpServlet {

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
        Integer gameid =jsonNode.get("GameID").asInt();

        ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

        //first lets get GameDetailsthe username password and then pass those in to the db for
        // authenticate the request.  if successfull continue on otherwise return response not authorized.
        ApisAuthentication apisa = new ApisAuthentication();
        Boolean authenticated = apisa.AuthenticateRequest(request,db,rs);

        //lets get the list of clubs for the current season.  The method doesn't need to know what the current season
        //is, the stored procedure will look it up and then retrieve the list.

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode object2 = objectMapper.createObjectNode();



        try{
            if (!authenticated){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Credentials");
            };
            //get basic game details first
            CallableStatement cs = db.prepareCall("CALL scaha.getSCAHAGameDetail(?)");
            cs.setInt("GameID",gameid);
            rs = cs.executeQuery();

            if (rs != null){

                while (rs.next()) {
                    Integer HomeTeamID = rs.getInt("idteamhome");
                    Integer AwayTeamID = rs.getInt("idteamaway");
                    object2.put("HomeTeamID",HomeTeamID);
                    object2.put("AwayTeamID",AwayTeamID);

                }

            }
            rs.close();

            //this adds players from both teams to the tree
            cs = db.prepareCall("CALL scaha.getSCAHAGameRosters(?)");
            cs.setInt("GameID",gameid);
            rs = cs.executeQuery();
            ArrayNode arrayPlayer = objectMapper.createArrayNode();
            object2.put("Players",arrayPlayer);
            List<ObjectNode> templist = new ArrayList<ObjectNode>();

            if (rs != null){

                while (rs.next()) {
                    ObjectNode object = objectMapper.createObjectNode();
                    object.put ("PlayerID", rs.getInt("playerid"));
                    object.put( "RosterID", rs.getInt("idroster"));
                    object.put( "TeamID", rs.getInt("idteam"));
                    object.put( "HomeAwayFlag", rs.getString("homeawayflag"));
                    object.put( "FirstName", rs.getString("fname"));
                    object.put( "LastName", rs.getString("lname"));
                    object.put( "JerseyNumber", rs.getString("jerseynumber"));

                    templist.add(object);

                    object = null;

                }

                arrayPlayer.addAll(templist);
                templist.clear();
            }
            rs.close();

            //this adds goals for both teams to the tree.
            cs = db.prepareCall("CALL scaha.getSCAHAGameGoals(?)");
            cs.setInt("GameID",gameid);
            rs = cs.executeQuery();
            ArrayNode arrayGoals = objectMapper.createArrayNode();
            object2.put("Goals",arrayGoals);

            if (rs != null){

                while (rs.next()) {
                    ObjectNode object = objectMapper.createObjectNode();
                    object.put ("GoalID", rs.getInt("idscoring"));
                    object.put( "TeamID", rs.getInt("idteam"));
                    object.put( "Time", rs.getString("timescored"));
                    object.put( "Period", rs.getInt("period"));
                    object.put( "Scoredby", rs.getInt("idroster_goal"));
                    object.put( "Assist1by", rs.getInt("idroster_a1"));
                    object.put( "Assist2by", rs.getInt("idroster_a2"));
                    object.put( "GoalType", rs.getString("GoalType"));

                    templist.add(object);

                    object = null;

                }

                arrayGoals.addAll(templist);
                templist.clear();
            }
            rs.close();

            //this section is for populating the goalie information.
            cs = db.prepareCall("CALL scaha.getSCAHAGameGoalieInfo(?)");
            cs.setInt("GameID",gameid);
            rs = cs.executeQuery();
            ArrayNode arrayGoalies = objectMapper.createArrayNode();
            object2.put("Goalies",arrayGoalies);

            if (rs != null){

                while (rs.next()) {
                    ObjectNode object = objectMapper.createObjectNode();
                    object.put ("GoalieID", rs.getInt("idsog"));
                    object.put( "RosterID", rs.getInt("idroster"));
                    object.put( "TeamID", rs.getInt("idteam"));
                    object.put( "MinsPlayed", rs.getString("timeplayed"));
                    object.put( "Period1Shots", rs.getInt("period1shots"));
                    object.put( "Period2Shots", rs.getInt("period2shots"));
                    object.put( "Period3Shots", rs.getInt("period3shots"));
                    object.put( "OTShots", rs.getInt("otshots"));
                    
                    templist.add(object);

                    object = null;

                }

                arrayGoalies.addAll(templist);
                templist.clear();
            }
            rs.close();


            cs = db.prepareCall("CALL scaha.getSCAHAGamePenalties(?)");
            cs.setInt("GameID",gameid);
            rs = cs.executeQuery();
            ArrayNode arrayPenalties = objectMapper.createArrayNode();
            object2.put("Penalties",arrayPenalties);

            if (rs != null){

                while (rs.next()) {
                    ObjectNode object = objectMapper.createObjectNode();
                    object.put ("PenaltyID", rs.getInt("idpenalty"));
                    object.put( "TeamID", rs.getInt("idteam"));
                    object.put( "RosterID", rs.getInt("idroster"));
                    object.put( "Description", rs.getString("penaltytype"));
                    object.put( "Length", rs.getString("length"));
                    object.put( "TimeofPenalty", rs.getString("timeofpenalty"));
                    object.put( "IsGameMisconduct", rs.getInt("gamemisconduct"));
                    object.put( "IsMatch", rs.getInt("matchpenalty"));

                    templist.add(object);

                    object = null;

                }

                arrayPenalties.addAll(templist);
                templist.clear();
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

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object2);

        response.getOutputStream().println(json);


    }


}
