public class Main {
    public static void main(String[] args) {
        String url = "https://vnexpress.net";


        SiteMap siteMap = new SiteMap(url);
        siteMap.createMap();

        siteMap.getLinks("https://vnexpress.net");
    }
}