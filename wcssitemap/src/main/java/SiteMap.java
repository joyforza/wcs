
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class SiteMap {
    private HashMap<String, Integer> urlMap = new HashMap<String, Integer>();
    private String rootDomain = "";


    public SiteMap(String rootDomain) {
        this.rootDomain = rootDomain;
        urlMap.clear();
    }

    public List<String> crawlURL(String nodeURL) {
            List<String> newURL = new ArrayList<String>();

            try {
                Document doc = Jsoup.connect(nodeURL).get();

                Elements aElements = doc.getElementsByTag("a");

                for (Element e : aElements) {
                    String candidateUrl = e.attr("href");
                    if (candidateUrl.length() > 0 && candidateUrl.charAt(0) == '/') {
                        candidateUrl = rootDomain + candidateUrl;
                    }
                    if (candidateUrl.length() > 0)
                        newURL.add(candidateUrl);
                }
            }
            catch (IOException ex) {
                System.out.println("URL ERROR!");
            }
            return newURL;
    }

    public void createMap() {
        Queue<String> queue = new LinkedList<String>();
        queue.add(rootDomain);

        while (!queue.isEmpty()) {
            String curUrl = queue.peek();

            System.out.println(curUrl);

            queue.remove();

            List<String> newUrl = crawlURL(curUrl);

            for (String url : newUrl) {
                if (url.charAt(0) == '/') url = rootDomain + url;
                if (urlMap.containsKey(url)) {
                    // exist
                }
                else
                    if (url.contains(rootDomain))
                        queue.add(url);
            }
        }
    }

}
