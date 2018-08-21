/*
 * Copyright 2017 dmfs GmbH
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.burhanrashid52.imageeditor.color;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.dmfs.android.view.ViewPager;


/**
 * A ViewPager that shows the content in a square area.
 *
 * @author Marten Gajda
 */
public class SquareViewPager extends ViewPager
{
    public SquareViewPager(Context context)
    {
        super(context);
    }


    public SquareViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getChildCount() <= 1)
        {
            // only the pagetitlestrip is present, nothing to do.
            return;
        }

        int titleStripHeight = getChildAt(0).getMeasuredHeight();

        int width = getMeasuredWidth();

        if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.AT_MOST)
        {
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        }

        // the new height is the height of the PagerTitleStrip + the width of the content.
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(titleStripHeight + width, View.MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
