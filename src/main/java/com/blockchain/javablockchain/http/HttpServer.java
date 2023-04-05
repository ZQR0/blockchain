package com.blockchain.javablockchain.http;

import com.blockchain.javablockchain.core.Block;
import com.blockchain.javablockchain.core.ChainMessage;
import com.blockchain.javablockchain.core.Peer;
import com.blockchain.javablockchain.core.PeerThread;
import com.blockchain.javablockchain.utils.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
public class HttpServer {

    private int port;
    private ServerSocket serverSocket;
    private ScheduledThreadPoolExecutor executor;
    private Peer peer;
    private List<Block> blockchain;

    private static boolean isEnabled = false;

    public void startHost() {
        try {
            this.serverSocket = new ServerSocket(this.port);

            this.executor.execute(() -> {
                try {

                    isEnabled = true;
                    while (isEnabled) {
                        final PeerThread peerThread = new PeerThread(this.serverSocket.accept(), peer);
                        peerThread.run();
                    }
                    serverSocket.close();
                    isEnabled = false;
                } catch (IOException ex) {
                    log.error("IOException handled in startHost() method");
                }
                    });
        } catch (IOException ex) {
            log.error("IOException handled in startHost() method");
        }
    }

    public void stopHost() {
        try {
            this.serverSocket.close();
        } catch (IOException ex) {
            log.error("IOException handled");
        }
    }

    public void broadcast(MessageType type, Block block, List<Peer> peers) {
        peers.forEach(peer -> sendMessage(type, peer.getPort(), peer.getAddress(), block));
    }

    public void sendMessage(MessageType type, int port, String host, Block ...blocks) {
        try (
                final Socket socket = new Socket(peer.getAddress(), peer.getPort());
                final ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                )
        {
            Object fromPeerObject;
            while ((fromPeerObject = objectInputStream.readObject()) != null) {
                if (fromPeerObject instanceof ChainMessage) {
                    ChainMessage message = (ChainMessage) fromPeerObject;

                    log.info(String.format("%d : %s", this.port, message.toString()));

                    switch (message.messageType) {
                        case READY -> objectOutputStream.writeObject(ChainMessage.builder()
                                .sender(this.port)
                                .messageType(MessageType.READY)
                                .receiver(this.port)
                                .blocks(Arrays.asList(blocks))
                                .build()
                        );
                        case RSP_ALL_BLOCKS -> {
                            if (!message.blocks.isEmpty() && this.blockchain.size() == 1) {
                                blockchain = new ArrayList<>(message.blocks);
                            }
                        }
                    }
                }
            }

        } catch (IOException | ClassNotFoundException ex) {
            log.error("IOException handled");
        }
    }
}
