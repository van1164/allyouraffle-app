package com.allyouraffle.allyouraffle.android.home

import androidx.compose.ui.graphics.vector.ImageVector
import com.allyouraffle.allyouraffle.android.home.myiconpack.IcTickets
import kotlin.collections.List as ____KtList

public object MyIconPack

private var __HomeIcons: ____KtList<ImageVector>? = null

public val MyIconPack.HomeIcons: ____KtList<ImageVector>
  get() {
    if (__HomeIcons != null) {
      return __HomeIcons!!
    }
    __HomeIcons= listOf(IcTickets)
    return __HomeIcons!!
  }
