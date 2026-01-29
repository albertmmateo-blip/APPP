package com.appp.avisos.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.appp.avisos.CategoryFragment
import com.appp.avisos.CompraSubcategoryFragment
import com.appp.avisos.FacturesSubcategoryFragment
import com.appp.avisos.UserSessionManager
import com.appp.avisos.VendaSubcategoryFragment

/**
 * Adapter for ViewPager2 that displays category fragments
 * 
 * @param fragmentActivity The FragmentActivity that hosts the ViewPager2
 * @param categories Array of category names to create fragments for
 */
class CategoryPagerAdapter(
    private val fragmentActivity: FragmentActivity,
    private val categories: Array<String>
) : FragmentStateAdapter(fragmentActivity) {
    
    private val sessionManager = UserSessionManager(fragmentActivity)
    
    override fun getItemCount(): Int = categories.size
    
    override fun createFragment(position: Int): Fragment {
        // For Factures, Compra, and Venda categories, show subcategory selection for Pedro
        val category = categories[position]
        return when {
            category == "Factures" && sessionManager.getCurrentUser() == "Pedro" -> {
                FacturesSubcategoryFragment.newInstance()
            }
            category == "Compra" && sessionManager.getCurrentUser() == "Pedro" -> {
                CompraSubcategoryFragment.newInstance()
            }
            category == "Venda" && sessionManager.getCurrentUser() == "Pedro" -> {
                VendaSubcategoryFragment.newInstance()
            }
            else -> {
                CategoryFragment.newInstance(category)
            }
        }
    }
}
