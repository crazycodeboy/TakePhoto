package multipleimageselect.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.jph.takephoto.R;
import java.util.ArrayList;
import multipleimageselect.models.Album;

/**
 * Created by Darshan on 4/14/2015.
 */
public class CustomAlbumSelectAdapter extends CustomGenericAdapter<Album> {
  private boolean enableCamera;
  private OnItemClick onItemClick;

  public CustomAlbumSelectAdapter(Context context, ArrayList<Album> albums, boolean enableCamera) {
    super(context, albums);
    this.enableCamera = enableCamera;
  }

  public void setOnItemClick(OnItemClick onItemClick) {
    this.onItemClick = onItemClick;
  }

  @Override public int getCount() {
    if (enableCamera) {
      return super.getCount() + 1;
    }
    return super.getCount();
  }

  @Override public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;

    if (convertView == null) {
      convertView = layoutInflater.inflate(R.layout.grid_view_item_album_select, null);

      viewHolder = new ViewHolder();
      viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view_album_image);
      viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view_album_name);
      viewHolder.frameLayout = (FrameLayout) convertView.findViewById(R.id.frame_album_item);

      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.imageView.getLayoutParams().width = size;
    viewHolder.imageView.getLayoutParams().height = size;
    //启用拍照
    if (enableCamera) {
      if (0 == position) {
        if (null != onItemClick) {
          viewHolder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
              onItemClick.takePhoto();
            }
          });
        }
        viewHolder.textView.setText("拍照");
        Glide.with(context).load(R.drawable.ic_camera).centerCrop().into(viewHolder.imageView);
      } else {
        if (null != onItemClick) {
          viewHolder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
              onItemClick.select(position-1);
            }
          });
        }
        viewHolder.textView.setText(arrayList.get(position - 1).name);
        Glide.with(context)
            .load(arrayList.get(position - 1).cover)
            .placeholder(R.drawable.image_placeholder)
            .centerCrop()
            .into(viewHolder.imageView);
      }
    } else {
      //不启用拍照
      if (null != onItemClick) {
        viewHolder.frameLayout.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            onItemClick.select(position);
          }
        });
      }
      viewHolder.textView.setText(arrayList.get(position).name);
      Glide.with(context)
          .load(arrayList.get(position).cover)
          .placeholder(R.drawable.image_placeholder)
          .centerCrop()
          .into(viewHolder.imageView);
    }

    return convertView;
  }

  private static class ViewHolder {
    public FrameLayout frameLayout;
    public ImageView imageView;
    public TextView textView;
  }

  public interface OnItemClick {
    void select(int pos);

    void takePhoto();
  }
}
