package me.fernthedev.lightclientandroidv2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import me.fernthedev.lightclientandroidv2.backend.AClient;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
@Keep
public class ServerLoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    public static boolean active = false;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mAddressView;
    private EditText mPasswordView;
    private EditText mPortView;
    private View mProgressView;
    private View mLoginFormView;

    private static ServerLoginActivity serverLoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_login);
        // Set up the login form.
        mAddressView = findViewById(R.id.address);
        mPortView = findViewById(R.id.port);
        populateAutoComplete();

        mPasswordView = findViewById(R.id.password);
        /*mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });*/

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        serverLoginActivity = this;
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mAddressView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, v -> requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS));
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            mAuthTask = null;
            //return;
        }

        // Reset errors.
        mAddressView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String address = mAddressView.getText().toString();
        String password = mPasswordView.getText().toString();

        int port = 2000;
        if(!mPortView.getText().toString().equals("")) {
            port = Integer.parseInt(mPortView.getText().toString());
        }

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid address
        if (TextUtils.isEmpty(address)) {
            mAddressView.setError(getString(R.string.error_field_required));
            focusView = mAddressView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);

            AuthInfo authInfo = new AuthInfo();
            authInfo.setIp(address);
            authInfo.setPort(port);
            authInfo.setPassword(password);

            mAuthTask = new UserLoginTask();

            mAuthTask.execute(authInfo);
        }
    }

    public UserLoginTask getmAuthTask() {
        return mAuthTask;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return !password.isEmpty();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private static AClient client;


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public static class UserLoginTask extends AsyncTask<AuthInfo, Integer, Boolean> {


        protected Boolean doInBackground(AuthInfo... authInfos) {
            AuthInfo authInfo = authInfos[0];


            if(client == null) {
                client = new AClient(authInfo.getIp(), authInfo.getPort(), authInfo.getPassword(), serverLoginActivity);
            }

            client.initialize();

            while(client.getClientThread().registering) {
                try {
                    //System.out.println("Registering " + authInfo.getIp() + ":" + authInfo.getPort());

                    if(isCancelled()) break;

                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Registered finish " + client.registered);
            return client.registered;
        }

        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if(result) {
                ConsoleIO.setaWaitForCommand(client.getWaitForCommand());
                serverLoginActivity.startActivity(new Intent(serverLoginActivity, ConsoleIO.class));
            }else{
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(serverLoginActivity);

                alertDialog.setTitle("Connection error");
                alertDialog.setMessage("Unable to connect to server1");
                alertDialog.setPositiveButton("Ok", (dialoge, which) -> {

                });
                alertDialog.show();
                cancel(true);
            }
        }


        //Used as handler to cancel task if back button is pressed
        private UserLoginTask updateTask = null;
        public ProgressDialog dialog = new ProgressDialog(serverLoginActivity);

        @Override
        protected void onPreExecute(){
            System.out.println("Executing pre");
            updateTask = this;
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setOnDismissListener(dialoge -> updateTask.cancel(true));
            dialog.setMessage("Connecting to server... ");
            dialog.show();
        }



        protected void onCancelled() {
            dialog.dismiss();
        }
    }
}

