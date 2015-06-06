package com.formakidov.rssreader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.formakidov.rssreader.R;
import com.formakidov.rssreader.fragment.NewsDetailFragment;
import com.formakidov.rssreader.fragment.NewsListFragment;

/**
 * An activity representing a list of News. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link NewsDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link NewsListFragment} and the item details
 * (if present) is a {@link NewsDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link NewsListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class NewsListActivity extends Activity implements NewsListFragment.Callbacks {
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        if (findViewById(R.id.news_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
//            ((NewsListFragment) getFragmentManager()
//                    .findFragmentById(R.id.news_list))
//                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    @Override
    public void onItemSelected(int index) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(NewsDetailFragment.EXTRA_NEWS_INDEX, index);
            NewsDetailFragment fragment = new NewsDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.news_detail_container, fragment)
                    .commit();
        } else {
        	//TODO: NewsPagerActivity.class
            Intent detailIntent = new Intent(this, NewsDetailActivity.class);
            detailIntent.putExtra(NewsDetailFragment.EXTRA_NEWS_INDEX, index);
            startActivity(detailIntent);
        }
    }
}
