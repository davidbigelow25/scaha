package com.scaha.apis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gbli.context.ContextManager;
import com.scaha.apis.ApisAuthenticationUSAHockey;
import com.scaha.objects.UsaHockeyRegistration;
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
import com.gbli.common.USAHRegClient;
import java.util.logging.Logger;



public class GetUsaHockey extends HttpServlet {

    private static final JSONArray HTTP = null;
    private UsaHockeyRegistration usar = null;
    private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

    /*private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());*/

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        response.getOutputStream().println("do get worked");

    }



    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //lets pull values from the requesting objects clubid, optional teamid, optional year
        LOGGER.info("starting post request:" + request + ",response" + response);

        //ObjectMapper readobjectMapper = new ObjectMapper();
        LOGGER.info("instantiating object mapper");

        //JsonNode jsonNode = readobjectMapper.readTree(request.getReader());
        //LOGGER.info("loading jsonnode" + jsonNode);

        //String inusanumber = jsonNode.get("USANumber").textValue();
        String inusanumber = request.getParameter("USANumber");
        LOGGER.info("getting usa number:" + inusanumber);


        // authenticate the request.  if successfull continue on otherwise return response not authorized.
        ApisAuthenticationUSAHockey apisa = new ApisAuthenticationUSAHockey();
        LOGGER.info("starting apisa authentication");

        Boolean authenticated = apisa.AuthenticateRequest(request);
        LOGGER.info("authentication has been returned");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode object2 = objectMapper.createObjectNode();

        LOGGER.info("setup object mappers");

        if (!authenticated){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Credentials");
            LOGGER.info("authentication returned invalid credentials");

        };

        //lets call usa hockey reg client
        LOGGER.info("calling USAHRegClient");

        try{
            this.usar = USAHRegClient.pullRegistartionRecord(inusanumber);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("error in calling usahregclient" + e.toString());

        }

        LOGGER.info("loading resulting  values");

        object2.put("USAHnum",usar.getUSAHnum());
        object2.put("FirstName",usar.getFirstName());
        object2.put("MiddleInit",usar.getMiddleInit());
        object2.put("LastName",usar.getLastName());
        object2.put("Address",usar.getAddress());
        object2.put("City",usar.getCity());
        object2.put("Zipcode",usar.getZipcode());
        object2.put("State",usar.getState());
        object2.put("Country",usar.getCountry());
        object2.put("Citizen",usar.getCitizen());
        object2.put("ForZip",usar.getForZip());
        object2.put("DOB",usar.getDOB());
        object2.put("Gender",usar.getGender());
        object2.put("HPhone",usar.getHPhone());
        object2.put("BPhone",usar.getBPhone());
        object2.put("Email",usar.getEmail());
        object2.put("PGSFName",usar.getPGSFName());
        object2.put("PGSLName",usar.getPGSLName());
        object2.put("PGSMName",usar.getPGSMName());

        LOGGER.info("finished loading values" + object2);

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object2);

        LOGGER.info("loading json");

        response.getOutputStream().println(json);

    }


}
