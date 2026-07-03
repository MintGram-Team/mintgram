package org.telegram.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.Objects;

public class MintGramSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter adapter;
    private final ArrayList<ItemInner> items = new ArrayList<>();

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_PRIVACY_BLOCK = 1;
    private static final int VIEW_TYPE_SHADOW = 2;
    private static final int VIEW_TYPE_FEATURES_BLOCK = 3;
    private static final int VIEW_TYPE_GHOST_BLOCK = 4;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString(R.string.MintGramSettings));
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
        items.add(new ItemInner(VIEW_TYPE_HEADER, 0, LocaleController.getString(R.string.MintGramPrivacy)));
        items.add(new ItemInner(VIEW_TYPE_PRIVACY_BLOCK, 1, null));
        items.add(new ItemInner(VIEW_TYPE_SHADOW, 2, null));
        items.add(new ItemInner(VIEW_TYPE_HEADER, 3, LocaleController.getString(R.string.MintGramGhostMode)));
        items.add(new ItemInner(VIEW_TYPE_GHOST_BLOCK, 4, null));
        items.add(new ItemInner(VIEW_TYPE_SHADOW, 5, null));
        items.add(new ItemInner(VIEW_TYPE_HEADER, 6, LocaleController.getString(R.string.MintGramFeatures)));
        items.add(new ItemInner(VIEW_TYPE_FEATURES_BLOCK, 7, null));
        items.add(new ItemInner(VIEW_TYPE_SHADOW, 2, LocaleController.getString(R.string.MintGramFeaturesInfo)));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
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
            } else if (viewType == VIEW_TYPE_PRIVACY_BLOCK) {
                view = new PrivacyBlockCell(getContext());
            } else if (viewType == VIEW_TYPE_GHOST_BLOCK) {
                view = new GhostBlockCell(getContext());
            } else if (viewType == VIEW_TYPE_FEATURES_BLOCK) {
                view = new FeaturesBlockCell(getContext());
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
