package me.lab.crawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Database {
    private static File baseDir = new File("db");

    public static boolean isURLDownloaded(String URL) throws IOException {
        return new File(convertURLToFileName(URL)).exists();
    }

    public static void saveURL(String URL, String content) throws IOException {
        File file = new File(convertURLToFileName(URL));
        file.getParentFile().mkdirs();
        new FileWriter(file).write(content);
    }

    private static String convertURLToFileName(String URL) throws IOException {
        if (URL.endsWith("#")) {
            URL = URL.substring(0, URL.length() - 1);
        }
        if (!URL.endsWith("/")) {
            URL = URL + "/";
        }

        final String protocolSeparator = "://";
        String protocol = URL.substring(0, URL.indexOf(protocolSeparator));
        URL = URL.substring(URL.indexOf(protocolSeparator) + protocolSeparator.length());
        String domain = URL.substring(0, URL.indexOf("/"));
        String uri = URL.substring(URL.indexOf("/") + 1);
        return baseDir.getCanonicalPath() + File.separator + protocol + File.separator + domain + File.separator + "uri-" + uri.replaceAll("/", ".");
    }


}
