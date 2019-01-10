package me.fernthedev.lightclientandroidv2;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import java.util.logging.LogRecord;

import me.fernthedev.lightclientandroidv2.backend.AWaitForCommand;
@Keep
public class ConsoleIO extends AppCompatActivity {

    private static final String newline = System.getProperty("line.separator");

    private TextView mMessage;
    private Button mSendButton;
    private static TextView mConsoleOutput;

    private static AWaitForCommand aWaitForCommand;

    public static void setaWaitForCommand(AWaitForCommand aWaitForCommand) {
        ConsoleIO.aWaitForCommand = aWaitForCommand;
    }

    private static ConsoleIO consoleIO;

    private static String oldLog = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console_io);

        mMessage = findViewById(R.id.messageBox);

        consoleIO = this;

        mConsoleOutput = findViewById(R.id.consoleOutputText);

        mConsoleOutput.setFocusable(false);
        mConsoleOutput.setCursorVisible(false);
        mConsoleOutput.setText(oldLog);
        mConsoleOutput.setMovementMethod(new ScrollingMovementMethod());

        mSendButton = findViewById(R.id.send);

        mSendButton.setOnClickListener(v -> {

            aWaitForCommand.sendMessage(mMessage.getText().toString());
            mMessage.setText("");
        });


    }

    public static ConsoleIO getConsoleIO() {
        return consoleIO;
    }

    public static class LogHandler extends java.util.logging.Handler {

        private static ServerLoginActivity serverLoginActivity;

        public LogHandler(ServerLoginActivity serverLoginActivity) {
            LogHandler.serverLoginActivity = serverLoginActivity;
        }

        @Override
        public synchronized void publish(LogRecord record) {
            serverLoginActivity.runOnUiThread(() -> {
                if(mConsoleOutput != null) {
                    mConsoleOutput.append(record.getMessage() + newline);
                    // find the amount we need to scroll.  This works by
                    // asking the TextView's internal layout for the position
                    // of the final line and then subtracting the TextView's height
                    final int scrollAmount = mConsoleOutput.getLayout().getLineTop(mConsoleOutput.getLineCount()) - mConsoleOutput.getHeight();
                    // if there is no need to scroll, scrollAmount will be <=0
                    if (scrollAmount > 0)
                        mConsoleOutput.scrollTo(0, scrollAmount);
                    else
                        mConsoleOutput.scrollTo(0, 0);
                } else {
                    addLog(record.getMessage());

                }
            });

        }

        private static synchronized void addLog(String thing) {
            oldLog += thing + newline;
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() {

        }
    }
}
