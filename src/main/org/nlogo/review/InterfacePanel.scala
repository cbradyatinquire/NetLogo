// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.review

import java.awt.Color.GRAY
import java.awt.Color.WHITE

import scala.Option.option2Iterable

import javax.swing.JPanel

class InterfacePanel(val reviewTab: ReviewTab) extends JPanel {

  override def isOptimizedDrawingEnabled = false

  setOpaque(true)
  setLayout(null) // disable layout manager to use absolute positioning

  private var _widgetPanels: Seq[JPanel] = Seq.empty
  def widgetPanels = _widgetPanels

  reviewTab.state.afterRunChangePub.newSubscriber { event =>
    _widgetPanels.foreach(remove)
    _widgetPanels = event.newRun.toSeq.flatMap {
      WidgetPanels.create(reviewTab.ws, _)
    }
    // we go the panels in back-to-front order but we
    // need to add them in front-to-back order so
    // that they're painted in the correct z-order:
    _widgetPanels.reverse.foreach(add)
  }

  override def paintComponent(g: java.awt.Graphics) {
    setBackground(if (reviewTab.state.currentRun.isDefined) WHITE else GRAY)
    super.paintComponent(g)
  }

  override def getPreferredSize: java.awt.Dimension =
    if (_widgetPanels.nonEmpty) {
      val maxX = _widgetPanels.map(p => p.getLocation().x + p.getSize().width).max
      val maxY = _widgetPanels.map(p => p.getLocation().y + p.getSize().height).max
      new java.awt.Dimension(maxX, maxY)
    } else new java.awt.Dimension(0, 0)
}
