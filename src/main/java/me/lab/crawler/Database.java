package me.lab.crawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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

    public static void saveURL(String URL) throws IOException {
        File file = new File(Database.convertURLToFileName(URL));
        Path targetPath = file.toPath();
        file.mkdirs();
        URL url = new URL(URL);
        Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public static String getContentURL(String URL) throws IOException {
        File file = new File(Database.convertURLToFileName(URL));
        String content = "";
        if (file.exists() && !file.isDirectory()) {
            content = new String(Files.readAllBytes(file.toPath()));
        }
        return content;
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
