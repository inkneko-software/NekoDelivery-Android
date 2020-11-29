package com.inkneko.delivery.ui.login;

import android.widget.EditText;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.inkneko.delivery.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<String> state;

    public LoginViewModel() {
        state = new MutableLiveData<>();
    }

    public LiveData<String> getText(String username, String password) {
        new LoginThread(username, password, state).start();
        return state;
    }
}

    class LoginThread extends Thread
    {

        public String username;
        public String password;
        private MutableLiveData<String> state;
        public LoginThread(String a, String b, MutableLiveData<String> c){
            username = a;
            password = b;
            state = c;
        }

        public void run()
        {

            OkHttpClient httpClient = new OkHttpClient();

            RequestBody data = RequestBody.create("phone=" + username + "&password=" + password, MediaType.get("application/x-www-form-urlencoded"));

            Request request = new Request.Builder().url("http://inkneko.com:8960/api/v1/auth/loginAccount").post(data).build();
            String response = null;
            try
            {
                response = httpClient.newCall(request).execute().body().string();
                if (response.length() == 0){
                    state.postValue("登录成功");
                    return;
                }
                else
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg = jsonObject.get("msg").toString();
                        state.postValue(msg);
                        return;
                    }
                    catch(JSONException e)
                    {

                    }

                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            state.postValue("网络异常");
        }
    }