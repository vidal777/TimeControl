package com.example.timecontrol;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class TabAdapter extends FragmentPagerAdapter {


    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int tabPosition) {
        switch(tabPosition){
            case 0:
                ProfileTab profileTab = new ProfileTab();
                return profileTab;
            case 1:
                UsersTab usersTab=new UsersTab();
                return usersTab;
            case 2:
                SettingsTab settingsTab=new SettingsTab();
                return settingsTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Profile";
            case 1:
                return "Users";
            case 2:
                return "Settings";
            default:
                return null;
        }
    }

}
