
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
    // inverse graph
    private List<List<SiteLink>> invGraph = null;

    // vertices
    private int verticesNum;
    // type of node
    private List<Integer> typeNode = new ArrayList<Integer>();


    // make inverse graph
    public void makeInverseGraph() {
        invGraph = new ArrayList<List<SiteLink>>();
        int sz = graph.size();
        for (int i = 0; i < sz; i++) invGraph.add(new ArrayList<SiteLink>());

        for (int i = 0; i < graph.size(); i++) {

            List<SiteLink> listSite = graph.get(i);
            for (SiteLink siteLink : listSite) {

                String src = siteLink.getSrcUrl();
                String des = siteLink.getDesUrl();

                // src --> des ( desType )
                int desId = urlMap.get(des);
                int srcId = urlMap.get(src);

                int srcType = typeNode.get(srcId);

                SiteLink invSiteLink = new SiteLink(des, src, srcType);
                //invSiteLink.setSrcUrl(des);
                //invSiteLink.setDesUrl(src);
                //invSiteLink.setDesType(srcType);

                invGraph.get(desId).add(invSiteLink);

            }
        }

    }

    public List<SiteLink> getReferenceLinks(String url) {
        int id = urlMap.get(url);
        return invGraph.get(id);
    }

    public void printReferenceLinks(String url) {
        int id = urlMap.get(url);
        for (SiteLink siteLink : invGraph.get(id)) {
            System.out.println("link: " + siteLink.getDesUrl() + " type: " + siteLink.getDesType());
        }
    }

    //
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

    // list all page that url direct to
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
                    // old node
                    int mapCurUrl = urlMap.get(url);
                    int typeCurUrl = typeNode.get(mapCurUrl);
                    //
                    SiteLink sl = new SiteLink(curUrl, url, typeCurUrl);
                    graph.get(mapId).add(sl);
                }
            }
        }
    }

    // get decode of graph
    public String getDecodeGraph() {

        HashSet<Integer> hashSet = new HashSet<Integer>();
        int root = 0;

        ArrayList<Integer> degList = new ArrayList<Integer>();
        degList.add(root);
        hashSet.add(root);

        List<List<Integer>> codeMap = new ArrayList<List<Integer>>();

        codeMap.add(new ArrayList<Integer>(Arrays.asList(0)));

        while (true) {

            System.out.println("new pharse:");
            ArrayList<Integer> newList = new ArrayList<Integer>();

            int counting = 1;

            List<Integer> addition = new ArrayList<Integer>();

            for (int j = 0; j < degList.size(); j++) {
                int node = degList.get(j);


                for (int i = 0; i < graph.get(node).size(); i++) {
                    SiteLink siteLink = graph.get(node).get(i);
                    String toUrl = siteLink.getDesUrl();
                    int toUrlId = urlMap.get(toUrl);

                    if (!hashSet.contains(toUrlId)) {
                        newList.add(toUrlId);
                        addition.add(counting);
                        hashSet.add(toUrlId);
                    }
                }
                counting++;
            }


            if (newList.size() == 0) break;

            degList.clear();

            for (int j = 0; j < newList.size(); j++)
                degList.add(newList.get(j));
            newList.clear();

            codeMap.add(addition);
        }


        /*1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                8, 8, 11, 25, 25, 25, 25, 25, 25, 25, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 32, 32, 32, 32, 32, 32, 32,
                4, 4, 6, 7, 8,
        */

        /*List<List<Integer>> codeMap = new ArrayList<List<Integer>>();
        codeMap.add(new ArrayList<Integer>(Arrays.asList(1)));
        codeMap.add(new ArrayList<Integer>(Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)));
        codeMap.add(new ArrayList<Integer>(Arrays.asList(8, 8, 11, 25, 25, 25, 25, 25, 25, 25, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 32, 32, 32, 32, 32, 32, 32)));
        codeMap.add(new ArrayList<Integer>(Arrays.asList(4, 4, 6, 7, 8)));

        for (int i = 0; i < codeMap.size(); i++) {
            for (int j = 0; j < codeMap.get(i).size(); j++)
                System.out.print(codeMap.get(i).get(j) + ", ");
            System.out.println();
        } */

        List<List<Integer> > genMap = new ArrayList<List<Integer>>();

        genMap.add(new ArrayList<Integer>(Arrays.asList(1)));

        for (int i = 1; i < codeMap.size(); i++) {

            List<Integer> subGenMap = new ArrayList<Integer>();


            int count = 0;
            int pos = 0;

            for (int j = 0; j < genMap.get(i - 1).size(); j++) {
                if (genMap.get(i - 1).get(j) > 0) count++;

                boolean isHave = false;
                while (pos < codeMap.get(i).size() && codeMap.get(i).get(pos) == count) {
                    subGenMap.add(codeMap.get(i).get(pos));
                    pos++;
                    isHave = true;
                }

                if (!isHave)
                    subGenMap.add(0);

            }

            genMap.add(subGenMap);
            /*System.out.println("done " + i);
            for (int j = 0; j < subGenMap.size(); j++)
                System.out.print(subGenMap.get(j) + " ");
            System.out.println(); */
        }

        System.out.println("Finishing:");
        for (int i = 0; i < genMap.size(); i++) {
            System.out.print("[");
            for (int j = 0; j < genMap.get(i).size(); j++)
                System.out.print(genMap.get(i).get(j) + ", ");
            System.out.println("], ");
        }

        return "";
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
