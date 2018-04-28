public class UserTools {

    public static boolean isVerified(String page) {
        return !WebHelper.getPage(page).contains("\"is_verified\":false");
    }

    public static int getFollowers(String page) {
        return Integer.parseInt(WebHelper.getPage(page).split("\"followed_by\":{\"count\":")[1].split("}")[0]);
    }

    public static void main(String[] args) {
        //System.out.println(isVerified("https://instagram.com/danbilzerian/"));
    }

}
