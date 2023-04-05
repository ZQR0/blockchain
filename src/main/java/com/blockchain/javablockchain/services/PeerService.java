package com.blockchain.javablockchain.services;

import com.blockchain.javablockchain.core.Block;
import com.blockchain.javablockchain.core.Peer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PeerService {

    private final List<Peer> peers = new ArrayList<>();
    private final String HOST_NAME = "localhost";
    private static final Block ROOT_BLOCK = new Block(0, "ROOT", "ROOT_HASH");

    public Peer addPeer(int port, String name) {
        Peer peer = new Peer(port, this.HOST_NAME, name, ROOT_BLOCK, this.peers);
        peer.startPeerHost();
        peers.add(peer);
        log.info(String.format("Peer %s:%d added", name, port));

        return peer;
    }

    public Peer getPeer(String name) {
        for (Peer p : peers) {
            if (p.getName().equals(name)) return p;
        }

        log.error(String.format("Peer with name %s not found", name));
        return null;
    }

    public List<Peer> getPeers() {
        return this.peers;
    }

    public void deletePeer(String name) {
        Peer peer = this.getPeer(name);
        if (peer != null) {
            peer.stopPeerHost();
            this.peers.remove(peer);
        }
    }

    public List<Block> getPeerBlockchain(String name) {
        Peer peer = this.getPeer(name);
        if (peer != null) return peer.getBlockchain();

        return null;
    }

    public void deleteAllPeers() {
        for (Peer p : this.peers) {
            p.stopPeerHost();
        }

        this.peers.clear();
    }

    public Block createBlock(String name) {
        Peer peer = this.getPeer(name);

        if (peer != null) return peer.createBlock();
        return null;
    }

}
