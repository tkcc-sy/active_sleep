package com.paramount.bed.ui.front.slider;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paramount.bed.R;
import com.paramount.bed.data.model.SliderModel;
import com.paramount.bed.data.provider.SliderProvider;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SliderItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SliderItemFragment extends Fragment {

    private int defaultImage = R.drawable.s1;
    private int defaultImage2 = R.drawable.s2;
    private int defaultImage3 = R.drawable.s3;
    private int defaultImage4 = R.drawable.s4;
    private int defaultImage5 = R.drawable.s5;
    private String defaultCaption = "";
    private SliderModel currentSliderModel;


    public SliderItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SliderItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SliderItemFragment newInstance(SliderModel sliderModel) {
        SliderItemFragment fragment = new SliderItemFragment();
        fragment.currentSliderModel = sliderModel;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slider_item, container, false);
        ImageView imgsSlide = view.findViewById(R.id.slideimg);
        TextView description = view.findViewById(R.id.txtdescription);
        if (currentSliderModel != null) {
            String caption = currentSliderModel.getCaption();
            if (caption == null) caption = "";
            if (caption.equals(SliderProvider.DEFAULT_IMAGE_KEY)) { //magic keyword for default image
                description.setText(defaultCaption);
//                imgsSlide.setImageResource(defaultImage);
                Glide.with(getContext()).load(defaultImage).into(imgsSlide);
            } else if (caption.equals(SliderProvider.DEFAULT_IMAGE_KEY2)) { //magic keyword for default image
                description.setText(defaultCaption);
//                imgsSlide.setImageResource(defaultImage2);
                Glide.with(getContext()).load(defaultImage2).into(imgsSlide);
            } else if (caption.equals(SliderProvider.DEFAULT_IMAGE_KEY3)) { //magic keyword for default image
                description.setText(defaultCaption);
//                imgsSlide.setImageResource(defaultImage3);
                Glide.with(getContext()).load(defaultImage3).into(imgsSlide);
            } else if (caption.equals(SliderProvider.DEFAULT_IMAGE_KEY4)) { //magic keyword for default image
                description.setText(defaultCaption);
//                imgsSlide.setImageResource(defaultImage4);
                Glide.with(getContext()).load(defaultImage4).into(imgsSlide);
            } else if (caption.equals(SliderProvider.DEFAULT_IMAGE_KEY5)) { //magic keyword for default image
                description.setText(defaultCaption);
//                imgsSlide.setImageResource(defaultImage5);
                Glide.with(getContext()).load(defaultImage5).into(imgsSlide);
            } else {
                description.setText(caption);
                Glide.with(getContext()).load(currentSliderModel.getImageBytes()).into(imgsSlide);
            }
        }

        return view;
    }

}
