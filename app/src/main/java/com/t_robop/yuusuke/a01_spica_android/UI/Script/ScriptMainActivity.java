package com.t_robop.yuusuke.a01_spica_android.UI.Script;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.Script;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.t_robop.yuusuke.a01_spica_android.R;
import com.t_robop.yuusuke.a01_spica_android.databinding.ActivityScriptMainBinding;
import com.t_robop.yuusuke.a01_spica_android.model.ScriptModel;
import com.t_robop.yuusuke.a01_spica_android.util.UdpReceive;
import com.t_robop.yuusuke.a01_spica_android.util.UdpSend;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.t_robop.yuusuke.a01_spica_android.model.ScriptModel.SpicaBlock.FOR_END;
import static com.t_robop.yuusuke.a01_spica_android.model.ScriptModel.SpicaBlock.IF_END;

public class ScriptMainActivity extends AppCompatActivity implements ScriptContract.ScriptView, BlockSelectFragment.BlockClickListener {

    private ScriptContract.Presenter mScriptPresenter;

    ActivityScriptMainBinding mBinding;

    private ScriptMainAdapter mScriptAdapter;
    private LinearLayoutManager mScriptLayoutManager;

    private BlockSelectFragment blockSelectFragment;
    private BlockDetailFragment blockDetailFragment;

    private UdpReceive udpReceive;

