package com.intracomtelecom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

import static spark.Spark.*;

public class CellLogFilter {
    private static Logger log = LoggerFactory.getLogger(CellLogFilter.class);

    public static void main(String[] args) {
        String dbURL = "jdbc:sqlite::resource:data.sqlite";
        Sql2o sql2o = new Sql2o(dbURL);
//        Connection con;
//        try {
//            con = DriverManager.getConnection(dbURL);
//        } catch (SQLException ex) {
//            log.error("Cannot find SQLite DB.");
//        }
        get("/logs", (req, res) -> {
            try (Connection conn = sql2o.open()) {
                List<Log> posts = conn.createQuery("select * from posts")
                        .executeAndFetch(Log.class);
                return posts;
            }
        });
    }
}