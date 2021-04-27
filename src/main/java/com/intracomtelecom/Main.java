package com.intracomtelecom;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        get("/logs", (req, res) -> "Hello World");
    }
}