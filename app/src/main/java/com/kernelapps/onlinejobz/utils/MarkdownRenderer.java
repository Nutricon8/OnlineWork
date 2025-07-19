package com.kernelapps.onlinejobz.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.webkit.WebView;

import androidx.core.content.ContextCompat;

public class MarkdownRenderer {

    public static void renderMarkdown(WebView webView, String markdown) {
        if (TextUtils.isEmpty(markdown)) {
            webView.loadData("", "text/html", "UTF-8");
            return;
        }

        // Convert markdown to HTML
        String html = convertMarkdownToHtml(markdown);

        TypedValue typedValue = new TypedValue();
        Context context = webView.getContext();

        // Resolve background color from theme
        context.getTheme().resolveAttribute(android.R.attr.windowBackground, typedValue, true);
        int bgColor = ContextCompat.getColor(context, typedValue.resourceId != 0 ? typedValue.resourceId : typedValue.data);

// Resolve text color from theme
        context.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        int textColor = ContextCompat.getColor(context, typedValue.resourceId != 0 ? typedValue.resourceId : typedValue.data);

// Convert to hex strings
        String bgColorHex = String.format("#%06X", (0xFFFFFF & bgColor));
        String textColorHex = String.format("#%06X", (0xFFFFFF & textColor));


        // Load the HTML into WebView with basic styling
        /*String styledHtml = "p, li, blockquote, pre, code { font-size: 18px; } " +  // Ensure other elements also have font-size
                "h1, h2, h3 { color: #111; } " +
                "pre { background-color: #f5f5f5;} " +
                "code {background-color: #f5f5f5;} " +
                "blockquote { border-left: 4px solid #ddd; } " +
                "a { color: #0066cc; }";*/

        String styledHtml = "<html><head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />" +
                "<style type=\"text/css\">" +
                "body { background-color: " + bgColorHex + "; color: " + textColorHex + "; font-family: sans-serif; line-height: 1.5; padding: 8px; }" +
                "h1, h2, h3 { color: " + textColorHex + "; }" +
                "pre { background-color: #f0f0f0; padding: 12px; border-radius: 4px; overflow-x: auto; }" +
                "code { font-family: monospace; background-color: #e0e0e0; padding: 2px 4px; border-radius: 3px; }" +
                "blockquote { border-left: 4px solid #aaa; padding-left: 12px; color: #666; margin-left: 0; }" +
                "a { color: #1e88e5; text-decoration: none; }" +
                "</style>" +
                "</head><body>" + html + "</body></html>";


        webView.loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null);
    }

    private static String convertMarkdownToHtml(String markdown) {
        // Basic markdown to HTML conversion
        String html = markdown
                // Headers
                .replaceAll("(?m)^# (.*?)$", "<h1>$1</h1>")
                .replaceAll("(?m)^## (.*?)$", "<h2>$1</h2>")
                .replaceAll("(?m)^### (.*?)$", "<h3>$1</h3>")

                // Bold and italic
                .replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>")
                .replaceAll("\\*(.*?)\\*", "<em>$1</em>")
                .replaceAll("_(.*?)_", "<em>$1</em>")

                // Lists
                .replaceAll("(?m)^\\* (.*?)$", "<li>$1</li>")
                .replaceAll("(?m)^- (.*?)$", "<li>$1</li>")
                .replaceAll("(?m)^\\+ (.*?)$", "<li>$1</li>")
                .replaceAll("(?m)(<li>.*</li>)", "<ul>$1</ul>")

                // Code blocks
                .replaceAll("(?s)```(.*?)```", "<pre><code>$1</code></pre>")
                .replaceAll("`(.*?)`", "<code>$1</code>")

                // Blockquotes
                .replaceAll("(?m)^> (.*?)$", "<blockquote>$1</blockquote>")

                // Links
                .replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\"$2\">$1</a>")

                // Images
                .replaceAll("!\\[(.*?)\\]\\((.*?)\\)", "<img src=\"$2\" alt=\"$1\"/>")

                // Paragraphs (handle multiple newlines)
                .replaceAll("(?m)(^[^<].*$)", "<p>$1</p>")
                .replaceAll("(?m)(<p>[^<].*?</p>)", "$1")

                // Line breaks
                .replaceAll("  $", "<br/>");

        return html;
    }
}