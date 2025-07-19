package com.kernelapps.onlinejobz.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.kernelapps.onlinejobz.BaseActivity;
import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.models.JobItem;
import com.kernelapps.onlinejobz.utils.AdsManager;
import com.kernelapps.onlinejobz.utils.FavoritesManager;
import com.kernelapps.onlinejobz.utils.MarkdownRenderer;

//import org.commonmark.node.Node;
//import org.commonmark.parser.Parser;
//import org.commonmark.renderer.html.HtmlRenderer;

//import com.vladsch.flexmark.html.HtmlRenderer;
//import com.vladsch.flexmark.parser.Parser;

//import com.vladsch.flexmark.util.ast.Node;


public class JobActivity extends BaseActivity {
    private MenuItem favoriteItem;
    private JobItem jobItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        WebView webView = findViewById(R.id.webView);
        //TextView textDescription = findViewById(R.id.textDescription);
        configureWebView(webView);
        ImageView headerImage = findViewById(R.id.header_image);

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Deserialize JobItem
        jobItem = (JobItem) getIntent().getSerializableExtra("jobItem");

        if (jobItem != null) {
            // Load header image
            Glide.with(this)
                    .load(jobItem.getImage())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(headerImage);

            // Get markdown content (assuming jobItem.getDescription() returns markdown)
            String markdown = jobItem.getDescription();

            //Custom markdown Render in WebView
            MarkdownRenderer.renderMarkdown(webView, markdown);

            //commonmark-java + HtmlCompat
            //renderMarkdownWithCommonMark(textDescription, markdown);

            //Flexmark + WebView
            //renderMarkdownInWebView(webView, markdown);

            //Flexmark + HtmlCompat
            //textDescription.setText(renderMarkdown(markdown));
        }

        collapsingToolbarLayout.setTitle(""); // Hide title initially

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isTitleVisible = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(jobItem != null ? jobItem.getTitle() : "Job");
                    isTitleVisible = true;
                } else if (isTitleVisible) {
                    collapsingToolbarLayout.setTitle("");
                    isTitleVisible = false;
                }
            }
        });

        // Load ads
        FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
        AdsManager.loadAndShowBanner(this, bannerContainer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_job, menu);
        favoriteItem = menu.findItem(R.id.action_favorite);
        updateFavoriteIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.action_favorite) {
            toggleFavorite();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleFavorite() {
        if (jobItem == null) return;

        boolean isFav = FavoritesManager.isFavorite(this, jobItem.getId());
        if (isFav) {
            FavoritesManager.removeFavorite(this, jobItem.getId());
        } else {
            FavoritesManager.addFavorite(this, jobItem);
        }
        updateFavoriteIcon();
    }

    private void updateFavoriteIcon() {
        if (favoriteItem == null || jobItem == null) return;

        boolean isFav = FavoritesManager.isFavorite(this, jobItem.getId());
        favoriteItem.setIcon(isFav
                ? R.drawable.round_favorite_24
                : R.drawable.round_favorite_border_24);
        //favoriteItem.getIcon().setTint(ContextCompat.getColor(this, R.color.blue));
    }

    // Renders markdown using Flexmark + HtmlCompat
    /*private Spanned renderMarkdown(String markdownText) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownText);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);

        return HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY);
    }*/

    //Flexmark + WebView
    /*private void renderMarkdownInWebView(WebView webView, String markdown) {
        Parser parser = Parser.builder().build();
        com.vladsch.flexmark.util.ast.Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);

        String styledHtml = "<html><body style='padding:16px; font-size: 15px; color: #333;'>" + html + "</body></html>";
        webView.loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null);
    }*/

    //commonmark-java + HtmlCompat
    /*private void renderMarkdownWithCommonMark(TextView textView, String markdownString) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownString);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);

        Spanned spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY);
        textView.setText(spanned);
    }*/


    private void configureWebView(WebView webView) {
        // Enable JavaScript if needed (for some advanced markdown features)
        webView.getSettings().setJavaScriptEnabled(false);

        // Disable file access
        webView.getSettings().setAllowFileAccess(false);

        // Disable content access
        webView.getSettings().setAllowContentAccess(false);

        // Disable zoom controls
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);

        // Enable responsive layout
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        // Disable long click
        webView.setOnLongClickListener(v -> true);
    }
}
