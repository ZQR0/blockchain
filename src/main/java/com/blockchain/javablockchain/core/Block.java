package com.blockchain.javablockchain.core;

import com.blockchain.javablockchain.utils.KeyUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@Data
public class Block implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final KeyUtils keyUtils = new KeyUtils();

    private int index;
    private String creator;
    private String hash;
    private String previousHash;
    private Long timestamp;

    public Block(int index, String creator, String previousHash) {
        this.index = index;
        this.creator = creator;
        this.hash = this.keyUtils.calculateHash(String.valueOf(index) + previousHash + String.valueOf(timestamp));
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Block {"
                + "index: " + index + ",\n"
                + "creator: " + creator + ",\n"
                + "hash: " + hash + ",\n"
                //+ "previousHash: " + previousHash + ",\n"
                //+ "timestamp: " + timestamp + ",\n"
                + "}";
        // TODO: make code more beautiful
    }

    @Override
    public int hashCode() {
        int hashCode = index;
        hashCode = 31 * hashCode + timestamp.hashCode();
        hashCode = 31 * hashCode + creator.hashCode();
        hashCode = 31 * hashCode + hash.hashCode();
        hashCode = 31 * hashCode + previousHash.hashCode();

        return hashCode;
    }

    @Override
    public boolean equals(@NonNull Object object) {
        if (this == object) return true;
        if (getClass() != object.getClass()) return false;

        Block block = (Block) object;
        return index == block.index
                && creator.equals(block.creator)
                && hash.equals(block.hash)
                && previousHash.equals(block.hash)
                && timestamp.equals(block.timestamp);
    }

}
