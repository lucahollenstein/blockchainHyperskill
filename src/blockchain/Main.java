package blockchain;
 
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
 
public class Main {
    public static void main(String[] args) {
 
        Blockchain blockchain = Blockchain.get();
        System.out.println();
        Future<String> result = blockchain.setBlockLimit(5);
        blockchain.startMining(10);
        try {
            System.out.println(result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
