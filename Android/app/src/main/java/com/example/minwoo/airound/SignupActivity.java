package com.example.minwoo.airound;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class SignupActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView signup_mEmailView;
    private EditText signup_mPasswordView;
    private EditText signup_mConfirmPasswordView;

    private CheckBox signup_checkbox;

    private Button signup_mSignupButton;

    private View signup_mProgressView;
    private View signup_mLoginFormView;
;
    Animation fade_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Set up the login form.
        signup_mEmailView = (AutoCompleteTextView) findViewById(R.id.signup_email);

        final View focusView = null;

        signup_mPasswordView = (EditText) findViewById(R.id.signup_password);
        signup_mConfirmPasswordView = (EditText)findViewById(R.id.signup_confirm);


        signup_mSignupButton = (Button) findViewById(R.id.signup_submit1);
        signup_mSignupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignup();
            }
        });

        signup_checkbox = (CheckBox)findViewById(R.id.signup_checkBox);
        signup_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(signup_checkbox.isChecked())
                    signup_mSignupButton.setEnabled(true);
                else
                    signup_mSignupButton.setEnabled(false);
            }
        });


        signup_mLoginFormView = findViewById(R.id.signup_form);
        signup_mProgressView = findViewById(R.id.signup_progress);

        fade_in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);

    }//Oncreate


    private void attemptSignup() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        signup_mEmailView.setError(null);
        signup_mPasswordView.setError(null);
        signup_mConfirmPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = signup_mEmailView.getText().toString();
        String password = signup_mPasswordView.getText().toString();
        String cpw = signup_mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.

       //password
        if (TextUtils.isEmpty(password)) {
            signup_mPasswordView.setError(getString(R.string.error_field_required));
            focusView = signup_mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            signup_mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = signup_mPasswordView;
            cancel = true;
        }

        //비밀번호 확인
        if (TextUtils.isEmpty(cpw)) {
            signup_mConfirmPasswordView.setError(getString(R.string.error_field_required));
            focusView = signup_mConfirmPasswordView;
            cancel = true;
        }else if(!password.equals(cpw)) {
            signup_mConfirmPasswordView.setError(getString(R.string.error_dismach_password));
            focusView = signup_mConfirmPasswordView;
            cancel = true;
        }





        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            signup_mEmailView.setError(getString(R.string.error_field_required));
            focusView = signup_mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            signup_mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = signup_mEmailView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }
            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //서버에 아이디 비번 등록 요청










                //finish();
            } else {

                signup_mPasswordView.setError(getString(R.string.error_incorrect_password));
                signup_mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
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

            signup_mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            signup_mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    signup_mLoginFormView.setVisibility(show ? View.GONE : View.GONE);
                }
            });

            signup_mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            signup_mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    signup_mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            signup_mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            signup_mLoginFormView.setVisibility(show ? View.GONE : View.GONE);
        }
    }












    //////////////////////////////////////////////////////////////
    /////////////////////자동 생성된것들//////////////////////////
    //////////////////////////////////////////////////////////////
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
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(SignupActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        signup_mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////
}

