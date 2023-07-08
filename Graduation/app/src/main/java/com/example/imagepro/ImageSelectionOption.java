package com.example.imagepro;

public class ImageSelectionOption {
    private final int iconResId;
    private final String optionText;

    public ImageSelectionOption(int iconResId, String optionText) {
        this.iconResId = iconResId;
        this.optionText = optionText;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getOptionText() {
        return optionText;
    }
}
