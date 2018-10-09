public class Main {
    public static void main(String[] args) {
        String url = "https://block.vn";
        String url2 = "https://tuyendung.tiki.vn";


        SiteMap siteMap = new SiteMap(url2);
        siteMap.createMap();

        //siteMap.getLinks(url);

        // comments:
        //siteMap.makeInverseGraph();
        //siteMap.printReferenceLinks("https://block.vn/dang-nhap");

        System.out.println(siteMap.getDecodeGraph());
    }
}