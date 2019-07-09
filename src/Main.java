public class Main {

    public static void main(String[] args) {
        Clerk c1 = new Clerk();
        Pilgrim p1 = new Pilgrim("ali", c1);
        Pilgrim p2 = new Pilgrim("abbas", c1);
        Pilgrim p3 = new Pilgrim("mamad", c1);
        Pilgrim p4 = new Pilgrim("hashem", c1);
        Pilgrim p5 = new Pilgrim("izad", c1);
        Pilgrim p6 = new Pilgrim("mehdi", c1);
        Pilgrim p7 = new Pilgrim("ghasem", c1);
        p1.start();
        p2.start();
        p3.start();
        p4.start();
        p5.start();
        p6.start();
        p7.start();
    }
}
