package me.fernthedev.lightclientandroidv2.backend;

import android.support.annotation.Keep;

import com.github.fernthedev.client.Client;
import com.github.fernthedev.client.EventListener;
import com.github.fernthedev.packets.ConnectedPacket;
import com.github.fernthedev.packets.FillPasswordPacket;
import com.github.fernthedev.packets.RegisterPacket;
@Keep
public class AEventListener extends EventListener {

    private AClient client;

    public AEventListener(AClient client) {
        super(client);
        this.client = client;
    }

    @Override
    public void recieved(Object p) {
        if(p instanceof FillPasswordPacket) {
            Client.WaitForCommand.sendMessage(client.getPassword());
        }else if (p instanceof ConnectedPacket) {
            super.recieved(p);
            client.getClientThread().registering = false;
            client.registered = true;
        }else if (p instanceof RegisterPacket) {
            client.registered = true;
            client.getClientThread().registering = false;
            Client.getLogger().info("Successfully connected to server");
        } else super.recieved(p);
    }

}
