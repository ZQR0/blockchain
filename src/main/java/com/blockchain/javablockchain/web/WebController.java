package com.blockchain.javablockchain.web;


import com.blockchain.javablockchain.core.Block;
import com.blockchain.javablockchain.core.Peer;
import com.blockchain.javablockchain.services.PeerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController(value = "api/blockchain/")
@AllArgsConstructor
public class WebController {

    private final PeerService peerService;

    @GetMapping(path = "get-all-peers")
    public List<Peer> getAllPeers() {
        return this.peerService.getPeers();
    }

    @GetMapping(
            path = "get-peer",
            params = "name"
    )
    public Peer getPeerByName(@RequestParam(name = "name") String name) {
        return this.peerService.getPeer(name);
    }

    @DeleteMapping(path = "delete-peer", params = "name")
    public void deletePeer(@RequestParam(name = "name") String name) {
        this.peerService.deletePeer(name);
    }

    @DeleteMapping(path = "delete-all-peers")
    public void deleteAllPeers() {
        this.peerService.deleteAllPeers();
    }

    @PostMapping(path = "add-peer", params = { "name", "port" })
    public Peer addPeer(@RequestParam(name = "name") String name, @RequestParam(name = "port") int port) {
        return this.peerService.addPeer(port, name);
    }

    @PostMapping(path = "mine-block", params = "peer")
    public Block addBlock(@RequestParam(name = "peer") String name) {
        return this.peerService.createBlock(name);
    }

}
