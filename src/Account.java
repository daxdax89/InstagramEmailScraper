
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Account {

    private String sourceCode = "";
    private boolean kill = false;

    public Account(String page) {
        sourceCode = WebHelper.getPage(page);
        if (sourceCode.equals("not found")) {
            System.out.println("new Account " + page + " not found"); // comment out
            kill = true;
        } else {
            kill = false;
        }
    }

    public void setSource(String page) {
        sourceCode = WebHelper.getPage(page);
        if (sourceCode.equals("not found")) {
            System.out.println("setSource Account " + page + " not found"); // comment out
            kill = true;
        } else {
            kill = false;
        }
    }

    public boolean shouldKill() {
        return kill;
    }

    public int getFollowers() {

        return Integer.parseInt(sourceCode.split(Pattern.quote("\"followed_by\": {\"count\": "))[1].split("}")[0]);
    }

    public boolean isVerified() {
        if (sourceCode.contains("\"is_verified\": false,")) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isPrivate() {
        if (sourceCode.contains("\"is_private\": false,")) {
            return false;
        } else {
            return true;
        }
    }

    public String getEmail() {
        String ePattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]+";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(getBiography());
        if (m.find()) {
            return m.group();
        } else {
            return "not found";
        }
    }

    public String getBiography() {
        //"biography":"
        //","external_url":
        if (!sourceCode.contains("\"biography\": \"")) {
            return "";
        }
        return sourceCode.split(Pattern.quote("\"biography\": \""))[1].split("\", \"external_url\":")[0].replace("\\n", " ").replaceAll("\\\\u.{4}", "");
    }

    public String[] getImages() {
        //{"code":"
        //","owner"
        //System.out.println(sourceCode);
        String[] stageOne = sourceCode.split(Pattern.quote("\"code\": \""));
        //System.out.println(stageOne[0]);
        String[] stageTwo = new String[stageOne.length - 1];

        for (int i = 0; i < stageOne.length - 1; i++) {
            stageTwo[i] = stageOne[i + 1].split("\", \"date\"")[0];
        }

        return stageTwo;
    }
}
