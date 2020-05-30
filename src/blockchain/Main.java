package blockchain;

import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int numBlocks = 5;
        String previousHash = "0";
        System.out.println("Enter how many zeros the hash must start with: " );
        int prefix = sc.nextInt();
        for(int i = 1; i <= numBlocks; i++){
            long timeStamp = new Date().getTime();
            Block block = new Block(previousHash, timeStamp, i, prefix);
            System.out.println(block.toString());


            previousHash = block.getHash();
        }

    }
}