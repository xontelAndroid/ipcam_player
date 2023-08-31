package com.xontel.ipcam_player.hikvision;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.xontel.ipcam_player.IpCam;
import com.xontel.ipcam_player.LoadingDots;
import com.xontel.ipcam_player.R;


public class CamPlayerView extends CardView implements SurfaceHolder.Callback, View.OnClickListener {
    public static final String TAG = CamPlayerView.class.getSimpleName();
    private LoadingDots mLoadingDots;
    private TextView errorTextView;

    private TextView name;

    private TextView playerName;
    private ImageView addBtn;
    private ViewStub surfaceStub;
    private SurfaceView mSurfaceView;

    private Context context;

    private SurfaceCallback mSurfaceCallback;
    private boolean isSurfaceCreated;

    private ClickListener mClickListener;



    public CamPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CamPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init( ) {
//        this.mClickListener = clickListener;
        setCardBackgroundColor(context.getColor(android.R.color.darker_gray));
        inflate(context, R.layout.item_player_view, this);
        mLoadingDots = findViewById(R.id.loading_dots);
        name = findViewById(R.id.tv_cam_name);
        playerName = findViewById(R.id.player_name);
        errorTextView = findViewById(R.id.error_stream);
        addBtn = findViewById(R.id.iv_add);
        surfaceStub = findViewById(R.id.stub);
        mSurfaceView = (SurfaceView) surfaceStub.inflate();
        mSurfaceView.getHolder().addCallback(this);
        setOnClickListener(this);
    }


    public void showLoading(boolean show) {
        mLoadingDots.setVisibility(show ? VISIBLE : GONE);
    }

    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    public void setCamName(String camName) {
        name.setVisibility(VISIBLE);
        name.setText(camName);
    }


    public void showError(String logMessage) {
        errorTextView.setVisibility(VISIBLE);
        errorTextView.setText(logMessage);
    }

    public void onAttachToPlayer(SurfaceCallback surfaceCallback, String hashCode) {
        Log.v(TAG, "atttach___");
        this.playerName.setText(hashCode);
        mLoadingDots.setVisibility(View.VISIBLE);
//        name.setVisibility(View.VISIBLE);
//        name.setText(mIpCam.getName());
        addBtn.setVisibility(View.GONE);
        this.mSurfaceCallback = surfaceCallback;
    }

    public void onDetachedFromPlayer() {
        this.mSurfaceCallback = null;
        mLoadingDots.setVisibility(View.GONE);
        addBtn.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
        errorTextView.setText("");
        name.setVisibility(GONE);
        name.setText("");
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        isSurfaceCreated = true;
        if (mSurfaceCallback != null)
            mSurfaceCallback.onSurfaceCreated();

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        isSurfaceCreated = false;
        if (mSurfaceCallback != null)
            mSurfaceCallback.onSurfaceDestroyed();
    }

    public boolean isSurfaceCreated() {
        return isSurfaceCreated;
    }

    @Override
    public void onClick(View view) {
//        mClickListener.onViewClicked(mSurfaceCallback != null, this);
    }


    public interface HikClickViews {
        void onHikClick(IpCam ipCam);
    }

    public interface SurfaceCallback {
        void onSurfaceCreated();

        void onSurfaceDestroyed();
    }

    public interface ClickListener{
        void onViewClicked(boolean isAttachedToPlayer, CamPlayerView camPlayerView);
    }
}

