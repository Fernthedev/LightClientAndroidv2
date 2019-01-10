package me.fernthedev.lightclientandroidv2.backend.netty;

import android.app.AlertDialog;
import android.support.annotation.Keep;
import android.widget.EditText;

import com.github.fernthedev.client.EventListener;
import com.github.fernthedev.client.netty.ClientHandler;
import com.github.fernthedev.packets.ConnectedPacket;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.logging.Level;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import me.fernthedev.lightclientandroidv2.R;
import me.fernthedev.lightclientandroidv2.ServerLoginActivity;
import me.fernthedev.lightclientandroidv2.backend.AClient;
@Keep
@ChannelHandler.Sharable
public class AClientHandler extends ClientHandler {


    private AClient client;

    public AClientHandler(AClient client, EventListener listener, ServerLoginActivity serverLoginActivity) {
        super(client, listener);
        this.client = client;
        this.serverLoginActivity = serverLoginActivity;
        mAddressView = serverLoginActivity.findViewById(R.id.address);
        String os = client.getOSName();
        connectedPacket = new ConnectedPacket(client.name,os);
    }

    private EditText mAddressView;
    private ServerLoginActivity serverLoginActivity;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        boolean register = client.registered;
        super.channelActive(ctx);
        client.registered = register;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        client.getClientThread().registering = false;
        client.registered = false;

        serverLoginActivity.getmAuthTask().cancel(true);
        serverLoginActivity.getmAuthTask().dialog.dismiss();

        serverLoginActivity.runOnUiThread(() -> {
            //mAddressView.setError(serverLoginActivity.getString(R.string.error_field_required));
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(serverLoginActivity, android.R.style.Theme_Material_Dialog_Alert);
            builder.setTitle("Error")
                    .setMessage(serverLoginActivity.getString(R.string.error_address_notfound))
                    .setNegativeButton(android.R.string.ok, (dialog, which) -> {
                        // do nothing
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        AClient.getLogger().log(Level.WARNING, "Server could not be found");
        client.getClientThread().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /*
        if(cause instanceof ConnectTimeoutException || cause.getCause() instanceof ConnectTimeoutException) {
            serverLoginActivity.runOnUiThread(() -> {
                mAddressView.setError(serverLoginActivity.getString(R.string.error_field_required));
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(serverLoginActivity, android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Error")
                        .setMessage(serverLoginActivity.getString(R.string.error_field_required))
                        .setNegativeButton(android.R.string.ok, (dialog, which) -> {
                            // do nothing
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                AClient.getLogger().log(Level.WARNING, "Server could not be found.");
            });


        }else{*/

        client.getClientThread().registering = false;

            serverLoginActivity.runOnUiThread(() -> {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(serverLoginActivity, 0);
                builder.setTitle("Error: " + cause.getMessage())
                        .setMessage(ExceptionUtils.getStackTrace(cause))
                        .setNegativeButton(android.R.string.ok, (dialog, which) -> {
                            // do nothing
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            });

            AClient.getLogger().log(Level.WARNING,cause.getMessage(),cause);

            client.getClientThread().close();
      //  }
    }




}
