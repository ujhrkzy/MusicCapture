package com.ujhrkzy.musiccapture;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {

    private Camera camera;

    /**
     * コンストラクタ
     */
    public CameraPreview(Context context, Camera cam) {
        super(context);
        camera = cam;
        // サーフェスホルダーの取得とコールバック通知先の設定
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * SurfaceView 生成
     */
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // カメラインスタンスに、画像表示先を設定
            camera.setPreviewDisplay(holder);
            // プレビュー開始
            camera.startPreview();
        } catch (IOException e) {
            //
            e.printStackTrace();
        }
    }

    /**
     * SurfaceView 破棄
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    /**
     * SurfaceHolder が変化したときのイベント
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        // 最適サイズを取得
        // Camera.Parameters params = mCam.getParameters();
        // List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        // Camera.Size optimalSize = getOptimalPreviewSize(sizes, width,
        // height);
        // params.setPreviewSize(optimalSize.width, optimalSize.height);
        // mCam.setParameters(params);
        //
        // // プレビュースタート（Changedは最初にも1度は呼ばれる）
        camera.startPreview();
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w,
            int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}