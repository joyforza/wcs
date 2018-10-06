public class Main {
    public static void main(String[] args) {
        String url = "https://block.vn";


        SiteMap siteMap = new SiteMap(url);
        siteMap.createMap();

        //siteMap.getLinks("https://vnexpress.net");
        siteMap.makeInverseGraph();
        siteMap.printReferenceLinks("https://block.vn/dang-nhap");
    }
}