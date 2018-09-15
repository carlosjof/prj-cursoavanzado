package com.example.carlosjof.prjreport.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carlosjof.prjreport.R;
import com.example.carlosjof.prjreport.model.UploadImage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context contexto;
    private List<UploadImage> uploadImagenes;

    public ImageAdapter(Context context, List<UploadImage> uploadImages) {
        contexto = context;
        uploadImagenes = uploadImages;

    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contexto).inflate(R.layout.show_post_content, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        UploadImage uploadCurrent = uploadImagenes.get(position);
        holder.textView.setText(uploadCurrent.getDescription());
        holder.textViewLocation.setText(uploadCurrent.getLocation());
        Picasso.get()
                .load(uploadCurrent.getURLImage())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return uploadImagenes.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView textViewLocation;
        public ImageView imageView;
        public ImageButton button;

        public ImageViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.txt_name_activity_postcontent);
            imageView = itemView.findViewById(R.id.img_activity_postcontent);
            button = itemView.findViewById(R.id.btnshare_activity_postcontent);
            textViewLocation = itemView.findViewById(R.id.txt_location_activity_postcontent);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imageView.buildDrawingCache();
                    Bitmap bitmap = imageView.getDrawingCache();
                    try {

                        File file = new File(imageView.getContext().getCacheDir(), bitmap + ".jpg");
                        FileOutputStream fileOutputStream = null;
                        fileOutputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        file.setReadable(true, false);
                        final Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        intent.setType("image/*");
                        contexto.startActivity(intent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }
}
