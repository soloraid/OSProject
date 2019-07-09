import java.util.ArrayList;
import java.util.concurrent.locks.*;

class Clerk extends Thread {
    private static String[] boxes;
    private static ArrayList<Integer> busyBoxes;
    private static final Lock lock = new ReentrantLock();

    Clerk() {
        boxes = new String[100];
        for(int i = 0 ; i < 100 ; i ++)
        {
            boxes[i] = "";
        }
        busyBoxes = new ArrayList<>();
    }

    //gets bags from the pilgrim and puts them in boxes if there is space available.
    boolean getBagsFromPilgrim(String name, int number) {
        if (getNewBagsIndex(name) == -1) { //if there is no bags with this name
            lock.lock();
            int index = bestFitIndex(number);
            if (index != -1 && !isBoxBusy(index, number)) {
                setBusyBoxes(index, number);
                lock.unlock();
                addBags(name, number, index);
                removeBusyBoxes(index, number);
                return true;
            }
            else {
                System.out.println("no BestFit place found");
                lock.unlock();
                return false;
            }
        }
        else { //if there is bags with this name
            int index = getNewBagsIndex(name);
            lock.lock();
            if (enoughBoxesInPlace(number, index)) { // if there is enough empty boxes after the boxes with this name
                setBusyBoxes(index, number);
                lock.unlock();
                addBags(name, number, getNewBagsIndex(name));
                removeBusyBoxes(index, number);
                return true;
            } else if (enoughBoxesInShrine(number)) { // if we have enough empty boxes in the shrine
                reArrange(name);
                index = getNewBagsIndex(name);
                setBusyBoxes(index, number);
                lock.unlock();
                addBags(name, number, getNewBagsIndex(name));
                removeBusyBoxes(index, number);
                return true;
            } else {
                System.out.println("problem: not enough space. please wait!");
                lock.unlock();
                return false;
            }
        }

    }

    // empties the boxes with this pilgrim's bags
    void giveBagsToPilgrim(String name) {
        lock.lock();
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i] == name)
                boxes[i] = "";
        }
        lock.unlock();
    }
    // rearranges the boxes so we can put new bags for this guy. ( defragmentation )
    private void reArrange(String name) {
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<Integer> pilgrimBoxesSize = new ArrayList<>();
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i] != "") {
                int size = 0;
                String tempId = boxes[i];
                nameList.add(tempId);
                while (boxes[i] == tempId) {
                    size++;
                    i++;
                    if (i == boxes.length)
                        break;
                }
                i--;
                pilgrimBoxesSize.add(size);
            }
        }
        String temp[] = new String[boxes.length];
        for(int i = 0 ; i < boxes.length ; i ++)
            temp[i] = "";
        int i = 0;
        for (String pilgrim : nameList) {
            if (pilgrim != name) {
                for (int j = 0; j < pilgrimBoxesSize.get(nameList.indexOf(pilgrim)); j++) {
                    temp[i] = pilgrim;
                    i++;
                }
            }
        }
        for (int j = 0; j < pilgrimBoxesSize.get(nameList.indexOf(name)); j++) {
            temp[i] = name;
            i++;
        }
        boxes = temp;
    }

    // check if there is enough boxes in shrine to put n new boxes
    private boolean enoughBoxesInShrine(int n) {
        int space = 0;
        for (String box : boxes) {
            if (box == "")
                space++;
        }
        return (space >= n);
    }

    //check if there is enough boxes after the given index
    private boolean enoughBoxesInPlace(int n, int index) {
        for (int i = index; i < index + n; i++) {
            if (i >= boxes.length || boxes[i] != "")
                return false;
        }
        return true;
    }

    //fill the boxes with new bags
    private void addBags(String name, int n, int index) {
        for (int i = index; i < index + n; i++) {
            boxes[i] = name;
        }
    }

    //finds the index to add new bags for this particular pilgrim
    private int getNewBagsIndex(String name) {
        for (int i = boxes.length - 1; i >= 0; i--) {
            if (boxes[i] == name) {
                return i + 1;
            }
        }
        return -1;
    }

    //finds empty blocks and chooses the block with minumum size
    private int bestFitIndex(int n) {
        lock.lock();
        ArrayList<Integer> startIndexes = new ArrayList<>();
        ArrayList<Integer> blockSizes = new ArrayList<>();
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i] == "") {
                int size = 0;
                startIndexes.add(i);
                while (boxes[i] == "") {
                    size++;
                    i++;
                    if (i == boxes.length)
                        break;
                }
                blockSizes.add(size);
            }
        }
        lock.unlock();
        while (true) {
            int min = 1<<29;
            for (Integer blockSize : blockSizes) {
                if (blockSize < min) {
                    min = blockSize;
                }
            }
            if (!blockSizes.contains(min)) { // no place was found
                return -1;
            }
            if (n <= min) { // if n ( number of bags ) is less than minumum. we use this.
                return startIndexes.get(blockSizes.indexOf(min));
            } else { //n is bigger than minimum so we set this blocksize to max and it never gets chosen
                blockSizes.set(blockSizes.indexOf(min), 1<<30);
            }
        }

    }

    //sets boxes that are gonna be busy while bags are being added to them
    private void setBusyBoxes(int startIndex, int n) {
        for (int i = startIndex; i < startIndex + n; i++) {
            busyBoxes.add(i);
        }
        System.out.println("busy boxes: " + busyBoxes);
    }

    // removes from the busy boxes array because the boxes are no longer busy
    private void removeBusyBoxes(int startIndex, int n) {
        lock.lock();
        for (int i = startIndex; i < startIndex + n; i++) {
            if (i >= boxes.length)
                break;
            if (!busyBoxes.contains(i))
                continue;
            busyBoxes.remove(busyBoxes.indexOf(i));
        }
        lock.unlock();
    }

    private boolean isBoxBusy(int startIndex, int n) {
        for (int i = startIndex; i < startIndex + n; i++) {
            if (busyBoxes.contains(i))
                return true;
        }
        return false;
    }

    //prints the current status of boxes
    static void printStatus() {
        lock.lock();
        System.out.print("[");
        for(int i = 0 ; i < boxes.length ; i ++)
        {
            System.out.print(" " + boxes[i] + " ,");
        }
        System.out.println("]");
        lock.unlock();
    }

}