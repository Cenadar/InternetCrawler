package me.lab.crawler;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

public class Downloader {
    private static final int tries = 5;

    public static void main(String[] args) throws IOException {
        File dir = new File(".");
        String loc = dir.getCanonicalPath() + File.separator + "record.txt";
        FileWriter fstream = new FileWriter(loc, true);
        BufferedWriter out = new BufferedWriter(fstream);
        out.newLine();
        out.close();

        //processPage("http://dt.ua");
        processPage("http://m.football.ua");

        //File file = new File(loc);
    }

    public static void processPage(String URL) throws IOException {
        // invalid link
        if (URL.contains(".pdf") || URL.contains("@")
                || URL.contains(":80")|| URL.contains(".jpg")
                || URL.contains(".pdf") || URL.contains(".jpg")) {
            return;
        }

        if (!URL.startsWith("http://m.football.ua")) {
            // url of other site -> do nothing
            return;
        }

        // check existance
        if (!Database.isURLDownloaded(URL)) {
            System.out.println("------ :  " + URL);

            Document doc = null;
            try {
                Database.saveURL(URL, "");
                for (int i = 0; doc == null && i < tries; ++i) {
                    try {
                        doc = Jsoup.connect(URL).get();
                    } catch (HttpStatusException e) {
                        System.err.println(e.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (doc != null) {
                Database.saveURL(URL, doc.outerHtml());

                Elements questions = doc.select("a[href]");
                for (Element link : questions) {
                    processPage(link.attr("abs:href"));
                }
            }
        }
    }
}