    float sizeX;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script_main);

        hideNavigationBar();
        udpReceive = new UdpReceive(this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_script_main);

        mBinding.recyclerScript.setHasFixedSize(true);
        mScriptLayoutManager = new LinearLayoutManager(this);
        mScriptLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.recyclerScript.setLayoutManager(mScriptLayoutManager);
        mScriptAdapter = new ScriptMainAdapter(this);
        mBinding.recyclerScript.setAdapter(mScriptAdapter);

        /**
         * Presenterの初期化
         */
        blockSelectFragment = new BlockSelectFragment();
        blockDetailFragment = new BlockDetailFragment();
        new ScriptPresenter(this, blockSelectFragment, blockDetailFragment);

        /**
         * 追加ボタンクリック時
         */
        mScriptAdapter.setOnConductorClickListener(new ScriptMainAdapter.onItemClickListener() {
            @Override
            public void onClick(View view, int pos, int ifState, boolean isInLoop) {
                hideNavigationBar();
                mScriptPresenter.setState(ScriptPresenter.ViewState.SELECT);
                ScriptModel scriptModel = new ScriptModel(pos, ifState, isInLoop);
                inflateFragment(scriptModel);
            }
        });
        mScriptAdapter.setOnConductorIfClickListener(new ScriptMainAdapter.onItemClickListener() {
            @Override
            public void onClick(View view, int pos, int ifState, boolean isInLoop) {
                hideNavigationBar();
                mScriptPresenter.setState(ScriptPresenter.ViewState.SELECT);
                ScriptModel scriptModel = new ScriptModel(pos, ifState, isInLoop);
                inflateFragment(scriptModel);
            }
        });

        /**
         * ブロッククリック時
         */
        mScriptAdapter.setOnBlockClickListener(new ScriptMainAdapter.onItemClickListener() {
            @Override
            public void onClick(View view, int pos, int ifState, boolean isInLoop) {
                hideNavigationBar();
                if (0 <= pos) {
                    /**
                     * ブロック設定へ
                     */
                    mScriptPresenter.setState(ScriptPresenter.ViewState.EDIT);
                    ScriptModel scriptModel = mScriptPresenter.getScripts().get(pos);
                    if (scriptModel.getBlock() == IF_END || scriptModel.getBlock() == FOR_END)
                        return;
                    inflateFragment(scriptModel);
                }
            }
        });
        mScriptAdapter.setOnBlockIfClickListener(new ScriptMainAdapter.onItemClickListener() {
            @Override
            public void onClick(View view, int pos, int ifState, boolean isInLoop) {
                hideNavigationBar();
                /**
                 * ブロック設定へ
                 */
                mScriptPresenter.setState(ScriptPresenter.ViewState.EDIT);
                ScriptModel scriptModel = mScriptPresenter.getScripts().get(pos);
                if (scriptModel.getBlock() == IF_END || scriptModel.getBlock() == FOR_END)
                    return;
                inflateFragment(scriptModel);
            }
        });

        /**
         * ブロックロングクリック時
         */
        mScriptAdapter.setOnBlockLongClickListener(new ScriptMainAdapter.onItemLongClickListener() {
            @Override
            public void onLongClick(View view, int pos) {
                //実行
            }
        });
        mScriptAdapter.setOnBlockIfLongClickListener(new ScriptMainAdapter.onItemLongClickListener() {
            @Override
            public void onLongClick(View view, int pos) {
                //実行
            }
        });

        /**
         * Detailフラグメントのボタンが押された時
         */
        blockDetailFragment.setAddClickListener(new BlockDetailFragment.DetailListener() {
            @Override
            public void onClickAdd(ScriptModel script) {
                ScriptPresenter.ViewState state = mScriptPresenter.getState();
                if (state == ScriptPresenter.ViewState.ADD) {
                    mScriptPresenter.insertScript(script, script.getPos());
                } else if (state == ScriptPresenter.ViewState.EDIT) {
                    mScriptPresenter.setScript(script, script.getPos());
                } else {
                    Toast.makeText(ScriptMainActivity.this, "ADDorEDITでエラー", Toast.LENGTH_SHORT).show();
                }
                mScriptPresenter.setState(ScriptPresenter.ViewState.SCRIPT);
                clearFragments();
            }

            @Override
            public void onClickDelete(int pos) {
                mScriptPresenter.removeScript(pos);
                mScriptPresenter.setState(ScriptPresenter.ViewState.SCRIPT);
                clearFragments();
            }
        });

        /**
         * fabがクリックされた時
         */
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objectSave();
                String sendData = mScriptPresenter.getSendableScripts();
                final SharedPreferences pref = getSharedPreferences("udp_config", Context.MODE_PRIVATE);
                final String ip = pref.getString("ip", "");
                if (ip.isEmpty()) {
                    Toast.makeText(ScriptMainActivity.this, R.string.script_main_activity_failed_empty_ip, Toast.LENGTH_SHORT).show();
                } else if (sendData.length() <= 0) {
                    Toast.makeText(ScriptMainActivity.this, R.string.script_main_activity_failed_empty_block, Toast.LENGTH_SHORT).show();
                } else {
                    udpReceive.UdpReceiveStandby();
                    UdpSend udp = new UdpSend();
                    udp.UdpSendText(sendData);
                    Log.d("sendData", sendData);
                    Toast.makeText(ScriptMainActivity.this, R.string.script_main_activity_send_success, Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * fabが1.2秒以上長押しされた時
         */
        final long[] then = {0};
        mBinding.fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    then[0] = System.currentTimeMillis();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ((System.currentTimeMillis() - then[0]) > 1200) {
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });


        /**
         *
         * 透明ボタンが押されたときの処理
         */
        Button restoreButton = findViewById(R.id.restore_btn);
        restoreButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                objectLoad();
                return false;
            }
        });

    }

    /**
     * Fragment生成メソッド
     */
    public void inflateFragment(ScriptModel scriptModel) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ScriptPresenter.ViewState state = mScriptPresenter.getState();
        mScriptPresenter.setTargetScript(scriptModel);
        if (state == ScriptPresenter.ViewState.SELECT && !blockSelectFragment.isAdded()) {
            /**
             * ブロック追加用の選択画面へ
             */
            fragmentTransaction.add(R.id.conductor_fragment, blockSelectFragment);
        } else if (state == ScriptPresenter.ViewState.EDIT && !blockDetailFragment.isAdded()) {
            /**
             * ブロック設定用の詳細画面へ
             */
            fragmentTransaction.add(R.id.conductor_fragment, blockDetailFragment);
        } else if (state == ScriptPresenter.ViewState.ADD && !blockDetailFragment.isAdded()) {
            /**
             * ブロック追加用の詳細画面へ
             * todo 上記EDITと同じなのは一旦分かりやすさのため
             */
            fragmentTransaction.add(R.id.conductor_fragment, blockDetailFragment);
        }
        fragmentTransaction.commit();
    }

    /**
     * 出てるFragmentを消すメソッド
     */
    public void clearFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(blockDetailFragment);
        fragmentTransaction.remove(blockSelectFragment);
        fragmentTransaction.commit();

    }


    /**
     * 追加時のブロック選択画面で選択されたブロックを元にフラグメント生成
     */
    @Override
    public void onClickButton(ScriptModel.SpicaBlock block) {
        mScriptPresenter.setState(ScriptPresenter.ViewState.ADD);
        ScriptModel scriptModel = mScriptPresenter.getTargetScript();
        scriptModel.setBlock(block);
        inflateFragment(scriptModel);
    }

    /**
     * スクリプトのリストを投げるとUI構築してくれる神メソッド
     */
    @Override
    public void drawScripts(ArrayList<ScriptModel> scripts) {
        mScriptAdapter.clear();

        //スタートブロック記述
        ScriptModel scriptStart = new ScriptModel();
        scriptStart.setBlock(ScriptModel.SpicaBlock.START);
        scriptStart.setPos(-1);
        scriptStart.setIfState(0);
        mScriptAdapter.addDefault(0, scriptStart);

        //引数を使ってUIに反映させる
        int ifIndex = -1;
        int laneIndex = 1;
        boolean isInloop = false;
        for (int i = 0; i < scripts.size(); i++) {
            ScriptModel script = scripts.get(i);
            script.setPos(i);
            script.setInLoop(isInloop);

            if (script.getBlock() == ScriptModel.SpicaBlock.FOR_START) {
                isInloop = true;
            } else if (script.getBlock() == ScriptModel.SpicaBlock.FOR_END) {
                isInloop = false;
            }

            if (script.getIfState() == 0) {
                //通常
                mScriptAdapter.addDefault(laneIndex, script);
                laneIndex++;
            } else if (script.getIfState() == 1) {
                //true
                mScriptAdapter.addSpecial(laneIndex, script);
                laneIndex++;
            } else if (script.getIfState() == 2) {
                //false
                mScriptAdapter.addDefault(ifIndex, script);
                if (ifIndex == laneIndex) {
                    laneIndex++;
                }
                ifIndex++;
            }
            if (script.getBlock() == ScriptModel.SpicaBlock.IF_START) {
                ifIndex = laneIndex;
            }
            if (script.getBlock() == IF_END) {
                ifIndex = -1;
            }
        }

        //エンドブロック記述
        ScriptModel scriptEnd = new ScriptModel();
        scriptEnd.setBlock(ScriptModel.SpicaBlock.END);
        mScriptAdapter.addDefault(mScriptAdapter.getItemCount(), scriptEnd);

        mScriptAdapter.notifyDataSetChanged();

        mBinding.canvasView.setCommandBlocks(mScriptAdapter);
        mBinding.canvasView.windowSizeChange(sizeX, (float) mScriptAdapter.getItemCount());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        sizeX = mBinding.recyclerScript.getWidth();
    }

    @Override
    public void setPresenter(ScriptContract.Presenter presenter) {
        this.mScriptPresenter = presenter;
        this.mScriptPresenter.start();
    }


    private void objectSave() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // TODO 復元処理が問題なかったら消す
