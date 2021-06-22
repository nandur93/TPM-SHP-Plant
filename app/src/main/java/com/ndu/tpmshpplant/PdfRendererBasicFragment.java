/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ndu.tpmshpplant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.github.chrisbanes.photoview.PhotoView;

import org.jetbrains.annotations.NotNull;

/**
 * This fragment has a big {@link ImageView} that shows PDF pages, and 2
 * {@link Button}s to move between pages.
 */
public class PdfRendererBasicFragment extends Fragment {

    private PhotoView image;

/*    private final View.OnClickListener mOnClickListener = (view) -> {
        switch (view.getId()) {
            case R.id.previous:
                if (mViewModel != null) {
                    mViewModel.showPrevious();
                }
                break;
            case R.id.next:
                if (mViewModel != null) {
                    mViewModel.showNext();
                }
                break;
        }
    };*/

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pdf_renderer_basic_fragment, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // View references.
        image = view.findViewById(R.id.image);
        image.setOnClickListener(v -> ((DetailActivity) requireActivity()).startMuPDFActivityWithExampleFile());
/*        final Button buttonPrevious = view.findViewById(R.id.previous);
        final Button buttonNext = view.findViewById(R.id.next);*/

        // Bind data.
        PdfRendererBasicViewModel mViewModel = new ViewModelProvider(this).get(PdfRendererBasicViewModel.class);
        final LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        mViewModel.getPageInfo().observe(viewLifecycleOwner, pageInfo -> {
            if (pageInfo == null) {
                return;
            }
            final Activity activity = getActivity();
            if (activity != null) {
                activity.setTitle(getString(R.string.app_name_with_index,
                        pageInfo.index + 1, pageInfo.count));
            }
        });

        mViewModel.getPageBitmap().observe(viewLifecycleOwner, image::setImageBitmap);
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(getActivity(), "landscape", Toast.LENGTH_SHORT).show();
            image.setScaleType(PhotoView.ScaleType.FIT_CENTER);
            Log.d("TAG", "onViewCreated: centerCrop");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(getActivity(), "portrait", Toast.LENGTH_SHORT).show();
            image.setScaleType(PhotoView.ScaleType.FIT_START);
            Log.d("TAG", "onViewCreated: fitStart");
        }
    }
}
