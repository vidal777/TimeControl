package com.example.timecontrol;




import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import static com.example.timecontrol.SignActivity.prefe;


public class TabAdapter extends FragmentPagerAdapter {
    private String user_type;

    TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int tabPosition) {
        user_type=prefe.getString("User",null);
        switch(tabPosition){
            case 0:
                return new ProfileTab();
            case 1:
                if (user_type.equals("User")){
                    return new UsersTab();
                }else{
                    return new AdminTab();
                }
            case 2:
                if (user_type.equals("User")){
                    return new ExpenseTab();
                }else{
                    return new ExpenseAdminTab();
                }
            case 3:
                return new SettingsTab();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        user_type=prefe.getString("User",null);
        switch (position){
            case 0:
                return "Profile";
            case 1:
                if (user_type.equals("User")) return "Users";
                else return "Admin";
            case 2:
                return  "Expense";
            case 3:
                return "Settings";
            default:
                return null;
        }
    }

}
