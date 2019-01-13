package vn.bcl.garanbcl.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.cielyang.android.clearableedittext.ClearableEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;

import vn.bcl.garanbcl.AccountActivity;
import vn.bcl.garanbcl.R;
import vn.bcl.garanbcl.util.Constants;


public class AccountInfoFragment extends Fragment {

    // UI references.
    private ClearableEditText mEmailView;
    private ClearableEditText mPasswordView;
    private ClearableEditText mRePasswordView;
    private ClearableEditText mNameView;
    private ClearableEditText mPhoneView;
    private View mProgressView;
    private View mInfoFormView;
    private static final String EMAIL = "email";

    private TextView mSaveInfoButton;

    protected FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public AccountInfoFragment() {}

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_info, container, false);

        bindViews();
        setListeners();

        return view;
    }

    private void setListeners() {
        mSaveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSave();
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
                    ((AccountActivity) getActivity()).hideKeyboard(mNameView);
                } else{
                    ((AccountActivity) getActivity()).showKeyboard(mNameView);
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
                    ((AccountActivity) getActivity()).hideKeyboard(mEmailView);
                } else{
                    ((AccountActivity) getActivity()).showKeyboard(mEmailView);
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
                    ((AccountActivity) getActivity()).hideKeyboard(mPasswordView);
                } else{
                    ((AccountActivity) getActivity()).showKeyboard(mPasswordView);
                }
            }
        });

        mRePasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptSave();
                    return true;
                }
                return false;
            }
        });
        mRePasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ((AccountActivity) getActivity()).hideKeyboard(mRePasswordView);
                } else{
                    ((AccountActivity) getActivity()).showKeyboard(mRePasswordView);
                }
            }
        });
    }

    private void bindViews() {
        mNameView = view.findViewById(R.id.name);
        mEmailView = view.findViewById(R.id.email);
        mPhoneView = view.findViewById(R.id.phone);
        mPasswordView = view.findViewById(R.id.password);
        mRePasswordView = view.findViewById(R.id.repassword);
        mSaveInfoButton = view.findViewById(R.id.save_info_button);
        mInfoFormView = view.findViewById(R.id.info_form);
        mProgressView = view.findViewById(R.id.login_progress);

        mNameView.setText(mAuth.getCurrentUser().getDisplayName());
        mPhoneView.setText(mAuth.getCurrentUser().getPhoneNumber());
        mEmailView.setText(mAuth.getCurrentUser().getEmail());
    }

    private void attemptSave() {
        // Reset errors.
        mNameView.setError(null);
        mEmailView.setError(null);
        mPhoneView.setError(null);
        mPasswordView.setError(null);
        mRePasswordView.setError(null);

        final String name = mNameView.getText().toString();
        final String email = mEmailView.getText().toString();
        final String phone = mPhoneView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final String repassword = mRePasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password) || password.isEmpty()) {
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
            mNameView.setError(getString(R.string.error_invalid_name));
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
            FirebaseUser user = mAuth.getCurrentUser();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profileUpdates);

            user.updateEmail(email);
            if(password != null || !password.isEmpty())
                user.updatePassword(password);


            showProgress(false);
            Toast.makeText(getActivity(), "Lưu thành công!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNameValid(String name) {
        return (name.length() < 10 || name.indexOf(" ") < 0 || name.startsWith(" ") || name.matches(".*\\d+.*"));
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

            mInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mInfoFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
