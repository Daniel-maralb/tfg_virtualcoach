package com.example.virtualcoach.usermanagement.modules.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.virtualcoach.R;
import com.example.virtualcoach.usermanagement.model.DisplayUser;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
    public static final String STATE_KEY_USERNAME = "login_username";

    private LoginViewModel loginViewModel;
    private NavController navController;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar loadingProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        navController = NavHostFragment.findNavController(this);

        usernameEditText = view.findViewById(R.id.login_username);
        passwordEditText = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_btn_login);
        registerButton = view.findViewById(R.id.login_btn_register);
        loadingProgressBar = view.findViewById(R.id.loading);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        MutableLiveData<String> usernameArgument =
                Objects.requireNonNull(navController.getCurrentBackStackEntry()).getSavedStateHandle()
                        .getLiveData(STATE_KEY_USERNAME);

        usernameArgument.observe(getViewLifecycleOwner(), value -> {
            usernameEditText.setText(value);
            usernameEditText.setError(null);
            passwordEditText.setText("");
            passwordEditText.setError(null);
        });

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), this::onLoginFormStateChanged);
        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), this::onLoginResultChanged);

        configureTextInputs();

        registerButton.setOnClickListener(v -> navigateToRegisterFragment());

        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        loginViewModel.login(usernameEditText.getText().toString(),
                passwordEditText.getText().toString());
    }

    private void navigateToRegisterFragment() {
        NavHostFragment.findNavController(this)
                .navigate(
                        LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                                .setUsername(usernameEditText.getText().toString())
                                .setPassword(passwordEditText.getText().toString()));
    }

    private void configureTextInputs() {
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };

        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.loginDataChanged(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());

                LoginFormState formState = loginViewModel.getLoginFormState().getValue();

                if (!isNull(formState) && formState.isDataValid())
                    loginViewModel.login(
                            usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
            }
            return false;
        });
    }

    private void handleSuccessfulLogin(DisplayUser model) {
        String welcome = getString(R.string.welcome) + model.displayName;
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        }


    }

    private void showLoginFailed(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    errorString,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void onLoginFormStateChanged(LoginFormState loginFormState) {
        if (loginFormState == null)
            return;

        loginButton.setEnabled(loginFormState.isDataValid());

        if (!isNull(loginFormState.getUsernameError()))
            usernameEditText.setError(getString(loginFormState.getUsernameError()));

        if (!isNull(loginFormState.getPasswordError()))
            passwordEditText.setError(getString(loginFormState.getPasswordError()));

    }

    private void onLoginResultChanged(LoginResult loginResult) {
        if (isNull(loginResult))
            return;

        loadingProgressBar.setVisibility(View.GONE);

        if (!isNull(loginResult.getError())) {
            showLoginFailed(loginResult.getError());

        } else if (!isNull(loginResult.getSuccess())) {
            handleSuccessfulLogin(loginResult.getSuccess());

            NavHostFragment.findNavController(this).navigate(
                    LoginFragmentDirections.actionLoginFragmentToTrainingProgressFragment()
            );
        }
    }
}