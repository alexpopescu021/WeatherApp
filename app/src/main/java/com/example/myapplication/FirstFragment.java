package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.FragmentFirstBinding;

import java.util.concurrent.Executor;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    String CITY = "craiova,ro";
    String API = "85c5a62b4b095f1ad2e957d9b84421ad";
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



//        binding.btVar1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//            }
//        });
    }

    private static class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        public void onPreExecute()
        {
                super.onPreExecute();
        }

        @Override
        public void onPostExecute(String result)
        {
            super.onPostExecute(result);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}