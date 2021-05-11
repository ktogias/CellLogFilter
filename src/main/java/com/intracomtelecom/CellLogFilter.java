package com.intracomtelecom;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.io.IOException;

import static spark.Spark.*;

public class CellLogFilter {
    private static final Logger log = LoggerFactory.getLogger(CellLogFilter.class);

    public static void main(String[] args) {
        String dbURL = "jdbc:sqlite::resource:data.sqlite";
        Sql2o sql2o = new Sql2o(dbURL, null, null);

        get("/logs", (req, res) -> {
            try (Connection conn = sql2o.open()) {
                log.debug("Fetch all logs");
                return dataToJson(conn.createQuery("SELECT * FROM PerformanceKPIsHourly")
                        .executeAndFetch(Log.class));
            }
        });

        get("/cells/:eNodeB", (req, res) -> {
            try (Connection conn = sql2o.open()) {
                log.debug("Fetch all cells of eNodeB " + req.params(":eNodeB"));
                return dataToJson(conn.createQuery("SELECT DISTINCT Cell FROM PerformanceKPIsHourly WHERE eNodeB = :eNodeB")
                        .addParameter("eNodeB", req.params(":eNodeB"))
                        .executeScalarList(String.class));
            }
        });

        get("/enodeb", (req, res) -> {
            try (Connection conn = sql2o.open()) {
                log.debug("Fetch eNodeB lists");
                return dataToJson(conn.createQuery("SELECT DISTINCT eNodeB FROM PerformanceKPIsHourly")
                        .executeScalarList(String.class));
            }
        });

        after((request, response) -> {
            response.type("application/json");
            response.header("Content-Encoding", "gzip");
        });

        options("/*",
                (request, response) -> {

                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }

                    return "OK";
                });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    }

    public static String dataToJson(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(data);
        } catch (IOException e){
            log.error(e.toString());
            throw new RuntimeException("IOException from a StringWriter?");
        }
    }
}