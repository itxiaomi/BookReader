package com.justwayward.reader.ui.easyadapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.justwayward.reader.R;
import com.justwayward.reader.base.Constant;
import com.justwayward.reader.bean.BookHelpList;
import com.justwayward.reader.manager.SettingManager;
import com.justwayward.reader.utils.FormatUtils;
import com.justwayward.reader.view.recyclerview.adapter.BaseViewHolder;
import com.justwayward.reader.view.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * @author lfh.
 * @date 16/9/3.
 */
public class BookHelpAdapter extends RecyclerArrayAdapter<BookHelpList.HelpsBean> {


    public BookHelpAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<BookHelpList.HelpsBean>(parent, R.layout.item_community_book_help_list) {
            @Override
            public void setData(BookHelpList.HelpsBean item) {
                if (!SettingManager.getInstance().isNoneCover()) {
                    holder.setCircleImageUrl(R.id.ivBookCover, Constant.IMG_BASE_URL + item.author.avatar,
                            R.drawable.avatar_default);
                } else {
                    holder.setImageResource(R.id.ivBookCover, R.drawable.avatar_default);
                }

                holder.setText(R.id.tvBookType, String.format(mContext.getString(R.string
                        .book_detail_user_lv), item.author.lv))
                        .setText(R.id.tvTitle, item.title)
                        .setText(R.id.tvHelpfulYes, item.commentCount + "");

                if (TextUtils.equals(item.state, "hot")) {
                    holder.setVisible(R.id.tvHot, true);
                    holder.setVisible(R.id.tvTime, false);
                    holder.setVisible(R.id.tvDistillate, false);
                } else if (TextUtils.equals(item.state, "distillate")) {
                    holder.setVisible(R.id.tvDistillate, true);
                    holder.setVisible(R.id.tvHot, false);
                    holder.setVisible(R.id.tvTime, false);
                } else {
                    holder.setVisible(R.id.tvTime, true);
                    holder.setVisible(R.id.tvHot, false);
                    holder.setVisible(R.id.tvDistillate, false);
                    holder.setText(R.id.tvTime, FormatUtils.formatDate(item.created));
                }
            }
        };
    }
}
