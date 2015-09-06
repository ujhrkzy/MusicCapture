package com.ujhrkzy.musiccapture;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;

public class CaputureActivity extends ActionBarActivity {

    private Camera camera = null;

    private CameraPreview cameraPreview = null;

    private boolean isTakenPicture = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caputure);

        try {
            camera = Camera.open();
            // Portrait対応
            camera.setDisplayOrientation(90);
        } catch (Exception e) {
            this.finish();
            return;
        }
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        cameraPreview = new CameraPreview(this, camera);
        preview.addView(cameraPreview);
        cameraPreview.setOnTouchListener(new InnerOnTouchListener());
        // getWindow().clearFlags(
        // WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView(preview);
        // addContentView(new CameraOverlayView(this), new LayoutParams(
        // LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // カメラ破棄インスタンスを解放
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    /**
     * JPEG データ生成完了時のコールバック
     */
    private Camera.PictureCallback mPicJpgListener = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data == null) {
                return;
            }
            PictureWriter writer = new BitmapWriter();
            String imagePath = writer.wirte(data);

            // アンドロイドのデータベースへ登録
            // (登録しないとギャラリーなどにすぐに反映されないため)
            registAndroidDB(imagePath);
            // takePicture するとプレビューが停止するので、再度プレビュースタート
            camera.startPreview();
            isTakenPicture = false;
        }
    };

    private void registAndroidDB(String path) {
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = CaputureActivity.this
                .getContentResolver();
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put("_data", path);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
    }

    private class InnerOnTouchListener implements OnTouchListener {

        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_DOWN) {
                return true;
            }
            if (isTakenPicture) {
                return true;
            }
            // 撮影中の2度押し禁止用フラグ
            isTakenPicture = true;
            // 画像取得
            camera.autoFocus(new Camera.AutoFocusCallback() {

                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    camera.takePicture(null, null, mPicJpgListener);
                }
            });
            return true;
        }
    }
}
