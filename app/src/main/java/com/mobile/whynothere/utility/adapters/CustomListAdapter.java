package com.mobile.whynothere.utility.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.mobile.whynothere.R;
import com.mobile.whynothere.models.Comment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class CustomListAdapter  extends BaseAdapter {

    private List<Comment> listComment;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomListAdapter(List<Comment> listComment, Context acontext) {
        this.listComment = listComment;
        this.context = acontext;
        layoutInflater = LayoutInflater.from(acontext);
    }

    @Override
    public int getCount() { return listComment.size();  }

    @Override
    public Object getItem(int i) { return listComment.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_comment_layout, null);
            holder = new ViewHolder();
            holder.authorImageView = view.findViewById(R.id.listProfileAvatarID);
            holder.authorNameView = view.findViewById(R.id.titleNewPlaceID);
            holder.commentTextView = view.findViewById(R.id.descriptionNewPlaceID);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        URL url = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            url = new URL("https://instagram.fcia2-1.fna.fbcdn.net/v/t51.2885-19/s320x320/129774148_1769584386533325_563684751460050178_n.jpg?_nc_ht=instagram.fcia2-1.fna.fbcdn.net&_nc_ohc=rrTmSh1ro_EAX_u6lWM&tp=1&oh=c906bbd48d7852869b94b8f68622e95e&oe=6056DEBB");
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            holder.authorImageView.setImageBitmap(bmp);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Comment comment = this.listComment.get(i);
        holder.authorNameView.setText(comment.getAuthor());
        holder.commentTextView.setText(comment.getComment());
        //holder.authorImageView.setImageResource(comment.getPhotoProfile());

        return view;
    }

    static class ViewHolder{
        CircularImageView authorImageView;
        TextView authorNameView;
        TextView commentTextView;
    }
}
