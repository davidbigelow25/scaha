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
import java.util.Iterator;
import java.util.List;


public class SubmitGameDetails extends HttpServlet {

    private static final JSONArray HTTP = null;
    transient private ResultSet rs = null;
    public static String c_sp_profile = "Call scaha.profile(?,?)";
    public Integer gameid = 0;

    /*private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());*/

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        response.getOutputStream().println("do get worked");

    }



    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

        //first lets get the username password and then pass those in to the db for
        // authenticate the request.  if successfull continue on otherwise return response not authorized.
        ApisAuthentication apisa = new ApisAuthentication();
        Boolean authenticated = apisa.AuthenticateRequest(request,db,rs);

        //lets pull values from the requesting objects game id, players, goals, goalies, and penalties

        ObjectMapper readobjectMapper = new ObjectMapper();

        //need to iterate through all of the nodes to retrieve game details
        JsonNode jsonNode = readobjectMapper.readTree(request.getReader());
        traverse(jsonNode,db);


        //now lets load the saved game results and return to calling system with record id's
        ObjectMapper returnobjectMapper = new ObjectMapper();
        ObjectNode object2 = returnobjectMapper.createObjectNode();

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
                    object2.put("GameID",this.gameid);
                }

            }
            rs.close();

            //need to return all the players with indicator did they play or not
            cs = db.prepareCall("CALL scaha.getSCAHAGameMIA(?)");
            cs.setInt("GameID",this.gameid);
            rs = cs.executeQuery();
            ArrayNode arrayPlayer = returnobjectMapper.createArrayNode();
            object2.put("Players",arrayPlayer);
            List<ObjectNode> templist = new ArrayList<ObjectNode>();

            if (rs != null){

                while (rs.next()) {
                    ObjectNode object = returnobjectMapper.createObjectNode();
                    object.put( "RosterID", rs.getInt("idroster"));
                    object.put( "DidPlay", rs.getInt("didplay"));

                    templist.add(object);

                    object = null;

                }

                arrayPlayer.addAll(templist);
                templist.clear();
            }
            rs.close();

            //this adds goals for both teams to the tree.
            cs = db.prepareCall("CALL scaha.getSCAHAGameGoals(?)");
            cs.setInt("GameID",this.gameid);
            rs = cs.executeQuery();
            ArrayNode arrayGoals = returnobjectMapper.createArrayNode();
            object2.put("Goals",arrayGoals);

            if (rs != null){

                while (rs.next()) {
                    ObjectNode object = returnobjectMapper.createObjectNode();
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
            cs.setInt("GameID",this.gameid);
            rs = cs.executeQuery();
            ArrayNode arrayGoalies = returnobjectMapper.createArrayNode();
            object2.put("Goalies",arrayGoalies);

            if (rs != null){

                while (rs.next()) {
                    ObjectNode object = returnobjectMapper.createObjectNode();
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
            cs.setInt("GameID",this.gameid);
            rs = cs.executeQuery();
            ArrayNode arrayPenalties = returnobjectMapper.createArrayNode();
            object2.put("Penalties",arrayPenalties);

            if (rs != null){

                while (rs.next()) {
                    ObjectNode object = returnobjectMapper.createObjectNode();
                    object.put ("PenaltyID", rs.getInt("idpenalty"));
                    object.put( "TeamID", rs.getInt("idteam"));
                    object.put( "RosterID", rs.getInt("idroster"));
                    object.put( "Description", rs.getString("penaltytype"));
                    object.put( "Length", rs.getString("length"));
                    object.put( "TimeofPenalty", rs.getString("timeofpenalty"));
                    object.put( "Period", rs.getInt("period"));

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
        } finally {
            //
            // always clean up after yourself..
            //
            db.free();
        }

        String json = returnobjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object2);

        response.getOutputStream().println(json);

    }

    public void setGameid(Integer gameid) {
        this.gameid = gameid;
    }

    public Integer getGameid() {
        return gameid;
    }

    public void traverse(JsonNode root, ScahaDatabase db){
        String gamestatus = null;
        if(root.isObject()){
            Iterator<String> fieldNames = root.fieldNames();

            while(fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode fieldValue = root.get(fieldName);
                if (fieldName.equals("GameId")){
                   this.setGameid(fieldValue.asInt());
                }
                if (fieldName.equals("Status")){
                    gamestatus =root.get(fieldName).textValue().toString();

                }
                if (fieldName.equals("Players")){
                    ArrayNode playersNode = (ArrayNode) root.get("Players");

                    for(int i = 0; i < playersNode.size(); i++) {
                        JsonNode arrayElement = playersNode.get(i);
                        playerstraverse(arrayElement,db);
                    }
                }
                if (fieldName.equals("Goals")){
                    ArrayNode goalsNode = (ArrayNode) root.get("Goals");

                    for(int i = 0; i < goalsNode.size(); i++) {
                        JsonNode arrayElement = goalsNode.get(i);
                        goalstraverse(arrayElement,db);
                    }
                }
                if (fieldName.equals("Goalies")){
                    ArrayNode goaliesNode = (ArrayNode) root.get("Goalies");

                    //need to traverse goalies using penaltytraverse to set value and save
                    for(int i = 0; i < goaliesNode.size(); i++) {
                        JsonNode arrayElement = goaliesNode.get(i);
                        goaliestraverse(arrayElement,db);
                    }
                }
                if (fieldName.equals("Penalties")){
                    //need to traverse players using playertraverse to set value and save
                    ArrayNode penaltiesNode = (ArrayNode) root.get("Penalties");

                    //need to traverse penalties using goalietraverse to set value and save
                    for(int i = 0; i < penaltiesNode.size(); i++) {
                        JsonNode arrayElement = penaltiesNode.get(i);
                        penaltytraverse(arrayElement,db);
                    }
                }
                traverse(fieldValue,db);
            }
        } else if(root.isArray()){


        } else {
            // JsonNode root represents a single value field - do something with it.


        }

        //now that we have saved all the individual items lets set the game status
        setStatus(gamestatus,db);



    }

    public void setStatus(String status,ScahaDatabase db){


            //don't do anything if they played.  do something if they didn't play.
            try{

                CallableStatement pc = db.prepareCall("call scaha.updateLiveGameStatus(?,?)");
                pc.setInt(1,this.gameid);
                pc.setString(2, status);
                pc.executeUpdate();

                pc.close();
                db.cleanup();

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                /*LOGGER.info("ERROR IN getting suspension list");*/
                e.printStackTrace();
                db.rollback();
            } finally {
                //
                // always clean up after yourself..
                //
                db.free();
            }
    }

    public void playerstraverse(JsonNode root,ScahaDatabase db){

        if(root.isObject()){
            Integer didplay = root.get("DidPlay").asInt();
            Integer rosterid = root.get("RosterID").asInt();

            //what to do if rosterid is 0 ????

            //don't do anything if they played.  do something if they didn't play.
            if (didplay.equals(0)){
                try{

                    CallableStatement pc = db.prepareCall("call scaha.toggleMIA(?,?,?)");
                    pc.setInt(1,this.gameid);
                    pc.setInt(2, rosterid);
                    pc.setInt(3, didplay);
                    pc.executeUpdate();

                    pc.close();
                    db.cleanup();

                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    /*LOGGER.info("ERROR IN getting suspension list");*/
                    e.printStackTrace();
                    db.rollback();
                } finally {
                    //
                    // always clean up after yourself..
                    //
                    db.free();
                }
            }

        } else {
            // JsonNode root represents a single value field - do something with it.


        }
    }


    public void goalstraverse(JsonNode root,ScahaDatabase db){

        if(root.isObject()){
            Integer goalid = root.get("GoalID").asInt();
            Integer teamid = root.get("TeamID").asInt();
            Integer scoredby = root.get("Scoredby").asInt();
            Integer assist1 = root.get("Assist1by").asInt();
            Integer assist2 = root.get("Assist2by").asInt();
            String goaltype = root.get("GoalType").textValue();
            goaltype = setgoaltype(goaltype.toString());
            Integer period = root.get("Period").asInt();
            String timescored = root.get("Time").textValue();

            //what to do if rosterid is 0 ????

            //don't do anything if they played.  do something if they didn't play.
                try{

                    CallableStatement cs = db.prepareCall("call scaha.updateScoring(?,?,?,?,?,?,?,?,?,?,?)");

                    //LOGGER.info("HERE IS THE Starting Scoring ID:" + this.ID);

                    int i = 1;
                    cs.registerOutParameter(1, java.sql.Types.INTEGER);
                    cs.setInt(i++, goalid);
                    cs.setInt(i++, this.gameid);
                    cs.setInt(i++, teamid);
                    cs.setInt(i++, scoredby);
                    cs.setInt(i++, assist1);
                    cs.setInt(i++, assist2);
                    cs.setString(i++, goaltype);
                    cs.setInt(i++, period);
                    cs.setString(i++, timescored);
                    cs.setInt(i++,1);
                    cs.setString(i++,null);
                    cs.execute();

                    cs.close();
                    db.cleanup();

                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    /*LOGGER.info("ERROR IN getting suspension list");*/
                    e.printStackTrace();
                    db.rollback();
                } finally {
                    //
                    // always clean up after yourself..
                    //
                    db.free();
                }


        } else {
            // JsonNode root represents a single value field - do something with it.


        }
    }
    public void goaliestraverse(JsonNode root,ScahaDatabase db){

        if(root.isObject()){
            Integer goalieid = root.get("GoalieID").asInt();
            Integer rosterid = root.get("RosterID").asInt();
            Integer teamid = root.get("TeamID").asInt();
            Integer shots1 = root.get("Period1Shots").asInt();
            Integer shots2 = root.get("Period2Shots").asInt();
            Integer shots3 = root.get("Period3Shots").asInt();
            Integer OTShots = root.get("OTShots").asInt();
            String minsplayed = root.get("MinsPlayed").textValue();

            //what to do if rosterid is 0 ????

            //don't do anything if they played.  do something if they didn't play.
                try{

                    CallableStatement cs = db.prepareCall("call scaha.updateSog(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                    //LOGGER.info("HERE IS THE Starting Sog ID:" + this.ID + ", idRoster is:" + this.idroster);

                    int i = 1;
                    cs.registerOutParameter(1, java.sql.Types.INTEGER);
                    cs.setInt(i++, goalieid);
                    cs.setInt(i++, rosterid);
                    cs.setInt(i++, this.gameid);
                    cs.setInt(i++,teamid);
                    cs.setString(i++, minsplayed);
                    cs.setInt(i++, shots1);
                    cs.setInt(i++, shots2);
                    cs.setInt(i++, shots3);
                    cs.setInt(i++, OTShots);
                    cs.setInt(i++, 0);
                    cs.setInt(i++, 0);
                    cs.setInt(i++, 0);
                    cs.setInt(i++, 0);
                    cs.setInt(i++, 0);
                    cs.setInt(i++,1);
                    cs.setString(i++,null);
                    cs.execute();

                    cs.close();
                    db.cleanup();

                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    /*LOGGER.info("ERROR IN getting suspension list");*/
                    e.printStackTrace();
                    db.rollback();
                } finally {
                    //
                    // always clean up after yourself..
                    //
                    db.free();
                }


        } else {
            // JsonNode root represents a single value field - do something with it.


        }
    }

    public void penaltytraverse(JsonNode root,ScahaDatabase db){

        if(root.isObject()){
            Integer penaltyid = root.get("PenaltyID").asInt();
            Integer rosterid = root.get("RosterID").asInt();
            Integer teamid = root.get("TeamID").asInt();
            Integer period = root.get("Period").asInt();
            String penaltytype = root.get("Description").textValue();
            String length = root.get("Length").textValue();
            String TimePenalty = root.get("TimeofPenalty").textValue();


            //don't do anything if they played.  do something if they didn't play.
                try{

                    CallableStatement cs = db.prepareCall("call scaha.updatePenalty(?,?,?,?,?,?,?,?,?,?)");

                    //LOGGER.info("HERE IS THE Starting Penalty ID:" + this.ID);

                    int i = 1;
                    cs.registerOutParameter(1, java.sql.Types.INTEGER);
                    cs.setInt(i++, penaltyid);
                    cs.setInt(i++, rosterid);
                    cs.setInt(i++, this.gameid);
                    cs.setInt(i++, teamid);
                    cs.setString(i++, penaltytype);
                    cs.setInt(i++, period);
                    cs.setString(i++, length);
                    cs.setString(i++, TimePenalty);
                    cs.setInt(i++,1);
                    cs.setString(i++,null);
                    cs.execute();

                    cs.close();
                    db.cleanup();

                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    /*LOGGER.info("ERROR IN getting suspension list");*/
                    e.printStackTrace();
                    db.rollback();
                } finally {
                    //
                    // always clean up after yourself..
                    //
                    db.free();
                }

        } else {
            // JsonNode root represents a single value field - do something with it.


        }
    }

    public String setgoaltype(String goaltype){
        String newgoaltype = "";
        switch(goaltype){
            case "Even Strength": newgoaltype="E";
            break;
            case "Penalty Shot": newgoaltype="PS";
            break;
            case "Empty Net:": newgoaltype="N";
            break;
            case "Shorty": newgoaltype="S";
            break;
            case "Power Play": newgoaltype="P";
            break;
        }

        return newgoaltype;
    }
}
