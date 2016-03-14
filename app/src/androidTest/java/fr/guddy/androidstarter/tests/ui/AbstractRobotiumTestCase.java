package fr.guddy.androidstarter.tests.ui;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

public abstract class AbstractRobotiumTestCase<TypeActivity extends Activity> {
    //region Rule
    @Rule
    public final ActivityTestRule<TypeActivity> mActivityTestRule;
    //endregion

    //region Fields
    protected Solo mSolo;
    protected TypeActivity mActivity;
    protected Context mContextTest;
    protected Context mContextTarget;
    //endregion

    //region Constructor
    protected AbstractRobotiumTestCase(final ActivityTestRule<TypeActivity> poActivityTestRule) {
        mActivityTestRule = poActivityTestRule;
    }
    //endregion

    //region Test lifecycle
    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
        mSolo = new Solo(InstrumentationRegistry.getInstrumentation(), mActivity);
        mContextTest = InstrumentationRegistry.getContext();
        mContextTarget = InstrumentationRegistry.getTargetContext();
    }

    @After
    public void tearDown() throws Exception {
        mSolo.finishOpenedActivities();
    }
    //endregion
}
