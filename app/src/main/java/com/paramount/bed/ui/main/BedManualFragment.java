package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.ble.pojo.NSBedSetting;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.ui.BaseV4Fragment;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.PermissionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BedManualFragment extends BaseV4Fragment {
    @BindView(R.id.combination_up)
    Button combinUpBtn;
    @BindView(R.id.combination_down)
    Button combinDownBtn;
    @BindView(R.id.head_up)
    Button headUpBtn;
    @BindView(R.id.head_down)
    Button headDownBtn;
    @BindView(R.id.leg_up)
    Button legUpBtn;
    @BindView(R.id.leg_down)
    Button legDownBtn;
    @BindView(R.id.height_up)
    Button heightUpBtn;
    @BindView(R.id.height_down)
    Button heightDownBtn;

    @BindView(R.id.txt0)
    TextView txt0;
    @BindView(R.id.txt1)
    TextView txt1;
    @BindView(R.id.txt2)
    TextView txt2;
    @BindView(R.id.txt3)
    TextView txt3;

    @BindView(R.id.height_container)
    LinearLayout heightContainer;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.tvTag)
    TextView tvTag;

    public BedManualListener listener;

    private int arrowTouchDelayTime = 50;

    private boolean combiArrowUpPressed;
    private Handler combiArrowUpDelayHandler;
    private Runnable combiArrowUpDelayCallback = new Runnable() {
        @Override
        public void run() {
            if (listener != null && combiArrowUpDelayHandler != null) {
                combiArrowUpPressed = true;
                listener.onIncreaseCombinationTouchStart();
            }
        }
    };

    private boolean combiArrowDownPressed;
    private Handler combiArrowDownDelayHandler;
    private Runnable combiArrowDownDelayCallback = new Runnable() {
        @Override
        public void run() {
            if (listener != null && combiArrowDownDelayHandler != null) {
                combiArrowDownPressed = true;
                listener.onDecreaseCombinationTouchStart();
            }
        }
    };

    private boolean headArrowUpPressed;
    private Handler headArrowUpDelayHandler;
    private Runnable headArrowUpDelayCallback = new Runnable() {
        @Override
        public void run() {
            if (listener != null && headArrowUpDelayHandler != null) {
                headArrowUpPressed = true;
                listener.onIncreaseHeadTouchStart();
            }
        }
    };

    private boolean headArrowDownPressed;
    private Handler headArrowDownDelayHandler;
    private Runnable headArrowDownDelayCallback = new Runnable() {
        @Override
        public void run() {
            if (listener != null && headArrowDownDelayHandler != null) {
                headArrowDownPressed = true;
                listener.onDecreaseHeadTouchStart();
            }
        }
    };


    private boolean legArrowUpPressed;
    private Handler legArrowUpDelayHandler;
    private Runnable legArrowUpDelayCallback = new Runnable() {
        @Override
        public void run() {
            if (listener != null && legArrowUpDelayHandler != null) {
                legArrowUpPressed = true;
                listener.onIncreaseLegTouchStart();
            }
        }
    };

    private boolean legArrowDownPressed;
    private Handler legArrowDownDelayHandler;
    private Runnable legArrowDownDelayCallback = new Runnable() {
        @Override
        public void run() {
            if (listener != null && legArrowDownDelayHandler != null) {
                legArrowDownPressed = true;
                listener.onDecreaseLegTouchStart();
            }
        }
    };

    private boolean heightArrowUpPressed;
    private Handler heightArrowUpDelayHandler;
    private Runnable heightArrowUpDelayCallback = new Runnable() {
        @Override
        public void run() {
            if (listener != null && heightArrowUpDelayHandler != null) {
                heightArrowUpPressed = true;
                listener.onIncreaseHeightTouchStart();
            }
        }
    };

    private boolean heightArrowDownPressed;
    private Handler heightArrowDownDelayHandler;
    private Runnable heightArrowDownDelayCallback = new Runnable() {
        @Override
        public void run() {
            if (listener != null && heightArrowDownDelayHandler != null) {
                heightArrowDownPressed = true;
                listener.onDecreaseHeightTouchStart();
            }
        }
    };


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_bed_manual, container, false);
        listener = (RemoteActivity) getActivity();
        ButterKnife.bind(this, view);

        combinUpBtn.setOnTouchListener((v, motionEvent) -> {
            handleCombiUpTouch(motionEvent, v);
            return false;
        });

        combinDownBtn.setOnTouchListener((v, motionEvent) -> {
            handleCombiDownTouch(motionEvent, v);
            return false;
        });

        headUpBtn.setOnTouchListener((v, motionEvent) -> {
            handleHeadUpTouch(motionEvent, v);
            return false;
        });

        headDownBtn.setOnTouchListener((v, motionEvent) -> {
            handleHeadDownTouch(motionEvent, v);
            return false;
        });

        legUpBtn.setOnTouchListener((v, motionEvent) -> {
            handleLegUpTouch(motionEvent, v);
            return false;
        });

        legDownBtn.setOnTouchListener((v, motionEvent) -> {
            handleLegDownTouch(motionEvent, v);
            return false;
        });

        heightUpBtn.setOnTouchListener((v, motionEvent) -> {
            handleHeightUpTouch(motionEvent, v);
            return false;
        });

        heightDownBtn.setOnTouchListener((v, motionEvent) -> {
            handleHeightDownTouch(motionEvent, v);
            return false;
        });

        applyLocalization(view);
        disableUIByLocPermission();

        if (DisplayUtils.FONTS.bigFontStatus(getActivity())) {
            tvTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        }
        //Set Default Setting Is Not Support Height
        setHeightAvailable(NemuriScanModel.get() != null && NemuriScanModel.get().isHeightSupported());
        return view;
    }

    private void disableUIByLocPermission() {
        if (!PermissionUtil.locationFeatureEnabled(getActivity())) disableUI();
    }

    public void enableUI() {
        if (combinUpBtn != null) combinUpBtn.setEnabled(true);
        if (combinDownBtn != null) combinDownBtn.setEnabled(true);

        if (headUpBtn != null) headUpBtn.setEnabled(true);
        if (headDownBtn != null) headDownBtn.setEnabled(true);

        if (legUpBtn != null) legUpBtn.setEnabled(true);
        if (legDownBtn != null) legDownBtn.setEnabled(true);

        if (heightUpBtn != null) heightUpBtn.setEnabled(true);
        if (heightDownBtn != null) heightDownBtn.setEnabled(true);

        if (txt0 != null) txt0.setEnabled(true);
        if (txt1 != null) txt1.setEnabled(true);
        if (txt2 != null) txt2.setEnabled(true);
        if (txt3 != null) txt3.setEnabled(true);

        if (tvTitle != null) tvTitle.setEnabled(true);
        if (tvTag != null) tvTag.setEnabled(true);
    }

    public void disableUI() {
        if (combinUpBtn != null) combinUpBtn.setEnabled(false);
        if (combinDownBtn != null) combinDownBtn.setEnabled(false);

        if (headUpBtn != null) headUpBtn.setEnabled(false);
        if (headDownBtn != null) headDownBtn.setEnabled(false);

        if (legUpBtn != null) legUpBtn.setEnabled(false);
        if (legDownBtn != null) legDownBtn.setEnabled(false);

        if (heightUpBtn != null) heightUpBtn.setEnabled(false);
        if (heightDownBtn != null) heightDownBtn.setEnabled(false);

        if (txt0 != null) txt0.setEnabled(false);
        if (txt1 != null) txt1.setEnabled(false);
        if (txt2 != null) txt2.setEnabled(false);
        if (txt3 != null) txt3.setEnabled(false);

        if (tvTitle != null) tvTitle.setEnabled(false);
        if (tvTag != null) tvTag.setEnabled(false);
    }

    public void applyLock(NSBedSetting bedSetting) {
        Activity activity = getActivity();
        if (activity == null) return;
        activity.runOnUiThread(() -> {
            heightUpBtn.setEnabled(!bedSetting.isHeightLocked());
            heightDownBtn.setEnabled(!bedSetting.isHeightLocked());
            heightUpBtn.setAlpha(bedSetting.isHeightLocked() ? 0.5f : 1f);
            heightDownBtn.setAlpha(bedSetting.isHeightLocked() ? 0.5f : 1f);

            headUpBtn.setEnabled(!bedSetting.isHeadLocked());
            headDownBtn.setEnabled(!bedSetting.isHeadLocked());
            headUpBtn.setAlpha(bedSetting.isHeadLocked() ? 0.5f : 1f);
            headDownBtn.setAlpha(bedSetting.isHeadLocked() ? 0.5f : 1f);

            legUpBtn.setEnabled(!bedSetting.isLegLocked());
            legDownBtn.setEnabled(!bedSetting.isLegLocked());
            legUpBtn.setAlpha(bedSetting.isLegLocked() ? 0.5f : 1f);
            legDownBtn.setAlpha(bedSetting.isLegLocked() ? 0.5f : 1f);

            combinUpBtn.setEnabled(!bedSetting.isCombiLocked());
            combinDownBtn.setEnabled(!bedSetting.isCombiLocked());
            combinUpBtn.setAlpha(bedSetting.isCombiLocked() ? 0.5f : 1f);
            combinDownBtn.setAlpha(bedSetting.isCombiLocked() ? 0.5f : 1f);
        });
    }

    public void setHeightAvailable(boolean isAvailable) {
        Activity activity = getActivity();
        if (activity == null) return;
        activity.runOnUiThread(() -> {
            if (isAvailable) {
                heightContainer.setVisibility(View.VISIBLE);
            } else {
                heightContainer.setVisibility(View.GONE);
            }
        });
    }

    private void handleCombiUpTouch(MotionEvent motionEvent, View view) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                motionEvent.getAction() == MotionEvent.ACTION_CANCEL ||
                isOutside(view, motionEvent)) {
            if (listener != null) {
                if (combiArrowUpDelayHandler != null) {
                    combiArrowUpDelayHandler.removeCallbacks(combiArrowUpDelayCallback);
                    combiArrowUpDelayHandler = null;
                }
                if (combiArrowUpPressed) {
                    combiArrowUpPressed = false;
                    listener.onIncreaseCombinationTouchEnd();
                }
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (combiArrowUpDelayHandler == null) {
                combiArrowUpDelayHandler = new Handler();
                combiArrowUpDelayHandler.postDelayed(combiArrowUpDelayCallback, arrowTouchDelayTime);
            }
        }
    }

    private void handleCombiDownTouch(MotionEvent motionEvent, View view) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                motionEvent.getAction() == MotionEvent.ACTION_CANCEL ||
                isOutside(view, motionEvent)) {
            if (listener != null) {
                if (combiArrowDownDelayHandler != null) {
                    combiArrowDownDelayHandler.removeCallbacks(combiArrowDownDelayCallback);
                    combiArrowDownDelayHandler = null;
                }
                if (combiArrowDownPressed) {
                    combiArrowDownPressed = false;
                    listener.onDecreaseCombinationTouchEnd();
                }
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (combiArrowDownDelayHandler == null) {
                combiArrowDownDelayHandler = new Handler();
                combiArrowDownDelayHandler.postDelayed(combiArrowDownDelayCallback, arrowTouchDelayTime);
            }
        }
    }

    private void handleHeadUpTouch(MotionEvent motionEvent, View view) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                motionEvent.getAction() == MotionEvent.ACTION_CANCEL ||
                isOutside(view, motionEvent)) {
            if (listener != null) {
                if (headArrowUpDelayHandler != null) {
                    headArrowUpDelayHandler.removeCallbacks(headArrowUpDelayCallback);
                    headArrowUpDelayHandler = null;
                }
                if (headArrowUpPressed) {
                    headArrowUpPressed = false;
                    listener.onIncreaseHeadTouchEnd();
                }
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (headArrowUpDelayHandler == null) {
                headArrowUpDelayHandler = new Handler();
                headArrowUpDelayHandler.postDelayed(headArrowUpDelayCallback, arrowTouchDelayTime);
            }
        }
    }

    private void handleHeadDownTouch(MotionEvent motionEvent, View view) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                motionEvent.getAction() == MotionEvent.ACTION_CANCEL ||
                isOutside(view, motionEvent)) {
            if (listener != null) {
                if (headArrowDownDelayHandler != null) {
                    headArrowDownDelayHandler.removeCallbacks(headArrowUpDelayCallback);
                    headArrowDownDelayHandler = null;
                }
                if (headArrowDownPressed) {
                    headArrowDownPressed = false;
                    listener.onDecreaseHeadTouchEnd();
                }
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (headArrowDownDelayHandler == null) {
                headArrowDownDelayHandler = new Handler();
                headArrowDownDelayHandler.postDelayed(headArrowDownDelayCallback, arrowTouchDelayTime);
            }
        }
    }

    private void handleLegUpTouch(MotionEvent motionEvent, View view) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                motionEvent.getAction() == MotionEvent.ACTION_CANCEL ||
                isOutside(view, motionEvent)) {
            if (listener != null) {
                if (legArrowUpDelayHandler != null) {
                    legArrowUpDelayHandler.removeCallbacks(legArrowUpDelayCallback);
                    legArrowUpDelayHandler = null;
                }
                if (legArrowUpPressed) {
                    legArrowUpPressed = false;
                    listener.onIncreaseLegTouchEnd();
                }
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (legArrowUpDelayHandler == null) {
                legArrowUpDelayHandler = new Handler();
                legArrowUpDelayHandler.postDelayed(legArrowUpDelayCallback, arrowTouchDelayTime);
            }
        }
    }

    private void handleLegDownTouch(MotionEvent motionEvent, View view) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                motionEvent.getAction() == MotionEvent.ACTION_CANCEL ||
                isOutside(view, motionEvent)) {
            if (listener != null) {
                if (legArrowDownDelayHandler != null) {
                    legArrowDownDelayHandler.removeCallbacks(legArrowDownDelayCallback);
                    legArrowDownDelayHandler = null;
                }
                if (legArrowDownPressed) {
                    legArrowDownPressed = false;
                    listener.onDecreaseLegTouchEnd();
                }
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (legArrowDownDelayHandler == null) {
                legArrowDownDelayHandler = new Handler();
                legArrowDownDelayHandler.postDelayed(legArrowDownDelayCallback, arrowTouchDelayTime);
            }
        }
    }

    private void handleHeightUpTouch(MotionEvent motionEvent, View view) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                motionEvent.getAction() == MotionEvent.ACTION_CANCEL ||
                isOutside(view, motionEvent)) {
            if (listener != null) {

                if (heightArrowUpDelayHandler != null) {
                    heightArrowUpDelayHandler.removeCallbacks(heightArrowUpDelayCallback);
                    heightArrowUpDelayHandler = null;
                }
                if (heightArrowUpPressed) {
                    heightArrowUpPressed = false;
                    listener.onIncreaseHeightTouchEnd();
                }
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (heightArrowUpDelayHandler == null) {
                heightArrowUpDelayHandler = new Handler();
                heightArrowUpDelayHandler.postDelayed(heightArrowUpDelayCallback, arrowTouchDelayTime);
            }
        }
    }

    private void handleHeightDownTouch(MotionEvent motionEvent, View view) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                motionEvent.getAction() == MotionEvent.ACTION_CANCEL ||
                isOutside(view, motionEvent)) {
            if (listener != null) {

                if (heightArrowDownDelayHandler != null) {
                    heightArrowDownDelayHandler.removeCallbacks(heightArrowDownDelayCallback);
                    heightArrowDownDelayHandler = null;
                }
                if (heightArrowDownPressed) {
                    heightArrowDownPressed = false;
                    listener.onDecreaseHeightTouchEnd();
                }
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (heightArrowDownDelayHandler == null) {
                heightArrowDownDelayHandler = new Handler();
                heightArrowDownDelayHandler.postDelayed(heightArrowDownDelayCallback, arrowTouchDelayTime);
            }
        }
    }

    public boolean isOutside(View v, MotionEvent e) {
        return e.getX() < 0 || e.getY() < 0 || e.getX() > v.getMeasuredWidth() || e.getY() > v.getMeasuredHeight();
    }

    public interface BedManualListener {
        void onIncreaseCombinationTouchStart();

        void onIncreaseCombinationTouchEnd();

        void onDecreaseCombinationTouchStart();

        void onDecreaseCombinationTouchEnd();

        void onIncreaseHeadTouchStart();

        void onIncreaseHeadTouchEnd();

        void onDecreaseHeadTouchStart();

        void onDecreaseHeadTouchEnd();

        void onIncreaseLegTouchStart();

        void onIncreaseLegTouchEnd();

        void onDecreaseLegTouchStart();

        void onDecreaseLegTouchEnd();

        void onIncreaseHeightTouchStart();

        void onIncreaseHeightTouchEnd();

        void onDecreaseHeightTouchStart();

        void onDecreaseHeightTouchEnd();
    }

}
