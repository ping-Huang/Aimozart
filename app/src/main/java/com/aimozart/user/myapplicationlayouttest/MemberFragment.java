package com.aimozart.user.myapplicationlayouttest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MemberFragment extends Fragment {
    private Member member;

    public static MemberFragment newInstance(Member member) {
        MemberFragment fragment = new MemberFragment();
        Bundle args = new Bundle();
        args.putSerializable("member", member);
        fragment.setArguments(args);
        return fragment;
    }

    public MemberFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            member = (Member) getArguments().getSerializable("member");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_fragment, container, false);

        TextView tvName = (TextView) view
                .findViewById(R.id.tvName);
        tvName.setText(member.getName());

        return view;
    }
}
