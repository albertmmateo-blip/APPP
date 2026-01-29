package com.appp.avisos.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.appp.avisos.RecycleBinFragment
import com.appp.avisos.database.Note

/**
 * Adapter for the ViewPager2 in RecycleBinActivity.
 * Manages the two tabs for Esborrades and Finalitzades.
 */
class RecycleBinPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private val deletionTypes = arrayOf(
        Note.DELETION_TYPE_ESBORRADES,
        Note.DELETION_TYPE_FINALITZADES
    )

    override fun getItemCount(): Int = deletionTypes.size

    override fun createFragment(position: Int): Fragment {
        return RecycleBinFragment.newInstance(deletionTypes[position])
    }
}
