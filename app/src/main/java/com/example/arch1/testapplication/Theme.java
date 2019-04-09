package com.example.arch1.testapplication;

class Theme {

    static final String GREEN = "green";
    static final String ORANGE = "orange";
    static final String BLUE = "blue";
    static final String RED = "red";
    static final String LIGHT_GREEN = "lgreen";
    static final String PINK = "pink";
    static final String PURPLE = "purple";
    static final String MATERIAL_LIGHT = "material";
    static final String MATERIAL_DARK = "materialdark";
    static final String DEFAULT = "default";


    static int getTheme(String themeName) {
        switch (themeName) {
            case GREEN :
                return R.style.GreenAppTheme;
            case ORANGE :
                return R.style.AppTheme;
            case BLUE :
                return R.style.BlueAppTheme;
            case RED :
                return R.style.RedAppTheme;
            case LIGHT_GREEN :
                return R.style.LightGreenAppTheme;
            case PINK :
                return R.style.PinkAppTheme;
            case PURPLE :
                return R.style.PurpleAppTheme;
            case MATERIAL_LIGHT :
                return R.style.MaterialLight;
            case MATERIAL_DARK :
                return R.style.MaterialDark;
            case DEFAULT :
                return R.style.DefAppTheme;
            default :
                return R.style.MaterialLight;
        }
    }

    static void changeTheme(String themeName, AppPreferences preferences) {
        switch (themeName) {
            case GREEN :
                preferences.setStringPreference(AppPreferences.APP_THEME, GREEN);
                break;
            case ORANGE :
                preferences.setStringPreference(AppPreferences.APP_THEME, ORANGE);
                break;
            case BLUE :
                preferences.setStringPreference(AppPreferences.APP_THEME, BLUE);
                break;
            case RED :
                preferences.setStringPreference(AppPreferences.APP_THEME, RED);
                break;
            case LIGHT_GREEN :
                preferences.setStringPreference(AppPreferences.APP_THEME, LIGHT_GREEN);
                break;
            case PINK :
                preferences.setStringPreference(AppPreferences.APP_THEME, PINK);
                break;
            case PURPLE :
                preferences.setStringPreference(AppPreferences.APP_THEME, PURPLE);
                break;
            case MATERIAL_LIGHT :
                preferences.setStringPreference(AppPreferences.APP_THEME, MATERIAL_LIGHT);
                break;
            case MATERIAL_DARK :
                preferences.setStringPreference(AppPreferences.APP_THEME, MATERIAL_DARK);
                break;
            case DEFAULT :
                preferences.setStringPreference(AppPreferences.APP_THEME, DEFAULT);
                break;
            default :
                preferences.setStringPreference(AppPreferences.APP_THEME, MATERIAL_LIGHT);
        }
    }

    static ListData getThemeData(String themeName) {
        ListData data = new ListData();
        data.setTitle("Themes");
        data.setImg(R.drawable.ic_outline_color_lens_24px);

        switch (themeName) {
            case GREEN :
                data.setBody("Green");
                break;
            case ORANGE :
                data.setBody("Orange");
                break;
            case BLUE :
                data.setBody("Blue");
                break;
            case RED :
                data.setBody("Red");
                break;
            case LIGHT_GREEN :
                data.setBody("Light Green");
                break;
            case PINK :
                data.setBody("Pink");
                break;
            case PURPLE :
                data.setBody("Purple");
                break;
            case MATERIAL_LIGHT :
                data.setBody("Material Light");
                break;
            case MATERIAL_DARK :
                data.setBody("Material Dark");
                break;
            case DEFAULT :
                data.setBody("Classic");
                break;
            default :
                data.setBody("Material Light");
        }

        return data;
    }

}
