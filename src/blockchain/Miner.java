package blockchain;

public class Miner implements Runnable {

    final private int id;
    final private Blockchain blockchain;

    public Miner(int num, Blockchain blockchain) {
        this.id = num;
        this.blockchain = blockchain;
    }

    @Override
    public void run() {
        while (blockchain.isMinersWorking()) {
            Block newBlock = new Block(id, blockchain);
            if (newBlock.isValid) blockchain.addNewBlock(newBlock);
        }
    }
}