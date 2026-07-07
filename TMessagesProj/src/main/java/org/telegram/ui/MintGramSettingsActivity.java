package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.Objects;

public class MintGramSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter adapter;
    private final ArrayList<ItemInner> items = new ArrayList<>();
    private int selectedSection = -1;

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_PRIVACY_BLOCK = 1;
    private static final int VIEW_TYPE_SHADOW = 2;
    private static final int VIEW_TYPE_FEATURES_BLOCK = 3;
    private static final int VIEW_TYPE_GHOST_BLOCK = 4;
    private static final int VIEW_TYPE_MAPS_BLOCK = 5;
    private static final int VIEW_TYPE_BRAND_HEADER = 6;
    private static final int VIEW_TYPE_SECTION_BLOCK = 7;
    private static final int VIEW_TYPE_EMPTY_BLOCK = 8;
    private static final int VIEW_TYPE_LINKS_BLOCK = 9;
    private static final int VIEW_TYPE_MESSAGE_SIZE_BLOCK = 10;

    public MintGramSettingsActivity() {
    }

    public MintGramSettingsActivity(int section) {
        selectedSection = section;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        actionBar.setAdaptiveBackground(listView);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(adapter = new ListAdapter());
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        updateItems();
        return fragmentView;
    }

    private void updateItems() {
        items.clear();
        if (selectedSection == -1) {
            items.add(new ItemInner(VIEW_TYPE_BRAND_HEADER, 0, null));
            items.add(new ItemInner(VIEW_TYPE_HEADER, 1, LocaleController.getString(R.string.MintGramFunctions)));
            items.add(new ItemInner(VIEW_TYPE_SECTION_BLOCK, 2, null));
            items.add(new ItemInner(VIEW_TYPE_SHADOW, 6, null));
            items.add(new ItemInner(VIEW_TYPE_HEADER, 19, LocaleController.getString(R.string.MintGramLinks)));
            items.add(new ItemInner(VIEW_TYPE_LINKS_BLOCK, 20, null));
            items.add(new ItemInner(VIEW_TYPE_SHADOW, 24, null));
        } else if (selectedSection == 0) {
            items.add(new ItemInner(VIEW_TYPE_HEADER, 4, LocaleController.getString(R.string.MintGramMainSection)));
            items.add(new ItemInner(VIEW_TYPE_PRIVACY_BLOCK, 5, null));
            items.add(new ItemInner(VIEW_TYPE_SHADOW, 6, null));
            items.add(new ItemInner(VIEW_TYPE_HEADER, 7, LocaleController.getString(R.string.MintGramGhostMode)));
            items.add(new ItemInner(VIEW_TYPE_GHOST_BLOCK, 8, null));
            items.add(new ItemInner(VIEW_TYPE_SHADOW, 9, null));
            items.add(new ItemInner(VIEW_TYPE_HEADER, 10, LocaleController.getString(R.string.MintGramFeatures)));
            items.add(new ItemInner(VIEW_TYPE_FEATURES_BLOCK, 11, null));
            items.add(new ItemInner(VIEW_TYPE_SHADOW, 12, null));
            items.add(new ItemInner(VIEW_TYPE_HEADER, 13, LocaleController.getString(R.string.MintGramMaps)));
            items.add(new ItemInner(VIEW_TYPE_MAPS_BLOCK, 14, null));
            items.add(new ItemInner(VIEW_TYPE_SHADOW, 15, createYandexAgreementText()));
            items.add(new ItemInner(VIEW_TYPE_SHADOW, 16, LocaleController.getString(R.string.MintGramFeaturesInfo)));
        } else if (selectedSection == 1) {
            items.add(new ItemInner(VIEW_TYPE_HEADER, 22, LocaleController.getString(R.string.MintGramCustomizationSection)));
            items.add(new ItemInner(VIEW_TYPE_MESSAGE_SIZE_BLOCK, 17, null));
            items.add(new ItemInner(VIEW_TYPE_SHADOW, 18, null));
        } else if (selectedSection == 2) {
            items.add(new ItemInner(VIEW_TYPE_HEADER, 23, LocaleController.getString(R.string.MintGramOtherSection)));
            items.add(new ItemInner(VIEW_TYPE_EMPTY_BLOCK, 18, LocaleController.getString(R.string.MintGramOtherEmpty)));
        } else if (selectedSection == 3) {
            items.add(new ItemInner(VIEW_TYPE_HEADER, 24, LocaleController.getString(R.string.MintGramHelpSection)));
            items.add(new ItemInner(VIEW_TYPE_LINKS_BLOCK, 25, null));
            items.add(new ItemInner(VIEW_TYPE_SHADOW, 26, null));
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private CharSequence createYandexAgreementText() {
        String text = LocaleController.getString(R.string.MintGramYandexMapsAgreement);
        String link = LocaleController.getString(R.string.MintGramYandexMapsAgreementLink);
        SpannableString spannable = new SpannableString(text);
        int start = text.indexOf(link);
        if (start >= 0) {
            spannable.setSpan(new URLSpan("https://yandex.ru/legal/maps_api_offer/"), start, start + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    private static class ItemInner extends AdapterWithDiffUtils.Item {
        public CharSequence text;
        public int id;

        public ItemInner(int viewType, int id, CharSequence text) {
            super(viewType, false);
            this.id = id;
            this.text = text;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ItemInner item = (ItemInner) o;
            return id == item.id && Objects.equals(text, item.text);
        }
    }

    private class ListAdapter extends AdapterWithDiffUtils {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == VIEW_TYPE_HEADER) {
                view = new HeaderCell(getContext());
            } else if (viewType == VIEW_TYPE_BRAND_HEADER) {
                view = new BrandHeaderCell(getContext());
            } else if (viewType == VIEW_TYPE_SECTION_BLOCK) {
                view = new SectionBlockCell(getContext());
            } else if (viewType == VIEW_TYPE_PRIVACY_BLOCK) {
                view = new PrivacyBlockCell(getContext());
            } else if (viewType == VIEW_TYPE_GHOST_BLOCK) {
                view = new GhostBlockCell(getContext());
            } else if (viewType == VIEW_TYPE_FEATURES_BLOCK) {
                view = new FeaturesBlockCell(getContext());
            } else if (viewType == VIEW_TYPE_MAPS_BLOCK) {
                view = new MapsBlockCell(getContext());
            } else if (viewType == VIEW_TYPE_EMPTY_BLOCK) {
                view = new EmptyBlockCell(getContext());
            } else if (viewType == VIEW_TYPE_LINKS_BLOCK) {
                view = new LinksBlockCell(getContext());
            } else if (viewType == VIEW_TYPE_MESSAGE_SIZE_BLOCK) {
                view = new MessageSizeBlockCell(getContext());
            } else {
                view = new TextInfoPrivacyCell(getContext());
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (position < 0 || position >= items.size()) {
                return;
            }
            ItemInner item = items.get(position);
            if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
                ((HeaderCell) holder.itemView).setText(item.text);
            } else if (holder.getItemViewType() == VIEW_TYPE_BRAND_HEADER) {
                ((BrandHeaderCell) holder.itemView).bind();
            } else if (holder.getItemViewType() == VIEW_TYPE_SECTION_BLOCK) {
                ((SectionBlockCell) holder.itemView).bind();
            } else if (holder.getItemViewType() == VIEW_TYPE_SHADOW) {
                TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                if (TextUtils.isEmpty(item.text)) {
                    cell.setFixedSize(12);
                    cell.setText(null);
                } else {
                    cell.setFixedSize(0);
                    cell.setText(item.text);
                }
            } else if (holder.getItemViewType() == VIEW_TYPE_PRIVACY_BLOCK) {
                ((PrivacyBlockCell) holder.itemView).bind();
            } else if (holder.getItemViewType() == VIEW_TYPE_GHOST_BLOCK) {
                ((GhostBlockCell) holder.itemView).bind();
            } else if (holder.getItemViewType() == VIEW_TYPE_FEATURES_BLOCK) {
                ((FeaturesBlockCell) holder.itemView).bind();
            } else if (holder.getItemViewType() == VIEW_TYPE_MAPS_BLOCK) {
                ((MapsBlockCell) holder.itemView).bind();
            } else if (holder.getItemViewType() == VIEW_TYPE_EMPTY_BLOCK) {
                ((EmptyBlockCell) holder.itemView).bind(item.text);
            } else if (holder.getItemViewType() == VIEW_TYPE_LINKS_BLOCK) {
                ((LinksBlockCell) holder.itemView).bind();
            } else if (holder.getItemViewType() == VIEW_TYPE_MESSAGE_SIZE_BLOCK) {
                ((MessageSizeBlockCell) holder.itemView).bind();
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override
        public int getItemViewType(int position) {
            if (position < 0 || position >= items.size()) {
                return 0;
            }
            return items.get(position).viewType;
        }

    }

    private static class BigHeaderCell extends FrameLayout {
        private final TextView textView;

        public BigHeaderCell(Context context) {
            super(context);
            setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(22), AndroidUtilities.dp(20), AndroidUtilities.dp(10));
            textView = new TextView(context);
            textView.setTextSize(23);
            textView.setTextColor(getAccentColor());
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 34));
        }

        public void setText(CharSequence text) {
            textView.setText(text);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), heightMeasureSpec);
        }
    }

    private static class BrandHeaderCell extends FrameLayout {
        private final ImageView logoView;
        private final TextView titleView;
        private final TextView versionView;

        public BrandHeaderCell(Context context) {
            super(context);
            setPadding(0, 0, 0, 0);

            logoView = new ImageView(context);
            logoView.setImageResource(R.drawable.mintgram_logo_icon);
            logoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            logoView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));
            logoView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(28), getAccentColor()));
            addView(logoView, LayoutHelper.createFrame(108, 108, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 56, 0, 0));

            titleView = new TextView(context);
            titleView.setText("MintGram");
            titleView.setTextSize(30);
            titleView.setTypeface(AndroidUtilities.bold());
            titleView.setGravity(Gravity.CENTER);
            titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            titleView.setSingleLine(true);
            addView(titleView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 42, Gravity.TOP | Gravity.LEFT, 32, 188, 32, 0));

            versionView = new TextView(context);
            versionView.setTextSize(20);
            versionView.setGravity(Gravity.CENTER);
            versionView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
            versionView.setSingleLine(true);
            addView(versionView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 30, Gravity.TOP | Gravity.LEFT, 32, 228, 32, 0));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(282), MeasureSpec.EXACTLY));
        }

        public void bind() {
            versionView.setText(getVersionText());
        }
    }

    private class SectionBlockCell extends FrameLayout {
        private final TextSettingsCell mainCell;
        private final TextSettingsCell customizationCell;
        private final TextSettingsCell otherCell;
        private final TextSettingsCell helpCell;

        public SectionBlockCell(Context context) {
            super(context);
            setPadding(AndroidUtilities.dp(12), 0, AndroidUtilities.dp(12), AndroidUtilities.dp(4));

            LinearLayout block = createBlock(context);
            addView(block, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            mainCell = createRow(context);
            customizationCell = createRow(context);
            otherCell = createRow(context);
            helpCell = createRow(context);

            block.addView(mainCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
            block.addView(customizationCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
            block.addView(otherCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
            block.addView(helpCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));

            mainCell.setOnClickListener(v -> presentFragment(new MintGramSettingsActivity(0)));
            customizationCell.setOnClickListener(v -> Toast.makeText(getContext(), LocaleController.getString(R.string.MintGramCustomizationTemporarilyUnavailable), Toast.LENGTH_SHORT).show());
            otherCell.setOnClickListener(v -> presentFragment(new MintGramSettingsActivity(2)));
            helpCell.setOnClickListener(v -> openTelegramLink(getContext(), "mintgramsupport"));
        }

        public void bind() {
            bindBlockRow(mainCell, LocaleController.getString(R.string.MintGramMainSection), R.drawable.settings_features, true);
            bindBlockRow(customizationCell, LocaleController.getString(R.string.MintGramCustomizationSection), R.drawable.settings_chat, true);
            customizationCell.setAlpha(0.45f);
            bindBlockRow(otherCell, LocaleController.getString(R.string.MintGramOtherSection), R.drawable.settings_policy, true);
            bindBlockRow(helpCell, LocaleController.getString(R.string.MintGramHelpSection), R.drawable.settings_ask, false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), heightMeasureSpec);
        }
    }

    private static class EmptyBlockCell extends FrameLayout {
        private final TextView textView;

        public EmptyBlockCell(Context context) {
            super(context);
            setPadding(AndroidUtilities.dp(12), 0, AndroidUtilities.dp(12), AndroidUtilities.dp(4));

            LinearLayout block = createBlock(context);
            block.setPadding(AndroidUtilities.dp(21), AndroidUtilities.dp(18), AndroidUtilities.dp(21), AndroidUtilities.dp(18));
            addView(block, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            textView = new TextView(context);
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
            block.addView(textView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        public void bind(CharSequence text) {
            textView.setText(text);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), heightMeasureSpec);
        }
    }

    private static class LinksBlockCell extends FrameLayout {
        private final TextSettingsCell channelCell;
        private final TextSettingsCell chatCell;
        private final TextSettingsCell supportCell;
        private final TextSettingsCell faqCell;

        public LinksBlockCell(Context context) {
            super(context);
            setPadding(AndroidUtilities.dp(12), 0, AndroidUtilities.dp(12), AndroidUtilities.dp(4));

            LinearLayout block = createBlock(context);
            addView(block, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            channelCell = createRow(context);
            chatCell = createRow(context);
            supportCell = createRow(context);
            faqCell = createRow(context);

            block.addView(channelCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
            block.addView(chatCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
            block.addView(supportCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
            block.addView(faqCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));

            channelCell.setOnClickListener(v -> openTelegramLink(getContext(), "mintgram_tg"));
            chatCell.setOnClickListener(v -> openTelegramLink(getContext(), "mintgram_chat"));
            supportCell.setOnClickListener(v -> openTelegramLink(getContext(), "mintgramsupport"));
            faqCell.setOnClickListener(v -> openTelegramLink(getContext(), "mintgram_faq"));
        }

        public void bind() {
            bindBlockRow(channelCell, LocaleController.getString(R.string.MintGramChannel), "@mintgram_tg", R.drawable.settings_channel, true);
            bindBlockRow(chatCell, LocaleController.getString(R.string.MintGramChat), "@mintgram_chat", R.drawable.settings_group, true);
            bindBlockRow(supportCell, LocaleController.getString(R.string.MintGramSupport), "@mintgramsupport", R.drawable.settings_ask, true);
            bindBlockRow(faqCell, LocaleController.getString(R.string.MintGramFAQ), "@mintgram_faq", R.drawable.settings_faq, false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), heightMeasureSpec);
        }
    }

    private void selectSection(int section) {
        if (selectedSection == section) {
            return;
        }
        selectedSection = section;
        updateItems();
    }

    private static LinearLayout createBlock(Context context) {
        LinearLayout block = new LinearLayout(context);
        block.setOrientation(LinearLayout.VERTICAL);
        block.setClipToOutline(true);
        block.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(28), Theme.getColor(Theme.key_windowBackgroundWhite)));
        return block;
    }

    private static TextSettingsCell createRow(Context context) {
        TextSettingsCell cell = new TextSettingsCell(context, 21);
        cell.setBackground(Theme.getSelectorDrawable(false));
        return cell;
    }

    private static void bindBlockRow(TextSettingsCell cell, CharSequence text, int icon, boolean divider) {
        cell.setText(text, divider);
        cell.setIcon(icon);
    }

    private static void bindBlockRow(TextSettingsCell cell, CharSequence text, CharSequence value, int icon, boolean divider) {
        cell.setTextAndValue(text, value, divider);
        cell.setIcon(icon);
        cell.setTextValueColor(getAccentColor());
    }

    private static void openTelegramLink(Context context, String username) {
        Browser.openUrl(context, "https://t.me/" + username);
    }

    private static String getVersionText() {
        return "12.0.2";
    }

    private static int getAccentColor() {
        return 0xFF3E927A;
    }

    private static class MessageSizeBlockCell extends FrameLayout {
        private static final int MIN_SIZE = 12;
        private static final int MAX_SIZE = 30;
        private final TextView titleView;
        private final TextView valueView;
        private final MessageSizeSliderView sliderView;
        private final TextView previewNameView;
        private final TextView previewMessageView;
        private final TextView previewTimeView;
        private final TextView previewReplyNameView;
        private final TextView previewReplyTextView;
        private final TextView previewReplyIconView;

        public MessageSizeBlockCell(Context context) {
            super(context);
            setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(2), AndroidUtilities.dp(12), AndroidUtilities.dp(4));

            LinearLayout block = new LinearLayout(context);
            block.setOrientation(LinearLayout.VERTICAL);
            block.setClipToOutline(true);
            block.setPadding(0, 0, 0, AndroidUtilities.dp(14));
            block.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(25), Theme.getColor(Theme.key_windowBackgroundWhite)));
            addView(block, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            FrameLayout controls = new FrameLayout(context);
            controls.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(18), AndroidUtilities.dp(20), 0);
            block.addView(controls, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 146));

            titleView = new TextView(context);
            titleView.setTextColor(getAccentColor());
            titleView.setTextSize(22);
            titleView.setSingleLine(true);
            controls.addView(titleView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 36, Gravity.LEFT | Gravity.TOP, 0, 0, 0, 0));

            valueView = new TextView(context);
            valueView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            valueView.setTextSize(18);
            valueView.setGravity(Gravity.CENTER);
            valueView.setIncludeFontPadding(false);
            valueView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(8), Theme.multAlpha(getAccentColor(), 0.22f)));
            controls.addView(valueView, LayoutHelper.createFrame(48, 34, Gravity.LEFT | Gravity.TOP, 190, 0, 0, 0));

            TextView smallView = new TextView(context);
            smallView.setText(LocaleController.getString(R.string.MintGramMessageSizeSmall));
            smallView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            smallView.setTextSize(17);
            controls.addView(smallView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 30, Gravity.LEFT | Gravity.TOP, 0, 54, 0, 0));

            TextView largeView = new TextView(context);
            largeView.setText(LocaleController.getString(R.string.MintGramMessageSizeLarge));
            largeView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            largeView.setTextSize(17);
            largeView.setGravity(Gravity.RIGHT);
            controls.addView(largeView, LayoutHelper.createFrame(150, 30, Gravity.RIGHT | Gravity.TOP, 0, 54, 0, 0));

            sliderView = new MessageSizeSliderView(context);
            sliderView.setCallback(size -> applyMessageSize(size, true));
            controls.addView(sliderView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 40, Gravity.LEFT | Gravity.TOP, 0, 96, 0, 0));

            FrameLayout previewContainer = new FrameLayout(context);
            previewContainer.setClipChildren(false);
            block.addView(previewContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 314));

            FrameLayout tinyBubble = new FrameLayout(context);
            tinyBubble.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(7), AndroidUtilities.dp(10), AndroidUtilities.dp(7));
            GradientDrawable tinyBackground = new GradientDrawable();
            tinyBackground.setColor(0xFF313340);
            tinyBackground.setCornerRadius(AndroidUtilities.dp(8));
            tinyBubble.setBackground(tinyBackground);
            previewContainer.addView(tinyBubble, LayoutHelper.createFrame(118, 64, Gravity.LEFT | Gravity.TOP, 36, 26, 0, 0));

            TextView tinyNameView = new TextView(context);
            tinyNameView.setText("immat0x1");
            tinyNameView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            tinyNameView.setTextSize(17);
            tinyNameView.setSingleLine(true);
            tinyBubble.addView(tinyNameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 25, Gravity.LEFT | Gravity.TOP));

            TextView tinyTextView = new TextView(context);
            tinyTextView.setText("bay");
            tinyTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            tinyTextView.setTextSize(17);
            tinyTextView.setSingleLine(true);
            tinyBubble.addView(tinyTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 25, Gravity.LEFT | Gravity.TOP, 0, 26, 0, 0));

            FrameLayout photoBubble = new FrameLayout(context);
            GradientDrawable photoBackground = new GradientDrawable();
            photoBackground.setColor(0xFF2B3833);
            photoBackground.setCornerRadius(AndroidUtilities.dp(12));
            photoBubble.setBackground(photoBackground);
            previewContainer.addView(photoBubble, LayoutHelper.createFrame(210, 178, Gravity.RIGHT | Gravity.TOP, 0, 18, 28, 0));

            ImageView photoLogo = new ImageView(context);
            photoLogo.setImageResource(R.drawable.mintgram_logo_icon);
            photoLogo.setAlpha(0.28f);
            photoLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoBubble.addView(photoLogo, LayoutHelper.createFrame(96, 96, Gravity.CENTER));

            TextView photoTimeView = new TextView(context);
            photoTimeView.setText("23:25:43 ✓✓");
            photoTimeView.setTextSize(13);
            photoTimeView.setTextColor(Theme.getColor(Theme.key_chat_outTimeText));
            photoTimeView.setGravity(Gravity.CENTER);
            photoTimeView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(13), 0xCC262A33));
            photoBubble.addView(photoTimeView, LayoutHelper.createFrame(92, 27, Gravity.RIGHT | Gravity.BOTTOM, 0, 0, 8, 8));

            FrameLayout bubble = new FrameLayout(context);
            bubble.setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(10), AndroidUtilities.dp(12), AndroidUtilities.dp(8));
            GradientDrawable bubbleBackground = new GradientDrawable();
            bubbleBackground.setColor(0xFF1F2227);
            bubbleBackground.setCornerRadius(AndroidUtilities.dp(22));
            bubble.setBackground(bubbleBackground);
            previewContainer.addView(bubble, LayoutHelper.createFrame(320, 112, Gravity.LEFT | Gravity.BOTTOM, 26, 0, 0, 10));

            FrameLayout reply = new FrameLayout(context);
            GradientDrawable replyBackground = new GradientDrawable();
            replyBackground.setColor(0xFF333743);
            replyBackground.setCornerRadius(AndroidUtilities.dp(8));
            reply.setBackground(replyBackground);
            bubble.addView(reply, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 45, Gravity.LEFT | Gravity.TOP, 0, 0, 0, 0));

            View replyLine = new View(context);
            replyLine.setBackgroundColor(getAccentColor());
            reply.addView(replyLine, LayoutHelper.createFrame(3, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));

            previewReplyNameView = new TextView(context);
            previewReplyNameView.setText("8055");
            previewReplyNameView.setTextColor(getAccentColor());
            previewReplyNameView.setTextSize(15);
            previewReplyNameView.setSingleLine(true);
            reply.addView(previewReplyNameView, LayoutHelper.createFrame(90, 22, Gravity.LEFT | Gravity.TOP, 12, 4, 0, 0));

            previewReplyIconView = new TextView(context);
            previewReplyIconView.setText("◻");
            previewReplyIconView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            previewReplyIconView.setTextSize(13);
            previewReplyIconView.setSingleLine(true);
            reply.addView(previewReplyIconView, LayoutHelper.createFrame(22, 22, Gravity.LEFT | Gravity.TOP, 12, 24, 0, 0));

            previewReplyTextView = new TextView(context);
            previewReplyTextView.setText(LocaleController.getString(R.string.AttachSticker));
            previewReplyTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            previewReplyTextView.setTextSize(15);
            previewReplyTextView.setSingleLine(true);
            reply.addView(previewReplyTextView, LayoutHelper.createFrame(170, 22, Gravity.LEFT | Gravity.TOP, 34, 24, 0, 0));

            previewNameView = new TextView(context);
            previewNameView.setTextColor(getAccentColor());
            previewNameView.setTextSize(13);
            previewNameView.setTypeface(AndroidUtilities.bold());
            previewNameView.setSingleLine(true);
            previewNameView.setVisibility(View.GONE);
            bubble.addView(previewNameView, LayoutHelper.createFrame(1, 1));

            previewMessageView = new TextView(context);
            previewMessageView.setTextColor(Theme.getColor(Theme.key_chat_messageTextIn));
            previewMessageView.setSingleLine(true);
            previewMessageView.setIncludeFontPadding(false);
            bubble.addView(previewMessageView, LayoutHelper.createFrame(228, 46, Gravity.LEFT | Gravity.BOTTOM, 0, 0, 70, 0));

            previewTimeView = new TextView(context);
            previewTimeView.setText("23:27:33");
            previewTimeView.setTextColor(Theme.getColor(Theme.key_chat_inTimeText));
            previewTimeView.setTextSize(12);
            previewTimeView.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
            bubble.addView(previewTimeView, LayoutHelper.createFrame(68, 28, Gravity.RIGHT | Gravity.BOTTOM, 0, 0, 10, 7));
        }

        public void bind() {
            titleView.setText(LocaleController.getString(R.string.MintGramMessageSize));
            previewNameView.setText(LocaleController.getString(R.string.MintGramPreviewSender));
            previewMessageView.setText(LocaleController.getString(R.string.MintGramPreviewMessage));
            int size = Math.max(MIN_SIZE, Math.min(MAX_SIZE, SharedConfig.fontSize));
            sliderView.setSize(size);
            updatePreview(size);
        }

        private void applyMessageSize(int size, boolean save) {
            updatePreview(size);
            if (!save || SharedConfig.fontSize == size) {
                return;
            }
            SharedConfig.fontSize = size;
            SharedConfig.fontSizeIsDefault = false;
            SharedPreferences preferences = getContext().getSharedPreferences("mainconfig", Context.MODE_PRIVATE);
            preferences.edit().putInt("fons_size", SharedConfig.fontSize).apply();
            Theme.createCommonMessageResources();
            for (int account = 0; account < UserConfig.MAX_ACCOUNT_COUNT; account++) {
                NotificationCenter.getInstance(account).postNotificationName(NotificationCenter.updateInterfaces, MessagesController.UPDATE_MASK_AVATAR | MessagesController.UPDATE_MASK_NAME);
            }
        }

        private void updatePreview(int size) {
            valueView.setText(String.valueOf(size));
            previewMessageView.setTextSize(size);
            previewReplyNameView.setTextSize(Math.max(13, size - 3));
            previewReplyTextView.setTextSize(Math.max(13, size - 4));
            previewReplyIconView.setTextSize(Math.max(11, size - 5));
            previewTimeView.setTextSize(12);
            invalidate();
        }
    }

    private static class MessageSizeSliderView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();
        private int size = SharedConfig.fontSize;
        private Callback callback;

        public MessageSizeSliderView(Context context) {
            super(context);
        }

        public void setCallback(Callback callback) {
            this.callback = callback;
        }

        public void setSize(int size) {
            this.size = Math.max(MessageSizeBlockCell.MIN_SIZE, Math.min(MessageSizeBlockCell.MAX_SIZE, size));
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int cy = getMeasuredHeight() / 2;
            int knobWidth = AndroidUtilities.dp(5);
            int lineHeight = AndroidUtilities.dp(8);
            int left = 0;
            int right = getMeasuredWidth();
            float progress = (size - MessageSizeBlockCell.MIN_SIZE) / (float) (MessageSizeBlockCell.MAX_SIZE - MessageSizeBlockCell.MIN_SIZE);
            int knobX = Math.round(left + progress * (right - left));

            paint.setColor(getAccentColor());
            rect.set(left, cy - lineHeight / 2f, Math.max(left, knobX - AndroidUtilities.dp(10)), cy + lineHeight / 2f);
            canvas.drawRoundRect(rect, lineHeight / 2f, lineHeight / 2f, paint);

            paint.setColor(Theme.getColor(Theme.key_switchTrack));
            rect.set(Math.min(right, knobX + AndroidUtilities.dp(10)), cy - lineHeight / 2f, right, cy + lineHeight / 2f);
            canvas.drawRoundRect(rect, lineHeight / 2f, lineHeight / 2f, paint);

            paint.setColor(getAccentColor());
            rect.set(knobX - knobWidth / 2f, cy - AndroidUtilities.dp(22), knobX + knobWidth / 2f, cy + AndroidUtilities.dp(22));
            canvas.drawRoundRect(rect, AndroidUtilities.dp(3), AndroidUtilities.dp(3), paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP) {
                getParent().requestDisallowInterceptTouchEvent(true);
                float progress = Math.max(0, Math.min(1, event.getX() / Math.max(1, getMeasuredWidth())));
                int newSize = Math.round(MessageSizeBlockCell.MIN_SIZE + progress * (MessageSizeBlockCell.MAX_SIZE - MessageSizeBlockCell.MIN_SIZE));
                if (newSize != size) {
                    size = newSize;
                    invalidate();
                    if (callback != null) {
                        callback.onSizeChanged(size);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                return true;
            }
            return super.onTouchEvent(event);
        }

        private interface Callback {
            void onSizeChanged(int size);
        }
    }

    private static class PrivacyBlockCell extends FrameLayout {
        private final TextCheckCell hideReadCell;
        private final TextCheckCell keepDeletedCell;

        public PrivacyBlockCell(Context context) {
            super(context);
            setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(2), AndroidUtilities.dp(12), AndroidUtilities.dp(4));

            LinearLayout block = new LinearLayout(context);
            block.setOrientation(LinearLayout.VERTICAL);
            block.setClipToOutline(true);
            block.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(25), Theme.getColor(Theme.key_windowBackgroundWhite)));
            addView(block, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            hideReadCell = new TextCheckCell(context, 21);
            keepDeletedCell = new TextCheckCell(context, 21);

            block.addView(hideReadCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
            block.addView(keepDeletedCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));

            hideReadCell.setOnClickListener(v -> {
                SharedConfig.setHideReadReceipts(!SharedConfig.hideReadReceipts);
                bind();
            });
            keepDeletedCell.setOnClickListener(v -> {
                if (!SharedConfig.keepDeletedMessages) {
                    SharedConfig.setKeepDeletedMessages(true);
                }
                bind();
                showDeletedColorDialog(getContext());
            });
        }

        private void showDeletedColorDialog(Context context) {
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(0, AndroidUtilities.dp(6), 0, AndroidUtilities.dp(6));

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle(LocaleController.getString(R.string.MintGramDeletedColorTitle))
                    .setView(layout)
                    .setNegativeButton(LocaleController.getString(R.string.MintGramEnableKeepDeleted), (dialogInterface, which) -> {
                        SharedConfig.setKeepDeletedMessages(true);
                        bind();
                    })
                    .setPositiveButton(LocaleController.getString(R.string.MintGramDisableKeepDeleted), (dialogInterface, which) -> {
                        SharedConfig.setKeepDeletedMessages(false);
                        bind();
                    })
                    .create();

            addColorRow(layout, LocaleController.getString(R.string.MintGramDeletedStyleRed), 0xFFE53935, 0, dialog);
            addColorRow(layout, LocaleController.getString(R.string.MintGramDeletedStyleBlue), 0xFF1E88E5, 1, dialog);
            addColorRow(layout, LocaleController.getString(R.string.MintGramDeletedStyleWhite), 0xFFFFFFFF, 2, dialog);
            dialog.show();
        }

        private void addColorRow(LinearLayout layout, CharSequence text, int color, int style, AlertDialog dialog) {
            Context context = layout.getContext();
            LinearLayout row = new LinearLayout(context);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setPadding(AndroidUtilities.dp(24), 0, AndroidUtilities.dp(20), 0);
            row.setBackground(Theme.getSelectorDrawable(false));

            View colorView = new View(context);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(color);
            drawable.setStroke(AndroidUtilities.dp(SharedConfig.deletedMessageStyle == style ? 3 : 1), SharedConfig.deletedMessageStyle == style ? Theme.getColor(Theme.key_windowBackgroundWhiteBlueText) : Theme.getColor(Theme.key_divider));
            colorView.setBackground(drawable);
            row.addView(colorView, LayoutHelper.createLinear(20, 20, android.view.Gravity.CENTER_VERTICAL));

            TextView textView = new TextView(context);
            textView.setText(text);
            textView.setSingleLine(true);
            textView.setTextSize(16);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            textView.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.addView(textView, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1f, 14, 0, 0, 0));

            row.setOnClickListener(v -> {
                SharedConfig.setDeletedMessageStyle(style);
                dialog.dismiss();
                bind();
            });

            layout.addView(row, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 52));
        }

        public void bind() {
            hideReadCell.setTextAndCheck(LocaleController.getString(R.string.MintGramHideReadReceipts), SharedConfig.hideReadReceipts, true);
            keepDeletedCell.setTextAndCheck(LocaleController.getString(R.string.MintGramKeepDeletedMessages), SharedConfig.keepDeletedMessages, false);
        }
    }

    private static class GhostBlockCell extends FrameLayout {
        private final TextCheckCell hideOnlineCell;
        private final TextCheckCell hideTypingCell;
        private final TextCheckCell hideRecordVideoCell;
        private final TextCheckCell hideUploadVideoCell;
        private final TextCheckCell hideRecordVoiceCell;
        private final TextCheckCell hideUploadPhotoCell;
        private final TextCheckCell hideUploadFileCell;

        public GhostBlockCell(Context context) {
            super(context);
            setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(2), AndroidUtilities.dp(12), AndroidUtilities.dp(4));

            LinearLayout block = new LinearLayout(context);
            block.setOrientation(LinearLayout.VERTICAL);
            block.setClipToOutline(true);
            block.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(25), Theme.getColor(Theme.key_windowBackgroundWhite)));
            addView(block, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            hideOnlineCell = new TextCheckCell(context, 21);
            hideTypingCell = new TextCheckCell(context, 21);
            hideRecordVideoCell = new TextCheckCell(context, 21);
            hideUploadVideoCell = new TextCheckCell(context, 21);
            hideRecordVoiceCell = new TextCheckCell(context, 21);
            hideUploadPhotoCell = new TextCheckCell(context, 21);
            hideUploadFileCell = new TextCheckCell(context, 21);

            block.addView(hideOnlineCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
            block.addView(hideTypingCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
            block.addView(hideRecordVideoCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
            block.addView(hideUploadVideoCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
            block.addView(hideRecordVoiceCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
            block.addView(hideUploadPhotoCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
            block.addView(hideUploadFileCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));

            hideOnlineCell.setOnClickListener(v -> {
                SharedConfig.setGhostHideOnline(!SharedConfig.ghostHideOnline);
                bind();
            });
            hideTypingCell.setOnClickListener(v -> {
                SharedConfig.setGhostHideTyping(!SharedConfig.ghostHideTyping);
                bind();
            });
            hideRecordVideoCell.setOnClickListener(v -> {
                SharedConfig.setGhostHideRecordVideo(!SharedConfig.ghostHideRecordVideo);
                bind();
            });
            hideUploadVideoCell.setOnClickListener(v -> {
                SharedConfig.setGhostHideUploadVideo(!SharedConfig.ghostHideUploadVideo);
                bind();
            });
            hideRecordVoiceCell.setOnClickListener(v -> {
                SharedConfig.setGhostHideRecordVoice(!SharedConfig.ghostHideRecordVoice);
                bind();
            });
            hideUploadPhotoCell.setOnClickListener(v -> {
                SharedConfig.setGhostHideUploadPhoto(!SharedConfig.ghostHideUploadPhoto);
                bind();
            });
            hideUploadFileCell.setOnClickListener(v -> {
                SharedConfig.setGhostHideUploadFile(!SharedConfig.ghostHideUploadFile);
                bind();
            });
        }

        public void bind() {
            hideOnlineCell.setTextAndCheck(LocaleController.getString(R.string.MintGramGhostHideOnline), SharedConfig.ghostHideOnline, true);
            hideTypingCell.setTextAndCheck(LocaleController.getString(R.string.MintGramGhostHideTyping), SharedConfig.ghostHideTyping, true);
            hideRecordVideoCell.setTextAndCheck(LocaleController.getString(R.string.MintGramGhostHideRecordVideo), SharedConfig.ghostHideRecordVideo, true);
            hideUploadVideoCell.setTextAndCheck(LocaleController.getString(R.string.MintGramGhostHideUploadVideo), SharedConfig.ghostHideUploadVideo, true);
            hideRecordVoiceCell.setTextAndCheck(LocaleController.getString(R.string.MintGramGhostHideRecordVoice), SharedConfig.ghostHideRecordVoice, true);
            hideUploadPhotoCell.setTextAndCheck(LocaleController.getString(R.string.MintGramGhostHideUploadPhoto), SharedConfig.ghostHideUploadPhoto, true);
            hideUploadFileCell.setTextAndCheck(LocaleController.getString(R.string.MintGramGhostHideUploadFile), SharedConfig.ghostHideUploadFile, false);
        }
    }

    private static class FeaturesBlockCell extends FrameLayout {
        private final TextCheckCell freeTranscriptionCell;
        private final LinearLayout block;

        public FeaturesBlockCell(Context context) {
            super(context);
            setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(2), AndroidUtilities.dp(12), AndroidUtilities.dp(4));

            block = new LinearLayout(context);
            block.setOrientation(LinearLayout.VERTICAL);
            block.setClipToOutline(true);
            block.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(25), Theme.getColor(Theme.key_windowBackgroundWhite)));
            addView(block, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            freeTranscriptionCell = new TextCheckCell(context, 21);
            block.addView(freeTranscriptionCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));

            freeTranscriptionCell.setOnClickListener(v -> {
                SharedConfig.setPlumFreeVoiceTranscription(false);
                Toast.makeText(context, LocaleController.getString(R.string.MintGramTranscriptionTemporarilyUnavailable), Toast.LENGTH_SHORT).show();
                bind();
            });
        }

        public void bind() {
            freeTranscriptionCell.setTextAndCheck(LocaleController.getString(R.string.MintGramFreeVoiceTranscription), false, true);
            freeTranscriptionCell.setEnabled(false, null);
            block.setAlpha(0.62f);
        }
    }

    private static class MapsBlockCell extends FrameLayout {
        private final TextCheckCell yandexMapsCell;

        public MapsBlockCell(Context context) {
            super(context);
            setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(2), AndroidUtilities.dp(12), AndroidUtilities.dp(4));

            LinearLayout block = new LinearLayout(context);
            block.setOrientation(LinearLayout.VERTICAL);
            block.setClipToOutline(true);
            block.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(25), Theme.getColor(Theme.key_windowBackgroundWhite)));
            addView(block, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            yandexMapsCell = new TextCheckCell(context, 21);
            block.addView(yandexMapsCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));

            yandexMapsCell.setOnClickListener(v -> {
                SharedConfig.setMintGramMapProvider(SharedConfig.mintGramMapProvider == 2 ? 0 : 2);
                bind();
            });
        }

        public void bind() {
            yandexMapsCell.setTextAndCheck(LocaleController.getString(R.string.MintGramUseYandexMaps), SharedConfig.mintGramMapProvider == 2, false);
        }
    }

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    @Override
    public void onInsets(int left, int top, int right, int bottom) {
        listView.setPadding(0, 0, 0, bottom);
        listView.setClipToPadding(false);
    }
}
