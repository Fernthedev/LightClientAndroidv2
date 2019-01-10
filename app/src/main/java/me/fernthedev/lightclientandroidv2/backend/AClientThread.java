package me.fernthedev.lightclientandroidv2.backend;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Keep;

import com.github.fernthedev.client.ClientThread;
import com.github.fernthedev.exceptions.DebugException;

import java.util.List;

import me.fernthedev.lightclientandroidv2.ServerLoginActivity;
import me.fernthedev.lightclientandroidv2.backend.netty.AClientHandler;
@Keep
public class AClientThread extends ClientThread {
    public boolean registering = false;
    private ServerLoginActivity serverLoginActivity;

    public AClientThread(AClient client, ServerLoginActivity serverLoginActivity) {
        super(client);
        listener = new AEventListener(client);
        clientHandler = new AClientHandler(client,listener, serverLoginActivity);
        this.serverLoginActivity = serverLoginActivity;
    }

    @Override
    public synchronized void connect() {
        registering = true;
        super.connect();
    }

    @Override
    public void sendObject(Object packet) {
        super.sendObject(packet);
    }

    @Override
    public void close() {
        new DebugException().printStackTrace();
        super.close();
        if(isAppIsInBackground(serverLoginActivity)) {
            serverLoginActivity.startActivity(new Intent(serverLoginActivity, ServerLoginActivity.class));
        }

    }

    public boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    @Override
    public void run() {

    }

}
