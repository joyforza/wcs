
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SiteMap {
    private static HashMap<String, Integer> urlMap = new HashMap<String, Integer>();
    private static String rootDomain = "";

    public static List<String> crawlURL(String nodeURL) {
            List<String> newURL = new ArrayList<String>();

            try {
                Document doc = Jsoup.connect(nodeURL).get();

                Elements aElements = doc.getElementsByTag("a");

                for (Element e : aElements) {
                    String candidateUrl = e.attr("href");
                    if (candidateUrl.charAt(0) == '/') {
                        candidateUrl = rootDomain + candidateUrl;
                    }

                    if (urlMap.containsKey(candidateUrl)) {

                    } else {

                    }
                }
            }
            catch (IOException ex) {
                System.out.println("URL ERROR!");
            }
            return newURL;
    }

    public static String getRootDomain(String rootUrl) {
        return  rootUrl;
    }

    public static void createMap(String rootUrl) {
        rootDomain =  getRootDomain(rootUrl);

        List<String> queueUrl = new ArrayList<String>();


        while (true) {
            List<String> newUrls = crawlURL()
        }
    }


    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://zing.vn/songcungworldcup/tin-tuc").get();

        Elements aElements = doc.getElementsByTag("a");

        for (Element e : aElements) {
            System.out.println(e.attr("href"));
        }
    }
}
