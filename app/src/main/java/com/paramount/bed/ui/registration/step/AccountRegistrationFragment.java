package com.paramount.bed.ui.registration.step;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.paramount.bed.R;
import com.paramount.bed.data.model.ValidationSNSModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.BLEFragment;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.NetworkUtil;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;


public class AccountRegistrationFragment extends BLEFragment {
    LoginButton facebookLoginButton;
    TwitterLoginButton twitterLoginButton;
    SignInButton googleLoginButton;
    CallbackManager callbackManager;

    RegistrationStepActivity activity;
    private static final String EMAIL = "email";
    LinearLayout btnEmail, btnFacebook, btnTwitter, btnGoogle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_step_account_registration, container, false);
        activity = (RegistrationStepActivity) getActivity();

        btnEmail = (LinearLayout) view.findViewById(R.id.btnEmail);
        btnFacebook = (LinearLayout) view.findViewById(R.id.btnFacebook);
        btnTwitter = (LinearLayout) view.findViewById(R.id.btnTwitter);
        btnGoogle = (LinearLayout) view.findViewById(R.id.btnGoogle);

        //Change Request : Hide Google+ Methode
        btnGoogle.setVisibility(View.GONE);

        facebookLoginButton = (LoginButton) view.findViewById(R.id.facebookLoginButton);
        twitterLoginButton = (TwitterLoginButton) view.findViewById(R.id.twitterLoginButton);

        btnEmail.setOnClickListener(registerWithEmail());
        btnFacebook.setOnClickListener(registerWithFacebook());
        btnTwitter.setOnClickListener(registerWithTwitter());
        btnGoogle.setOnClickListener(registerWithGoogle());

        enableAction(true);

        RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
        activity.registerData.setEmail("");

        callbackManager = CallbackManager.Factory.create();

        facebookLoginButton.setPermissions(Collections.singletonList(EMAIL));
        facebookLoginButton.setFragment(this);

        // Callback registration
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFacebookUser(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                enableAction(true);
            }

            @Override
            public void onError(FacebookException exception) {
                enableAction(true);
            }
        });

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                getTwitterUser(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                enableAction(true);
            }
        });

        applyLocalization(view);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void enableAction(Boolean isEnable) {
        btnEmail.setEnabled(isEnable);
        btnFacebook.setEnabled(isEnable);
        btnTwitter.setEnabled(isEnable);
        btnGoogle.setEnabled(isEnable);
    }

    private void isSNSMethode(Boolean isSNS) {
        //Set If This SNS Or Not
        ValidationSNSModel.clear();
        ValidationSNSModel validationSNSModel = new ValidationSNSModel();
        validationSNSModel.setIsSNS(isSNS);
        validationSNSModel.insert();
    }

    private View.OnClickListener registerWithEmail() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableAction(false);
                isSNSMethode(false);

                RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
                activity.registerData.setEmail("");
                activity.registerData.setType(0);
                activity.TYPE = 0;
                activity.go(activity.FRAGMENT_EMAIL_INPUT);
                enableAction(true);
            }
        };
    }

    //#region Register SNS with Facebook
    private View.OnClickListener registerWithFacebook() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableAction(false);
                if (!NetworkUtil.isNetworkConnected(activity)) {
                    DialogUtil.offlineDialog(activity, getContext());
                    enableAction(true);
                } else {
//                    if (AccessToken.getCurrentAccessToken() != null) {
//                        getFacebookUser(AccessToken.getCurrentAccessToken());
//                    } else {
                    deleteAccessToken(getContext());
                    deleteTwitterSession(getContext());
                    facebookLoginButton.performClick();
//                    }
                }
            }
        };
    }

    public void getFacebookUser(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        isSNSMethode(true);

                        try {
                            String email = object.getString("email");
                            activity.registerData.setType(1);
                            activity.registerData.setEmail(email);
                            activity.TYPE = 1;
                            activity.ACCESS_TOKEN = accessToken.getToken();
                            activity.go(activity.FRAGMENT_EMAIL_INPUT);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            DialogUtil.createSimpleOkDialog(activity, "", "無効なアカウント", LanguageProvider.getLanguage("UI000802C003"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteAccessToken(getContext());
                                }
                            });
                        }
                        enableAction(true);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static void deleteAccessToken(Context context) {
        try {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();
            LoginManager.getInstance().logOut();
        } catch (Exception e) {

        }
    }
    //#endregion

    //#region Register SNS with Twitter
    private View.OnClickListener registerWithTwitter() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                enableAction(false);
                if (!NetworkUtil.isNetworkConnected(activity)) {
                    DialogUtil.offlineDialog(activity, getContext());
                    enableAction(true);
                } else {
                    TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
//                    if (session != null) {
//                        getTwitterUser(session);
//                    } else {
//                        twitterLoginButton.performClick();
//                    }
                    deleteAccessToken(getContext());
                    deleteTwitterSession(getContext());
                    twitterLoginButton.performClick();
                }
            }
        };
    }

    public void getTwitterUser(TwitterSession session) {
        TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                isSNSMethode(true);

                activity.registerData.setEmail(result.data);
                activity.registerData.setType(2);
                activity.TYPE = 2;
                activity.ACCESS_TOKEN = session.getAuthToken().token;
                activity.go(activity.FRAGMENT_EMAIL_INPUT);

                enableAction(true);
            }

            @Override
            public void failure(TwitterException exception) {
                enableAction(true);
            }
        });

    }

    public static void deleteTwitterSession(Context context) {
        try {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();
            TwitterCore.getInstance().getSessionManager().clearActiveSession();
        } catch (Exception e) {

        }
    }
    //#endregion

    //#region Register SNS with Google
    private View.OnClickListener registerWithGoogle() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                enableAction(false);
                RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
                if (!NetworkUtil.isNetworkConnected(activity)) {
                    DialogUtil.offlineDialog(activity, getContext());
                    enableAction(true);
                } else {
                    activity.handleLoginGoogle();
                }
            }
        };
    }
    //#endregion
}

