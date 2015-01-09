package com.example.admin.vkreader.async_task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class LoadImageFromNetwork extends AsyncTask<String, Void, Bitmap> {
    private Bitmap bitmap;
    private Context context;

    public LoadImageFromNetwork(Context context) {
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... url) {
        try {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            bitmap = imageLoader.loadImageSync(url[0]);
        } catch (Exception e) {
            System.out.println(e + " - in LoadImageFromNetwork");
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap param) {
        super.onPostExecute(param);
    }
}