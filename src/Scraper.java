import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scraper implements Runnable {

    private String sourceCode = "";

    //Should only be image pages
    //Ex: https://instagram.com/p/4LGIIkoDjR/
    public Scraper(String page) {
        //System.out.println(page);
        sourceCode = WebHelper.getPage(page);
    }

    @Override
    public void run() {
        String[] users = scrapeUsers();
        for (String user : users) {
            Main.addToCrawl("https://www.instagram.com/" + user);
            //System.out.println(users[i]);
        }
    }

    // Should use profilepages
    private String[] newScrapeUsers() {

        if (!sourceCode.contains("\"caption\": \"")) {
            // Looking for "caption": "
            System.out.println("no caption");
            return new String[0];
        }
        String stageOne = sourceCode.split(Pattern.quote("\"caption\": \""))[1];
        String stageTwo = stageOne.split("\",\"likes\":")[0].replaceAll("\\\\u.{4}", "");
        ArrayList<String> results = new ArrayList<>();

        //System.out.println("1: " + stageOne + " -> " + stageTwo);
        String ePattern = "@(\\w+)";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(stageTwo);
        while (m.find()) {
            //System.out.println("-> " + m.group().substring(1));
            results.add(m.group().substring(1));
        }
        return removeDuplicates(results.toArray(new String[0]));
    }

    @SuppressWarnings("unused")
    private String[] scrapeUsers() {
        String[] stageOne = sourceCode.split(Pattern.quote("\"text\": \"@"));
        String[] stageTwo = new String[stageOne.length - 1];
        String[] finalStage = new String[stageTwo.length];

        for (int i = 0; i < stageOne.length - 1; i++) {
            //System.out.println("1: " + i + " " + stageOne[i+1] + " -> " + stageOne[i+1].split("\"")[0]);
            //stageTwo[i] = stageOne[i+1].split("\"")[0];
            //System.out.println("1: " + i + " " + stageOne[i+1] + " -> " + stageOne[i+1].split(" ")[0].split("\n")[0]);
            stageTwo[i] = stageOne[i + 1].split(" ")[0].split("\n")[0];
        }

        for (int i = 0; i < stageTwo.length; i++) {
            finalStage[i] = stageTwo[i].split("\"")[0];
            if (finalStage[i].contains("\\")) {
                finalStage[i] = finalStage[i].substring(0, finalStage[i].indexOf("\\"));
            }
            //finalStage[i] = stageTwo[i].split(" ")[0].split("\n")[0];
            //System.out.println("F: " + i + " " + finalStage[i]);
        }

        return removeDuplicates(finalStage);

    }

    private String[] removeDuplicates(String[] in) {
        ArrayList<String> results = new ArrayList<>();
        boolean found = false;
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < results.size(); j++) {
                if (in[i].equals(results.get(j))) {
                    found = true;
                    break;
                }
            }
            if (found) {
                continue;
            }
            results.add(in[i]);
        }

        return results.toArray(new String[0]);
    }

    public static void main(String[] args) {
        Scraper s = new Scraper("https://www.instagram.com/p/BSHKriPFuIE/");
        String[] test = s.newScrapeUsers();
        //String[] test = s.removeDuplicates(new String[] {"test","test"});
        for (String test1 : test) {
            System.out.println(test1);
        }

    }
}
