package org.telegram.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import org.telegram.ui.ActionBar.BottomSheet;
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
    private static final int VIEW_TYPE_OTHER_SUPPORT_BLOCK = 11;
    private static final int VIEW_TYPE_OTHER_ACTIONS_BLOCK = 12;
    private static final int VIEW_TYPE_BIG_HEADER = 13;
    private static final int VIEW_TYPE_SUPPORT_INFO = 14;

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
            items.add(new ItemInner(VIEW_TYPE_BIG_HEADER, 23, LocaleController.getString(R.string.MintGramOtherSection)));
            items.add(new ItemInner(VIEW_TYPE_HEADER, 27, LocaleController.getString(R.string.MintGramSupportHeader)));
            items.add(new ItemInner(VIEW_TYPE_OTHER_SUPPORT_BLOCK, 28, null));
            items.add(new ItemInner(VIEW_TYPE_SUPPORT_INFO, 29, createSupportInfoText()));
            items.add(new ItemInner(VIEW_TYPE_OTHER_ACTIONS_BLOCK, 30, null));
            items.add(new ItemInner(VIEW_TYPE_SHADOW, 31, null));
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

    private CharSequence createSupportInfoText() {
        String text = LocaleController.getString(R.string.MintGramSupportDevelopmentInfo);
        String colored = LocaleController.getString(R.string.MintGramSupportDevelopmentAccent);
        SpannableString spannable = new SpannableString(text);
        int start = text.indexOf(colored);
        if (start >= 0) {
            spannable.setSpan(new ForegroundColorSpan(0xFFFFB8CE), start, start + colored.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
            } else if (viewType == VIEW_TYPE_BIG_HEADER) {
                view = new BigHeaderCell(getContext());
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
            } else if (viewType == VIEW_TYPE_OTHER_SUPPORT_BLOCK) {
                view = new OtherSupportBlockCell(getContext());
            } else if (viewType == VIEW_TYPE_OTHER_ACTIONS_BLOCK) {
                view = new OtherActionsBlockCell(getContext());
            } else if (viewType == VIEW_TYPE_SUPPORT_INFO) {
                view = new SupportInfoCell(getContext());
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
            } else if (holder.getItemViewType() == VIEW_TYPE_BIG_HEADER) {
                ((BigHeaderCell) holder.itemView).setText(item.text);
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
            } else if (holder.getItemViewType() == VIEW_TYPE_OTHER_SUPPORT_BLOCK) {
                ((OtherSupportBlockCell) holder.itemView).bind();
            } else if (holder.getItemViewType() == VIEW_TYPE_OTHER_ACTIONS_BLOCK) {
                ((OtherActionsBlockCell) holder.itemView).bind();
            } else if (holder.getItemViewType() == VIEW_TYPE_SUPPORT_INFO) {
                ((SupportInfoCell) holder.itemView).bind(item.text);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == VIEW_TYPE_SUPPORT_INFO;
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
            setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(24), AndroidUtilities.dp(20), AndroidUtilities.dp(12));
            textView = new TextView(context);
            textView.setTextSize(30);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48));
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
            addView(logoView, LayoutHelper.createFrame(108, 108, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 42, 0, 0));

            titleView = new TextView(context);
            titleView.setText("MintGram");
            titleView.setTextSize(30);
            titleView.setTypeface(AndroidUtilities.bold());
            titleView.setGravity(Gravity.CENTER);
            titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            titleView.setSingleLine(true);
            addView(titleView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 42, Gravity.TOP | Gravity.LEFT, 32, 174, 32, 0));

            versionView = new TextView(context);
            versionView.setTextSize(20);
            versionView.setGravity(Gravity.CENTER);
            versionView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
            versionView.setSingleLine(true);
            addView(versionView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 30, Gravity.TOP | Gravity.LEFT, 32, 214, 32, 0));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(248), MeasureSpec.EXACTLY));
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
            helpCell.setOnClickListener(v -> openTelegramLink(getContext(), "mintsupport"));
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
            supportCell.setOnClickListener(v -> openTelegramLink(getContext(), "mintsupport"));
            faqCell.setOnClickListener(v -> openTelegramLink(getContext(), "mintgram_faq"));
        }

        public void bind() {
            bindBlockRow(channelCell, LocaleController.getString(R.string.MintGramChannel), "@mintgram_tg", R.drawable.settings_channel, true);
            bindBlockRow(chatCell, LocaleController.getString(R.string.MintGramChat), "@mintgram_chat", R.drawable.settings_group, true);
            bindBlockRow(supportCell, LocaleController.getString(R.string.MintGramSupport), "@mintsupport", R.drawable.settings_ask, true);
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

    private static void copyCardDetails(Context context) {
        if (AndroidUtilities.addToClipboard("2204120130545938")) {
            Toast.makeText(context, LocaleController.getString(R.string.CardNumberCopied), Toast.LENGTH_SHORT).show();
        }
    }

    private static String getVersionText() {
        return "12.0.3";
    }

    private static int getAccentColor() {
        return 0xFF3E927A;
    }

    private static int getSupportColor() {
        return 0xFFFF8FB1;
    }

    private static class OtherSupportBlockCell extends FrameLayout {
        private final PaymentRowCell tonkeeperCell;
        private final PaymentRowCell tonSpaceCell;
        private final PaymentRowCell cryptoBotCell;
        private final PaymentRowCell cardCell;

        public OtherSupportBlockCell(Context context) {
            super(context);
            setPadding(AndroidUtilities.dp(12), 0, AndroidUtilities.dp(12), AndroidUtilities.dp(4));

            LinearLayout block = createBlock(context);
            addView(block, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            tonkeeperCell = new PaymentRowCell(context);
            tonSpaceCell = new PaymentRowCell(context);
            cryptoBotCell = new PaymentRowCell(context);
            cardCell = new PaymentRowCell(context);

            block.addView(tonkeeperCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 54));
            block.addView(tonSpaceCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 54));
            block.addView(cryptoBotCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 54));
            block.addView(cardCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 54));

            View.OnClickListener unavailableClick = v -> Toast.makeText(getContext(), LocaleController.getString(R.string.MintGramSupportTemporarilyUnavailable), Toast.LENGTH_SHORT).show();
            tonkeeperCell.setOnClickListener(unavailableClick);
            tonSpaceCell.setOnClickListener(unavailableClick);
            cryptoBotCell.setOnClickListener(unavailableClick);
            cardCell.setOnClickListener(v -> copyCardDetails(getContext()));
        }

        public void bind() {
            tonkeeperCell.bind("Tonkeeper", R.drawable.settings_ton, 0xFF193245, false, true);
            tonkeeperCell.setAlpha(0.45f);
            tonSpaceCell.bind("TON Space", R.drawable.settings_wallet, 0xFF29A8EA, false, true);
            tonSpaceCell.setAlpha(0.45f);
            cryptoBotCell.bind("CryptoBot", R.drawable.mintgram_cryptobot, 0xFF2CA8DF, true, true);
            cryptoBotCell.setAlpha(0.45f);
            cardCell.bind(LocaleController.getString(R.string.MintGramCard), R.drawable.mintgram_yoomoney, 0xFF7533EA, true, false);
            cardCell.setAlpha(1.0f);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), heightMeasureSpec);
        }
    }

    private static class PaymentRowCell extends FrameLayout {
        private final ImageView imageView;
        private final TextView textView;
        private final Paint dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private boolean needDivider;

        public PaymentRowCell(Context context) {
            super(context);
            setBackground(Theme.getSelectorDrawable(false));

            imageView = new ImageView(context);
            imageView.setClipToOutline(true);
            addView(imageView, LayoutHelper.createFrame(28, 28, Gravity.LEFT | Gravity.CENTER_VERTICAL, 28, 0, 0, 0));

            textView = new TextView(context);
            textView.setTextSize(16);
            textView.setSingleLine(true);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP, 71, 0, 16, 0));
        }

        public void bind(CharSequence text, int icon, int backgroundColor, boolean bitmap, boolean divider) {
            textView.setText(text);
            imageView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(8), backgroundColor));
            imageView.setScaleType(bitmap ? ImageView.ScaleType.CENTER_CROP : ImageView.ScaleType.FIT_CENTER);
            int padding = bitmap ? 0 : AndroidUtilities.dp(6);
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setImageResource(icon);
            needDivider = divider;
            setWillNotDraw(!divider);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (needDivider) {
                dividerPaint.setColor(Theme.getColor(Theme.key_divider));
                canvas.drawRect(AndroidUtilities.dp(71), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight(), dividerPaint);
            }
        }
    }

    private static class OtherActionsBlockCell extends FrameLayout {
        private final TextSettingsCell badgeCell;
        private final TextSettingsCell exportCell;
        private final TextSettingsCell resetCell;
        private final TextSettingsCell deleteCell;

        public OtherActionsBlockCell(Context context) {
            super(context);
            setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(4), AndroidUtilities.dp(12), AndroidUtilities.dp(4));

            LinearLayout block = createBlock(context);
            addView(block, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            badgeCell = createRow(context);
            exportCell = createRow(context);
            resetCell = createRow(context);
            deleteCell = createRow(context);

            block.addView(badgeCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 54));
            block.addView(exportCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 54));
            block.addView(resetCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 54));
            block.addView(deleteCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 54));

            badgeCell.setOnClickListener(v -> showBadgeSheet(getContext()));
            exportCell.setOnClickListener(v -> exportSettings(getContext()));
            resetCell.setOnClickListener(v -> showResetSettingsDialog(getContext()));
            deleteCell.setOnClickListener(v -> showDeleteAccountDialog(getContext()));
        }

        public void bind() {
            bindBlockRow(badgeCell, LocaleController.getString(R.string.MintGramGetBadge), R.drawable.settings_gift, true);
            bindBlockRow(exportCell, LocaleController.getString(R.string.MintGramExportSettings), R.drawable.settings_features, true);
            bindBlockRow(resetCell, LocaleController.getString(R.string.MintGramResetSettings), R.drawable.settings_policy, true);
            bindBlockRow(deleteCell, LocaleController.getString(R.string.MintGramDeleteAccount), R.drawable.msg_delete, false);
            deleteCell.setTextColor(getSupportColor());
        }

        private static void showBadgeSheet(Context context) {
            BottomSheet.Builder builder = new BottomSheet.Builder(context, false);
            HalfSheetContainer container = new HalfSheetContainer(context);
            builder.setCustomView(container);

            ScrollView scrollView = new ScrollView(context);
            scrollView.setFillViewport(false);
            scrollView.setClipToPadding(false);
            container.addView(scrollView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT, 0, 0, 0, 66));

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(AndroidUtilities.dp(22), AndroidUtilities.dp(16), AndroidUtilities.dp(22), AndroidUtilities.dp(4));
            scrollView.addView(layout, new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            HeartLogoView logoView = new HeartLogoView(context);
            layout.addView(logoView, LayoutHelper.createLinear(82, 74, Gravity.CENTER_HORIZONTAL, 0, 0, 0, 10));

            TextView titleView = new TextView(context);
            titleView.setText(LocaleController.getString(R.string.MintGramSupportSheetTitle));
            titleView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            titleView.setTextSize(21);
            titleView.setGravity(Gravity.CENTER);
            titleView.setTypeface(AndroidUtilities.bold());
            layout.addView(titleView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 6));

            TextView subtitleView = new TextView(context);
            subtitleView.setText(LocaleController.getString(R.string.MintGramSupportSheetSubtitle));
            subtitleView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
            subtitleView.setTextSize(14);
            subtitleView.setGravity(Gravity.CENTER);
            subtitleView.setLineSpacing(AndroidUtilities.dp(2), 1f);
            layout.addView(subtitleView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 8, 0, 8, 14));

            addFeature(layout, R.drawable.settings_stars, LocaleController.getString(R.string.MintGramSupportSheetDonateTitle), LocaleController.getString(R.string.MintGramSupportSheetDonateText));
            addFeature(layout, R.drawable.settings_features, LocaleController.getString(R.string.MintGramSupportSheetConfirmTitle), LocaleController.getString(R.string.MintGramSupportSheetConfirmText));
            addFeature(layout, R.drawable.settings_gift, LocaleController.getString(R.string.MintGramSupportSheetBadgeTitle), LocaleController.getString(R.string.MintGramSupportSheetBadgeText));

            TextView closeView = new TextView(context);
            closeView.setText(LocaleController.getString(R.string.Close));
            closeView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            closeView.setTextSize(16);
            closeView.setGravity(Gravity.CENTER);
            closeView.setTypeface(AndroidUtilities.bold());
            closeView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(26), getAccentColor(), getAccentColor() & 0xDDFFFFFF));
            container.addView(closeView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.BOTTOM | Gravity.LEFT, 22, 0, 22, 12));

            BottomSheet sheet = builder.create();
            closeView.setOnClickListener(v -> sheet.dismiss());
            sheet.fixNavigationBar();
            sheet.show();
        }

        private static void addFeature(LinearLayout layout, int icon, CharSequence title, CharSequence text) {
            Context context = layout.getContext();
            LinearLayout row = new LinearLayout(context);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.TOP);
            layout.addView(row, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

            ImageView iconView = new ImageView(context);
            iconView.setImageResource(icon);
            iconView.setColorFilter(Theme.getColor(Theme.key_dialogTextBlack));
            row.addView(iconView, LayoutHelper.createLinear(32, 32, 0, 2, 12, 0));

            LinearLayout texts = new LinearLayout(context);
            texts.setOrientation(LinearLayout.VERTICAL);
            row.addView(texts, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1f));

            TextView titleView = new TextView(context);
            titleView.setText(title);
            titleView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            titleView.setTextSize(15);
            titleView.setTypeface(AndroidUtilities.bold());
            texts.addView(titleView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            TextView textView = new TextView(context);
            textView.setText(text);
            textView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
            textView.setTextSize(13);
            textView.setLineSpacing(AndroidUtilities.dp(2), 1f);
            texts.addView(textView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 3, 0, 0));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), heightMeasureSpec);
        }
    }

    private static class HalfSheetContainer extends FrameLayout {
        public HalfSheetContainer(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height = AndroidUtilities.displaySize.y / 2;
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
    }

    private static class SupportInfoCell extends FrameLayout {
        private final TextView textView;

        public SupportInfoCell(Context context) {
            super(context);
            setBackground(Theme.getSelectorDrawable(false));
            setOnClickListener(v -> OtherActionsBlockCell.showBadgeSheet(getContext()));

            textView = new TextView(context);
            textView.setTextSize(14);
            textView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
            textView.setPadding(0, AndroidUtilities.dp(10), 0, AndroidUtilities.dp(17));
            textView.setOnClickListener(v -> OtherActionsBlockCell.showBadgeSheet(getContext()));
            addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 24, 0, 24, 0));
        }

        public void bind(CharSequence text) {
            textView.setText(text);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        }
    }

    private static void exportSettings(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, LocaleController.getString(R.string.MintGramExportSettings));
        intent.putExtra(Intent.EXTRA_TEXT, buildExportText());
        try {
            context.startActivity(Intent.createChooser(intent, LocaleController.getString(R.string.MintGramExportSettings)));
        } catch (Exception e) {
            Toast.makeText(context, LocaleController.getString(R.string.MintGramExportUnavailable), Toast.LENGTH_SHORT).show();
        }
    }

    private static String buildExportText() {
        return "MintGram 12.0.3\n"
                + "hideReadStatus=" + SharedConfig.hideReadReceipts + "\n"
                + "keepDeletedMessages=" + SharedConfig.keepDeletedMessages + "\n"
                + "ghostHideOnline=" + SharedConfig.ghostHideOnline + "\n"
                + "ghostHideTyping=" + SharedConfig.ghostHideTyping + "\n"
                + "ghostHideRecordVideo=" + SharedConfig.ghostHideRecordVideo + "\n"
                + "ghostHideUploadVideo=" + SharedConfig.ghostHideUploadVideo + "\n"
                + "ghostHideRecordVoice=" + SharedConfig.ghostHideRecordVoice + "\n"
                + "ghostHideUploadPhoto=" + SharedConfig.ghostHideUploadPhoto + "\n"
                + "ghostHideUploadFile=" + SharedConfig.ghostHideUploadFile + "\n"
                + "deletedMessageStyle=" + SharedConfig.deletedMessageStyle + "\n"
                + "mapProvider=" + SharedConfig.mintGramMapProvider + "\n"
                + "messageSize=" + SharedConfig.fontSize;
    }

    private static void showResetSettingsDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(R.string.MintGramResetSettings));
        builder.setMessage(LocaleController.getString(R.string.MintGramResetSettingsText));
        builder.setPositiveButton(LocaleController.getString(R.string.Reset), (dialogInterface, i) -> resetSettings(context));
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.show();
    }

    private static void resetSettings(Context context) {
        SharedConfig.setHideReadReceipts(false);
        SharedConfig.setKeepDeletedMessages(false);
        SharedConfig.setPlumFreeVoiceTranscription(false);
        SharedConfig.setGhostHideOnline(false);
        SharedConfig.setGhostHideTyping(false);
        SharedConfig.setGhostHideRecordVideo(false);
        SharedConfig.setGhostHideUploadVideo(false);
        SharedConfig.setGhostHideRecordVoice(false);
        SharedConfig.setGhostHideUploadPhoto(false);
        SharedConfig.setGhostHideUploadFile(false);
        SharedConfig.setDeletedMessageStyle(0);
        SharedConfig.setMintGramMapProvider(0);
        SharedConfig.fontSize = AndroidUtilities.isTablet() && !AndroidUtilities.isFold() ? 18 : 16;
        SharedConfig.fontSizeIsDefault = true;
        SharedPreferences preferences = context.getSharedPreferences("mainconfig", Context.MODE_PRIVATE);
        preferences.edit().remove("fons_size").apply();
        Theme.createCommonMessageResources();
        for (int account = 0; account < UserConfig.MAX_ACCOUNT_COUNT; account++) {
            NotificationCenter.getInstance(account).postNotificationName(NotificationCenter.updateInterfaces, MessagesController.UPDATE_MASK_AVATAR | MessagesController.UPDATE_MASK_NAME);
        }
        Toast.makeText(context, LocaleController.getString(R.string.MintGramSettingsResetDone), Toast.LENGTH_SHORT).show();
    }

    private static void showDeleteAccountDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(R.string.MintGramDeleteAccount));
        builder.setMessage(LocaleController.getString(R.string.MintGramDeleteAccountText));
        builder.setPositiveButton(LocaleController.getString(R.string.MintGramDeleteAccountOpen), (dialogInterface, i) -> Browser.openUrl(context, "https://my.telegram.org/delete"));
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.show();
    }

    private static class HeartLogoView extends FrameLayout {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();

        public HeartLogoView(Context context) {
            super(context);
            setWillNotDraw(false);
            ImageView logo = new ImageView(context);
            logo.setImageResource(R.drawable.mintgram_logo_icon);
            logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
            addView(logo, LayoutHelper.createFrame(54, 54, Gravity.CENTER, 0, -AndroidUtilities.dp(3), 0, 0));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            path.reset();
            path.moveTo(w / 2f, h * 0.88f);
            path.cubicTo(w * 0.08f, h * 0.58f, w * 0.02f, h * 0.26f, w * 0.22f, h * 0.12f);
            path.cubicTo(w * 0.36f, h * 0.02f, w * 0.48f, h * 0.10f, w / 2f, h * 0.24f);
            path.cubicTo(w * 0.52f, h * 0.10f, w * 0.64f, h * 0.02f, w * 0.78f, h * 0.12f);
            path.cubicTo(w * 0.98f, h * 0.26f, w * 0.92f, h * 0.58f, w / 2f, h * 0.88f);
            path.close();
            paint.setColor(getAccentColor());
            canvas.drawPath(path, paint);
        }
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
