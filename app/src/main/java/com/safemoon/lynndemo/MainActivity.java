package com.safemoon.lynndemo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.safemoon.lynndemo.databinding.ActivityMainBinding;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private WebSocketHandlingViewModel webSocketModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Timber.treeCount() == 0) {
            Timber.plant(new Timber.DebugTree());
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        webSocketModel= new ViewModelProvider(this).get(WebSocketHandlingViewModel.class);
        webSocketModel.simulate();
        initReceiveResponse();
        Timber.tag("Lynn").d("onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        Timber.tag("Lynn").d("onPostCreate");
        super.onPostCreate(savedInstanceState);
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.tag("Lynn").d("requestSocketServer");
                requestSocketServer();
            }
        });
    }

    private void requestSocketServer() {
        String username = binding.tilUserName.getEditText().getText().toString();
        String password = binding.tilPassWord.getEditText().getText().toString();
        sendMessage("Demo Message");
    }

    public void sendMessage(String toSend) {
        Timber.tag("Lynn").d("Home sendMessage: "+ toSend);
        if (toSend != null && !toSend.isEmpty()) {
            webSocketModel.sendMessage(toSend);
        }
    }

    public void initReceiveResponse() {
        webSocketModel.getResponse().observe(this, (message) ->  {
            Timber.tag("Lynn").d("initReceiveResponse: "+ message);
            if (message != null && !message.isEmpty()) {
                parseMessage(message);
                closeSocket();
            }
        });
    }

    public void parseMessage(String message) {
        Timber.tag("Lynn").d("parseMessage message: %s", message);
    }

    public void closeSocket() {
        webSocketModel.closeSocket();
    }

    public void openSocket() {
        webSocketModel.openSocket();
    }
}