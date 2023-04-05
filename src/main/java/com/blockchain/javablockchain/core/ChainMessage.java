package com.blockchain.javablockchain.core;

import com.blockchain.javablockchain.utils.MessageType;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Builder
public class ChainMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public int sender;
    public int receiver;
    public MessageType messageType;
    public List<Block> blocks;

    @Override
    public String toString() {
        return String.format("Message { sender: %s, receiver: %s, messageType: %s, blocks: %s }",
                this.sender, this.receiver, this.messageType, this.blocks);
    }
}
