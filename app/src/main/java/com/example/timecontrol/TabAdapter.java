package com.example.timecontrol;




import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import static com.example.timecontrol.SignActivity.prefe;


public class TabAdapter extends FragmentPagerAdapter {
    String user_type;

    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int tabPosition) {
        user_type=prefe.getString("User",null);
        switch(tabPosition){
            case 0:
                ProfileTab profileTab = new ProfileTab();
                return profileTab;
            case 1:
                if (user_type.equals("User")){
                    UsersTab usersTab=new UsersTab();
                    return usersTab;
                }else{
                    AdminTab adminTab=new AdminTab();
                    return adminTab;
                }
            case 2:
                if (user_type.equals("User")){
                    ExpenseTab expenseTab=new ExpenseTab();
                    return expenseTab;
                }else{
                    ExpenseAdminTab expenseAdminTab=new ExpenseAdminTab();
                    return expenseAdminTab;
                }
            case 3:
                SettingsTab settingsTab=new SettingsTab();
                return settingsTab;
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
