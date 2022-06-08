package com.example.nodejssocketio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvListUsers;
    private ListView lvContent;
    private EditText etContent;
    private ImageView ivAddUser, ivAddChat;
    private ArrayList<String> listUser;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhXa();


        try {
            mSocket = IO.socket("http://192.168.1.157:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();

        ivAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString().trim();
                // Nếu etContent có dữ liệu
                if (content.length() > 0) {
                    mSocket.emit("client-send-user", content);

                }
            }
        });
        mSocket.on("server-send-result", onRetrieveData);
        mSocket.on("server-send-listUser", onListUser);
    }

    private Emitter.Listener onListUser = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray array = object.getJSONArray("array");
                        for (int i = 0; i < array.length(); i++) {
                            listUser.add(array.getString(i));
                            Toast.makeText(getApplicationContext(), array.getString(i), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        }
    };

    // Mảng object
    private Emitter.Listener onRetrieveData = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // tương tự như asyntask, có tác dụng tác động lên màn hình hiện tại
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        boolean check = object.getBoolean("tontai");
                        if (check ) {
                            Toast.makeText(getApplicationContext(), "Đã có tài khoản", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private void anhXa() {
        etContent = findViewById(R.id.et_content);
        ivAddUser = findViewById(R.id.iv_add_user);
        ivAddChat = findViewById(R.id.iv_add_chat);
        listUser = new ArrayList<>();
    }

}