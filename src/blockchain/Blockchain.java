package blockchain;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Blockchain implements Serializable {

    final transient static String FILENAME = "blockchain.dat";
    private volatile transient int zerosQty = 0;

    private transient ExecutorService miningPool;
    volatile transient static boolean minersWorking = false;
    private transient Integer stopMiningAtBlock = null;

    private volatile int latestBlockNumber = -1;
    private volatile String latestBlockHash = "0";

    transient private volatile String output = "";
    transient private CompletableFuture<String> outputResult;

    final static long serialVersionUID = 2L;
    private ArrayList<Block> blocks;


    public static Blockchain get() {

//        try {
//            var bc = Blockchain.loadFromDisk();
//
//            if (!bc.validate()) throw new Exception("Cant validate blockchain");
//            return bc;
//        } catch (Exception e) {
//            System.out.println("Cant load blockchain from disk");
//            System.out.println(e.getMessage());
//            return new Blockchain(new ArrayList<>());
//        }
        return new Blockchain(new ArrayList<>());
    }

    public static Blockchain loadFromDisk() throws Exception {
        File file = new File(FILENAME);
        try (FileInputStream fis = new FileInputStream(file)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (Blockchain) ois.readObject();
        }
    }

    public synchronized void addNewBlock(Block newBlock) {
        if (newBlock.getId() != latestBlockNumber + 1) return;
        if (!newBlock.validate(latestBlockHash)) return;

        output += "\n" + newBlock;

        // CALCULATES zerosQty depends on generation time
        if (newBlock.getGenerationTime() > 1 && zerosQty > 0) {
            zerosQty--;
            output += "N was decreased by 1\n";
        } else if (newBlock.getGenerationTime() < 1) {
            zerosQty++;
            output += "N was increased to " + zerosQty + '\n';
        } else {
            output += "N stays the same\n";
        }

        // ADD NEW BLOCK TO BLOCKCHAIN AND WRITE IT TO DISK
        blocks.add(newBlock);
        latestBlockNumber = blocks.size() - 1;
        latestBlockHash = newBlock.getBlockHash();

        saveBlockchain();

        // CHECK IF WE HAVE MINED ENOUGH BLOCKS FOR THIS TASK
        if (stopMiningAtBlock != null &&
                latestBlockNumber >= stopMiningAtBlock) {

            stopMiningAtBlock = null;

            if (outputResult != null) {
                outputResult.complete(output);
            }

            stopMining();

        }
    }


    public Future<String> setBlockLimit(int blocksQty) {
        output = "";
        outputResult = new CompletableFuture<>();
        stopMiningAtBlock = latestBlockNumber + blocksQty;
        return outputResult;
    }

    public void startMining(int minersQty) {
        miningPool = Executors.newFixedThreadPool(minersQty);
        minersWorking = true;
        for (int i = 0; i < minersQty; i++) {
            miningPool.submit(new Miner(i, this));
        }
    }

    public void stopMining() {
        minersWorking = false;
        if (miningPool != null) {
            miningPool.shutdown();
            try {
                miningPool.awaitTermination(2, TimeUnit.SECONDS);
//                System.out.println("All mining processes stopped");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public boolean validate() {
        String prevHash = "0";
        for (var block : blocks) {
            if (!block.validate(prevHash)) {
                System.out.println("Invalid block: " + block);
                return false;
            }
            prevHash = block.getBlockHash();
        }
        return true;
    }

    public synchronized int getZerosQty() {
        return zerosQty;
    }

    private synchronized void saveBlockchain() {
        File file = new File(FILENAME);
        try {
            try (var fos = new FileOutputStream(file, false)) {
                var oos = new ObjectOutputStream(fos);
                oos.writeObject(this);
            }
        } catch (Exception e) {
            System.out.println("Cant save file " + FILENAME);
            e.printStackTrace();
        }
    }

    public int getLatestBlockNumber() {
        return latestBlockNumber;
    }

    public String getLatestBlockHash() {
        return latestBlockHash;
    }


    private Blockchain(ArrayList<Block> blocks) {
        this.blocks = blocks;
    }

    public static boolean isMinersWorking() {
        return minersWorking;
    }
}