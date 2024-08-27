package svg

import androidx.compose.ui.graphics.vector.ImageVector
import svg.ticketicon.AllIcons
import svg.ticketicon.IcTickets
import svg.ticketicon.Ticketicon
import kotlin.collections.List as ____KtList

public object TicketIcon

private var __AllIcons: ____KtList<ImageVector>? = null

public val TicketIcon.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= Ticketicon.AllIcons + listOf(IcTickets)
    return __AllIcons!!
  }
