package me.fernthedev.lightclientandroidv2.backend;

import android.support.annotation.Keep;

import com.github.fernthedev.client.WaitForCommand;
@Keep
public class AWaitForCommand extends WaitForCommand {

    static boolean running;

    private AClient client;
    private boolean checked;

    public AWaitForCommand(AClient client) {
        super(client);
    }


    @Override
    public void run() {
        running = true;

    }



}
