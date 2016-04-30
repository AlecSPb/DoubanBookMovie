package com.xuejinwei.doubanbookmovie.doubanbookmovie.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xuejinwei.doubanbookmovie.doubanbookmovie.R;
import com.xuejinwei.doubanbookmovie.doubanbookmovie.ui.base.activity.SwipeBackActivity;
import com.xuejinwei.doubanbookmovie.doubanbookmovie.util.CommonUtil;
import com.xuejinwei.doubanbookmovie.doubanbookmovie.widget.DialogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xuejinwei on 16/4/25.
 * Email:xuejinwei@outlook.com
 */
public class BookCollectionDetailActivity extends SwipeBackActivity {
    public static final String COLLECTION_ID = "collection_id";
    String bookid;
    @Bind(R.id.toolbar)     Toolbar            toolbar;
    @Bind(R.id.iv_avatar)   ImageView          iv_avatar;
    @Bind(R.id.tv_title)    TextView           tv_title;
    @Bind(R.id.ratingBar)   AppCompatRatingBar ratingBar;
    @Bind(R.id.tv_rating)   TextView           tv_rating;
    @Bind(R.id.ll_rating)   LinearLayout       ll_rating;
    @Bind(R.id.tv_author)   TextView           tv_author;
    @Bind(R.id.tv_price)    TextView           tv_price;
    @Bind(R.id.tv_status)   TextView           tv_status;
    @Bind(R.id.tv_update)   TextView           tv_update;
    @Bind(R.id.tv_commit)   TextView           tv_commit;
    @Bind(R.id.progressBar) ProgressBar        progressBar;
    @Bind(R.id.ll_root)     LinearLayout       ll_root;
    @Bind(R.id.cardview)    CardView           cardview;


    public static void start(Activity activity, String book_id) {
        Intent starter = new Intent(activity, BookCollectionDetailActivity.class);
        starter.putExtra(COLLECTION_ID, book_id);
        activity.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_collection_detail);
        ButterKnife.bind(this);
        bookid = getIntent().getStringExtra(COLLECTION_ID);
        onRefresh();
    }

    private void onRefresh() {
        ll_root.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        runRxTaskOnUi(mApiWrapper.getBookCollectionsDetail(bookid), bookCollections -> {

            Glide.with(BookCollectionDetailActivity.this)
                    .load(bookCollections.book.images.large)
                    .fitCenter()
                    .into(iv_avatar);
            tv_title.setText(bookCollections.book.title);
            if (!bookCollections.book.rating.average.equals("0")) {
                tv_rating.setText(bookCollections.book.rating.average);
                ratingBar.setRating(Float.parseFloat(bookCollections.book.rating.average));
                ll_rating.setVisibility(View.VISIBLE);
            } else {
                ll_rating.setVisibility(View.GONE);
            }
            tv_price.setText(bookCollections.book.price);
            tv_author.setText(bookCollections.book.getAuthor());
            tv_commit.setText(bookCollections.comment);
            tv_status.setText(bookCollections.getStatus());
            tv_update.setText(bookCollections.updated);
            ll_root.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            cardview.setOnClickListener(v -> BookDetailActivity.start(this, bookCollections.book_id));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_collection_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_book_collection_edit) {
            BookCollectionDetailEditActivity.start(BookCollectionDetailActivity.this, BookCollectionDetailEditActivity.Type.EDIT, bookid);
            return true;
        }

        if (item.getItemId() == R.id.action_book_collection_delete) {
            DialogUtil.simpleMessage(this, "确定删除？", () -> runRxTaskOnUi(mApiWrapper.deleteBookCollections(bookid), success -> {
                CommonUtil.toast("删除成功");
                finish();
            }));

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onRefresh();
        }
    }
}
