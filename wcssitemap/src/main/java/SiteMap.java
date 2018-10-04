
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class SiteMap {
    private HashMap<String, Integer> urlMap = new HashMap<String, Integer>();

    private String rootDomain = "";

    // graph
    private List<List<SiteLink>> graph = new ArrayList<List<SiteLink>>();
    // vertices
    private int verticesNum;
    // type of node
    private List<Integer> typeNode = new ArrayList<Integer>();


    public SiteMap(String rootDomain) {
        this.rootDomain = rootDomain;
        urlMap.clear();
        verticesNum = 0;
    }

    public List<String> crawlURL(String nodeURL) {
            //System.out.println("Tree begin at: " + nodeURL);
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
                System.out.println("URL ERROR! on access page: " + nodeURL);
            }

            //System.out.println("found: " + newURL.size());
            return newURL;
    }

    public void getLinks(String url) {
        int mapId = urlMap.get(url);
        List<SiteLink> list = graph.get(mapId);
        for (SiteLink siteLink : list) {
            System.out.println(siteLink.getDesUrl() + " " + siteLink.getDesType());
        }
    }


    public void createMap() {
        Queue<String> queue = new LinkedList<String>();
        queue.add(rootDomain);
        //
        urlMap.put(rootDomain, verticesNum);
        typeNode.add(1);
        verticesNum++;
        graph.add(new ArrayList<SiteLink>());


        while (!queue.isEmpty()) {
            String curUrl = queue.peek();
            int mapId = urlMap.get(curUrl);

            System.out.println(curUrl);

            queue.remove();

            List<String> newUrl = crawlURL(curUrl);

            for (String url : newUrl) {
                url = url.trim();

                if (!urlMap.containsKey(url)) {
                    // add to map & graph
                    urlMap.put(url, verticesNum);
                    verticesNum++;
                    graph.add(new ArrayList<SiteLink>());

                    if (url.contains(rootDomain)) {
                        // internal
                        SiteLink sl = new SiteLink(curUrl, url, 1);
                        graph.get(mapId).add(sl);
                        queue.add(url);
                        typeNode.add(1);
                    }
                    else {
                        // external
                        SiteLink sl = new SiteLink(curUrl, url, 2);
                        graph.get(mapId).add(sl);
                        typeNode.add(2);
                    }
                }
                else {
                    //
                    int mapCurUrl = urlMap.get(url);
                    int typeCurUrl = typeNode.get(mapCurUrl);
                    //
                    SiteLink sl = new SiteLink(curUrl, url, typeCurUrl);
                    graph.get(mapId).add(sl);
                }
            }
        }
    }

    public class SiteLink {
        private String srcUrl;
        private String desUrl;
        private int desType; // 2:external or 1:internal

        public String getSrcUrl() {
            return srcUrl;
        }

        public void setSrcUrl(String srcUrl) {
            this.srcUrl = srcUrl;
        }

        public String getDesUrl() {
            return desUrl;
        }

        public void setDesUrl(String desUrl) {
            this.desUrl = desUrl;
        }

        public int getDesType() {
            return desType;
        }

        public void setDesType(int desType) {
            this.desType = desType;
        }

        public SiteLink(String srcUrl, String desUrl, int desType) {
            this.srcUrl = srcUrl;
            this.desUrl = desUrl;
            this.desType = desType;
        }
    }

}
