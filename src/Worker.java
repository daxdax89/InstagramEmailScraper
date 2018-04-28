
public class Worker implements Runnable {

    private boolean running = true;
    private int id = -1;

    //Should only be account pages
    //Ex: https://instagram.com/danbilzerian/
    public Worker(int id) {
        this.id = id + 1;
    }

    public void setPage(String page) {
    }

    @Override
    public void run() {

        while (running) {
            try {
                if (Main.findAccounts == 0) {
                    System.out.println("Done finding accounts, closing thread " + id);
                    running = false;
                    return;
                }
                if (System.currentTimeMillis() >= Main.finishTime) {
                    System.out.println(System.currentTimeMillis());
                    System.out.println("Time has run out, closing thread " + id);
                    running = false;
                    return;
                }

                //System.out.println("wow121");
                String accPath = Main.getNext();
                if (accPath.equals("empty")) {
                    try {
                        long sleep = (long) (Math.random() * 5000);
                        //System.out.println("["+id+"] Accounts list is empty, sleeping for "+ sleep + "ms"); // comment out
                        Thread.sleep(sleep);
                        continue;
                    } catch (InterruptedException e) {
                    }
                }

                Account a = new Account(accPath);
                int icheck = 0;
                do {
                    if (a.shouldKill()) {
                        a.setSource(accPath);
                        try {
                            long sleep = (long) (Math.random() * 5000);
                            System.out.println("[" + id + "] Account (" + accPath + ") invalid, sleeping for " + sleep + "ms, try " + icheck); // comment out
                            Thread.sleep(sleep);
                            icheck++;
                            continue;
                        } catch (InterruptedException e) {
                        }
                    } else {
                        icheck = 5;
                    }
                    icheck++;
                } while (icheck <= 5);
                if (a.shouldKill()) { //if their page is not found
                    System.out.println("[" + id + "][" + accPath.split("/")[accPath.split("/").length - 1] + "]: invalid or deleted account");
                    new Thread().start();
                    continue;
                }

                String[] imagePaths = a.getImages();
                if (imagePaths.length == 0) {
                    //System.out.println("["+id+"] imagePaths.length equals 0"); // comment out
                    continue;
                }
                int loop = 2;
                if (Main.amountOfThreads >= imagePaths.length) {
                    loop = imagePaths.length;
                } else {
                    loop = Main.amountOfThreads;
                }
                for (int i = 0; i < loop; i++) {
                    //System.out.println("["+id+"] https://www.instagram.com/p/"+imagePaths[i]); // comment out
                    new Thread(new Scraper("https://www.instagram.com/p/" + imagePaths[i])).start();
                }

                if (a.getFollowers() < Main.minFollowers) {
                    System.out.println("[" + id + "][" + accPath.split("/")[accPath.split("/").length - 1] + "]: not enough followers");
                    continue;
                }

                if (a.isVerified()) {
                    System.out.println("[" + id + "][" + accPath.split("/")[accPath.split("/").length - 1] + "]: account verified");
                    continue;
                }

                String email = a.getEmail();
                if (email.equals("not found") || email.contains("hotmail") || email.contains("msn") || email.contains("outlook")) {
                    System.out.println("[" + id + "][" + accPath.split("/")[accPath.split("/").length - 1] + "]: No email or Hotmail/MSN/Outlook email rejected");
                    //System.out.println(id+" - "+"no email");
                } else {
                    String StrStartName = Main.getStartName();
                    System.out.println("[" + id + "][" + accPath.split("/")[accPath.split("/").length - 1] + "]: FOUND NEW ACCOUNT");
                    Main.foundAccount();
                    //System.out.println(accPath);
                    //System.out.println(accPath.split("/")[accPath.split("/").length-1] + ":"+email);
                    Main.writeToFile(accPath.split("/")[accPath.split("/").length - 1] + ":" + email);
                    WebHelper.sendGetRequest("https://www.mailcollector.nl/collecting/collect.php?apikey=zXPwYUIpyt1E6TL7IU8NCdO5BguVUWgQ&&username=" + accPath.split("/")[accPath.split("/").length - 1] + "&&email=" + email + "&&startname=" + StrStartName);

                }
                //System.out.println("["+id+"] Mark account as crawled"); // comment out
                Main.addToAlreadyCrawled(accPath);

            } catch (Exception e) {
                System.out.println("[" + id + "] Error in worker; quitting");
                running = false;
                Thread thread = new Thread(new Scraper("https://www.instagram.com/p/"));
                return;
            }
        }
    }
}
