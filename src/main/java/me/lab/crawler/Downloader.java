package me.lab.crawler;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Downloader {
    private static final int tries = 5;
    private static final String site = "http://m.football.ua";

    public static void main(String[] args) throws IOException {
        File dir = new File(".");
        String loc = dir.getCanonicalPath() + File.separator + "record.txt";
        FileWriter fstream = new FileWriter(loc, true);
        BufferedWriter out = new BufferedWriter(fstream);
        out.newLine();
        out.close();

        processPage(site);
    }

    private static String download(URL url) {
        InputStream is = null;
        BufferedReader br;
        String line;

        StringBuilder sb = new StringBuilder();
        try {
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static void processPage(String URL) throws IOException {
        // invalid link
        if (URL.contains(".pdf") || URL.contains("@")
                || URL.contains(":80") || URL.contains(".jpg")
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
                    //doc = Jsoup.connect(URL).timeout(3000).get();
                    String html = download(new URL(URL));
                    doc = Jsoup.parse(html);

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
