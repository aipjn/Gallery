package com.javano1.gallery.activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.javano1.gallery.adapter.GalleryAdapter;
import com.javano1.gallery.view.SpaceItemDecoration;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private ArrayList<String> pathList = new ArrayList<>();
    private MHandler mHandler = new MHandler(this);

    private static final int FINISH_GET_IMAGE_PATH = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_title);
        setSupportActionBar(toolbar);

        getImagesPath();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_map) {
            Log.i("MENU", "action_map");
            return true;
        } else if (id == R.id.action_camera) {
            Log.i("MENU", "action_camera");
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getImagesPath() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = GalleryActivity.this.getContentResolver();

                String selection = MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?";
                String[] selectionArgs = {"image/jpeg", "image/png"};
                String sortOrder = MediaStore.Images.Media.DATE_MODIFIED;
                Cursor mCursor = mContentResolver.query(mImageUri, null, selection, selectionArgs, sortOrder);

                if (mCursor == null) return;
                while (mCursor.moveToNext()) {
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    String parentName = new File(path).getParentFile().getName();
                    if (parentName.equals("Camera") || parentName.equals("camera"))
                        pathList.add(path);
                }

                mHandler.sendEmptyMessage(FINISH_GET_IMAGE_PATH);
                mCursor.close();
            }
        }).start();
    }

    private void initRecyclerView() {
        if (!pathList.isEmpty()) {
            RecyclerView gRecyclerView = findViewById(R.id.grecyclerview);
            GalleryAdapter gAdapter = new GalleryAdapter(this, pathList);
            RecyclerView.LayoutManager gLayoutManager = new GridLayoutManager(this, 3);
            SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration(5);
            gRecyclerView.setHasFixedSize(true);
            gRecyclerView.setAdapter(gAdapter);
            gRecyclerView.setLayoutManager(gLayoutManager);
            gRecyclerView.addItemDecoration(spaceItemDecoration);
            gAdapter.notifyDataSetChanged();
        }
    }

    private static class MHandler extends Handler {
        WeakReference<GalleryActivity> mActivity;

        private MHandler(GalleryActivity activity) {
            if (activity != null)
                mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FINISH_GET_IMAGE_PATH)
                mActivity.get().initRecyclerView();
        }
    }
}
