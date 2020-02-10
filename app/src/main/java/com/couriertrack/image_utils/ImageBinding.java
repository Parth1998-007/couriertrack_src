package com.couriertrack.image_utils;

public class ImageBinding {
    private static final String TAG = "ImageBinding";

   /* @BindingAdapter({"app:userImageUrl"})
    public static void loadUserImage(ImageView view, String imageUrl) {
        AppLog.e(TAG,"loadImage : "+imageUrl);
        Drawable defaultImage = view.getContext().getResources().getDrawable(R.drawable.icon_user_default);
        RequestOptions requestOption=new RequestOptions().placeholder(defaultImage).error(defaultImage);
        if(!TextUtils.isEmpty(imageUrl))
            Glide.with(view.getContext()).load(imageUrl).apply(requestOption).thumbnail(0.1f).into(view);
        else
            Glide.with(view.getContext()).load(defaultImage).into(view);
    }
    @BindingAdapter({"app:loadImageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        AppLog.e(TAG,"loadImage : "+imageUrl);
        Drawable defaultImage = view.getContext().getResources().getDrawable(R.drawable.place_holder);
        RequestOptions requestOption=new RequestOptions().placeholder(defaultImage).error(defaultImage);
        if(!TextUtils.isEmpty(imageUrl))
            Glide.with(view.getContext()).load(imageUrl).apply(requestOption).thumbnail(0.1f).into(view);
        else
            view.setImageResource(R.drawable.place_holder);
    }*/

}
