package com.formakidov.rssreader;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.formakidov.rssreader.data.FeedItem;
import com.formakidov.rssreader.fragment.FeedListFragment;
import com.formakidov.rssreader.tools.Constants;

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
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        FeedItem item = items.get(position);
        String name = item.getName();
        String url = item.getUrl();
        viewHolder.feedName.setText(name);
        viewHolder.url.setText(url);
        viewHolder.picture.setImageResource(R.drawable.ic_launcher);
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
                                fragment.showEditFeedDialog(position);
                                break;
                            case R.id.delete:
                                deleteItem(position);
                                break;
                        }
                        return false;
                    }
                });
                menu.show();
            }
        });
    }

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
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(FeedItem newItem) {
        items.add(newItem);
        DatabaseManager manager = DatabaseManager.getInstance(fragment.getActivity());
        manager.addFeed(newItem);
        notifyItemInserted(getItemCount()-1);
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

        public ViewHolder(View itemView) {
            super(itemView);
            feedName = (TextView) itemView.findViewById(R.id.feed_name);
            url = (TextView) itemView.findViewById(R.id.url);
            picture = (ImageView) itemView.findViewById(R.id.picture);
            itemMenu = (ImageView) itemView.findViewById(R.id.menu_three_dot);
        }
    }
}
