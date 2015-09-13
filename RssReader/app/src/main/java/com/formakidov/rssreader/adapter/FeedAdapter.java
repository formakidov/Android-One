package com.formakidov.rssreader.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.formakidov.rssreader.DatabaseManager;
import com.formakidov.rssreader.R;
import com.formakidov.rssreader.data.FeedItem;
import com.formakidov.rssreader.fragment.FeedListFragment;
import com.formakidov.rssreader.task.LoadDefaultImageUrlTask;
import com.formakidov.rssreader.tools.Constants;
import com.formakidov.rssreader.tools.Tools;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>  implements Constants {

    private final FeedListFragment fragment;
    private final List<FeedItem> items;

    public FeedAdapter(FeedListFragment fragment, List<FeedItem> items) {
        this.fragment = fragment;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_feed, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final FeedItem item = items.get(position);
        String name = item.getName();
        String url = item.getUrl();
        viewHolder.feedName.setText(name);
        viewHolder.url.setText(url);
        LoadDefaultImageUrlTask task = new LoadDefaultImageUrlTask() {
            @Override
            protected void onPostExecute(String s) {
                Tools.imageLoader.loadImage(s, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                        viewHolder.progress.setVisibility(View.GONE);
                        viewHolder.picture.setVisibility(View.VISIBLE);
                        viewHolder.picture.setImageResource(R.drawable.ic_launcher);
                    }

                    @Override
                    public void onLoadingComplete(String arg0, View arg1, Bitmap b) {
                        viewHolder.progress.setVisibility(View.GONE);
                        viewHolder.picture.setVisibility(View.VISIBLE);
                        if (null != b) {
                            viewHolder.picture.setImageBitmap(b);
                        } else {
                            viewHolder.picture.setImageResource(R.drawable.ic_launcher);
                        }
                    }
                });
            }
        };
        task.execute(url);

        viewHolder.itemMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(fragment.getActivity(), v);
                menu.getMenuInflater().inflate(R.menu.feed_item_menu, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem paramMenuItem) {
                        switch (paramMenuItem.getItemId()) {
                            case R.id.edit:
                                fragment.showEditFeedDialog(items.indexOf(item));
                                break;
                            case R.id.delete:
                                deleteItem(items.indexOf(item));
                                if (getItemCount() == 0) {
                                    fragment.changeErrorMessageVisibility(true);
                                }
                                break;
                        }
                        return false;
                    }
                });
                menu.show();
            }
        });
    }

//    private void animateViewChanges(final View showView, final View hideView) {
//        Animation fadeOutAnimation = AnimationUtils.loadAnimation(fragment.getActivity(), R.anim.fade_out);
//        fadeOutAnimation.setDuration(CHANGE_VIEW_ANIMATION_DURATION);
//        hideView.startAnimation(fadeOutAnimation);
//        fadeOutAnimation.setAnimationListener(new SimpleAnimationListener() {
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                hideView.setVisibility(View.INVISIBLE);
//                showView.setVisibility(View.VISIBLE);
//                Animation fadeInAnimation = AnimationUtils.loadAnimation(fragment.getActivity(), R.anim.fade_in);
//                fadeInAnimation.setDuration(CHANGE_VIEW_ANIMATION_DURATION);
//                showView.startAnimation(fadeInAnimation);
//            }
//        });
//
//    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public FeedItem getItem(int position) {
        return items.get(position);
    }

    public void deleteItem(int position) {
        DatabaseManager manager = DatabaseManager.getInstance(fragment.getActivity());
        manager.deleteFeed(getItem(position).getUUID());
        notifyItemRemoved(position);
        items.remove(position);
    }

    public void addItem(FeedItem newItem) {
        items.add(newItem);
        DatabaseManager manager = DatabaseManager.getInstance(fragment.getActivity());
        manager.addFeed(newItem);
        notifyItemInserted(getItemCount() - 1);
    }

    public void itemChanged(int position, FeedItem changedItem) {
        DatabaseManager manager = DatabaseManager.getInstance(fragment.getActivity());
        manager.editFeed(changedItem);
        items.set(position, changedItem);
        notifyItemChanged(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView picture;
        private final TextView feedName;
        private final TextView url;
        private final ImageView itemMenu;
        private final ProgressBar progress;

        public ViewHolder(View itemView) {
            super(itemView);
            feedName = (TextView) itemView.findViewById(R.id.feed_name);
            url = (TextView) itemView.findViewById(R.id.url);
            picture = (ImageView) itemView.findViewById(R.id.picture);
            itemMenu = (ImageView) itemView.findViewById(R.id.menu_three_dot);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }
}
