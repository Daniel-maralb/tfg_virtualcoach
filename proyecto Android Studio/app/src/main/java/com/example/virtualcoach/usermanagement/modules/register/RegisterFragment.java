package com.example.virtualcoach.usermanagement.modules.register;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.virtualcoach.R;
import com.example.virtualcoach.usermanagement.model.DisplayUser;
import com.example.virtualcoach.usermanagement.modules.login.LoginFragment;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {

    private RegisterViewModel registerViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        final NavController navController = NavHostFragment.findNavController(this);

        String username;
        String password;
        {
            RegisterFragmentArgs args = RegisterFragmentArgs.fromBundle(getArguments());
            username = args.getUsername();
            password = args.getPassword();
        }


        final EditText usernameEditText = view.findViewById(R.id.register_username);
        final EditText passwordEditText = view.findViewById(R.id.register_password);
        final EditText passwordRepeatEditText = view.findViewById(R.id.register_repeat_password);
        final Button registerButton = view.findViewById(R.id.register_btn_register);
        final ProgressBar loadingProgressBar = view.findViewById(R.id.loading);
        final TextView resultMessage = view.findViewById(R.id.register_result);

        registerViewModel.getRegisterFormState().observe(getViewLifecycleOwner(), loginFormState -> {
            if (isNull(loginFormState))
                return;

            registerButton.setEnabled(loginFormState.isDataValid());
            resultMessage.setVisibility(View.GONE);

            if (!isNull(loginFormState.getUsernameError()))
                usernameEditText.setError(getString(loginFormState.getUsernameError()));

            if (!isNull(loginFormState.getPasswordError()))
                passwordEditText.setError(getString(loginFormState.getPasswordError()));

            if (!isNull(loginFormState.getRepeatPasswordError()))
                passwordRepeatEditText.setError(getString(loginFormState.getRepeatPasswordError()));
        });

        registerViewModel.getRegisterResult().observe(getViewLifecycleOwner(), result -> {
            if (isNull(result))
                return;

            loadingProgressBar.setVisibility(View.GONE);

            if (!isNull(result.getError())) {
                Integer error = result.getError();

                showLoginFailed(error);
                resultMessage.setText(R.string.register_msg_failed);
                resultMessage.setVisibility(View.VISIBLE);

            } else if (!isNull(result.getSuccess())) {
                DisplayUser user = result.getSuccess();

                resultMessage.setText(R.string.register_msg_success);
                resultMessage.setVisibility(View.VISIBLE);
                updateUiWithUser(user);

                Objects.requireNonNull(navController.getPreviousBackStackEntry())
                        .getSavedStateHandle()
                        .set(LoginFragment.STATE_KEY_USERNAME, user.email);
                navController.popBackStack();
            }
        });

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
                registerViewModel.registerDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        passwordRepeatEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordRepeatEditText.addTextChangedListener(afterTextChangedListener);

        passwordRepeatEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                registerViewModel.registerDataChanged(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        passwordRepeatEditText.getText().toString());

                RegisterFormState formState = registerViewModel.getRegisterFormState().getValue();

                if (!isNull(formState) && formState.isDataValid())
                    registerUser(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
            }
            return false;
        });

        registerButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            registerUser(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });

        usernameEditText.setText(username);
        passwordEditText.setText(password);
    }

    private void updateUiWithUser(DisplayUser model) {
        String welcome = getString(R.string.register_msg_success);
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

    private void registerUser(String username, String password) {
        registerViewModel.register(username, password);
    }
}