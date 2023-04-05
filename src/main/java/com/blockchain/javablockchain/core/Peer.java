package com.blockchain.javablockchain.core;


/*
* Peer - is a main part of P2P blockchain, because peers are miners, clients
* and servers at the same time
*
* About serialization:
* We use JSON (Jackson serialization)
*/

import com.blockchain.javablockchain.http.HttpServer;
import com.blockchain.javablockchain.utils.MessageType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Slf4j
@NoArgsConstructor
@Data
public class Peer {

    private int port;
    private String name;
    private String address;
    private List<Peer> allPeers;
    private List<Block> blockchain;

    private final HttpServer HTTP_SERVER = new HttpServer(this.port, serverSocket, EXECUTOR, this, this.blockchain);

    private static ServerSocket serverSocket;
    private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(10);

    public Peer(int port, String address, String name, Block root, List<Peer> peers) {
        this.port = port;
        this.address = address;
        this.name = name;
        this.allPeers = peers;
        blockchain.add(root);
    }

    public Block createBlock() {
        if (blockchain.isEmpty()) return null;

        Block previousBlock = this.getLatestBlock();
        if (previousBlock == null) return null;

        final int index = previousBlock.getIndex() + 1;
        Block block = new Block(index, previousBlock.getHash(), name);
        return block;
    }

    public void addBlock(Block block) {
        if (isBlockValid(block)) blockchain.add(block);
    }

    private Block getLatestBlock() {
        if (blockchain.isEmpty()) return null;
        return blockchain.get(blockchain.size() - 1);
    }

    private boolean isBlockValid(Block block) {
        final Block latestBlock = this.getLatestBlock();
        if (latestBlock == null) return false;

        final int expected = block.getIndex() + 1;

        // It's always true (?)
//        if (block.getIndex() != expected) {
//            log.info(String.format("Invalid index. Expected: $s. Actual: %s", expected, block.getIndex()));
//            return false;
//        }

        if (Objects.equals(block.getPreviousHash(), latestBlock.getHash())) {
            log.info("Unmatched hash code");
            return false;
        }
        return true;
    }

    public void startPeerHost() {
        this.HTTP_SERVER.startHost();
    }

    public void stopPeerHost() {
        this.HTTP_SERVER.stopHost();
    }

    public void broadcastPeer(MessageType type, Block block, List<Peer> peers) {
        this.HTTP_SERVER.broadcast(type, block, peers);
    }

    public void sendMessage(MessageType type, int port, String host, Block ...blocks) {
        this.HTTP_SERVER.sendMessage(type, port, host, blocks);
    }

}
