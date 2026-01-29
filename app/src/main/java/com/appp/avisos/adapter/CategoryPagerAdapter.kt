package com.appp.avisos.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.appp.avisos.CategoryFragment

/**
 * Adapter for ViewPager2 that displays category fragments
 */
class CategoryPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val categories: Array<String>
) : FragmentStateAdapter(fragmentActivity) {
    
    override fun getItemCount(): Int = categories.size
    
    override fun createFragment(position: Int): Fragment {
        return CategoryFragment.newInstance(categories[position])
    }
}
