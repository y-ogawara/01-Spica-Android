package com.t_robop.yuusuke.a01_spica_android.UI.Script;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.t_robop.yuusuke.a01_spica_android.R;
import com.t_robop.yuusuke.a01_spica_android.model.ScriptModel;
import com.t_robop.yuusuke.a01_spica_android.databinding.ActivityBlockSelectBinding;

public class BlockSelectFragment extends Fragment implements ScriptContract.SelectView {

    private ScriptContract.Presenter mScriptPresenter;

    private ActivityBlockSelectBinding mBinding;

    public BlockSelectFragment() {
    }

    private BlockClickListener mListener;
    private OutSideClickListener outSideClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_block_select, container, false);
        View root = mBinding.getRoot();

        mBinding.bgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(BlockSelectFragment.this).commit();
                outSideClickListener.onClickOutSide();
            }
        });

        ScriptModel script = mScriptPresenter.getTargetScript();

        mBinding.selectDialogBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mBinding.susumu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickButton(ScriptModel.SpicaBlock.FRONT);
            }
        });
        mBinding.magaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickButton(ScriptModel.SpicaBlock.LEFT);
            }
        });
        mBinding.sagaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickButton(ScriptModel.SpicaBlock.BACK);
            }
        });

        if (script.getIfState() != 0) {
            mBinding.mosimo.setVisibility(View.INVISIBLE);
        } else {
            mBinding.mosimo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickButton(ScriptModel.SpicaBlock.IF_START);
                }
            });
        }
        if (script.isInLoop()) {
            mBinding.kurikaesu.setVisibility(View.INVISIBLE);
        } else {
            mBinding.kurikaesu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickButton(ScriptModel.SpicaBlock.FOR_START);
                }
            });
        }
        if (!(script.getIfState() != 0 && script.isInLoop())) {
            mBinding.nukeru.setVisibility(View.INVISIBLE);
        } else {
            mBinding.nukeru.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickButton(ScriptModel.SpicaBlock.BREAK);
                }
            });
        }

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        popupAnime(view);
    }

    public void drawArrangeableBlocks() {
        //todo
    }

    @Override
    public void setPresenter(ScriptContract.Presenter presenter) {
        this.mScriptPresenter = presenter;
    }


    public interface BlockClickListener {
        void onClickButton(ScriptModel.SpicaBlock block);
    }

    public interface OutSideClickListener{
        void onClickOutSide();
    }

    // FragmentがActivityに追加されたら呼ばれるメソッド
    @Override
    public void onAttach(Context context) {
        // APILevel23からは引数がActivity->Contextになっているので注意する

        // contextクラスがMyListenerを実装しているかをチェックする
        super.onAttach(context);
        if (context instanceof BlockClickListener) {
            // リスナーをここでセットするようにします
            mListener = (BlockClickListener) context;
            outSideClickListener = (OutSideClickListener) context;
        }
    }

    // FragmentがActivityから離れたら呼ばれるメソッド
    @Override
    public void onDetach() {
        super.onDetach();
        // 画面からFragmentが離れたあとに処理が呼ばれることを避けるためにNullで初期化しておく
        mListener = null;
        outSideClickListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void popupAnime(View view) {
        // ScaleAnimation(float fromX, float toX, float fromY, float toY, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue)
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.01f, 1.0f, 0.01f, 1.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // animation時間 msec
        scaleAnimation.setDuration(200);
        // 繰り返し回数
        scaleAnimation.setRepeatCount(0);
        // animationが終わったそのまま表示にする
        scaleAnimation.setFillAfter(true);
        //アニメーションの開始
        view.startAnimation(scaleAnimation);
    }
}
