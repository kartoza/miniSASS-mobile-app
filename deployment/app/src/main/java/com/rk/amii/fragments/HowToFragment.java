package com.rk.amii.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rk.amii.R;
import com.rk.amii.adapters.VideoAdapter;
import com.rk.amii.models.VideoModel;

import java.util.ArrayList;

/**
 * Create an instance of this fragment.
 */
public class HowToFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_how_to, container, false);

        RecyclerView videoView = view.findViewById(R.id.rvOfflineVideos);
        RecyclerView onlineVideoView = view.findViewById(R.id.rvOnlineVideos);

        // Create a list of offline videos
        ArrayList<VideoModel> videos = new ArrayList<>();
        videos.add(
                new VideoModel(
                        "android.resource://" + this.getActivity().getPackageName() + "/" + R.raw.intro,
                        getString(R.string.introduction_to_minisass),
                        "offline"));
        videos.add(
                new VideoModel(
                        "android.resource://" + this.getActivity().getPackageName() + "/" + R.raw.summary,
                        getString(R.string.minisass_in_summary),
                        "offline"));

        // Add the videos to the offline video view
        VideoAdapter videoAdapter = new VideoAdapter(this.getContext(), videos);
        videoView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        videoView.setAdapter(videoAdapter);

        // Create a list of online videos
        ArrayList<VideoModel> onlineVideos = new ArrayList<>();
        onlineVideos.add(
                new VideoModel(
                        "https://youtu.be/yGbi7P8RYoU",
                        getString(R.string.safety_concerns_for_minisass),
                        "online",
                        R.raw.imagehowtostaysafe));
        onlineVideos.add(
                new VideoModel(
                        "https://youtu.be/XJLcJMutXP8",
                        getString(R.string.what_is_in_your_minisass_kit),
                        "online",
                        R.raw.imagetheminisasskit));
        onlineVideos.add(
                new VideoModel(
                        "https://youtu.be/_-L-Xs4QJRg",
                        getString(R.string.what_do_you_need_to_put_together_a_minisass_kit_at_home),
                        "online",
                        R.raw.imagethehomemademinisasskit));
        onlineVideos.add(
                new VideoModel(
                        "https://youtu.be/WX_DkYyfnmk",
                        getString(R.string.choosing_your_site_for_minisass),
                        "online",
                        R.raw.imagechoosingasite));
        onlineVideos.add(
                new VideoModel(
                        "https://youtu.be/XY_p8usHx4Q",
                        getString(R.string.how_to_take_a_sample),
                        "online",
                        R.raw.imagehowtotakeasample));
        onlineVideos.add(
                new VideoModel(
                        "https://youtu.be/8RATZXY2jyo",
                        getString(R.string.cleaning_your_sample),
                        "online",
                        R.raw.imagecleaningyoursample));
        onlineVideos.add(
                new VideoModel(
                        "https://youtu.be/hKdPiSSVL0s",
                        getString(R.string.using_the_dichotomous_key),
                        "online",
                        R.raw.imagehowtousethekey));
        onlineVideos.add(
                new VideoModel(
                        "https://youtu.be/O_deXdCQIfM",
                        getString(R.string.how_to_calculate_your_score),
                        "online",
                        R.raw.imagehowtocalculatescore));
        onlineVideos.add(
                new VideoModel(
                        "https://youtu.be/uUJTrkZKL6U",
                        getString(R.string.uploading_your_minisass_score),
                        "online",
                        R.raw.imageuploadingyourscore));
        onlineVideos.add(
                new VideoModel(
                        "https://youtu.be/uU7hOj4zjG0",
                        getString(R.string.using_minisass_for_monitoring),
                        "online",
                        R.raw.imageusingminisassformonitoring));

        // Add the videos to the online video view
        VideoAdapter onlineVideoAdapter = new VideoAdapter(this.getContext(), onlineVideos);
        onlineVideoView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        onlineVideoView.setAdapter(onlineVideoAdapter);

        return view;
    }
}