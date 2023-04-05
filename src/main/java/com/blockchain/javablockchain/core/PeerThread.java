package com.blockchain.javablockchain.core;

import com.blockchain.javablockchain.utils.MessageType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class PeerThread implements Runnable {

    private Socket client;
    private Peer peer;

    @Override
    public void run() {
        try (
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(this.client.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(this.client.getInputStream())
                )
        {
            ChainMessage chainMessage = new ChainMessage.ChainMessageBuilder()
                    .sender(this.peer.getPort())
                    .messageType(MessageType.READY)
                    .build();

            objectOutputStream.writeObject(chainMessage);

            Object fromClientObject;

            while ((fromClientObject = objectInputStream.readObject()) != null) {
                if (fromClientObject instanceof ChainMessage) {
                    ChainMessage message = (ChainMessage) fromClientObject;

                    if (MessageType.INFO_NEW_BLOCK.equals(message.messageType)) {
                        if (message.blocks.isEmpty()) {
                            log.error(String.format("Invalid block received : %s", message.blocks));
                        }

                        synchronized (this.peer) {
                            peer.addBlock(message.blocks.get(0));
                        }

                        break;
                    }
                    else if (MessageType.REQ_ALL_BLOCKS.equals(message.messageType)) {
                        objectOutputStream.writeObject(new ChainMessage.ChainMessageBuilder()
                                .sender(peer.getPort())
                                .messageType(MessageType.RSP_ALL_BLOCKS)
                                .blocks(peer.getBlockchain())
                                .build()
                        );
                    }
                }
            }
            client.close();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

}
