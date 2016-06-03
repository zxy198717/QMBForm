package com.quemb.qmbform.brower;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.quemb.qmbform.R;
import com.quemb.qmbform.pojo.ProcessedFile;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by alvinzeng on 4/15/16.
 */
public class PhotoBrowserActivity extends AppCompatActivity {

    public final static String PHOTOS = "PhotoBrowserFragment.PHOTOS";
    public final static String SELECTED_ITEM = "PhotoBrowserFragment.SELECTED_ITEM";
    public final static String PREVIEW_MODE = "PhotoBrowserActivity.PREVIEW_MODE";

    ViewPager viewpager;
    CircleIndicator indicator;
    Toolbar toolbar;

    SamplePagerAdapter samplePagerAdapter;
    TextView titleTextView;

    ArrayList<ProcessedFile> photos = new ArrayList<>();
    int currentItem;
    int photoCount;
    boolean previewModel;
    VideoView videoView;

    protected LinearLayout footerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        previewModel = getIntent().getBooleanExtra(PREVIEW_MODE, false);
        if (previewModel) {
            getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN, android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.qm_activity_photo_preview);
        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (photos != null && photos.size() > 0 && viewpager.getCurrentItem() < photos.size()) {
            ProcessedFile processedFile = photos.get(viewpager.getCurrentItem());

            if (processedFile.isVideo() && videoView != null) {
                videoView.resume();
                videoView.requestFocus();
            }
        }
    }

    protected void setupViews() {
        photos = (ArrayList<ProcessedFile>) getIntent().getSerializableExtra(PHOTOS);
        photoCount = photos.size();
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        footerLayout =  (LinearLayout) findViewById(R.id.footerLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (previewModel) {
            toolbar.setVisibility(View.GONE);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewpager.setAdapter(samplePagerAdapter = new SamplePagerAdapter(photos));
        indicator.setViewPager(viewpager);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(position + 1 + "/" + photos.size());
                ProcessedFile processedFile = photos.get(position);

                if (processedFile.isVideo()) {
                    if (videoView != null) {
                        videoView.start();
                        videoView.resume();
                        videoView.requestFocus();
                        videoView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (videoView != null) {
                        videoView.pause();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        currentItem = getIntent().getIntExtra(SELECTED_ITEM, 0);
        if (currentItem > 0) {
            viewpager.setCurrentItem(currentItem);
        } else {
            setTitle(1 + "/" + photos.size());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_browser, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.delete) {
            confirmDialog(viewpager.getCurrentItem());
        }

        return true;
    }

    private void confirmDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage("确定要删除吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        photos.remove(position);
                        if (photos.size() == 0) {
                            finish();
                        } else {
                            samplePagerAdapter.notifyDataSetChanged();
                            setTitle(viewpager.getCurrentItem() + 1 + "/" + photos.size());
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void finish() {
        if (photoCount != photos.size()) {
            Intent intent = new Intent();
            intent.putExtra(PHOTOS, photos);
            setResult(RESULT_OK, intent);
        }
        super.finish();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        titleTextView.setText(title);
    }

    protected void pageItemClick() {
        if (previewModel) {
            super.finish();
            return;
        }
        if (toolbar.getVisibility() == View.GONE) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    class SamplePagerAdapter extends PagerAdapter {

        ArrayList<ProcessedFile> photos = new ArrayList<>();

        public SamplePagerAdapter(ArrayList<ProcessedFile> photos) {
            super();
            this.photos = photos;
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {

            ProcessedFile processedFile = photos.get(position);

            if (processedFile.isVideo()) {
                View v = LayoutInflater.from(PhotoBrowserActivity.this).inflate(R.layout.qm_video_item, null);

                videoView = (VideoView) v.findViewById(R.id.videoView);
                videoView.setVideoURI(Uri.parse(processedFile.getPath()));
                final ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
                final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progressBar);

                Glide.with(PhotoBrowserActivity.this).load(processedFile.getThumbPath()).into(imageView);
                v.findViewById(R.id.playButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageItemClick();
                    }
                });

                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        imageView.animate().alpha(0).setDuration(500).start();
                        progressBar.setVisibility(View.GONE);
                    }
                });

                if (currentItem == position) {
                    videoView.start();
                    videoView.resume();
                    videoView.requestFocus();
                    videoView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            videoView.setVisibility(View.VISIBLE);
                        }
                    }, 500);
                }

                container.addView(v, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                return v;
            }

            FrameLayout frameLayout = new FrameLayout(PhotoBrowserActivity.this);

            ImageView thumbImageView = new ImageView(PhotoBrowserActivity.this);
            thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            frameLayout.addView(thumbImageView, getResources().getDimensionPixelSize(R.dimen.browser_thumb_size), getResources().getDimensionPixelSize(R.dimen.browser_thumb_size));
            FrameLayout.LayoutParams thumbImageViewLP = (FrameLayout.LayoutParams) thumbImageView.getLayoutParams();
            thumbImageViewLP.gravity = Gravity.CENTER;
            if (photos.get(position).getThumbPath() != null && !photos.get(position).getThumbPath().isEmpty()) {
                Glide.with(PhotoBrowserActivity.this).load(photos.get(position).getThumbPath()).into(thumbImageView);
            }

            ProgressBar progressBar = new ProgressBar(PhotoBrowserActivity.this);
            frameLayout.addView(progressBar, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) progressBar.getLayoutParams();
            lp.gravity = Gravity.CENTER;

            PhotoView photoView = new PhotoView(container.getContext());
            // Now just add PhotoView to ViewPager and return it
            frameLayout.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            Glide.with(PhotoBrowserActivity.this).load(photos.get(position).getPath()).into(photoView);

            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    pageItemClick();
                }

                @Override
                public void onOutsidePhotoTap() {
                    pageItemClick();
                }
            });

            container.addView(frameLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            return frameLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}
