package org.jorge.lolin1.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.adapter.NavigationDrawerAdapter;
import org.jorge.lolin1.ui.adapter.NavigationDrawerAdapter.NavigationItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author poliveira
 *         24/10/2014
 */
public class NavigationDrawerFragment extends Fragment implements NavigationDrawerAdapter.NavigationDrawerCallbacks {
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private NavigationDrawerAdapter.NavigationDrawerCallbacks mCallbacks;
    private RecyclerView mDrawerList;
    private View mFragmentContainerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private int mCurrentSelectedPosition;
    private ActionBarActivity mActivity;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mActionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, Boolean.FALSE);
        mDrawerList = (RecyclerView) view.findViewById(R.id.drawerList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);
        mDrawerList.setHasFixedSize(Boolean.TRUE);
        mDrawerList.setOverScrollMode(View.OVER_SCROLL_NEVER);

        View helpAction = view.findViewById(R.id.action_help), settingsAction = view.findViewById(R.id.action_settings);

        settingsAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Display settings
            }
        });

        helpAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.help_url)));
                startActivity(browserIntent);
                mDrawerLayout.closeDrawer(mFragmentContainerView);
            }
        });

        final List<NavigationItem> navigationItems = readMenuItems();
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(LoLin1Application.getInstance().getContext(), navigationItems);
        adapter.setNavigationDrawerCallbacks(this);
        mDrawerList.setAdapter(adapter);
        selectItem(mCurrentSelectedPosition);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readSharedSetting(mActivity, PREF_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = Boolean.TRUE;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerAdapter.NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
        mActivity = (ActionBarActivity) activity;
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = mActivity.findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mActionBarDrawerToggle = new ActionBarDrawerToggle(mActivity, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                mActivity.invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = Boolean.TRUE;
                    saveSharedSetting(mActivity, PREF_USER_LEARNED_DRAWER, "true");
                }

                mActivity.invalidateOptionsMenu();
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState)
            mDrawerLayout.openDrawer(mFragmentContainerView);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public List<NavigationItem> readMenuItems() {
        List<NavigationItem> items = new ArrayList<>();
        Resources resources = getResources();
        final String NAVIGATION_TITLE_STANDARD_DRAWABLE_PATTERN = resources.getString(R.string.navigation_title_standard_resource_pattern),
                NAVIGATION_TITLE_SELECTED_DRAWABLE_PATTERN = resources.getString(R.string.navigation_title_selected_resource_pattern);
        final String[] itemNames = resources.getStringArray(R.array.navigation_drawer_items);
        final List<Drawable> standardItemIcons = new ArrayList<>(), selectedItemIcons = new ArrayList<>();
        for (int i = 0; i < itemNames.length; i++) {
            final String standardDrawableResourceName = String.format(NAVIGATION_TITLE_STANDARD_DRAWABLE_PATTERN, i),
                    selectedDrawableResourceName = String.format(NAVIGATION_TITLE_SELECTED_DRAWABLE_PATTERN, i);
            try {
                final Field standardDrawableResourceField = R.drawable.class.getDeclaredField(standardDrawableResourceName),
                        selectedDrawableResourceField = R.drawable.class.getDeclaredField(selectedDrawableResourceName);
                standardItemIcons.add(resources.getDrawable(standardDrawableResourceField.getInt(standardDrawableResourceField)));
                selectedItemIcons.add(resources.getDrawable(selectedDrawableResourceField.getInt(selectedDrawableResourceField)));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException("Expected drawable resource " + standardDrawableResourceName + " not found.");
            }
        }
        if (standardItemIcons.size() < itemNames.length) {
            throw new IllegalStateException("Not enough icons for this many navigation choices");
        }
        for (int i = 0; i < itemNames.length; i++) {
            items.add(new NavigationItem(itemNames[i], standardItemIcons.get(i), selectedItemIcons.get(i)));
        }
        return items;
    }

    public void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
        ((NavigationDrawerAdapter) mDrawerList.getAdapter()).selectPosition(position);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        selectItem(position);
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPref.getString(settingName, defaultValue);
    }

    public int getPosition() {
        return ((NavigationDrawerAdapter) mDrawerList.getAdapter()).getSelectedPosition();
    }
}