
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.net.*;
import java.io.*;

public class Main {

    static String apiKey = "";
    static int amountOfThreads = 0;
    static int minFollowers = 0;
    static int accountsChecked = 0;
    static int accountsFound = 0;
    static long finishTime = 0;
    static int findAccounts = 99999999;
    static int runTime = 0;
    static String StrStartName = "";

    ArrayList<Thread> InputApi = new ArrayList<>();

    ArrayList<Thread> thread = new ArrayList<>();
    private static ArrayList<String> alreadyCrawled = new ArrayList<>();
    private static ArrayList<String> crawlList = new ArrayList<>();

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("====================================");
        System.out.println("=         Instagram Scraper        =");
        System.out.println("=         Made By ikternet         =");
        System.out.println("====================================");

        File f = new File("api.txt");
        if (!(f.exists() && !f.isDirectory())) {
            // does not exist
            System.out.println("Enter the Api key for this server.");
            apiKey = s.next();
            createApi(apiKey);
        }
        Path apipath = Paths.get("", "api.txt");
        Charset charset = Charset.forName("ISO-8859-1");
        try {
            List<String> lines = Files.readAllLines(apipath, charset);

            lines.stream().forEach((line) -> {
                apiKey = line;
            });
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            getInput(apiKey);
        } catch (Exception e) {
            // niks
            System.out.println("Can't get inputs");
            System.exit(0);
        }
//        System.exit(0);

        System.out.println("Enter a start point (Ex: danbilzerian)");
        addToCrawl("https://www.instagram.com/" + s.next());
        System.out.println("Enter min # of followers");
        minFollowers = Integer.parseInt(s.next());
        System.out.println("Enter amount of threads");
        amountOfThreads = s.nextInt();
        System.out.println("Enter how long to run for (seconds) (enter -1 for unlimited)");
        int runTime = s.nextInt();
        if (runTime == -1) {
            finishTime = 99434949175691L;
        } else {
            finishTime = System.currentTimeMillis() + (runTime * 1000);
        }
        System.out.println("Enter amount of accounts to find (enter -1 for unlimited)");
        findAccounts = s.nextInt();

        System.out.println("Starting..");
        s.close();
        for (int i = 0; i < amountOfThreads; i++) {
            new Thread((Runnable)new Worker(i)).start();
        }
    }

    public static synchronized void foundAccount() {
        findAccounts--;
    }

    public static synchronized String getNext() {
        accountsChecked++;
        if (crawlList.isEmpty()) {
            return "empty";
        } else {
            //System.out.println(crawlList.get(crawlList.size()-1));
            String crawling = crawlList.get(crawlList.size() - 1);
            String cleanCrawl = crawling;
            // Remove all characters from the first backslash until the end, if there is one
            if (cleanCrawl.contains("\\")) {
                // Split it.
                cleanCrawl = cleanCrawl.substring(0, cleanCrawl.indexOf("\\"));
            }
            // Remove all non-ascii stuff like newlines etc
            cleanCrawl = cleanCrawl.replaceAll("[^a-zA-Z0-9_.:/]", "");
            if (!(cleanCrawl.equals(crawling))) {
                System.out.println("Cleaned page from " + crawling + " to " + cleanCrawl);
            }
            if (Main.wasCrawled(cleanCrawl)) {
                crawlList.remove(crawlList.size() - 1);
                return "empty";
            }
            addToAlreadyCrawled(cleanCrawl);
            crawlList.remove(crawlList.size() - 1);
            System.out.println(cleanCrawl); // comment out
            return cleanCrawl;
        }
    }

    public static synchronized void addToCrawl(String page) {
        for (int i = 0; i < alreadyCrawled.size(); i++) {
            if (alreadyCrawled.get(i).equals(page)) {
//                System.out.println("Page " + page + " already crawled"); // comment out
                return;
            }
        }
//        System.out.println("Adding " + page.split("/")[page.split("/").length - 1] + " to crawl");
        crawlList.add(page);
//        System.out.println("Crawlist " + page + " has been added"); // Comment out
    }

    public static synchronized void addToAlreadyCrawled(String page) {
//        System.out.println("Page " + page + " added to already crawled because crawling commences now"); // comment out
        alreadyCrawled.add(page);
    }

    public static boolean wasCrawled(String page) {
        for (int i = 0; i < alreadyCrawled.size(); i++) {
            if (page.equals(alreadyCrawled.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static synchronized void writeToFile(String in) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("IGresults.txt", true)))) {
            out.println(in);
        } catch (IOException e) {
        }
    }

    public static synchronized void createApi(String in) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("api.txt", true)))) {
            out.println(in);
        } catch (IOException e) {
        }
    }

    public static String getStartName() {
        return StrStartName;
    }

    public static void getInput(String apiKeyIn) throws Exception {
        try {
            URL apiUrl = new URL("https://www.mailcollector.nl/servers/input.php?key=" + apiKeyIn);
            String outputApi;
            try (BufferedReader apiIn = new BufferedReader(
                    new InputStreamReader(apiUrl.openStream()))) {
                String inputLine;
                outputApi = "";
                if ((inputLine = apiIn.readLine()) != null) {
                    outputApi = inputLine;
                }
            }

            if (!"".equals(outputApi)) {
                StrStartName = outputApi.substring(0, outputApi.indexOf(','));
                System.out.println("Name " + StrStartName);
                String StrRemainder = outputApi.substring(outputApi.indexOf(',') + 1);
                //System.out.println("Remainder "+StrRemainder);
                String StrMinFollowers = StrRemainder.substring(0, StrRemainder.indexOf(','));
                System.out.println("MinFollowers " + StrMinFollowers);
                StrRemainder = StrRemainder.substring(StrRemainder.indexOf(',') + 1);
                //System.out.println("Remainder "+StrRemainder);
                String StrThreads = StrRemainder.substring(0, StrRemainder.indexOf(','));
                System.out.println("Threads " + StrThreads);
                StrRemainder = StrRemainder.substring(StrRemainder.indexOf(',') + 1);
                //System.out.println("Remainder "+StrRemainder);
                String StrNumberOfAccounts = StrRemainder.substring(0, StrRemainder.indexOf(','));
                System.out.println("Accounts " + StrNumberOfAccounts);
                StrRemainder = StrRemainder.substring(StrRemainder.indexOf(',') + 1);
                //System.out.println("Remainder "+StrRemainder);
                String StrTimeout = StrRemainder;
                System.out.println("Timeout " + StrTimeout);

                addToCrawl("https://www.instagram.com/" + StrStartName);
                minFollowers = Integer.valueOf(StrMinFollowers);
                amountOfThreads = Integer.valueOf(StrThreads);
                runTime = Integer.valueOf(StrTimeout);
                if (runTime == -1) {
                    finishTime = 99434949175691L;
                } else {
                    finishTime = System.currentTimeMillis() + (runTime * 1000);
                }
                findAccounts = Integer.valueOf(StrNumberOfAccounts);

            } else {
                System.out.println("No inputs received");
                System.exit(0);
            }

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error!Bre");
        }
    }
}
