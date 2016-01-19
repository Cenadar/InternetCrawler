package me.lab.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.PrintWriter;
import java.net.URL;
import java.util.Scanner;

public class HTMLExtractor {
    private final static Double LIMIT = 0.2;

    private HTMLExtractor() {}

    private static Element getLargestElement(Elements children) {
        Element largest = children.get(0);
        for (Element e : children) {
            if (largest.text().length() < e.text().length())
                largest = e;
        }
        return largest;
    }

    private static Double calculateMean(int[] values) {
        double sum = 0;
        for (int x : values) {
            sum += x;
        }
        return sum / values.length;
    }

    private static double calculateVariance(int[] values) {
        double mean = calculateMean(values);
        double sum2 = 0;
        double dx;
        for (Integer x : values) {
            dx = mean - x;
            sum2 += dx * dx;
        }
        int n = values.length;
        return Math.sqrt(sum2 / n);
    }

    private static int[] getLengths(Elements children) {
        int n = children.size();
        int[] lengths = new int[n];
        int i = 0;
        for (Element e: children) {
            lengths[i++] = e.text().length();
        }
        return lengths;
    }

    public static String extractText(Element node) {
        Elements children = node.children();
        // empty
        if (children.size() == 0) {
            return node.text();
        }

        // one child
        if (children.size() == 1) {
            return extractText(children.get(0));
        }

        // calculate necessary coefficient
        int fullTextLength = node.text().length();
        double variance = calculateVariance(getLengths(children));
        double coef = 1.0 * variance / fullTextLength;

        if (coef < LIMIT) {
            return node.text();
        }

        Element largest = getLargestElement(children);
        return extractText(largest);
    }

    public static String extractDocumentData(Document doc) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("TITLE\n");
        buffer.append(doc.title());
        buffer.append("\nTEXT\n");
        buffer.append(extractText(doc.body()) + "\n");
        return buffer.toString();
    }


    public static void main(String[] args) throws Exception {
        //http://football.ua/fiction/286945-gol-redkaja-prelest-futbola-chast-2.html
        //http://ukranews.com/news/193990.Patriarh-Ierusalimskiy-Teofil-III-schitaet-edinstvo-putem-k-resheniyu-problem-Ukraini.ru
        //http://www.independent.co.uk/news/world/europe/refugee-crisis-inside-the-dunkirk-camp-where-2500-refugees-live-in-conditions-far-worse-than-the-a6784881.html

        System.out.println("Please enter URL");

        Scanner sc = new Scanner(System.in);
        String strURL = sc.nextLine();
        URL url = new URL(strURL);

        Document doc = Jsoup.connect(url.toString()).get();

        String fileName = "result.txt";
        PrintWriter out = new PrintWriter(fileName);

        System.out.println("Processing...");
        System.out.println("URL=" + url);

        String title = doc.title();
        out.println("TITLE=" + title);
        System.out.println("TITLE=" + title);

        Element body = doc.body();
        String result = extractText(body);

        out.println("TEXT=" + result);
        System.out.println("TEXT=" + result);
        out.close();

        System.out.println("SUCCESS!!!");
        System.out.println("Saved result to result file.");
    }
}
