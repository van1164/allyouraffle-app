package svg.ticketicon

import androidx.compose.ui.graphics.vector.ImageVector
import svg.TicketIcon
import kotlin.collections.List as ____KtList

public object TicketiconGroup

public val TicketIcon.Ticketicon: TicketiconGroup
  get() = TicketiconGroup

private var __AllIcons: ____KtList<ImageVector>? = null

public val TicketiconGroup.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf()
    return __AllIcons!!
  }
