package com.example.notepadproject.are.src.main.java.com.chinalwb.are.strategies.defaults;

import android.content.Context;
import android.content.Intent;
import android.text.style.URLSpan;
import android.util.Log;

import com.example.notepadproject.are.src.main.java.com.chinalwb.are.Util;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.spans.AreAtSpan;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.spans.AreImageSpan;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.spans.AreVideoSpan;
import com.example.notepadproject.are.src.main.java.com.chinalwb.are.strategies.AreClickStrategy;

import static com.example.notepadproject.are.src.main.java.com.chinalwb.are.spans.AreImageSpan.ImageType;

/**
 * Created by wliu on 30/06/2018.
 */

public class DefaultClickStrategy implements AreClickStrategy {
    @Override
    public boolean onClickAt(Context context, AreAtSpan atSpan) {
        Intent intent = new Intent();
        intent.setClass(context, DefaultProfileActivity.class);
        intent.putExtra("userKey", atSpan.getUserKey());
        intent.putExtra("userName", atSpan.getUserName());
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean onClickImage(Context context, AreImageSpan imageSpan) {
        Intent intent = new Intent();
        ImageType imageType = imageSpan.getImageType();
        intent.putExtra("imageType", imageType);
        if (imageType == ImageType.URI) {
            intent.putExtra("uri", imageSpan.getUri());
        } else if (imageType == ImageType.URL) {
            intent.putExtra("url", imageSpan.getURL());
        } else {
            intent.putExtra("resId", imageSpan.getResId());
        }
        intent.setClass(context, DefaultImagePreviewActivity.class);
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean onClickVideo(Context context, AreVideoSpan videoSpan) {
        Intent intent = new Intent();
        AreVideoSpan.VideoType videoType = videoSpan.getVideoType();
        intent.putExtra("videoType", videoType);
        if(videoType == AreVideoSpan.VideoType.LOCAL){
            intent.putExtra("local", videoSpan.getVideoPath());
            Log.e("HELLOOO", videoSpan.getVideoPath());
            intent.setClass(context, DefaultVideoPreviewActivity.class);
            context.startActivity(intent);
        }
        else if(videoType == AreVideoSpan.VideoType.SERVER){
            intent.putExtra("server", videoSpan.getVideoUrl());
            Log.e("HELLOOO", videoSpan.getVideoUrl());
            intent.setClass(context, DefaultVideoPreviewActivity.class);
            context.startActivity(intent);
        }
        else
            Util.toast(context, "Video span");
        return true;
    }

    @Override
    public boolean onClickUrl(Context context, URLSpan urlSpan) {
        // Use default behavior
        return false;
    }
}
