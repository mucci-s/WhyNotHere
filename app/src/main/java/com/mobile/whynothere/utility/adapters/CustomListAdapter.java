package com.mobile.whynothere.utility.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.mobile.whynothere.R;
import com.mobile.whynothere.models.Comment;

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
            holder.authorImageView = view.findViewById(R.id.profileAvatarID);
            holder.authorNameView = view.findViewById(R.id.authorID);
            holder.commentTextView = view.findViewById(R.id.commentID);
            holder.albumView = view.findViewById(R.id.imageGrid);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Comment comment = this.listComment.get(i);
        holder.authorNameView.setText(comment.getAuthor());
        holder.commentTextView.setText(comment.getComment());
        holder.authorImageView.setImageResource(comment.getPhotoProfile());
        holder.albumView.setAdapter(new DefaultImageAdaptorComment(comment.getImages(), this.context));
        return view;
    }

    static class ViewHolder{
        CircularImageView authorImageView;
        TextView authorNameView;
        TextView commentTextView;
        GridView albumView;
    }
}
