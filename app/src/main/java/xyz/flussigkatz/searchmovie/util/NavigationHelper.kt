package xyz.flussigkatz.searchmovie.util

import android.content.Context
import android.os.Bundle
import androidx.navigation.NavController
import xyz.flussigkatz.searchmovie.R

object NavigationHelper {

    /*
    when(menuItemId) {
        R.id.home_page -> {
            navController.navigate(R.id.)
        }
        R.id.history -> {
            navController.navigate(R.id.)
        }
        R.id.marked -> {
            navController.navigate(R.id.)
        }
        R.id.settings -> {
            navController.navigate(R.id.)
        }
    }
    */

    fun navigate(
        navController: NavController,
        menuItemId: Int,
        onScreenFragmentId: Int,
        context: Context?,
    ) {
        when (onScreenFragmentId) {
            R.id.homeFragment -> {
                when (menuItemId) {
                    R.id.home_page -> {
                    }
                    R.id.history -> {
                        navController.navigate(R.id.action_homeFragment_to_historyFragment)
                    }
                    R.id.marked -> {
                        navController.navigate(R.id.action_homeFragment_to_markedFragment)
                    }
                    R.id.settings -> {
                        navController.navigate(R.id.action_homeFragment_to_settingsFragment)
                    }
                }
            }
            R.id.historyFragment -> {
                when (menuItemId) {
                    R.id.home_page -> {
                        navController.navigate(R.id.action_historyFragment_to_homeFragment)
                    }
                    R.id.history -> {
                    }
                    R.id.marked -> {
                        navController.navigate(R.id.action_historyFragment_to_markedFragment)
                    }
                    R.id.settings -> {
                        navController.navigate(R.id.action_historyFragment_to_settingsFragment)
                    }
                }
            }
            R.id.markedFragment -> {
                when (menuItemId) {
                    R.id.home_page -> {
                        navController.navigate(R.id.action_markedFragment_to_homeFragment)
                    }
                    R.id.history -> {
                        navController.navigate(R.id.action_markedFragment_to_historyFragment)
                    }
                    R.id.marked -> {
                    }
                    R.id.settings -> {
                        navController.navigate(R.id.action_markedFragment_to_settingsFragment)
                    }
                }
            }
            R.id.settingsFragment -> {
                when (menuItemId) {
                    R.id.home_page -> {
                        navController.navigate(R.id.action_settingsFragment_to_homeFragment)
                    }
                    R.id.history -> {
                        navController.navigate(R.id.action_settingsFragment_to_historyFragment)
                    }
                    R.id.marked -> {
                        navController.navigate(R.id.action_settingsFragment_to_markedFragment)
                    }
                    R.id.settings -> {
                    }
                }
            }
            R.id.detailsFragment -> {
                when (menuItemId) {
                    R.id.home_page -> {
                        navController.navigate(R.id.action_detailsFragment_to_homeFragment)
                    }
                    R.id.history -> {
                        navController.navigate(R.id.action_detailsFragment_to_historyFragment)
                    }
                    R.id.marked -> {
                        navController.navigate(R.id.action_detailsFragment_to_markedFragment)
                    }
                    R.id.settings -> {
                        navController.navigate(R.id.action_detailsFragment_to_settingsFragment)
                    }
                }
            }
        }

    }


    fun navigateToDetailsFragment(
        navController: NavController,
        onScreenFragmentId: Int,
        bundle: Bundle,
    ) {
        when (onScreenFragmentId) {
            R.id.homeFragment -> {
                navController.navigate(R.id.action_homeFragment_to_detailsFragment, bundle)
            }
            R.id.historyFragment -> {
                navController.navigate(R.id.action_historyFragment_to_detailsFragment, bundle)
            }
            R.id.markedFragment -> {
                navController.navigate(R.id.action_markedFragment_to_detailsFragment, bundle)
            }
            R.id.settingsFragment -> {
                navController.navigate(R.id.action_settingsFragment_to_detailsFragment, bundle)
            }
        }

    }
}