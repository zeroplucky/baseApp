package com.mindaxx.zhangp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mindaxx.zhangp.R;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*
 * MediaRecorder 录制视频不支持暂停续录
 * https://blog.csdn.net/thelastalien/article/details/51545323
 * */
public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();
    //UI
    private TextView mTvCaptureTime;
    private ImageView mRecordControl;
    private ImageView mPauseRecord;
    private SurfaceView surfaceView;
    private SurfaceHolder mSurfaceHolder;
    //录像机状态标识
    private int mRecorderState;

    public static final int STATE_INIT = 0;
    public static final int STATE_RECORDING = 1;
    public static final int STATE_PAUSE = 2;
    //录制暂停时间间隔
    private Camera mCamera;
    private MediaRecorder mediaRecorder;
    private String mVideoPath;
    private int time = 30; // 录制总时长
    private Timer mTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_record);
        mVideoPath = getSDPath(getApplicationContext()) + getVideoName();
        initView();
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (null != mTvCaptureTime && msg.what == 1) {
                String time = (String) msg.obj;
                if (!TextUtils.isEmpty(time)) {
                    mTvCaptureTime.setText(time);
                }
            } else if (msg.what == 2) {
                stopRecord(); // 停止录制
            }
            return false;
        }
    });

    /*
     * 开始任务
     * */
    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (time != 0) {
                    String ss = new DecimalFormat("00").format(time);
                    String timeFormat = new String("00:" + ss);
                    Message msg = new Message();
                    msg.obj = timeFormat;
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                    time--;
                }
            }
        }, 0, 1000L);
    }

    /*
     * 取消任务
     * */
    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    private MediaRecorder.OnErrorListener OnErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mediaRecorder, int what, int extra) {
            try {
                if (mediaRecorder != null) {
                    mediaRecorder.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.record_surfaceView);
        mRecordControl = (ImageView) findViewById(R.id.record_control);
        mPauseRecord = (ImageView) findViewById(R.id.record_pause);
        mTvCaptureTime = (TextView) findViewById(R.id.tv_capture_time);
        mRecordControl.setOnClickListener(this);
        mPauseRecord.setOnClickListener(this);
        mPauseRecord.setEnabled(false);

        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.setFixedSize(320, 280); // 设置分辨率
        mSurfaceHolder.setKeepScreenOn(true);// 设置该组件不会让屏幕自动关闭
        mSurfaceHolder.addCallback(mSurfaceCallBack);
    }

    private SurfaceHolder.Callback mSurfaceCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            initCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            if (mSurfaceHolder.getSurface() == null) {
                return;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            releaseCamera();
        }
    };

    private void initCamera() {
        if (mCamera != null) {
            releaseCamera();
        }
        mCamera = Camera.open();
        if (mCamera == null) {
            Toast.makeText(this, "未能获取到相机！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //将相机与SurfaceHolder绑定
            mCamera.setPreviewDisplay(mSurfaceHolder);
            //配置CameraParams
            Camera.Parameters params = mCamera.getParameters();
            //设置相机的横竖屏(竖屏需要旋转90°)
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                params.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
            } else {
                params.set("orientation", "landscape");
                mCamera.setDisplayOrientation(0);
            }
            //设置聚焦模式
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            //缩短Recording启动时间
            params.setRecordingHint(true);
            //影像稳定能力
            if (params.isVideoStabilizationSupported()) {
                params.setVideoStabilization(true);
            }
            mCamera.setParameters(params);
            //启动相机预览
            mCamera.startPreview();
        } catch (IOException e) {
            //有的手机会因为兼容问题报错，这就需要开发者针对特定机型去做适配了
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRecord();
        releaseCamera();
    }

    /**
     * 开始录制视频
     */
    public boolean startRecord() {
        initCamera();
        //录制视频前必须先解锁Camera
        mCamera.unlock();
        configMediaRecorder();
        try {
            //开始录制
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 停止录制视频
     */
    public void stopRecord() {
        cancelTimer();
        if (mediaRecorder != null) {
            // 设置后不会崩
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setPreviewDisplay(null);
            //停止录制
            mediaRecorder.stop();
            mediaRecorder.reset();
            //释放资源
            mediaRecorder.release();
            mediaRecorder = null;
        }
        scannerMedia(getApplicationContext(), new File(mVideoPath));
    }

    /**
     * 点击中间按钮，执行的UI更新操作
     */
    private void refreshControlUI() {
        if (mRecorderState == STATE_INIT) {
            //录像时间计时
            mHandler.sendEmptyMessageDelayed(2, time * 1000); //
            startTimer();// 开始计时
            mRecordControl.setImageResource(R.drawable.record_video_stop);
            //1s后才能按停止录制按钮
            mRecordControl.setEnabled(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecordControl.setEnabled(true);
                }
            }, 1000);
            mPauseRecord.setVisibility(View.VISIBLE);
            mPauseRecord.setEnabled(true);

        } else if (mRecorderState == STATE_RECORDING) {
            mHandler.removeMessages(2);
            cancelTimer();

            mRecordControl.setImageResource(R.drawable.record_video_start);
            mPauseRecord.setVisibility(View.GONE);
            mPauseRecord.setEnabled(false);
        }
    }

    /**
     * 点击暂停继续按钮，执行的UI更新操作
     */
    private void refreshPauseUI() {
        if (mRecorderState == STATE_RECORDING) {
            mPauseRecord.setImageResource(R.drawable.control_play);
            mHandler.removeMessages(2);
            cancelTimer();
        } else if (mRecorderState == STATE_PAUSE) {
            mPauseRecord.setImageResource(R.drawable.control_pause);
            mHandler.sendEmptyMessageDelayed(2, time * 1000); //录像时间计时
            startTimer();// 开始计时
        }
    }

    /**
     * 扫描
     */
    public static void scannerMedia(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 配置MediaRecorder()
     */
    private void configMediaRecorder() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        mediaRecorder.reset();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setOnErrorListener(OnErrorListener);
        //使用SurfaceView预览
        mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        //1.设置采集声音
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        //设置采集图像
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //2.设置视频，音频的输出格式 mp4
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        //3.设置音频的编码格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //设置图像的编码格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //音频一秒钟包含多少数据位
        CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        mediaRecorder.setAudioEncodingBitRate(44100);
        if (mProfile.videoBitRate > 2 * 1024 * 1024) {
            mediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
        } else {
            mediaRecorder.setVideoEncodingBitRate(1024 * 1024);
        }
        mediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);
        //设置选择角度，顺时针方向，因为默认是逆向90度的，这样图像就是正常显示了,这里设置的是观看保存后的视频的角度
        mediaRecorder.setOrientationHint(getDisplayOrientation());
        //设置录像的分辨率
        mediaRecorder.setVideoSize(352, 288);
        //设置录像视频输出地址
        mediaRecorder.setOutputFile(mVideoPath);

    }

    public int getDisplayOrientation() {
        android.hardware.Camera.CameraInfo camInfo =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, camInfo);
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result = (camInfo.orientation - degrees + 360) % 360;
        return result;
    }

    /**
     * 创建视频文件保存路径
     */
    public static String getSDPath(Context context) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(context, "请查看您的SD卡是否存在！", Toast.LENGTH_SHORT).show();
            return null;
        }
        File sdDir = Environment.getExternalStorageDirectory();
        File eis = new File(sdDir.toString() + "/RecordVideo/");
        if (!eis.exists()) {
            eis.mkdir();
        }
        return sdDir.toString() + "/RecordVideo/";
    }

    private String getVideoName() {
        return "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
    }

    /*
     * 跳转到设置
     * */
    public void onSkipSettings(View view) {
        try {
            Intent intent = new Intent();
            //下面这种方案是直接跳转到当前应用的设置界面。
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getApplication().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.record_control: {
                if (mRecorderState == STATE_INIT) {
                    if (getSDPath(getApplicationContext()) == null) return;
                    if (!startRecord()) return; //开始录制视频
                    refreshControlUI();
                    mRecorderState = STATE_RECORDING;
                } else if (mRecorderState == STATE_RECORDING) {
                    stopRecord();//停止视频录制
                    mCamera.lock();//先给Camera加锁后再释放相机
                    releaseCamera();
                    refreshControlUI();
                    mRecorderState = STATE_INIT;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("videoPath", mVideoPath);
                            intent.putExtras(bundle);
                            setResult(111, intent);
                            finish();
                        }
                    }, 1000);
                } else if (mRecorderState == STATE_PAUSE) {
                    //代表视频暂停录制时，点击中心按钮
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("videoPath", mVideoPath);
                    intent.putExtras(bundle);
                    setResult(111, intent);
                    finish();
                }
                break;
            }
            case R.id.record_pause: {
                if (mRecorderState == STATE_RECORDING) {
                    stopRecord();
                    refreshPauseUI();
                    mRecorderState = STATE_PAUSE;
                } else if (mRecorderState == STATE_PAUSE) {
                    if (getSDPath(getApplicationContext()) == null) return;
                    if (!startRecord()) return;//继续视频录制
                    refreshPauseUI();
                    mRecorderState = STATE_RECORDING;
                }
            }
        }
    }
}
