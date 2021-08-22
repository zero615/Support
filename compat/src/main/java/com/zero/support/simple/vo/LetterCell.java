package com.zero.support.simple.vo;


import com.zero.support.compat.BR;
import com.zero.support.compat.R;
import com.zero.support.recycler.SimpleViewBound;
import com.zero.support.recycler.annotation.RecyclerViewBind;
import com.zero.support.recycler.manager.StickyHeaders;
import com.zero.support.compat.vo.BaseObject;

@RecyclerViewBind(LetterCell.LetterViewBound.class)
public class LetterCell extends BaseObject implements StickyHeaders {
    private String letter;

    public LetterCell(String letter) {
        this.letter = letter;
    }

    public String getLetter() {
        return letter;
    }

    @Override
    public boolean isStickyHeader(int position) {
        return true;
    }


    public static class LetterViewBound extends SimpleViewBound {

        public LetterViewBound() {
            super(BR.data, R.layout.view_bound_letter);
        }
    }
}
