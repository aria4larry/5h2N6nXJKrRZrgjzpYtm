package com.droidcat.stackranger.ui;

import android.os.Bundle;

import com.droidcat.stackranger.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import net.sf.stackwrap4j.entities.Answer;

import java.util.List;

public class QuestionActivity extends SlidingFragmentActivity implements AnswerFragment.onAnswerLoaded {
    SlidingMenu sm;
    AnswerFragment answerFragment;
    Bundle mExtras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBehindContentView(R.layout.menu_frame);
        setContentView(R.layout.content_frame);
        // Show the Up button in the action bar.

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            mExtras = getIntent().getExtras();
            QuestionFragment fragment = new QuestionFragment();
            fragment.setArguments(mExtras);
            fragment.setOnAnswerLoadedCallBack(this);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, fragment).commit();
        }
    }

    private void setupSlidingMenu(List<Answer> answers) {
        sm = getSlidingMenu();
        sm.setMode(SlidingMenu.RIGHT);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        sm.setTouchModeBehind(SlidingMenu.TOUCHMODE_MARGIN);
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadowright);
        sm.setBehindOffsetRes(R.dimen.answer_slidingmenu_offset);
        sm.setFadeDegree(0.35f);
        answerFragment = new AnswerFragment(answers);
        answerFragment.setArguments(mExtras);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, answerFragment).commit();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onAnswerLoaded(List<Answer> answers) {
        setupSlidingMenu(answers);
    }
}
