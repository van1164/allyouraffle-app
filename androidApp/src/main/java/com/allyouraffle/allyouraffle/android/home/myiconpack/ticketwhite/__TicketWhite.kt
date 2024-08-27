package com.allyouraffle.allyouraffle.android.home.myiconpack.ticketwhite

import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.collections.List as ____KtList

public object TicketWhite

private var __AllIcons: ____KtList<ImageVector>? = null

public val TicketWhite.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= Ticketicon.AllIcons + listOf(IcTickets)
    return __AllIcons!!
  }
