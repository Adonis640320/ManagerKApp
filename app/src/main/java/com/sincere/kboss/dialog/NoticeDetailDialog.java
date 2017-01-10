package com.sincere.kboss.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.sincere.kboss.R;
import com.sincere.kboss.global.Functions;
import com.sincere.kboss.stdata.STNotice;

import org.w3c.dom.Text;

/**
 * Created by Suju on 12/29/2016.
 */

public class NoticeDetailDialog extends CommonDialog{

    private SelectListner mListner;
    STNotice notice;

    public interface SelectListner{
        void onBack();
    }


    public NoticeDetailDialog(Context context, STNotice notice, SelectListner listner) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mListner = listner;
        this.notice = notice;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_notice_detail);
        ImageView ivNoticeClose = (ImageView)findViewById(R.id.ivNoticeClose);
        ivNoticeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListner.onBack();
                dismiss();
            }
        });

        TextView tvTitle = (TextView)findViewById(R.id.tvTitle);
        TextView tvDate = (TextView)findViewById(R.id.tvDate);
        TextView  tvDetail = (TextView)findViewById(R.id.tvDetail);
        tvTitle.setText(notice.f_title);
        tvDate.setText(Functions.changeDateTimeForm1(notice.f_date));
        tvDetail.setText(notice.f_content);
    }
}
