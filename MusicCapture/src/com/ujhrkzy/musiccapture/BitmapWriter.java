package com.ujhrkzy.musiccapture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;

public class BitmapWriter implements PictureWriter {

    @Override
    public String wirte(byte[] data) {
        String saveDirectory = Environment.getExternalStorageDirectory()
                .getPath() + "/test";
        createDirectory(saveDirectory);
        String imagePath = createImagePath(saveDirectory);

        // ファイル保存
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath, true);
            fos.write(data);
            Bitmap bmp = convertBitMap(data);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            Log.e("Debug", e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e("Debug", e.getMessage());
                }
            }
        }
        return imagePath;
    }

    private File createDirectory(String saveDirectory) {
        File directory = new File(saveDirectory);
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                Log.e("Debug", "Make Dir Error");
            }
        }
        return directory;
    }

    private String createImagePath(String directory) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.JAPAN);
        return directory + "/" + sf.format(cal.getTime()) + ".jpg";
    }

    private Bitmap convertBitMap(byte[] data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                options);
        // bitmap画像をカットする処理など
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        // 中央部分の四角形をカット
        bitmap = cutBitmap(bitmap, (w - h) / 10, 0, h, h);
        // 90度回転
        bitmap = rotateBitmap90(bitmap);
        return bitmap;
    }

    private Bitmap cutBitmap(Bitmap bmp, int x, int y, int w, int h) {
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bmp, -x, -y, null);
        return result;
    }

    private Bitmap rotateBitmap90(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Bitmap result = Bitmap.createBitmap(h, w, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.rotate(90, 0, 0);
        canvas.drawBitmap(bmp, 0, -h, null);
        return result;
    }
}
