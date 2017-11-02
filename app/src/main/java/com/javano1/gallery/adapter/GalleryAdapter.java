package com.javano1.gallery.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.javano1.gallery.activity.R;
import com.javano1.gallery.service.LoadImageService;
import com.javano1.gallery.view.GImageView;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.View_Holder> {

    private Context context;
    private ArrayList<String> pathList;

    public GalleryAdapter(Context context, ArrayList<String> pathList) {
        this.context = context;
        this.pathList = pathList;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grecyclerview_item, parent, false);
        return new View_Holder(v);
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {
        String path = pathList.get(position);
        int measuredSize = holder.imageView.getWidth();
        Bitmap bitmap = LoadImageService.getInstance().loadImageByPath(path, measuredSize, new LoadImageService.LoadImageCallBack() {
            @Override
            public void onLoadImage(Bitmap bitmap) {
                if(bitmap != null)
                    holder.imageView.setImageBitmap(bitmap);
            }
        });

        if (bitmap != null)
            holder.imageView.setImageBitmap(bitmap);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ONCLICK", "itemView.onclick");
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("ABC", "SIZE"+pathList.size());
        return pathList.isEmpty() ? 0 : pathList.size();
    }

    class View_Holder extends RecyclerView.ViewHolder {
        private GImageView imageView;

        View_Holder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item);
        }
    }
}
