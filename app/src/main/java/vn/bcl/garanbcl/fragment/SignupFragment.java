package vn.bcl.garanbcl.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.util.AsyncListUtil;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cielyang.android.clearableedittext.ClearableEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vn.bcl.garanbcl.LoginActivity;
import vn.bcl.garanbcl.R;
import vn.bcl.garanbcl.users.SmartUser;
import vn.bcl.garanbcl.util.LoginType;
import vn.bcl.garanbcl.util.SmartLogin;
import vn.bcl.garanbcl.util.SmartLoginConfig;
import vn.bcl.garanbcl.util.SmartLoginFactory;


public class SignupFragment extends Fragment {

    private UserSignupTask mAuthTask = null;

    // UI references.
    private ClearableEditText mEmailView;
    private ClearableEditText mPasswordView;
    private ClearableEditText mRePasswordView;
    private ClearableEditText mNameView;
    private View mProgressView;
    private View mSignupFormView;
    private static final String EMAIL = "email";

    private TextView mEmailSignUpButton, signInNowButton;
    SmartUser currentUser;
    SmartLoginConfig config;
    SmartLogin smartLogin;

    protected FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    public SignupFragment() {}

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);

        bindViews();
        setListeners();

        return view;
    }

    private void setListeners() {
        mEmailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignup();
            }
        });

        mNameView.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_NULL || id == KeyEvent.KEYCODE_ENTER) {
                    mEmailView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mNameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ((LoginActivity) getActivity()).hideKeyboard(mNameView);
                } else{
                    ((LoginActivity) getActivity()).showKeyboard(mNameView);
                }
            }
        });


        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_NULL || id == KeyEvent.KEYCODE_ENTER) {
                    mPasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mEmailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ((LoginActivity) getActivity()).hideKeyboard(mEmailView);
                } else{
                    ((LoginActivity) getActivity()).showKeyboard(mEmailView);
                }
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_NULL || id == KeyEvent.KEYCODE_ENTER) {
                    mRePasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });
        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ((LoginActivity) getActivity()).hideKeyboard(mPasswordView);
                } else{
                    ((LoginActivity) getActivity()).showKeyboard(mPasswordView);
                }
            }
        });

        mRePasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptSignup();
                    return true;
                }
                return false;
            }
        });
        mRePasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ((LoginActivity) getActivity()).hideKeyboard(mRePasswordView);
                } else{
                    ((LoginActivity) getActivity()).showKeyboard(mRePasswordView);
                }
            }
        });


        signInNowButton.setOnClickListener(new TextView.OnClickListener(){

            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).changeTab(0);
            }
        });
    }

    private void bindViews() {
        signInNowButton = view.findViewById(R.id.login_now);
        mNameView = view.findViewById(R.id.name);
        mEmailView = view.findViewById(R.id.email);
        mPasswordView = view.findViewById(R.id.password);
        mRePasswordView = view.findViewById(R.id.repassword);
        mEmailSignUpButton = view.findViewById(R.id.email_sign_up_button);
        mSignupFormView = view.findViewById(R.id.signup_form);
        mProgressView = view.findViewById(R.id.login_progress);
    }

    private void attemptSignup() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mRePasswordView.setError(null);

        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String repassword = mRePasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_short_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(repassword) && !isPasswordValid(repassword) && !password.equals(repassword)) {
            mRePasswordView.setError(getString(R.string.error_repassword));
            focusView = mRePasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        } else if (!isNameValid(name)) {
            mNameView.setError(getString(R.string.error_invalid_email));
            focusView = mNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserSignupTask();
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isNameValid(String name) {
        return false;
    }

    private boolean isEmailValid(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
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

            mSignupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignupFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mSignupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserSignupTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            mEmailView.requestFocus();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);

            mEmailView.requestFocus();
        }


    }

}
