package com.t_robop.yuusuke.a01_spica_android.UI.Script;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.t_robop.yuusuke.a01_spica_android.R;

public class SettingActivity extends AppCompatActivity {

    TextView ipEditText;
    TextView portEditText;
    private EditText configLowSpeedEdit;
    private EditText configMiddleSpeedEdit;
    private EditText configHighSpeedEdit;
    private CheckBox ifCheckBox;
    private CheckBox loopCheckBox;


    Button saveButton;
    Button cancelButton;
    Button qrButton;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ipEditText = findViewById(R.id.ip_edit);
        portEditText = findViewById(R.id.port_edit);
        configLowSpeedEdit = findViewById(R.id.config_low_speed_edit);
        configMiddleSpeedEdit = findViewById(R.id.config_middle_speed_edit);
        configHighSpeedEdit = findViewById(R.id.config_high_speed_edit);
        ifCheckBox = findViewById(R.id.checkBoxIf);
        loopCheckBox = findViewById(R.id.checkBoxLoop);

        pref = getSharedPreferences("udp_config", Context.MODE_PRIVATE);
        final String ip = pref.getString("ip", "");
        final int port = pref.getInt("port", 50000);
        ipEditText.setText(ip);
        portEditText.setText(String.valueOf(port));

        ifCheckBox.setChecked(pref.getBoolean("ifState",true));
        loopCheckBox.setChecked(pref.getBoolean("loopState",true));

        int configLowSpeedNUm = pref.getInt("lowSpeed", 60);
        int configMiddleSpeedNum = pref.getInt("middleSpeed", 80);
        int configHighSpeedNum = pref.getInt("highSpeed", 100);

        configLowSpeedEdit.setText(String.valueOf(configLowSpeedNUm));
        configMiddleSpeedEdit.setText(String.valueOf(configMiddleSpeedNum));
        configHighSpeedEdit.setText(String.valueOf(configHighSpeedNum));

        saveButton = findViewById(R.id.save_btn);
        saveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        save();
                    }
                }
        );

        qrButton = findViewById(R.id.qr);
        qrButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new IntentIntegrator(SettingActivity.this).initiateScan();
                    }
                }
        );
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            String resultData = result.getContents();
            String[] Datas = resultData.split(";", 0);
            String ipString = (Datas[0].split(":")[1]);
            String portString = (Datas[1].split(":")[1]);
            ipEditText.setText(ipString);
            portEditText.setText(String.valueOf(portString));

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK){
            return super.onKeyDown(keyCode, event);
        }else{
            save();
            finish();
            return false;
        }
    }

    private void save() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ip", ipEditText.getText().toString());
        editor.putInt("port", Integer.parseInt(portEditText.getText().toString()));
        editor.putInt("lowSpeed", Integer.parseInt(configLowSpeedEdit.getText().toString()));
        editor.putInt("middleSpeed", Integer.parseInt(configMiddleSpeedEdit.getText().toString()));
        editor.putInt("highSpeed", Integer.parseInt(configHighSpeedEdit.getText().toString()));
        editor.putBoolean("ifState", ifCheckBox.isChecked());
        editor.putBoolean("loopState", loopCheckBox.isChecked());
        editor.apply();
        finish();
    }
}
