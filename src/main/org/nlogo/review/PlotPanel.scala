// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.review

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints.KEY_ANTIALIASING
import java.awt.RenderingHints.VALUE_ANTIALIAS_ON

import org.nlogo.mirror.ModelRun
import org.nlogo.plot.Plot
import org.nlogo.swing.Utils.createWidgetBorder
import org.nlogo.window.InterfaceColors
import org.nlogo.window.PlotWidgetGUI

class PlotPanel(
  val panelBounds: java.awt.Rectangle,
  val originalFont: java.awt.Font,
  run: ModelRun,
  initialPlot: Plot,
  legendIsOpen: Boolean,
  xAxisLabel: String,
  yAxisLabel: String)
  extends WidgetPanel {
  setBorder(createWidgetBorder)
  setBackground(InterfaceColors.PLOT_BACKGROUND)
  val gui = new PlotWidgetGUI(initialPlot, this)
  gui.legend.open = legendIsOpen
  gui.addToPanel(this)
  gui.xAxis.setLabel(xAxisLabel)
  gui.yAxis.setLabel(yAxisLabel)

  override def doLayout() {
    super.doLayout()
    for {
      frame <- run.currentFrame
      plot <- frame.plots.find(_.name == initialPlot.name)
    } {
      gui.plot = plot
    }
  }
}
