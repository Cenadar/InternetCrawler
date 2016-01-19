package me.lab.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Downloader {
    private static final int tries = 5;
    private static final String site = "http://m.football.ua";
    //private static final String site = "http://www.independent.co.uk";
    //private static final String site = "http://www.ukranews.com";

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

        if (!URL.startsWith(site)) {
            // url of other site -> do nothing
            return;
        }

        // check existance
        if (!Database.isURLDownloaded(URL)) {
            System.out.println("------ :  " + URL);
            URL url = new URL(URL);
            Document doc = null;

            try {
                Database.saveURL(URL, "");

                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    //Handle exception
                }

                InputStream stream = url.openStream();
                if (stream.available() != 0) {
                    String html = new Scanner(stream,"UTF-8").useDelimiter("\\A").next();
                    if (!html.isEmpty())
                        doc = Jsoup.parse(html);
                }
            } catch (IOException e) {
                //TODO: Uncomment this line
                //System.out.println("Page not exist " + URL);
                return;
            }

            if (doc != null) {
                String data = HTMLExtractor.extractDocumentData(URL, doc);
                Database.saveURL(URL, data);

                Elements questions = doc.select("a");
                for (Element link : questions) {
                    String ref = link.attr("href");
                    String absoluteRef = new URL(url, ref).toString();
                    processPage(absoluteRef);
                }
            }
        }
    }
}
