package blockchain;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

public class Block implements Serializable {
    final static long serialVersionUID = 2L;
    final private long id;
    final private int minerId;
    final private long timestamp;
    final private String previousHash;
    final private String blockHash;
    final private long generationTime;
    private long magicNumber;
    transient Blockchain blockchain;
    transient boolean isValid = true;


    public Block(int minerId, Blockchain blockchain) {
        this.blockchain = blockchain;
        this.minerId = minerId;

        this.id = blockchain.getLatestBlockNumber() + 1;
        this.previousHash = blockchain.getLatestBlockHash();
        this.timestamp = new Date().getTime();

//        System.out.println("Start mine block # " + id + " by miner - " + minerId);

        LocalDateTime start = LocalDateTime.now();
        this.blockHash = findMagicNumber();
        this.generationTime = Duration.between(start, LocalDateTime.now()).toSeconds();
    }


    private String findMagicNumber() {

        String hashBegin = "0".repeat(blockchain.getZerosQty());

        for (magicNumber = 0; magicNumber < Long.MAX_VALUE; magicNumber++) {
            String hash = generateHash();
            if (hash.startsWith(hashBegin)) return hash;
            if (magicNumber % 10 == 0) {
                if (id == blockchain.getLatestBlockNumber()) {
//                    System.out.println("Stop working on block " + id + " from miner" + minerId);
                    isValid = false;
                    return null;
                }
            }
        }
        return null;
    }


    public boolean validate(String previousHash) {
        if (this.previousHash == null || !this.previousHash.equals(previousHash)) return false;
        if (!generateHash().equals(this.blockHash)) return false;
        return true;
    }

    public long getId() {
        return id;
    }

    public long getGenerationTime() {
        return generationTime;
    }

    public String getBlockHash() {
        return blockHash;
    }



    @Override
    public String toString() {
        return "Block:" +
                "\nCreated by miner # " + minerId +
                "\nId: " + id + "\n" +
                "\nTimestamp: " + timestamp +
                "\nMagic number: " + magicNumber +
                "\nHash of the previous block:\n" + previousHash +
                "\nHash of the block:\n" + blockHash + '\n' +
                "\nBlock was generating for " + generationTime + " seconds\n";
    }
    private String generateHash() {
        return StringUtil.applySha256(id + " " + minerId + " " + timestamp + " " + previousHash + " " + magicNumber);
    }



}
