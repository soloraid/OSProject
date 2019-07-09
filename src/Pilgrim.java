public class Pilgrim extends Thread{
    private String pilgrimName;
    private boolean isIn;
    private Clerk clerk;
    Pilgrim(String name, Clerk clerk) {
        pilgrimName = name;
        isIn = false;
        this.clerk = clerk;
    }

    //pilgrim goes in and gives his bags to the clerk.
    private void goIn(Clerk clerk) {
        int bags = (int) (Math.random() * 10 + 1);
        System.out.println(pilgrimName + " gave " + bags + " bags");
        while (!clerk.getBagsFromPilgrim(pilgrimName, bags)) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(pilgrimName + " is waiting with " + bags + " bags");
        }
        clerk.printStatus();
    }

    //pilgrim goes out and gets his bag from the clerk
    private void goOut(Clerk clerk) {
        clerk.giveBagsToPilgrim(pilgrimName);
        System.out.println(pilgrimName + " went out with all of his bags");
        Clerk.printStatus();
    }

    @Override
    public void run() {
        goIn(clerk);
        isIn = true;
        while (isIn) {
            try {
                Thread.sleep((long) (3000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            double probability = Math.random();
            if (probability >= 0.2)
                goIn(clerk);
            else {
                goOut(clerk);
                isIn = false;
            }
        }
    }

}
