package com.everybodv.githubuser.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.everybodv.githubuser.model.DetailUserViewModel
import com.everybodv.githubuser.ui.adapter.MainAdapter
import com.everybodv.githubuser.data.remote.response.ItemsItem
import com.everybodv.githubuser.databinding.FragmentFollowsBinding
import com.everybodv.githubuser.model.UserFavoriteViewModel
import com.everybodv.githubuser.model.ViewModelFactory

class FollowsFragment : Fragment() {

    private lateinit var binding: FragmentFollowsBinding
    private lateinit var detailUserViewModel: DetailUserViewModel

    companion object {
        const val ARG_POSITION = "position"
        const val ARG_USERNAME = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var position : Int? = 0
        var userName = arguments?.getString(ARG_USERNAME)

        Log.d("Position", position.toString())
        Log.d("Username", userName.toString())

        detailUserViewModel =
            ViewModelProvider(requireActivity(),
                ViewModelProvider.NewInstanceFactory())[DetailUserViewModel::class.java]
        arguments?.let {
            position = it.getInt(ARG_POSITION)
            userName = it.getString(ARG_USERNAME)
        }

        if (position == 1) {
            showLoading(true)
            userName?.let {
                detailUserViewModel.getFollowers(it)
            }
            detailUserViewModel.followers.observe(viewLifecycleOwner) {
                showLoading(false)
                setFollows(it)
            }
        } else {
            showLoading(true)
            userName?.let { detailUserViewModel.getFollowing(it) }
            detailUserViewModel.following.observe(viewLifecycleOwner) {
                showLoading(false)
                setFollows(it)
            }
        }
    }

    private fun setFollows(listUser: List<ItemsItem>) {
        binding.apply {
            binding.rvListUserFollows.layoutManager = LinearLayoutManager(requireActivity())
            val adapter = MainAdapter(listUser)
            binding.rvListUserFollows.adapter = adapter
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.followsProgressBar.visibility = View.VISIBLE
        } else {
            binding.followsProgressBar.visibility = View.GONE
        }
    }


}