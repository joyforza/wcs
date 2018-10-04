public class Main {
    public static void main(String[] args) {
        String url = "https://block.vn";
        String url2 = "https://thethao.vnexpress.net";

        SiteMap siteMap = new SiteMap(url);
        siteMap.createMap();
    }
}