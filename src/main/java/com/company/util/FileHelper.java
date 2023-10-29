package com.company.util;

public class FileHelper {
    public static String getDbPath(String dbName) {
        String workingDir = System.getProperty("user.dir");
        return "jdbc:sqlite:%s/db/%s".formatted(workingDir, dbName);
    }
}