//        Gson gson = new Gson();
//        // objectをjson文字列へ変換
//        String jsonInstanceString = gson.toJson(mScriptPresenter.getScripts());
//        // 変換後の文字列をputStringで保存
//        pref.edit().putString("dataSave", jsonInstanceString).apply();


        String jsonInstanceString = mScriptPresenter.getSendableScripts();
//        // 変換後の文字列をputStringで保存
        pref.edit().putString("dataSave", jsonInstanceString).apply();
    }

    public void objectLoad() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // TODO 復元処理が問題なかったら消す
//        Gson gson = new Gson();
//        try {
//            // 保存されているjson文字列を取得
//            String userSettingString = prefs.getString("dataSave", "");
//            // json文字列を 「UserSettingクラス」のインスタンスに変換
//            ArrayList<ScriptModel> mScripts = gson.fromJson(userSettingString, new TypeToken<ArrayList<ScriptModel>>() {
//            }.getType());
//            mScriptPresenter.setScripts(mScripts);
//        } catch (Exception e) {
//            Toast.makeText(this, "データがありません", Toast.LENGTH_SHORT).show();
//        }
        String userSettingString = prefs.getString("dataSave", "");
        ArrayList<ScriptModel> models = commandConvert(userSettingString);
        mScriptAdapter.clear();
        mScriptPresenter.setScripts(models);


    }

    private void hideNavigationBar() {
        View sysView = getWindow().getDecorView();
        sysView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public ArrayList<ScriptModel> commandConvert(String cmdText) {
        ArrayList<ScriptModel> scripts = new ArrayList();
        ArrayList<String> cmds = splitCommand(cmdText,13);
        for (String cmd: cmds) {
            String str1 = cmd.substring(0,2);
            String str2 = cmd.substring(2,4);
            String str3 = cmd.substring(4,7);
            String str4 = cmd.substring(7,10);
            String str5 = cmd.substring(10,13);
            ScriptModel model = new ScriptModel();
            model.setIfState(Integer.parseInt(str1));

            ScriptModel.SpicaBlock[] values = ScriptModel.SpicaBlock.values();
            ScriptModel.SpicaBlock block = values[Integer.parseInt(str2)-1];
            model.setBlock(block);
            model.setSpeed(Integer.parseInt(str3));
            model.setValue(Integer.parseInt(str5));
            scripts.add(model);
        }
        return scripts;
    }

    ArrayList<String> splitCommand (String str, int length) {
        ArrayList<String> strs = new ArrayList<>();
        for (int i = 0; i < StringUtils.length(str); i += length) {
            strs.add(StringUtils.substring(str, i, i + length));
        }
        return strs;
    }
}
